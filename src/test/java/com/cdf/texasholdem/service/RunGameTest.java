package com.cdf.texasholdem.service;

import com.cdf.texasholdem.bean.Person;
import com.cdf.texasholdem.bean.Poker;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

class RunGameTest {

    @Test
    void shuffleTest() {
        ArrayList<Poker> list = new RunGame(null).CardInOrder();
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
    void licensingToPlayerTest() {
        CopyOnWriteArrayList<Person> players = new CopyOnWriteArrayList<>(Arrays.asList(new Person[]{
                new Person("Jackey"),
                new Person("Joe"),
                new Person("Nana"),
                new Person("Berlin")}));
        new RunGame(players).licensingToPlayer();
    }

    @Test
    void compareValueTest() {
        CopyOnWriteArrayList<Person> players = new CopyOnWriteArrayList<>(Arrays.asList(new Person[]{
                new Person("Jackey"),
                new Person("Joe"),
                new Person("Nana"),
                new Person("CEN"),
                new Person("BAIYUAN"),
                new Person("LINGDAO"),
                new Person("TAOGE"),
                new Person("Berlin")}));
        RunGame runGame = null;


        long l1 = System.currentTimeMillis();
        for (int i = 0; i < 1; i++) {
            runGame = new RunGame(players);
            runGame.initBoardCards();
            runGame.licensingToPlayer();
//        Poker[] boardCards = run.getBoard().getBoardCards();
//        System.out.println(boardCards[0] + "\n" + boardCards[1] + "\n" + boardCards[2]);

            runGame.addBoardCards();
//        boardCards = run.getBoard().getBoardCards();
//        System.out.println(boardCards[3]);

            runGame.addBoardCards();
//        boardCards = run.getBoard().getBoardCards();
//        System.out.println(boardCards[4]);
            System.out.println("winner: " + runGame.compareValue());
        }
        long l2 = System.currentTimeMillis();
        System.out.println(l2-l1);
        System.out.println();

        Poker[] boardCards = runGame.getBoard().getBoardCards();
        System.out.println(boardCards[0] + "\n" + boardCards[1] + "\n" + boardCards[2] + "\n" + boardCards[3] + "\n" + boardCards[4]);
        System.out.println("====================================");
        runGame.getPlayers().forEach(x -> System.out.println(x.getName() + ": " + x.getPokers()[0] + x.getPokers()[1]));

    }
}