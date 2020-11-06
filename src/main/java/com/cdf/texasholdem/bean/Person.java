package com.cdf.texasholdem.bean;

import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class Person {

    private Poker[] pokers = new Poker[2];
    private Integer currentWager = 0;
    private Integer playerIndex = -1;
    private boolean status = false;
    private boolean isAllin = false;
    // 游戏状态消息队列，用于响应给前端的长轮询请求
    private LinkedBlockingQueue<ServerResponse> MsgQueue = new LinkedBlockingQueue();
    // for DAO
    private String UUID;
    @Pattern(regexp = "(^[a-zA-Z0-9_-]{5,16}$)",
            message = "用户名必须为 5-16 位英文(或)数字的组合")
    private String id;
    @NotBlank
    private String password;
    private String name;
    private Integer bankroll = 0;

    public Person() { }

    public Person(String id, String password, String name, Integer bankroll) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.bankroll = bankroll;
    }

    // for test
    public Person(String name) {
        this.name = name;
    }

    public LinkedBlockingQueue<ServerResponse> getMsgQueue() {
        return MsgQueue;
    }

    public void setMsgQueue(LinkedBlockingQueue<ServerResponse> msgQueue) {
        MsgQueue = msgQueue;
    }

    public Integer getPlayerIndex() {
        return playerIndex;
    }

    public void setPlayerIndex(Integer playerIndex) {
        this.playerIndex = playerIndex;
    }

    public boolean isAllin() {
        return isAllin;
    }

    public void setAllin(boolean allin) {
        isAllin = allin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPokers(Poker[] pokers) {
        this.pokers = pokers;
    }

    public Integer getBankroll() {
        return bankroll;
    }

    public void setBankroll(Integer bankroll) {
        this.bankroll = bankroll;
    }

    public boolean isStatus() {
        return status;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getCurrentWager() {
        return currentWager;
    }

    public void setCurrentWager(Integer currentWager) {
        this.currentWager = currentWager;
    }

    public void setBankRoll(Integer bankRoll) {
        this.bankroll = bankRoll;
    }

    public Integer getBankRoll() {
        return this.bankroll;
    }

    public String getName() {
        return name;
    }

    public Poker[] getPokers() {
        return pokers;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Person{" +
                "pokers=" + Arrays.toString(pokers) +
                ", bankroll=" + bankroll +
                ", currentWager=" + currentWager +
                ", playerIndex=" + playerIndex +
                ", UUID='" + UUID + '\'' +
                ", userid='" + id + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + name + '\'' +
                '}';
    }
}
