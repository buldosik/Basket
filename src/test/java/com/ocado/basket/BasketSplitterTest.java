package com.ocado.basket;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BasketSplitterTest {

    private String path = "G:\\Projects\\Java\\basket\\data";

    @org.junit.jupiter.api.Test
    void split1() {
        BasketSplitterBrute basketSplitterBrute = new BasketSplitterBrute(path + "\\config1.json");

        List<String> input = GetInputJSON(path + "\\basket-11.json");

        Map<String, List<String>> answer = basketSplitterBrute.split(input);

        // Print the Map
        System.out.println("Map<String, List<String>>:");
        for (Map.Entry<String, List<String>> entry : answer.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
    @org.junit.jupiter.api.Test
    void split2() {
        BasketSplitterBrute basketSplitterBrute = new BasketSplitterBrute(path + "\\config.json");

        List<String> input = GetInputJSON(path + "\\basket-1.json");

        Map<String, List<String>> answer = basketSplitterBrute.split(input);

        // Print the Map
        System.out.println("Map<String, List<String>>:");
        for (Map.Entry<String, List<String>> entry : answer.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
    @org.junit.jupiter.api.Test
    void split3() {
        BasketSplitterBrute basketSplitterBrute = new BasketSplitterBrute(path + "\\config.json");

        List<String> input = GetInputJSON(path + "\\basket-2.json");

        Map<String, List<String>> answer = basketSplitterBrute.split(input);

        // Print the Map
        System.out.println("Map<String, List<String>>:");
        for (Map.Entry<String, List<String>> entry : answer.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
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