package com.ocado.basket;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BasketSplitter {

    private Map <String, List<String>> configData; //product : list of companies

    private final List<String> companies = new ArrayList<>();
    private final Map<String, Set<String>> uniqueItems = new HashMap<>();

    private int totalItemsToDelivery = 0;

    public BasketSplitter(String absolutePathToConfigFile) {
        File file = new File(absolutePathToConfigFile);
        ObjectMapper mapper = new ObjectMapper();
        try {
            configData = mapper.readValue(file, mapper.getTypeFactory().constructMapType(Map.class, String.class, List.class));
            for (Map.Entry<String, List<String>> entry : configData.entrySet()) {
                for (String value : entry.getValue()) {
                    if(!companies.contains(value)) {
                        companies.add(value);
                    }
                }
            }
        } catch (IOException e) {
            // ToDo change error tracking
            e.printStackTrace();
        }
    }

    public Map<String, List<String>> split(List<String> items) {
        totalItemsToDelivery = items.size();
        filteringBasketByCompanies(items);

        Map <String, List<String>> answer = new HashMap<>();;
        HashSet<String> deliveredItems = new HashSet<>();

        return answer;
    }

    private void filteringBasketByCompanies(List<String> items) {
        items.forEach(item ->
            configData.get(item).forEach(company ->
                uniqueItems.computeIfAbsent(company, k -> new HashSet<>()).add(item)
            )
        );

        companies.sort((s1, s2) -> Integer.compare(uniqueItems.get(s2).size(), uniqueItems.get(s1).size()));
    }

    // Function to merge two sets
    public <T> HashSet<T> mergeSets(Set<T> set1, Set<T> set2) {
        HashSet<T> mergedSet = new HashSet<>(set1);
        mergedSet.addAll(set2);
        return mergedSet;
    }

    // Function to calculate the difference between two sets
    private <T> int calculateDifference(Set<T> set1, Set<T> set2) {
        Set<T> difference = new HashSet<>(set1);
        difference.removeAll(set2); //possible error
        return difference.size();
    }
}
