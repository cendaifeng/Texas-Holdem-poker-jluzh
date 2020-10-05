package com.cdf;

public class Poker implements Comparable<Poker> {

    private Kind kind;
    private Color color;
    private int value;

    public Poker() { }

    public Poker(Kind kind, Color color) {
        this.kind = kind;
        this.color = color;
        this.value = kind.getValue() + color.getValue();
    }

    @Override
    public String toString() {
        return "Poker{" + "kind=" + kind + ", color=" + color +
                '}' + "\n";
    }

    @Override
    public int compareTo(Poker o) {
        return  this.value > o.value ? 1 : (this.value == o.value ? 0 : -1);
    }
}
