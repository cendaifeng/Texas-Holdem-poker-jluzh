package com.cdf.texasholdem.bean;

public enum Kind {

    TWO("2", 1000),
    THREE("3", 1500),
    FOUR("4", 2000),
    FIVE("5", 2500),
    SIX("6", 3000),
    SEVEN("7", 3500),
    EIGHT("8", 4000),
    NINE("9", 4500),
    TEN("10", 5000),
    JAKE("J", 5500),
    QUEEN("Q", 6000),
    KING("K", 6500),
    ACE("A", 7000);

    private final String name;
    private final long value;

    Kind(String name, long value) {
        this.name = name;
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
