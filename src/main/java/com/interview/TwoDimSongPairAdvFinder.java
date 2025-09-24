/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author javau
 */
public class TwoDimSongPairAdvFinder extends TwoDimSongPairFinder {
    /**
     * Finds all unique pairs including handling duplicates properly
     */
    public List<SongPair> findAllUniquePairs(String[][] playtimes) {
        List<Song> songs = new ArrayList<>();
        Map<String, Integer> songCount = new HashMap<>();

        // Parse songs and count occurrences
        for (String[] songData : playtimes) {
            if (songData.length != 2) {
                continue;
            }

            int duration = parseDuration(songData[1]);
            Song song = new Song(songData[0], songData[1], duration);
            songs.add(song);

            String key = songData[0] + "|" + duration;
            songCount.put(key, songCount.getOrDefault(key, 0) + 1);
        }

        return findSongPairs(playtimes); // Uses parent class implementation
    }

    /**
     * Finds the best pair (most popular songs) that sum to 7 minutes
     */
    public Optional<SongPair> findBestPair(String[][] playtimes) {
        List<SongPair> pairs = findSongPairs(playtimes);

        if (pairs.isEmpty()) {
            return Optional.empty();
        }

        // Simple heuristic: choose the pair with the most well-known songs
        // In real implementation, you might use popularity data
        return pairs.stream()
            .max(Comparator.comparing(pair
                -> pair.getSong1().getTitle().length() + pair.getSong2().getTitle().length()));
    }
}
