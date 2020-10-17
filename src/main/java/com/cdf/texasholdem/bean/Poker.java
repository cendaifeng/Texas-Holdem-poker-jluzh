package com.cdf.texasholdem.bean;

import java.io.Serializable;
import java.util.Objects;

public class Poker implements Comparable<Poker>, Serializable {

    private Kind kind;
    private Color color;
    private long value;

    public Poker(Kind kind, Color color) {
        this.kind = kind;
        this.color = color;
        this.value = kind.getValue() + color.getValue();
    }

    public Kind getKind() {
        return kind;
    }

    public Color getColor() {
        return color;
    }

    public long getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Poker{" + "kind=" + kind + ", color=" + color + ", value=" + value +
                '}' + "\n";
    }

    @Override
    public int compareTo(Poker o) {
        return  this.value > o.value ? 1 : (this.value == o.value ? 0 : -1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Poker poker = (Poker) o;
        return value == poker.value &&
                kind == poker.kind &&
                color == poker.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, color, value);
    }
}
