package com.cdf.texasholdem.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board {

    private Poker[] boardCards;

    private Integer pot = 0;

    // 初始 3 个台面牌位
    public Board() {
        boardCards = new Poker[3];
    }

    // 首 3 张公共牌
    public void Flop(ArrayList<Poker> cards) {
        boardCards[0] = cards.remove(0);
        boardCards[1] = cards.remove(0);
        boardCards[2] = cards.remove(0);
    }

    public Poker[] getBoardCards() {
        return this.boardCards;
    }

    public void setBoardCards(Poker[] boardCards) {
        this.boardCards = boardCards;
    }

    public void addCard(Poker poker) {
        List<Poker> pokers = new ArrayList<>(Arrays.asList(this.boardCards));
//        while (pokers.remove(null));
        pokers.add(poker);
        Poker[] newPokers = pokers.toArray(new Poker[pokers.size()]);
        this.setBoardCards(newPokers);
    }

    public Integer getPot() {
        return pot;
    }

    public void setPot(Integer pot) {
        this.pot = pot;
    }

    public void addPot(Integer add) {
        setPot(this.pot + add);
    }
}


