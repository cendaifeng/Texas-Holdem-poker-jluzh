package com.cdf;

import java.util.ArrayList;
import java.util.Arrays;

public class Run {

    public ArrayList<Poker> shuffle() {

        ArrayList<Poker> pokers = new ArrayList<>(54);

        for (Kind k : Kind.values()) {
            for (Color c : Color.values()) {
                pokers.add(new Poker(k, c));
            }
        }

        return pokers;
    }

    public void game(ArrayList<Poker> pokers) {
        ArrayList<Person> players = new ArrayList<>(Arrays.asList(new Person[]{
                new Person("Jackey"),
                new Person("Joe"),
                new Person("Nana"),
                new Person("Berlin")}));
        for (int i = 0; i < 4; i++) {
            players.get(i).getPokers().addAll(pokers.subList(i*13, (i+1)*13));
        }
        players.stream().forEach(System.out::println);
    }

}
