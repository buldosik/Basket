package com.ocado.basket;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BasketSplitter {
    // config data
    private Map <String, List<String>> configData;

    // list of all companies
    private final List<String> companies = new ArrayList<>();

    // Map with companies as keys, filtered items as value
    private final Map<String, Set<String>> filteredItems = new HashMap<>();
    // ToDo refactor
    private final List<List<String>> possibleSolutions = new ArrayList<>();

    // Total number of items in the basket
    private int totalItems = 0;

    /**
     * @param absolutePathToConfigFile
     */
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

    /**
     * @param _configData
     */
    public BasketSplitter(Map<String, List<String>> _configData) {
        configData = _configData;
    }

    /**
     * @param items Basket with items
     * @return
     */
    public Map<String, List<String>> split(List<String> items) {
        totalItems = items.size();
        // Filtering config data with basket
        FilteringBasketByCompanies(items);

        // Searching possible solutions with minimum number of companies
        List<String> outputCompaniesList = QueueSplit();

        // ToDo refactor
        // Getting most optimal solution
        if(possibleSolutions.size() > 1) {
            outputCompaniesList = CalculateOptimalSolution(possibleSolutions);
        }

        // Converting to map
        return PreparingOutputSplit(outputCompaniesList);
    }

    // ToDo refactor
    private List<String> CalculateOptimalSolution(List<List<String>> possibleOutputs) {
        List<Set<String>> deliveredItemsForEachSolution = new ArrayList<>();
        List<Integer> currentItemsForEachSolution = new ArrayList<>();
        for (List<String> items : possibleOutputs) {
            deliveredItemsForEachSolution.add(new HashSet<>());
            currentItemsForEachSolution.add(0);
        }
        int countOfCompanies = possibleOutputs.getFirst().size();
        for (int i = 0; i < countOfCompanies; i++) {
            int max = 0;
            for (int j = 0; j < possibleOutputs.size(); j++) {
                List<Pair<String, Integer>> values = GetPossibleMerges(deliveredItemsForEachSolution.get(j), possibleOutputs.get(j));
                if (values.isEmpty()) {
                    continue;
                }
                Pair <String, Integer> pair = values.getFirst();

                currentItemsForEachSolution.set(j, pair.second());
                Set<String> currentSet = deliveredItemsForEachSolution.get(j);
                currentSet.addAll(filteredItems.get(pair.first()));
                deliveredItemsForEachSolution.set(j, currentSet);

                max = Math.max(max, pair.second());
            }
            for (int j = 0; j < possibleOutputs.size(); j++) {
                if (currentItemsForEachSolution.get(j) < max) {
                    currentItemsForEachSolution.remove(j);
                    deliveredItemsForEachSolution.remove(j);
                    possibleOutputs.remove(j);
                    j--;
                }
            }
        }
        return possibleOutputs.getFirst();
    }

    /**
     * Function for filtering configuration data with the basket
     * @param items The items contained in the basket
     */
    private void FilteringBasketByCompanies(List<String> items) {
        filteredItems.clear();
        items.forEach(item ->
                configData.get(item).forEach(company ->
                        filteredItems.computeIfAbsent(company, k -> new HashSet<>()).add(item)
                )
        );

        companies.sort((s1, s2) -> Integer.compare(filteredItems.get(s2).size(), filteredItems.get(s1).size()));
    }

    protected Map<String, Set<String>> FilteringBasketByCompaniesTest(List<String> items) {
        FilteringBasketByCompanies(items);
        return filteredItems;
    }

    /**
     * Returns a list of companies that provide the most efficient delivery solution.
     *
     * @return A list of companies with the most optimal delivery solution.
     */
    private List<String> QueueSplit() {
        // List to hold the output companies
        List<String> outputCompaniesList = new ArrayList<>(companies);

        // Set to store passed combinations of companies
        Set<Set<String>> passedCombinations = new HashSet<>();

        // Set & List to hold the current combination from queue
        Set<String> currentItems = new HashSet<>();
        List<String> currentCompaniesList = new ArrayList<>();

        // Priority queue to manage combinations of companies and items
        PriorityQueue<Pair<List<String>, Set<String>>> queue = new PriorityQueue<>((pair1, pair2) -> Integer.compare(pair2.second().size(), pair1.second().size()));
        queue.add(new Pair<>(currentCompaniesList, currentItems));

        while (!queue.isEmpty()) {
            // Retrieve and remove the pair with the highest number of current items
            Pair<List<String>, Set<String>> pair = queue.poll();
            currentCompaniesList = pair.first();
            currentItems = pair.second();

            // Create a new combination set from the current list of companies
            Set<String> newCombination = new HashSet<>(currentCompaniesList);

            // Skip if this combination has already been processed
            if (passedCombinations.contains(newCombination))
                continue;
            passedCombinations.add(newCombination);

            // Check if the current combination meets the criteria for a complete solution
            if (currentItems.size() >= totalItems) {
                outputCompaniesList = CompareSolutions(outputCompaniesList, currentCompaniesList);
                continue;
            }

            // Skip if the number of companies in the current combination exceeds the output list size
            if (currentCompaniesList.size() >= outputCompaniesList.size())
                continue;

            List<Pair<String, Integer>> possibleCompaniesToAdd = GetPossibleMerges(currentItems, companies);

            for (Pair<String, Integer> company : possibleCompaniesToAdd) {
                // Create a new set of items by merging current items with the items from the selected company
                HashSet<String> newItems = new HashSet<>(currentItems);
                newItems = mergeSets(newItems, filteredItems.get(company.first()));

                // Create a new list of companies by adding the selected company to the current list
                List<String> newCompanies = new ArrayList<>(currentCompaniesList);
                newCompanies.add(company.first());

                // Add the new combination of companies and items to the queue
                queue.add(new Pair<>(newCompanies, newItems));
            }
        }
        return outputCompaniesList;
    }

    /**
     * Function to compare possible solutions
     * @param currentSolution
     * @param newSolution
     * @return
     */
    protected List<String> CompareSolutions(List<String> currentSolution, List<String> newSolution) {
        if (newSolution.size() == currentSolution.size()) {
            // Store new solution and calculate most optimal later
            possibleSolutions.add(newSolution);
        }
        if (newSolution.size() < currentSolution.size()) {
            // Update most optimal solution
            currentSolution = newSolution;
            possibleSolutions.clear();
            possibleSolutions.add(currentSolution);
        }
        return currentSolution;
    }

    // ToDo Tests
    /**
     * Function to calculate which company will add the most number of items to the current set
     * @param currentItems Current delivered items
     * @param companiesToPick Companies that able to add items to delivered
     * @return List of pairs {company name, number of items to add}
     */
    private List<Pair<String, Integer>> GetPossibleMerges(Set<String> currentItems, List<String> companiesToPick) {
        List<Pair<String, Integer>> possibleMerges = new ArrayList<>();
        for (String company : companiesToPick) {
            int difference =  calculateDifference(filteredItems.get(company), currentItems);
            if(difference <= 0)
                continue;
            possibleMerges.add(new Pair<>(company, difference));
        }
        possibleMerges.sort((s1, s2) -> Integer.compare(s2.second(), s1.second()));
        return possibleMerges;
    }

    // ToDo Tests
    /**
     * Converting list of companies to Map
     * @param outputCompanies List of companies
     * @return Map with companies as keys, items to deliver as values
     */
    private Map<String, List<String>> PreparingOutputSplit(List<String> outputCompanies) {
        Map <String, List<String>> output = new LinkedHashMap<>();

        // Contains all items that the company has already delivered
        HashSet<String> deliveredItems = new HashSet<>();

        // Calculate efficiency of companies
        List<Pair<String, Integer>> bestCompanies = GetPossibleMerges(deliveredItems, outputCompanies);

        while (!bestCompanies.isEmpty()) {
            // Get most efficient company
            String bestCompany = bestCompanies.getFirst().first();
            Set<String> companyItems = filteredItems.get(bestCompany);

            // Add to output current company
            companyItems.removeAll(deliveredItems);
            outputCompanies.remove(bestCompany);
            output.put(bestCompany, new ArrayList<>(companyItems));

            deliveredItems.addAll(companyItems);

            bestCompanies = GetPossibleMerges(deliveredItems, outputCompanies);
        }
        return output;
    }

    /**
     * Function to merge two sets
     * @param set1 Items
     * @param set2 Items
     * @return New set of items
     */
    protected <T> HashSet<T> mergeSets(Set<T> set1, Set<T> set2) {
        HashSet<T> mergedSet = new HashSet<>(set1);
        mergedSet.addAll(set2);
        return mergedSet;
    }

    /**
     * Function to calculate the difference between two sets
     * @param set1 The first set of items
     * @param set2 The second set of items
     * @return The number of items that set1 contains and set2 does not contain
     */
    protected <T> int calculateDifference(Set<T> set1, Set<T> set2) {
        Set<T> difference = new HashSet<>(set1);
        difference.removeAll(set2);
        return difference.size();
    }
}
