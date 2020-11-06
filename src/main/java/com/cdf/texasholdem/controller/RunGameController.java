package com.cdf.texasholdem.controller;

import com.cdf.texasholdem.bean.ServerResponse;
import com.cdf.texasholdem.bean.Person;
import com.cdf.texasholdem.mapper.PersonMapper;
import com.cdf.texasholdem.service.RunGame;
import com.cdf.texasholdem.service.RunGameManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.ReentrantLock;

import static com.cdf.texasholdem.utils.NetUtil.getGivenCookies;

/**
 * 控制牌桌的 Controller
 * 选牌桌，开始游戏，退出牌桌
 */
@Controller
@Scope("prototype")
public class RunGameController {

    @Autowired
    PersonMapper personMapper;

    @Autowired
    RunGame runGame;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final ReentrantLock reentrantLock = new ReentrantLock();

    /**
     * 创建牌桌之前的查询，若已存在该牌桌则不予创建
     * 于加入牌桌查询共用
     * @param tableIndex 牌桌编号
     * @return 注：在加入牌桌时，Msg为fail意为可以加入
     */
    @ResponseBody
    @GetMapping("/rungame/create")
    public ServerResponse checkTable(@RequestParam Integer tableIndex) {

        if ( !RunGameManager.runGameHashMap.containsKey(tableIndex) ){
            return ServerResponse.success();
        } else {
            return ServerResponse.fail().setCode(301).add("msg", "该牌桌已存在");
        }
    }

    /**
     * 创建牌桌
     * 创建后转发到加入牌桌
     * @param tableIndex 牌桌编号
     * @return
     */
    @PostMapping("/rungame/create")
    public String createTable(@RequestParam Integer tableIndex) {

        RunGameManager.addRunGame(tableIndex, runGame);
        runGame.setConfirmLeftMap(new ConcurrentHashMap<String, Boolean>());
        logger.trace("POST:/rungame/create");
        return "forward:/rungame/access";
    }

    /**
     * 玩家进入牌桌
     * 将tableIndex和playerIndex写入Session
     * @param tableIndex 牌桌编号
     * @param req HttpServletRequest
     * @param session
     * @return
     */
    @PostMapping("/rungame/access")
    public String accessTable(@RequestParam Integer tableIndex,
                              HttpServletRequest req, HttpSession session) {

        logger.trace("POST:/rungame/access");

        String userid = getGivenCookies(req.getCookies(), "loginUser");
        if (userid==null) {
            logger.error("用户登录信息有误");
            return "login";
        }
        session.setAttribute("tableIndex", tableIndex);
        RunGame table;
        try {
            table = RunGameManager.getRunGameHashMap().get(tableIndex);
            table.setTableIndex(tableIndex);
        } catch (NullPointerException e) {
            logger.error("居然找不到牌桌");
            return "/rungame/list";
        }
        if (table.getPlayers().size() >= 8) {
            logger.error("牌桌玩家超过八人，无法进入");
            return "/rungame/list";
        }
        // 到数据库中找person
        Person person = personMapper.getPersonById(userid);
        System.out.println(person);
        if (person.getBankRoll()<2) {
            logger.error("该玩家没钱");
            return "/rungame/list";
        }

        boolean b = table.getPlayers().stream()
                .anyMatch(x -> x.getId().equals(userid));
        if (!b) {
            table.addPlayer(person);
        } else {
            logger.error("该玩家已加入牌桌");
            return "/rungame/list";
        }
        // 玩家编号从0开始
        int playerIndex = table.getPlayers().size() - 1;
        session.setAttribute("playerIndex", playerIndex);
        person.setPlayerIndex(playerIndex);
        logger.trace("playerIndex: "+Integer.toString(playerIndex));
        return "playing";
    }

    /**
     * 游戏开始
     * 异步执行RunGame.run()
     * 将该牌桌的GameStatus设为"begin"
     * 将ArrayList中所有player加入BlockingQueue，其队首为列表中0号元素
     * 置0号玩家为当前回合玩家
     * @param session
     * @return
     */
    @ResponseBody
    @GetMapping("/rungame/begin")
    public ServerResponse begin(HttpSession session) {

        Integer tableIndex = (Integer) session.getAttribute("tableIndex");
        RunGame table = RunGameManager.getRunGameHashMap().get(tableIndex);
        logger.trace("GET:/rungame/begin");
        logger.trace(tableIndex.toString()+" 号桌开台");

        synchronized(table) {
            if (table.isRun())
                return ServerResponse.fail().add("msg", "该桌已经开始游戏");
            table.setRun(true);
            // 随机大小盲，唤醒队中第三个玩家（枪口）的下注操作
            CopyOnWriteArrayList<Person> players = table.getPlayers();
            table.setPlayingPlayers(new CopyOnWriteArrayList<>(players));
            table.setPlayerQueue(new ArrayBlockingQueue<Person>(players.size(), true, players));

            ArrayBlockingQueue<Person> playerQueue = table.getPlayerQueue();
//        Collections.shuffle((List<?>) playerQueue);
            try {
                Person person = playerQueue.take();
                playerQueue.put(person);
                table.playerBetWithoutCheck(person.getPlayerIndex(), 1);
                person = playerQueue.take();
                playerQueue.put(person);
                table.playerBetWithoutCheck(person.getPlayerIndex(), 2);
                table.nextPlayer(null, null, null);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            FutureTask<Integer> futureTask = new FutureTask(() -> {
                table.run();
                return 1;
            });
            new Thread(futureTask).start();
        }
        return ServerResponse.success();
    }

    @PostMapping("/rungame/exit")
    public String exit(HttpSession session) {
        Integer tableIndex = (Integer) session.getAttribute("tableIndex");
        Integer playerIndex = (Integer) session.getAttribute("playerIndex");
        RunGame table = RunGameManager.getRunGameHashMap().get(tableIndex);
        Person person = table.getPlayers().get(playerIndex);

        reentrantLock.lock();
        if (!table.getPlayerQueue().equals(null)) {
            table.playerFold(playerIndex);
        }
        reentrantLock.unlock();
        session.removeAttribute("playerIndex");
        session.removeAttribute("tableIndex");
        return "login";
    }
}
