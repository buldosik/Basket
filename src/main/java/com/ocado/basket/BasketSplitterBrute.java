package com.ocado.basket;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BasketSplitterBrute {
    private Map <String, List<String>> configData; //product : list of companies

    private final List<String> companies = new ArrayList<>();
    private List<String> minimumCompaniesList = new ArrayList<>();

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
            minimumCompaniesList = companies;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, List<String>> split(List<String> items) {
        totalItems = items.size();
        filteringBasketByCompanies(items);

        RecursiveSplit(new ArrayList<>(), new HashSet<>());

        return PreparingOutputSplit(minimumCompaniesList);
    }

    private void filteringBasketByCompanies(List<String> items) {
        items.forEach(item ->
                configData.get(item).forEach(company ->
                        uniqueItems.computeIfAbsent(company, k -> new HashSet<>()).add(item)
                )
        );

        companies.sort((s1, s2) -> Integer.compare(uniqueItems.get(s2).size(), uniqueItems.get(s1).size()));
    }

    private void RecursiveSplit(List<String> currentCompaniesList, Set<String> currentItems) {
        if (currentCompaniesList.size() >= minimumCompaniesList.size())
            return;
        List<Pair<String, Integer>> possibleCompaniesToAdd = GetPossibleMerges(currentCompaniesList, currentItems);

        for (Pair<String, Integer> company : possibleCompaniesToAdd) {
            HashSet<String> newItems = new HashSet<>(currentItems);
            newItems = mergeSets(newItems, uniqueItems.get(company.first()));

            List<String> newCompanies = new ArrayList<>(currentCompaniesList);
            newCompanies.add(company.first());

            if (newItems.size() >= totalItems) {
                CompareCompaniesLists(newCompanies);
                return;
            }
            RecursiveSplit(newCompanies, newItems);
        }
    }

    private void CompareCompaniesLists(List<String> newCompanies) {
        if (newCompanies.size() == minimumCompaniesList.size()) {
            for (int i = 0; i < newCompanies.size(); i++) {
                String oldCompany = minimumCompaniesList.get(i);
                String newCompany = newCompanies.get(i);
                if(uniqueItems.get(oldCompany).size() < uniqueItems.get(newCompany).size())
                    minimumCompaniesList = new ArrayList<>(newCompanies);
                if(uniqueItems.get(oldCompany).size() != uniqueItems.get(newCompany).size())
                    break;
            }
        }
        if (newCompanies.size() < minimumCompaniesList.size()) {
            minimumCompaniesList = new ArrayList<>(newCompanies);
        }
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
        difference.removeAll(set2); //possible error
        return difference.size();
    }
}
