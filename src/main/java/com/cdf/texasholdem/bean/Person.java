package com.cdf.texasholdem.bean;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Arrays;

public class Person {

    private Poker[] pokers;
    private Integer bankRoll;
    private Integer currentWager;
    private Integer playerIndex;
    private boolean status;
    private boolean isAllin;
    // for DAO
    private String UUID;
    @Pattern(regexp = "(^[a-zA-Z0-9_-]{6,16}$)|(^[\u2E80-\u9FFF]{2,5})",
            message = "用户名必须为 2-5 位汉字或者 6-16 位英文与数字的组合")
    private String id;
    @NotBlank
    private String password;
    private String name;

    public Person(String name) {
        this.name = name;
        this.pokers = new Poker[2];
        this.bankRoll = 20;
        this.status = false;
        this.isAllin = false;
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
        this.bankRoll = bankRoll;
    }

    public Integer getBankRoll() {
        return bankRoll;
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
                ", BankRoll=" + bankRoll +
                ", currentWager=" + currentWager +
                ", status=" + status +
                ", isAllin=" + isAllin +
                ", UUID='" + UUID + '\'' +
                ", id='" + id + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
