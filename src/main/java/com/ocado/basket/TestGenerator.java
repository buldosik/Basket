package com.ocado.basket;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class TestGenerator {
    private String path = "";

    public TestGenerator(String absolutePathToConfigFile) {
        path = absolutePathToConfigFile;
    }

    public void GenerateConfig(String filename, int countItems, int countCompanies) {
        String filePath = path + "\\config" + filename + ".json";
        Map<String, List<String>> output = new HashMap<>();

        Random rand = new Random();
        for (int i = 0; i < countItems; i++) {
            List<String> companiesForItem = new ArrayList<>();
            int l = rand.nextInt(countCompanies);
            int r = rand.nextInt(countCompanies);
            for (int j = 0; j < r + 1; j++) {
                int x = (l + j) % 10;
                companiesForItem.add("Company" + x);
            }
            output.put("Item" + i, companiesForItem);
        }
        // Print map as JSON
        ObjectMapper objectMapper = new ObjectMapper();
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            objectMapper.writeValue(fileWriter, output);
            System.out.println("JSON data has been written to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void GenerateTest(String filename, int test, int countItems) {
        String filePath = path + "\\basket-" + filename + test + ".json";
        List<String> output = new ArrayList<>();
        List<Integer> items = new ArrayList<>();

        Random rand = new Random();
        int totalItems = rand.nextInt(countItems / 10);
        while (items.size() < totalItems) {
            int item = rand.nextInt(countItems);
            if (items.contains(item))
                continue;
            items.add(item);
            output.add("Item" + item);
        }
        // Print map as JSON
        ObjectMapper objectMapper = new ObjectMapper();
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            objectMapper.writeValue(fileWriter, output);
            System.out.println("JSON data has been written to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void GenerateGroup(int items, int companies, String id) {
        GenerateConfig(id, items, companies);
        for (int i = 0; i < 10; i++) {
            GenerateTest(id, i, items);
        }
    }
}