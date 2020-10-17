package com.cdf.texasholdem.bean;

public enum Color {

    SPADE("spade", 400),
    HEART("heart", 300),
    CLUB("club", 200),
    DIAMOND("diamond", 100);

    private final String name;
    private final long value;

    Color(String name, long value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public long getValue() {
        return value;
    }
}
