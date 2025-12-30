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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author javau
 */
@lombok.extern.slf4j.Slf4j
public class BestRestaurantResult {
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
            // Build the URL with pagination support
            int page = 1;
            List<Restaurant> allRestaurants = new ArrayList<>();

            while (true) {
                String urlString = String.format("https://jsonmock.hackerrank.com/api/food_outlets?city=%s&votes=%s&page=%d",
                        URLEncoder.encode(city, "UTF-8"),
                        URLEncoder.encode(votes.toString(), "UTF-8"),
                        page
                );

                String jsonResponse = makeHttpRequest(urlString);
                String jsonResponse2 = makeHttpGet(urlString);
                log.info("getBestRestaurants \n jsonResponse {} \n jsonResponse2 {}", jsonResponse, jsonResponse2);
                JSONObject jsonObject = new JSONObject(jsonResponse);

                // Get the data array
                JSONArray restaurantsArray = jsonObject.getJSONArray("data");

                // If no restaurants found, return "NotFound"
                if (restaurantsArray.length() == 0 && page == 1) {
                    return "NotFound";
                }

                // Process each restaurant
                for (int i = 0; i < restaurantsArray.length(); i++) {
                    JSONObject restaurantJson = restaurantsArray.getJSONObject(i);

                    // Get user rating object
                    JSONObject userRating = restaurantJson.getJSONObject("user_rating");

                    Restaurant restaurant = new Restaurant(
                            restaurantJson.getString("name"),
                            userRating.getDouble("average_rating"),
                            userRating.getInt("votes")
                    );

                    allRestaurants.add(restaurant);
                }

                // Check if there are more pages
                int totalPages = jsonObject.getInt("total_pages");
                if (page >= totalPages) {
                    break;
                }
                page++;
            }

            // If no restaurants found
            if (allRestaurants.isEmpty()) {
                return "NotFound";
            }

            // Find the best restaurant based on rating and votes
            Collections.sort(allRestaurants, new Comparator<Restaurant>() {
                @Override
                public int compare(Restaurant r1, Restaurant r2) {
                    // First compare by rating (descending)
                    int ratingCompare = Double.compare(r2.rating, r1.rating);
                    if (ratingCompare != 0) {
                        return ratingCompare;
                    }
                    // If ratings are equal, compare by votes (descending)
                    return Integer.compare(r2.votes, r1.votes);
                }
            });

            // Return the best restaurant name
            return allRestaurants.get(0).name;

        } catch (Exception e) {
            // Handle exceptions gracefully
            return "NotFound";
        }
    }

    private static String makeHttpRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        // Read response
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

    private static String makeHttpGet(String urlString) throws IOException {
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(urlString);
            response = httpclient.execute(httpGet);
            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                entity = response.getEntity();
                log.info("return value {}", EntityUtils.toString(entity));
            }
        } catch (IOException | ParseException | JSONException ex) {
            log.error("Error makeHttpGet", urlString, ex);
		}
        finally {
			if (response != null) {
				try {
					response.close();
                } catch (IOException ex) {
				}
            }
		}

        return (entity == null) ? "" : EntityUtils.toString(entity);
    }

    // Helper class to store restaurant information
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
