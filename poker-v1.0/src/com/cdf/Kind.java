package com.cdf;

public enum Kind {

    ACE("1", 140),
    TWO("2", 150),
    THREE("3", 30),
    FOUR("4", 40),
    FIVE("5", 50),
    SIX("6", 60),
    SEVEN("7", 70),
    EIGHT("8", 80),
    NINE("9", 90),
    TEN("10", 100),
    JAKE("J", 110),
    QUEEN("Q", 120),
    KING("K", 130),
//    JOKER("JOKER", 999);
    ;

    private final String name;
    private final int value;

    Kind(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
