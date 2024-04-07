package com.ocado.basket;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

class BasketSplitterTest {

    private String path = "G:\\Projects\\Java\\basket\\data";

    @org.junit.jupiter.api.Test
    void split1() {
        BasketSplitter basketSplitter = new BasketSplitter(path + "\\config1.json");

        List<String> input = GetInputJSON(path + "\\basket-11.json");

        Map<String, List<String>> answer = basketSplitter.split(input);

        // Print the Map
        System.out.println("Map<String, List<String>>:");
        for (Map.Entry<String, List<String>> entry : answer.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
    @org.junit.jupiter.api.Test
    void split2() {
        BasketSplitter basketSplitter = new BasketSplitter(path + "\\config0.json");

        List<String> input = GetInputJSON(path + "\\basket-01.json");

        Map<String, List<String>> answer = basketSplitter.split(input);

        // Print the Map
        System.out.println("Map<String, List<String>>:");
        for (Map.Entry<String, List<String>> entry : answer.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
    @org.junit.jupiter.api.Test
    void split3() {
        BasketSplitter basketSplitter = new BasketSplitter(path + "\\config0.json");

        List<String> input = GetInputJSON(path + "\\basket-02.json");

        Map<String, List<String>> answer = basketSplitter.split(input);

        // Print the Map
        System.out.println("Map<String, List<String>>:");
        for (Map.Entry<String, List<String>> entry : answer.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
    @org.junit.jupiter.api.Test
    void split4() {
        BasketSplitter basketSplitter = new BasketSplitter(path + "\\config2.json");

        List<String> input = GetInputJSON(path + "\\basket-21.json");

        Map<String, List<String>> answer = basketSplitter.split(input);

        // Print the Map
        System.out.println("Map<String, List<String>>:");
        for (Map.Entry<String, List<String>> entry : answer.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
    @org.junit.jupiter.api.Test
    void split5() {
        BasketSplitter basketSplitter = new BasketSplitter(path + "\\config2.json");

        List<String> input = GetInputJSON(path + "\\basket-22.json");

        Map<String, List<String>> answer = basketSplitter.split(input);

        // Print the Map
        System.out.println("Map<String, List<String>>:");
        for (Map.Entry<String, List<String>> entry : answer.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    @org.junit.jupiter.api.Test
    void split6() {
        BasketSplitter basketSplitter = new BasketSplitter(path + "\\config2.json");

        List<String> input = GetInputJSON(path + "\\basket-23.json");

        Map<String, List<String>> answer = basketSplitter.split(input);

        // Print the Map
        System.out.println("Map<String, List<String>>:");
        for (Map.Entry<String, List<String>> entry : answer.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    @org.junit.jupiter.api.Test
    void split_group1() {
        BasketSplitter basketSplitter = new BasketSplitter(path + "\\config8.json");

        for (int i = 0; i < 10; i++) {
            List<String> input = GetInputJSON(path + "\\basket-8" + i + ".json");
            Map<String, List<String>> answer = basketSplitter.split(input);
            // Print the Map
            System.out.println("Map<String, List<String>>:");
            for (Map.Entry<String, List<String>> entry : answer.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }
    }

    List<String> GetInputJSON(String path) {
        File file = new File(path);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Parse JSON array into a List<String>
            return Arrays.asList(objectMapper.readValue(file, String[].class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return List.of();
    }
}