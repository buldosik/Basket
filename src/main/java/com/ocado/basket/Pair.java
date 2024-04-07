//Artsiom_Siandzevich_Java_Wroclaw
package com.ocado.basket;

record Pair<F, S>(F first, S second) {
    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}
