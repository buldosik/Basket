package com.ocado.basket;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, List<String>> map = new HashMap<>();

        map.put("1", Arrays.asList("A", "C"));
        map.put("2", Arrays.asList("A", "B"));
        map.put("3", Arrays.asList("A", "D"));
        map.put("4", Arrays.asList("A", "B"));
        map.put("5", Arrays.asList("A", "C", "D"));
        map.put("6", Arrays.asList("B", "C"));
        map.put("7", Arrays.asList("C", "D"));

        BasketSplitter basketSplitter = new BasketSplitter(map);

        List<String> basket = List.of("1", "2", "3");

        Map<String, Set<String>> output = basketSplitter.FilteringBasketByCompaniesTest(basket);

        System.out.println("Everything is ok, setup with dependencies");
    }
}