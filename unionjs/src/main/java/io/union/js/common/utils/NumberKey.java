package io.union.js.common.utils;

/**
 * Created by Administrator on 2017/7/17.
 */
public enum NumberKey {
    ZERO(0), ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9);

    NumberKey(int value) {
        this.value = value;
    }

    private int value;

    public int getValue() {
        return value;
    }
}
