package com.cdf;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class RunTest {

    @Test
    void shuffle() {
        ArrayList<Poker> list = new Run().shuffle();
        System.out.println("=========== 洗牌前 ===========");
        System.out.println(list);
        System.out.println("=========== 洗牌后 ===========");
        Collections.shuffle(list);
        System.out.println(list);
        System.out.println("=========== 排序后 ===========");
        Collections.sort(list);
        System.out.println(list);
    }

    @Test
    void game() {
        ArrayList<Poker> list = new Run().shuffle();
        Collections.shuffle(list);
        new Run().game(list);
    }
}