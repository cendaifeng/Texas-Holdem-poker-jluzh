package com.cdf.texasholdem.controller;

import com.cdf.texasholdem.bean.ServerResponse;
import com.cdf.texasholdem.bean.Person;
import com.cdf.texasholdem.bean.Poker;
import com.cdf.texasholdem.service.RunGame;
import com.cdf.texasholdem.service.RunGameManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * 控制玩家操作的 Controller
 * 下注，全押，弃牌等操作
 * 分别对应玩家在牌局中可控制的几个按钮
 */
@RestController
public class PlayerController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 玩家状态查询,同时返回下注信息，公共牌，和赢家
     * 若不在玩家当前回合则阻塞等待，若轮到玩家则返回xxxx
     * 若游戏状态未发生改变则阻塞等待，若轮到玩家则返回
     * 这里前端会建立Ajax长轮询
     * @param session
     * @return 若返回Msg.success()则该玩家的按钮高亮
     */
    @GetMapping("/player/polling")
    public ServerResponse getStatus(HttpSession session, ModelMap map) {
        Integer tableIndex = (Integer) session.getAttribute("tableIndex");
        Integer playerIndex = (Integer) session.getAttribute("playerIndex");
        RunGame table = RunGameManager.getRunGameHashMap().get(tableIndex);
        logger.debug("status:"+tableIndex.toString());
        CopyOnWriteArrayList<Person> players = table.getPlayers();
        Person person = players.get(playerIndex);
        
//        // 开启一个线程，在30s之后将status置为false，防止有人越过前端Ajax超时限制
//        FutureTask<Integer> futureTask = new FutureTask(() -> {
//            Thread.sleep(30000);
//            if (person.getStatus()) {
//                person.setStatus(false);
//            }
//            return 1;
//        });
//        new Thread(futureTask).start();

        ServerResponse poll = null;
        try {
            // 若超时则返回 null
            poll = person.getMsgQueue().poll(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (null == poll) {
            poll = new ServerResponse();
            poll.add("msg", "超时");
            poll.setCode(200);
            // 虽然是超时返回，但是在前端请求处响应为成功
            return poll;
        }
        if (person.getStatus())
            poll.add("msg", "轮到你了");

        return poll;
    }

    /**
     * 获取手牌
     * @param session
     * @return
     */
    @GetMapping("/player/cards")
    public ServerResponse getCards(HttpSession session) {
        Integer tableIndex = (Integer) session.getAttribute("tableIndex");
        Integer playerIndex = (Integer) session.getAttribute("playerIndex");
        RunGame table = RunGameManager.getRunGameHashMap().get(tableIndex);

        Person person = table.getPlayers().get(playerIndex);
        Poker[] pokers = person.getPokers();
        return ServerResponse.success().add("pokers", pokers);
    }

    /**
     * 玩家下注操作
     * 调用RunGame.nextPlayer()将玩家轮次后移，并将该玩家状态置false
     * 加锁，防止玩家重复提交表单导致的重复下注
     * @param wager 下注数量
     * @param session Session域
     * @return
     */
    @PostMapping("/player/bet")
    public ServerResponse bet(@RequestParam int wager,
                              HttpSession session) {
        Integer tableIndex = (Integer) session.getAttribute("tableIndex");
        Integer playerIndex = (Integer) session.getAttribute("playerIndex");
        RunGame table = RunGameManager.getRunGameHashMap().get(tableIndex);
        Person person = table.getPlayers().get(playerIndex);

        synchronized (person) {
            if (person.getCurrentWager() + wager >= table.getCallLimited()) {
                if (!table.playerBet(playerIndex, wager)) {
                    return ServerResponse.fail().add("msg", "错误：玩家金额不足或未到玩家回合");
                }
                table.nextPlayer(person.getName(), "bet", wager);
                person.setStatus(false);
            } else {
                return ServerResponse.fail().add("msg", "错误：下注小于最小跟注限制");
            }
        }
        return ServerResponse.success();
    }

    /**
     * 玩家过牌操作
     * 调用RunGame.nextRound()将玩家轮次后移，并将该玩家状态置false
     * 加锁，防止玩家重复提交表单
     * @param session Session域
     * @return
     */
    @PostMapping("/player/check")
    public ServerResponse check(HttpSession session) {
        Integer tableIndex = (Integer) session.getAttribute("tableIndex");
        Integer playerIndex = (Integer) session.getAttribute("playerIndex");
        RunGame table = RunGameManager.getRunGameHashMap().get(tableIndex);
        Person person = table.getPlayers().get(playerIndex);

        synchronized (person) {
            if (person.getCurrentWager() >= table.getCallLimited()) {
                // 检验是否在玩家回合
                if (!person.getStatus())
                    return ServerResponse.fail().add("msg", "错误：未到玩家回合");
                table.nextPlayer(person.getName(), "check", null);
                person.setStatus(false);
            } else {
                return ServerResponse.fail().add("msg", "错误：下注小于最小跟注限制，不能过牌");
            }
        }
        return ServerResponse.success();
    }

    /**
     * 玩家全押操作
     * @param session Session域
     * @return
     */
    @PutMapping("/player/bet")
    public ServerResponse allIn(HttpSession session) {
        Integer tableIndex = (Integer) session.getAttribute("tableIndex");
        Integer playerIndex = (Integer) session.getAttribute("playerIndex");
        RunGame table = RunGameManager.getRunGameHashMap().get(tableIndex);
        Person person = table.getPlayers().get(tableIndex);

        Integer wager = person.getBankRoll();
        synchronized (person) {
            if (!table.playerBet(playerIndex, wager))
                ServerResponse.fail().add("msg", "错误：玩家金额不足或未到玩家回合");
            if (person.getCurrentWager() > table.getCallLimited()) {
                // 已更新过已下注金额
                table.setCallLimited(person.getCurrentWager());
            }
            person.setAllin(true);
            table.nextPlayer(person.getName(), "allin", wager);
            person.setStatus(false);
        }
        return ServerResponse.success();
    }

    /**
     * 玩家弃牌操作
     * @param session
     * @return
     */
    @PostMapping("/player/fold")
    public ServerResponse fold(HttpSession session) {
        Integer tableIndex = (Integer) session.getAttribute("tableIndex");
        Integer playerIndex = (Integer) session.getAttribute("playerIndex");
        logger.debug("fold: "+playerIndex.toString());
        RunGame table = RunGameManager.getRunGameHashMap().get(tableIndex);
        Person person = table.getPlayers().get(playerIndex);

        synchronized (person) {
            table.playerFold(playerIndex);
            table.nextPlayer(person.getName(), "fold", null);
            person.setStatus(false);
        }
        return ServerResponse.success();
    }

}
