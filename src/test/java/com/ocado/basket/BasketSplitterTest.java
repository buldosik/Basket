package com.ocado.basket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BasketSplitterTest {

    private final String path = "SomePath";

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
        solutionSizes.sort(Collections.reverseOrder());
        outputSizes.sort(Collections.reverseOrder());

        // Comparing sizes
        return solutionSizes.equals(outputSizes);
    }


    @Nested
    @Disabled
    class Solutions {
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

    @Nested
    class Functions{
        BasketSplitter basketSplitter;
        @BeforeEach
        void initialising() {
            Map<String, List<String>> map = new HashMap<>();

            map.put("1", Arrays.asList("A", "C"));
            map.put("2", Arrays.asList("A", "B"));
            map.put("3", Arrays.asList("A", "D"));
            map.put("4", Arrays.asList("A", "B"));
            map.put("5", Arrays.asList("A", "C", "D"));
            map.put("6", Arrays.asList("B", "C"));
            map.put("7", Arrays.asList("C", "D"));

            basketSplitter = new BasketSplitter(map);
        }

        @Test
        void FilteringBasketByCompanies1() {
            List<String> basket = List.of("1", "2", "3");

            Map<String, Set<String>> output = basketSplitter.FilteringBasketByCompaniesTest(basket);

            Map<String, Set<String>> expected = new HashMap<>();
            expected.put("A", Set.of("1", "2", "3"));
            expected.put("B", Set.of("2"));
            expected.put("C", Set.of("1"));
            expected.put("D", Set.of("3"));

            assertEquals(expected, output);
        }

        @Test
        void FilteringBasketByCompanies2() {
            List<String> basket = List.of("1", "4", "5", "6");

            Map<String, Set<String>> output = basketSplitter.FilteringBasketByCompaniesTest(basket);

            Map<String, Set<String>> expected = new HashMap<>();
            expected.put("A", Set.of("1", "4", "5"));
            expected.put("B", Set.of("4", "6"));
            expected.put("C", Set.of("1", "5", "6"));
            expected.put("D", Set.of("5"));

            assertEquals(expected, output);
        }

        @Test
        void FilteringBasketByCompanies3() {
            List<String> basket = List.of("2", "3", "7");

            Map<String, Set<String>> output = basketSplitter.FilteringBasketByCompaniesTest(basket);

            Map<String, Set<String>> expected = new HashMap<>();
            expected.put("A", Set.of("2", "3"));
            expected.put("B", Set.of("2"));
            expected.put("C", Set.of("7"));
            expected.put("D", Set.of("3", "7"));

            assertEquals(expected, output);
        }


        @Test
        void compareSolutions1() {
            List<String> currentList = List.of("1", "2", "3");
            List<String> newList = List.of("1", "2", "3", "4");

            List<String> output = basketSplitter.CompareSolutions(currentList, newList);

            assertEquals(currentList, output);
        }

        @Test
        void compareSolutions2() {
            List<String> currentList = List.of("1", "2", "4", "3");
            List<String> newList = List.of("1", "2", "3", "4");

            List<String> output = basketSplitter.CompareSolutions(currentList, newList);

            assertEquals(currentList, output);
        }

        @Test
        void compareSolutions3() {
            List<String> currentList = List.of("1", "2", "3");
            List<String> newList = List.of("1", "2");

            List<String> output = basketSplitter.CompareSolutions(currentList, newList);

            assertEquals(newList, output);
        }


        @Test
        void mergeSets() {
            Set<Integer> set1 = new HashSet<>();
            set1.add(1);
            set1.add(2);
            set1.add(3);
            Set<Integer> set2 = new HashSet<>();
            set2.add(3);
            set2.add(4);
            set2.add(5);

            Set<Integer> output = basketSplitter.mergeSets(set1, set2);

            Set<Integer> expected = new HashSet<>();
            expected.add(1);
            expected.add(2);
            expected.add(3);
            expected.add(4);
            expected.add(5);
            assertEquals(expected, output);
        }

        @Test
        void calculateDifference() {
            Set<Integer> set1 = new HashSet<>();
            set1.add(1);
            set1.add(2);
            set1.add(3);
            Set<Integer> set2 = new HashSet<>();
            set2.add(3);
            set2.add(4);
            set2.add(5);

            int output = basketSplitter.calculateDifference(set1, set2);

            assertEquals(2, output);
        }
    }

}