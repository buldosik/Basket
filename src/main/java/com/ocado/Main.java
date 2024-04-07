package com.ocado;

import com.ocado.basket.TestGenerator;

public class Main {
    public static void main(String[] args) {
        String path = "G:\\Projects\\Java\\basket\\data";
        TestGenerator testGenerator = new TestGenerator(path);
        testGenerator.GenerateGroup(1000, 10, 30, "7");
    }
}