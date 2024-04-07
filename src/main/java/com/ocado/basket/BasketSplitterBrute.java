package com.ocado.basket;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BasketSplitterBrute {
    private Map <String, List<String>> configData; //product : list of companies

    private final List<String> companies = new ArrayList<>();

    private final Map<String, Set<String>> uniqueItems = new HashMap<>();
    private int totalItems = 0;

    public BasketSplitterBrute(String absolutePathToConfigFile) {
        File file = new File(absolutePathToConfigFile);
        ObjectMapper mapper = new ObjectMapper();
        try {
            configData = mapper.readValue(file, mapper.getTypeFactory().constructMapType(Map.class, String.class, List.class));
            configData.values().stream()
                    .flatMap(List::stream)
                    .distinct()
                    .forEach(companies::add);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, List<String>> split(List<String> items) {
        totalItems = items.size();
        filteringBasketByCompanies(items);

        List<String> outputCompaniesList = QueueSplit();

        return PreparingOutputSplit(outputCompaniesList);
    }

    private void filteringBasketByCompanies(List<String> items) {
        items.forEach(item ->
                configData.get(item).forEach(company ->
                        uniqueItems.computeIfAbsent(company, k -> new HashSet<>()).add(item)
                )
        );

        companies.sort((s1, s2) -> Integer.compare(uniqueItems.get(s2).size(), uniqueItems.get(s1).size()));
    }

    private List<String> QueueSplit() {
        Set<String> currentItems = new HashSet<>();
        List<String> currentCompaniesList = new ArrayList<>();
        List<String> outputCompaniesList = new ArrayList<>(companies);

        PriorityQueue<Pair<List<String>, Set<String>>> queue = new PriorityQueue<>(Comparator.comparingInt(pair -> pair.second().size()));
        queue.add(new Pair<>(currentCompaniesList, currentItems));

        while (!queue.isEmpty()) {
            Pair<List<String>, Set<String>> pair = queue.poll();
            currentCompaniesList = pair.first();

            if (currentCompaniesList.size() >= outputCompaniesList.size())
                continue;

            //System.out.println(currentCompaniesList + " --- " + currentItems.size());

            currentItems = pair.second();

            List<Pair<String, Integer>> possibleCompaniesToAdd = GetPossibleMerges(currentCompaniesList, currentItems);
            for (Pair<String, Integer> company : possibleCompaniesToAdd) {
                HashSet<String> newItems = new HashSet<>(currentItems);
                newItems = mergeSets(newItems, uniqueItems.get(company.first()));

                List<String> newCompanies = new ArrayList<>(currentCompaniesList);
                newCompanies.add(company.first());

                if (newItems.size() >= totalItems) {
                    outputCompaniesList = CompareCompaniesLists(outputCompaniesList, newCompanies);
                    continue;
                }
                queue.add(new Pair<>(newCompanies, newItems));
            }
        }
        return outputCompaniesList;
    }

    private List<String> CompareCompaniesLists(List<String> currentCompanies, List<String> newCompanies) {
        if (newCompanies.size() == currentCompanies.size()) {
            for (int i = 0; i < newCompanies.size(); i++) {
                String oldCompany = currentCompanies.get(i);
                String newCompany = newCompanies.get(i);
                if(uniqueItems.get(oldCompany).size() < uniqueItems.get(newCompany).size())
                    currentCompanies = newCompanies;
                if(uniqueItems.get(oldCompany).size() != uniqueItems.get(newCompany).size())
                    break;
            }
        }
        if (newCompanies.size() < currentCompanies.size()) {
            currentCompanies = newCompanies;
        }
        return currentCompanies;
    }

    private List<Pair<String, Integer>> GetPossibleMerges(List<String> currentCompaniesList, Set<String> currentItems) {
        List<Pair<String, Integer>> possibleMerges = new ArrayList<>();
        for (String company : companies) {
            if(currentCompaniesList.contains(company))
                continue;
            int difference =  calculateDifference(uniqueItems.get(company), currentItems);
            if(difference <= 0)
                continue;
            possibleMerges.add(new Pair<>(company, difference));
        }
        possibleMerges.sort((s1, s2) -> Integer.compare(s2.second(), s1.second()));
        return possibleMerges;
    }

    private Map<String, List<String>> PreparingOutputSplit(List<String> companies) {
        Map <String, List<String>> outputSplit = new HashMap<>();;
        HashSet<String> deliveredItems = new HashSet<>();
        for (String company : companies) {
            Set<String> companyItems = uniqueItems.get(company);
            companyItems.removeAll(deliveredItems);
            deliveredItems.addAll(companyItems);
            outputSplit.put(company, new ArrayList<>(companyItems));
        }
        return outputSplit;
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
        difference.removeAll(set2);
        return difference.size();
    }
}
