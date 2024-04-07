package com.ocado;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocado.basket.BasketSplitter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String path = "G:\\Projects\\Java\\basket\\data";
        BasketSplitter basketSplitter = new BasketSplitter(path + "\\config1.json");

        List<String> input = GetInputJSON(path + "\\basket-11.json");

        Map<String, List<String>> answer = basketSplitter.split(input);

        // Print the Map
        System.out.println("Map<String, List<String>>:");
        for (Map.Entry<String, List<String>> entry : answer.entrySet()) {
            System.out.println("\"" + entry.getKey() + "\" : " + entry.getValue());
        }
    }
    static List<String> GetInputJSON(String path) {
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