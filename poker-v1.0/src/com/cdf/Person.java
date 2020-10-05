package com.cdf;

import java.util.ArrayList;

public class Person {

    private String name;
    private ArrayList<Poker> pokers;

    public Person(String name) {
        this.name = name;
        this.pokers = new ArrayList<>(13);
    }

    public ArrayList<Poker> getPokers() {
        return pokers;
    }

    @Override
    public String
    toString() {
        return "Person{" + "name='" + name + '\''+ '\n' + ", pokers=" + pokers +
                '}';
    }
}
