package com.ocado.basket;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BasketSplitter {
    private Map <String, List<String>> configData; //product : list of companies

    private final List<String> companies = new ArrayList<>();

    private final Map<String, Set<String>> uniqueItems = new HashMap<>();
    private int totalItems = 0;

    public BasketSplitter(String absolutePathToConfigFile) {
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
        uniqueItems.clear();
        items.forEach(item -> {
            System.out.print(item + " , ");
            List<String> temp = configData.get(item);
            for (String temp1 : temp) {
                System.out.print(temp1 + " , ");
            }
            System.out.println();
                    configData.get(item).forEach(company ->
                            uniqueItems.computeIfAbsent(company, k -> new HashSet<>()).add(item)
                    );
                }
        );

        companies.sort((s1, s2) -> Integer.compare(uniqueItems.get(s2).size(), uniqueItems.get(s1).size()));
    }

    private List<String> QueueSplit() {
        List<String> outputCompaniesList = new ArrayList<>(companies);

        Set<Set<String>> passedCombinations = new HashSet<>();

        Set<String> currentItems = new HashSet<>();
        List<String> currentCompaniesList = new ArrayList<>();

        PriorityQueue<Pair<List<String>, Set<String>>> queue = new PriorityQueue<>(Comparator.comparingInt(pair -> pair.second().size()));
        queue.add(new Pair<>(currentCompaniesList, currentItems));

        while (!queue.isEmpty()) {
            Pair<List<String>, Set<String>> pair = queue.poll();
            currentCompaniesList = pair.first();
            currentItems = pair.second();

            if (currentItems.size() >= totalItems) {
                outputCompaniesList = CompareCompaniesLists(outputCompaniesList, currentCompaniesList);
                continue;
            }

            if (currentCompaniesList.size() >= outputCompaniesList.size())
                continue;

            Set<String> newCombination = new HashSet<>(currentCompaniesList);
            if (passedCombinations.contains(newCombination))
                continue;
            passedCombinations.add(newCombination);

            List<Pair<String, Integer>> possibleCompaniesToAdd = GetPossibleMerges(currentCompaniesList, currentItems, companies);
            for (Pair<String, Integer> company : possibleCompaniesToAdd) {
                HashSet<String> newItems = new HashSet<>(currentItems);
                newItems = mergeSets(newItems, uniqueItems.get(company.first()));

                List<String> newCompanies = new ArrayList<>(currentCompaniesList);
                newCompanies.add(company.first());

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

    private List<Pair<String, Integer>> GetPossibleMerges(List<String> currentCompaniesList, Set<String> currentItems, List<String> companiesToPick) {
        List<Pair<String, Integer>> possibleMerges = new ArrayList<>();
        for (String company : companiesToPick) {
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

    private Map<String, List<String>> PreparingOutputSplit(List<String> outputCompanies) {
        Map <String, List<String>> outputSplit = new LinkedHashMap<>();
        HashSet<String> deliveredItems = new HashSet<>();
        List<Pair<String, Integer>> bestCompanies = GetPossibleMerges(List.of(), deliveredItems, outputCompanies);
        while (!bestCompanies.isEmpty()) {
            String bestCompany = bestCompanies.getFirst().first();
            Set<String> companyItems = uniqueItems.get(bestCompany);
            companyItems.removeAll(deliveredItems);
            outputCompanies.remove(bestCompany);
            deliveredItems.addAll(companyItems);
            outputSplit.put(bestCompany, new ArrayList<>(companyItems));
            bestCompanies = GetPossibleMerges(List.of(), deliveredItems, outputCompanies);
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
