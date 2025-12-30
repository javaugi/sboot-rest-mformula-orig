/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.hackerrank.httpget;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author javau
 */
public class BestRestaurantResultNoJson {
    /*
     * Complete the 'getBestRestaurants' function below.
     *
     * The function is expected to return a STRING.
     * The function accepts following parameters:
     *  1. STRING city
     *  2. Integer votes
     */

    public static String getBestRestaurants(String city, Integer votes) {
        try {
            int page = 1;
            List<Restaurant> allRestaurants = new ArrayList<>();

            while (true) {
                String urlString = String.format("https://jsonmock.hackerrank.com/api/food_outlets?city=%s&cuisine=%s&page=%d",
                        URLEncoder.encode(city, "UTF-8"),
                        URLEncoder.encode(votes.toString(), "UTF-8"),
                        page
                );

                String jsonResponse = makeHttpRequest(urlString);

                // Parse total pages
                int totalPages = extractTotalPages(jsonResponse);

                // Extract restaurants from JSON
                List<Restaurant> pageRestaurants = extractRestaurants(jsonResponse);

                if (pageRestaurants.isEmpty() && page == 1) {
                    return "NotFound";
                }

                allRestaurants.addAll(pageRestaurants);

                if (page >= totalPages) {
                    break;
                }
                page++;
            }

            if (allRestaurants.isEmpty()) {
                return "NotFound";
            }

            // Find best restaurant
            Restaurant bestRestaurant = allRestaurants.get(0);
            for (Restaurant restaurant : allRestaurants) {
                if (restaurant.rating > bestRestaurant.rating
                        || (restaurant.rating == bestRestaurant.rating
                        && restaurant.votes > bestRestaurant.votes)) {
                    bestRestaurant = restaurant;
                }
            }

            return bestRestaurant.name;

        } catch (IOException e) {
            return "NotFound";
        }
    }

    private static String makeHttpRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        connection.disconnect();
        return response.toString();
    }

    private static int extractTotalPages(String json) {
        // Find "total_pages" in JSON
        String searchStr = "\"total_pages\":";
        int start = json.indexOf(searchStr);
        if (start == -1) {
            return 1;
        }

        start += searchStr.length();
        int end = json.indexOf(",", start);
        if (end == -1) {
            end = json.indexOf("}", start);
        }

        return Integer.parseInt(json.substring(start, end).trim());
    }

    private static List<Restaurant> extractRestaurants(String json) {
        List<Restaurant> restaurants = new ArrayList<>();

        // Find all restaurant objects
        String searchStr = "\"data\":[";
        int dataStart = json.indexOf(searchStr);
        if (dataStart == -1) {
            return restaurants;
        }

        dataStart += searchStr.length();
        int dataEnd = json.indexOf("]", dataStart);
        String dataArray = json.substring(dataStart, dataEnd);

        // Split by restaurant objects
        String[] restaurantObjects = dataArray.split("\\},\\{");

        for (String restaurantStr : restaurantObjects) {
            // Extract name
            String name = extractValue(restaurantStr, "\"name\":\"");

            // Extract rating
            String ratingStr = extractValue(restaurantStr, "\"average_rating\":");
            double rating = ratingStr != null ? Double.parseDouble(ratingStr) : 0.0;

            // Extract votes
            String votesStr = extractValue(restaurantStr, "\"votes\":");
            int votes = votesStr != null ? Integer.parseInt(votesStr) : 0;

            if (name != null) {
                restaurants.add(new Restaurant(name, rating, votes));
            }
        }

        return restaurants;
    }

    private static String extractValue(String json, String key) {
        int start = json.indexOf(key);
        if (start == -1) {
            return null;
        }

        start += key.length();

        // Check if value is string or number
        if (key.endsWith("\":")) {
            // String value
            int end = json.indexOf("\"", start);
            if (end == -1) {
                return null;
            }
            return json.substring(start, end);
        } else {
            // Numeric value
            int end = json.indexOf(",", start);
            if (end == -1) {
                end = json.indexOf("}", start);
            }
            if (end == -1) {
                return null;
            }
            return json.substring(start, end).trim();
        }
    }

    static class Restaurant {

        String name;
        double rating;
        int votes;

        Restaurant(String name, double rating, int votes) {
            this.name = name;
            this.rating = rating;
            this.votes = votes;
        }
    }
}
