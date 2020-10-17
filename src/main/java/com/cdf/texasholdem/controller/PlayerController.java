package com.cdf.texasholdem.controller;

import com.cdf.texasholdem.bean.Msg;
import com.cdf.texasholdem.bean.Person;
import com.cdf.texasholdem.bean.Poker;
import com.cdf.texasholdem.service.RunGame;
import com.cdf.texasholdem.service.RunGameManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 控制玩家操作的 Controller
 * 下注，全押，弃牌等操作
 * 分别对应玩家在牌局中可控制的几个按钮
 */
@RestController
public class PlayerController {

    private static final ReentrantLock reentrantLock = new ReentrantLock();

    Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 玩家状态查询,同时返回公共牌和赢家
     * 若不在玩家当前回合则阻塞等待，若轮到玩家则返回
     * 这里前端会建立Ajax长轮询
     * @param session
     * @return 若返回Msg.success()则该玩家的按钮高亮
     */
    @GetMapping("/player/status")
    public Msg getStatus(HttpSession session) {
        Integer tableIndex = (Integer) session.getAttribute("tableIndex");
        Integer playerIndex = (Integer) session.getAttribute("playerIndex");
        RunGame table = RunGameManager.getRunGameHashMap().get(tableIndex);
        Person person = table.getPlayers().get(playerIndex);

        int timestamp = 0;
        while (!person.getStatus()) {
            // 玩家状态为不是当前回合，等待
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            timestamp++;
            if (timestamp >= 60) {
                Msg msg = Msg.fail().add("msg", "超时").add("boardCard", table.getBoard().getBoardCards());
                if (table.getWinner() != null)
                    msg.add("winner", table.getWinner());
                return msg;
            }
        }
        // 开启一个线程，在30s之后将status置为false，防止有人越过前端Ajax超时限制
        FutureTask<Integer> futureTask = new FutureTask(() -> {
            Thread.sleep(30000);
            if (person.getStatus()) {
                person.setStatus(false);
            }
            return 1;
        });
        new Thread(futureTask).start();
        Msg msg = Msg.success().add("msg", "轮到你了").add("boardCard", table.getBoard().getBoardCards());
        if (table.getWinner() != null)
            msg.add("winner", table.getWinner());
        return msg;
    }

    /**
     * 获取手牌
     * @param session
     * @return
     */
    @GetMapping("/player/cards")
    public Msg getCards(HttpSession session) {
        Integer tableIndex = (Integer) session.getAttribute("tableIndex");
        Integer playerIndex = (Integer) session.getAttribute("playerIndex");
        RunGame table = RunGameManager.getRunGameHashMap().get(tableIndex);

        Poker[] pokers = table.getPlayers().get(playerIndex).getPokers();
        return Msg.success().add("pokers", pokers);
    }

    /**
     * 玩家下注操作
     * 调用RunGame.nextRound()将玩家轮次后移，并将该玩家状态置false
     * 加锁，防止玩家重复提交表单导致的重复下注
     * @param wager 下注数量
     * @param session Session域
     * @return
     */
    @PostMapping("/player/bet")
    public Msg bet(@RequestParam int wager,
                      HttpSession session) {
        Integer tableIndex = (Integer) session.getAttribute("tableIndex");
        Integer playerIndex = (Integer) session.getAttribute("playerIndex");
        RunGame table = RunGameManager.getRunGameHashMap().get(tableIndex);
        Person person = table.getPlayers().get(playerIndex);

        reentrantLock.lock();
        if (person.getCurrentWager() + wager >= table.getCallLimited()) {
            if (!table.playerBet(playerIndex, wager)) {
                return Msg.fail().add("msg", "错误：玩家金额不足或未到玩家回合");
            }
            table.nextPlayer();
            person.setStatus(false);
        } else {
            return Msg.fail().add("msg", "错误：下注小于最小跟注限制");
        }
        reentrantLock.unlock();
        return Msg.success();
    }

    /**
     * 玩家全押操作
     * @param wager 下注数量
     * @param session Session域
     * @return
     */
    @PutMapping("/player/bet")
    public Msg allIn(@RequestParam int wager,
                        HttpSession session) {
        Integer tableIndex = (Integer) session.getAttribute("tableIndex");
        Integer playerIndex = (Integer) session.getAttribute("playerIndex");
        RunGame table = RunGameManager.getRunGameHashMap().get(tableIndex);
        Person person = table.getPlayers().get(tableIndex);

        reentrantLock.lock();
        if (!table.playerBet(playerIndex, wager))
            Msg.fail().add("msg", "错误：玩家金额不足或未到玩家回合");
        if (person.getCurrentWager() > table.getCallLimited()) {
            // 已更新过已下注金额
            table.setCallLimited(person.getCurrentWager());
        }
        person.setAllin(true);
        table.nextPlayer();
        person.setStatus(false);
        reentrantLock.unlock();
        return Msg.success();
    }

    /**
     * 玩家弃牌操作
     * @param session
     * @return
     */
    @DeleteMapping("/player/fold")
    public Msg fold(HttpSession session) {
        Integer tableIndex = (Integer) session.getAttribute("tableIndex");
        Integer playerIndex = (Integer) session.getAttribute("playerIndex");
        logger.debug(playerIndex.toString());
        RunGame table = RunGameManager.getRunGameHashMap().get(tableIndex);
        Person person = table.getPlayers().get(playerIndex);

        reentrantLock.lock();
        table.playerFold(playerIndex);
        table.nextPlayer();
        person.setStatus(false);
        reentrantLock.unlock();
        return Msg.success();
    }

}
