package com.ocado.basket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BasketSplitterTest {

    private String path = "G:\\Projects\\Java\\basket\\data";

    List<String> GetBasketJSON(String path) {
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

    Map<String, List<String>> GetOutputJSON(String path) {
        Map<String, List<String>> outputMap = new HashMap<>();
        File file = new File(path);
        ObjectMapper mapper = new ObjectMapper();
        try {
            outputMap = mapper.readValue(file, mapper.getTypeFactory().constructMapType(Map.class, String.class, List.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputMap;
    }

    boolean isOptimizedDelivery(Map<String, List<String>> solution, Map<String, List<String>> outputMap) {
        if (solution.size() != outputMap.size()) {
            return false;
        }
        // Collecting sizes of lists from both maps
        List<Integer> solutionSizes = new ArrayList<>();
        List<Integer> outputSizes = new ArrayList<>();

        for (List<String> list : solution.values()) {
            solutionSizes.add(list.size());
        }

        for (List<String> list : outputMap.values()) {
            outputSizes.add(list.size());
        }

        // Sorting sizes in descending order
        Collections.sort(solutionSizes, Collections.reverseOrder());
        Collections.sort(outputSizes, Collections.reverseOrder());

        // Comparing sizes
        return solutionSizes.equals(outputSizes);
    }

    @ParameterizedTest(name = "Test - {0}")
    @ValueSource(ints = {1, 2})
    @DisplayName("Split - config0.json")
    void split0(int numberOfTest) {
        BasketSplitter basketSplitter = new BasketSplitter(path + "\\config0.json");

        List<String> input = GetBasketJSON(path + "\\basket-0" + numberOfTest + ".json");

        Map<String, List<String>> soluion = basketSplitter.split(input);
        Map<String, List<String>> optimalOutput = GetOutputJSON(path + "\\output-0" + numberOfTest + ".json");

        assertTrue(isOptimizedDelivery(soluion, optimalOutput));
    }

    @ParameterizedTest(name = "Test - {0}")
    @ValueSource(ints = {1})
    @DisplayName("Split - config1.json")
    void split1(int numberOfTest) {
        BasketSplitter basketSplitter = new BasketSplitter(path + "\\config1.json");

        List<String> input = GetBasketJSON(path + "\\basket-1" + numberOfTest + ".json");

        Map<String, List<String>> soluion = basketSplitter.split(input);
        Map<String, List<String>> optimalOutput = GetOutputJSON(path + "\\output-1" + numberOfTest + ".json");

        assertTrue(isOptimizedDelivery(soluion, optimalOutput));
    }

    @ParameterizedTest(name = "Test - {0}")
    @ValueSource(ints = {1, 2, 3})
    @DisplayName("Split - config2.json")
    void split2(int numberOfTest) {
        BasketSplitter basketSplitter = new BasketSplitter(path + "\\config2.json");

        List<String> input = GetBasketJSON(path + "\\basket-2" + numberOfTest + ".json");

        Map<String, List<String>> soluion = basketSplitter.split(input);
        Map<String, List<String>> optimalOutput = GetOutputJSON(path + "\\output-2" + numberOfTest + ".json");

        assertTrue(isOptimizedDelivery(soluion, optimalOutput));
    }

    @ParameterizedTest(name = "Test - {0}")
    @ValueSource(ints = {0})
    @DisplayName("Split - config7.json")
    void split7(int numberOfTest) {
        BasketSplitter basketSplitter = new BasketSplitter(path + "\\config7.json");

        List<String> input = GetBasketJSON(path + "\\basket-7" + numberOfTest + ".json");

        Map<String, List<String>> soluion = basketSplitter.split(input);
        Map<String, List<String>> optimalOutput = GetOutputJSON(path + "\\output-7" + numberOfTest + ".json");

        assertTrue(isOptimizedDelivery(soluion, optimalOutput));
    }

    @ParameterizedTest(name = "Test - {0}")
    @ValueSource(ints = {0})
    @DisplayName("Split - config8.json")
    void split8(int numberOfTest) {
        BasketSplitter basketSplitter = new BasketSplitter(path + "\\config8.json");

        List<String> input = GetBasketJSON(path + "\\basket-8" + numberOfTest + ".json");

        Map<String, List<String>> soluion = basketSplitter.split(input);
        Map<String, List<String>> optimalOutput = GetOutputJSON(path + "\\output-8" + numberOfTest + ".json");

        assertTrue(isOptimizedDelivery(soluion, optimalOutput));
    }
}