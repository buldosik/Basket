package com.ocado.basket;

public record Pair<F, S>(F first, S second) {

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}
