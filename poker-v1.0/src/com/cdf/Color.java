package com.cdf;

public enum Color {

    SPADE("spade", 4),
    HEART("heart", 3),
    CLUB("club", 2),
    DIAMOND("diamond", 1);

    private final String name;
    private final int value;

    Color(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
