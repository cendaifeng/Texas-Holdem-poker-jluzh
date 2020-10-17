package com.cdf.texasholdem.utils;

import com.cdf.texasholdem.bean.Color;
import com.cdf.texasholdem.bean.Kind;
import com.cdf.texasholdem.bean.Poker;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.cdf.texasholdem.utils.GameUtil.*;

class GameUtilTest {

    @Test
    void cardValueTest() {
        ArrayList<Poker> pokers = new ArrayList<>(Arrays.asList(new Poker[]{
                new Poker(Kind.ACE, Color.CLUB),
                new Poker(Kind.FIVE, Color.SPADE),
                new Poker(Kind.FIVE, Color.CLUB),
                new Poker(Kind.ACE, Color.SPADE),
                new Poker(Kind.FIVE, Color.DIAMOND),
                new Poker(Kind.QUEEN, Color.CLUB),
                new Poker(Kind.ACE, Color.DIAMOND),
        }));
        System.out.println(cardValue(pokers));
    }

    @Test
    void sameKindMapTest() {
        ArrayList<Poker> pokers = new ArrayList<>(Arrays.asList(new Poker[]{
                new Poker(Kind.ACE, Color.CLUB),
                new Poker(Kind.FIVE, Color.SPADE),
                new Poker(Kind.FIVE, Color.CLUB),
                new Poker(Kind.ACE, Color.SPADE),
                new Poker(Kind.FIVE, Color.DIAMOND),
                new Poker(Kind.QUEEN, Color.CLUB),
                new Poker(Kind.ACE, Color.DIAMOND),
        }));
        HashMap<Kind, Integer> map = sameKindMap(pokers);
        ArrayList<Kind> sameKind = getSameKind(map, 3);
        System.out.println(map);
        System.out.println(sameKind);
    }

    @Test
    void ifFlushTest() {
        ArrayList<Poker> pokers = new ArrayList<>(Arrays.asList(new Poker[]{
                new Poker(Kind.ACE, Color.CLUB),
                new Poker(Kind.FIVE, Color.SPADE),
                new Poker(Kind.FIVE, Color.CLUB),
                new Poker(Kind.ACE, Color.SPADE),
                new Poker(Kind.FOUR, Color.CLUB),
                new Poker(Kind.QUEEN, Color.CLUB),
                new Poker(Kind.TWO, Color.CLUB),
        }));
        ifFlush(pokers);
    }

    @Test
    void ifStraight_FlushTest() {
        ArrayList<Poker> pokers = new ArrayList<>(Arrays.asList(new Poker[]{
                new Poker(Kind.ACE, Color.CLUB),
                new Poker(Kind.FIVE, Color.SPADE),
                new Poker(Kind.KING, Color.CLUB),
                new Poker(Kind.TEN, Color.SPADE),
                new Poker(Kind.QUEEN, Color.CLUB),
                new Poker(Kind.JAKE, Color.CLUB),
                new Poker(Kind.TWO, Color.CLUB),
        }));
        ifStraight_Flush(pokers);
    }
}