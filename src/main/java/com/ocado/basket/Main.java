package com.ocado.basket;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String path = "G:\\Projects\\Java\\Basket\\data";
        BasketSplitter basketSplitter = new BasketSplitter(path + "\\config8.json");

        List<String> input = GetBasketJSON(path + "\\basket-80.json");

        Map<String, List<String>> output = basketSplitter.split(input);

        System.out.println("Map<String, List<String>>:");
        for (Map.Entry<String, List<String>> entry : output.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue().size() + " " + entry.getValue());
        }
    }
    static List<String> GetBasketJSON(String path) {
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