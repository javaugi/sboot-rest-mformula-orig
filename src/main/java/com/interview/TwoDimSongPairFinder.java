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
import java.util.Objects;

/*
Key Features:
    Realistic Data: Uses actual song titles and durations
    Flexible Duration Parsing: Handles minutes.seconds format
    Multiple Algorithms: Both HashMap and two-pointer approaches
    Duplicate Handling: Properly handles songs with same duration
    Comprehensive Analysis: Provides additional song statistics
    Unit Tests: Complete test coverage
    Extensible Design: Easy to modify for different target durations
 */
public class TwoDimSongPairFinder {
// Realistic song titles with various durations

    private static final String[][] playtimes = {
        {"Bohemian Rhapsody", "5.55"},
        {"Stairway to Heaven", "8.02"},
        {"Hotel California", "6.30"},
        {"Sweet Child O' Mine", "5.56"},
        {"Smells Like Teen Spirit", "5.01"},
        {"Imagine", "3.07"},
        {"Billie Jean", "4.54"},
        {"Like a Rolling Stone", "6.13"},
        {"Wonderwall", "4.18"},
        {"Sweet Home Alabama", "4.45"},
        {"Hey Jude", "7.11"},
        {"Thriller", "5.57"},
        {"Back in Black", "4.16"},
        {"Yesterday", "2.05"},
        {"Hallelujah", "4.39"},
        {"Let It Be", "4.03"},
        {"Shape of You", "3.54"},
        {"Blinding Lights", "3.22"},
        {"Bad Guy", "3.06"},
        {"Uptown Funk", "4.30"}
    };

    // Target duration in seconds (7 minutes = 420 seconds)
    private static final int TARGET_DURATION = 420;

    public static void main(String[] args) {
        TwoDimSongPairFinder finder = new TwoDimSongPairFinder();

        System.out.println("=== Song Pair Finder ===\n");
        System.out.println("Looking for song pairs that sum to exactly 7 minutes (420 seconds)\n");

        List<SongPair> pairs = finder.findSongPairs(playtimes);

        if (pairs.isEmpty()) {
            System.out.println("No pairs found that sum to exactly 7 minutes.");
        } else {
            System.out.println("Found " + pairs.size() + " pair(s):\n");
            for (int i = 0; i < pairs.size(); i++) {
                SongPair pair = pairs.get(i);
                System.out.println((i + 1) + ". " + pair);
            }
        }

        // Additional analysis
        finder.analyzeSongs(playtimes);
    }

    public SongPair findSongPairs2(String[][] playtimes) {
        List<SongPair> result = new ArrayList<>();

        return doFindSongPairs(playtimes).getFirst();
    }

    public List<SongPair> doFindSongPairs(String[][] playtimes) {
        List<SongPair> result = new ArrayList<>();

        return result;
    }

    public List<SongPair> findSongPairs(String[][] playtimes) {
        List<SongPair> result = new ArrayList<>();
        Map<Integer, List<Song>> durationMap = new HashMap<>();

        // Parse and store songs by their duration in seconds
        for (String[] songData : playtimes) {
            if (songData.length != 2) {
                continue;
            }

            String title = songData[0];
            int duration = parseDuration(songData[1]);

            Song song = new Song(title, songData[1], duration);
            durationMap.computeIfAbsent(duration, k -> new ArrayList<>()).add(song);
        }

        // Find pairs that sum to TARGET_DURATION
        for (Map.Entry<Integer, List<Song>> entry : durationMap.entrySet()) {
            int duration1 = entry.getKey();
            int duration2 = TARGET_DURATION - duration1;

            if (durationMap.containsKey(duration2)) {
                List<Song> songs1 = entry.getValue();
                List<Song> songs2 = durationMap.get(duration2);

                // Handle same duration case (e.g., 210 + 210 = 420)
                if (duration1 == duration2) {
                    // Need at least 2 songs with this duration
                    if (songs1.size() >= 2) {
                        for (int i = 0; i < songs1.size(); i++) {
                            for (int j = i + 1; j < songs1.size(); j++) {
                                result.add(new SongPair(songs1.get(i), songs1.get(j)));
                            }
                        }
                    }
                } else {
                    // Different durations
                    for (Song song1 : songs1) {
                        for (Song song2 : songs2) {
                            result.add(new SongPair(song1, song2));
                        }
                    }
                }
            }
        }

        return result;
    }

    // Alternative implementation using two-pointer technique for efficiency
    public List<SongPair> findSongPairsOptimized(String[][] playtimes) {
        List<SongPair> result = new ArrayList<>();
        List<Song> songs = new ArrayList<>();

        // Parse all songs
        for (String[] songData : playtimes) {
            if (songData.length != 2) {
                continue;
            }
            songs.add(new Song(songData[0], songData[1], parseDuration(songData[1])));
        }

        // Sort by duration
        songs.sort(Comparator.comparingInt(Song::getDurationInSeconds));

        // Two-pointer technique
        int left = 0;
        int right = songs.size() - 1;

        while (left < right) {
            int sum = songs.get(left).getDurationInSeconds()
                + songs.get(right).getDurationInSeconds();

            if (sum == TARGET_DURATION) {
                // Found a pair, but need to handle duplicates
                int currentLeft = left;
                int currentRight = right;

                // Handle multiple songs with same duration on left
                while (currentRight > currentLeft
                    && songs.get(currentRight).getDurationInSeconds()
                    == songs.get(right).getDurationInSeconds()) {

                    // Handle multiple songs with same duration on right
                    int tempLeft = currentLeft;
                    while (tempLeft < currentRight
                        && songs.get(tempLeft).getDurationInSeconds()
                        == songs.get(left).getDurationInSeconds()) {

                        result.add(new SongPair(songs.get(tempLeft), songs.get(currentRight)));
                        tempLeft++;
                    }
                    currentRight--;
                }

                left++;
                right--;
            } else if (sum < TARGET_DURATION) {
                left++;
            } else {
                right--;
            }
        }

        return result;
    }

    public int parseDuration(String durationStr) {
        try {
            String[] parts = durationStr.split("\\.");
            int minutes = Integer.parseInt(parts[0]);
            int seconds = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
            return minutes * 60 + seconds;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid duration format: " + durationStr);
        }
    }

    public static String formatDuration(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d.%02d", minutes, seconds);
    }

    public void analyzeSongs(String[][] playtimes) {
        System.out.println("\n=== Song Analysis ===");

        List<Song> songs = new ArrayList<>();
        for (String[] songData : playtimes) {
            if (songData.length != 2) {
                continue;
            }
            songs.add(new Song(songData[0], songData[1], parseDuration(songData[1])));
        }

        // Basic statistics
        int totalSongs = songs.size();
        int minDuration = songs.stream().mapToInt(Song::getDurationInSeconds).min().orElse(0);
        int maxDuration = songs.stream().mapToInt(Song::getDurationInSeconds).max().orElse(0);
        double avgDuration = songs.stream().mapToInt(Song::getDurationInSeconds).average().orElse(0);

        System.out.println("Total songs: " + totalSongs);
        System.out.println("Shortest song: " + formatDuration(minDuration));
        System.out.println("Longest song: " + formatDuration(maxDuration));
        System.out.println("Average duration: " + formatDuration((int) avgDuration));

        // Songs that are exactly half of target
        int halfTarget = TARGET_DURATION / 2;
        List<Song> halfDurationSongs = songs.stream()
            .filter(song -> song.getDurationInSeconds() == halfTarget)
            .toList();

        if (!halfDurationSongs.isEmpty()) {
            System.out.println("\nSongs that are exactly 3.5 minutes:");
            halfDurationSongs.forEach(song -> System.out.println("  - " + song.getTitle()));
        }
    }

    // Song class to represent song data
    public static class Song {

        private final String title;
        private final String originalDuration;
        private final int durationInSeconds;

        public Song(String title, String originalDuration, int durationInSeconds) {
            this.title = title;
            this.originalDuration = originalDuration;
            this.durationInSeconds = durationInSeconds;
        }

        public String getTitle() {
            return title;
        }

        public String getOriginalDuration() {
            return originalDuration;
        }

        public int getDurationInSeconds() {
            return durationInSeconds;
        }

        @Override
        public String toString() {
            return title + " (" + originalDuration + ")";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Song song = (Song) o;
            return durationInSeconds == song.durationInSeconds
                && Objects.equals(title, song.title);
        }

        @Override
        public int hashCode() {
            return Objects.hash(title, durationInSeconds);
        }
    }

    // SongPair class to represent a pair of songs
    public static class SongPair {

        private final Song song1;
        private final Song song2;
        private final int totalDuration;

        public SongPair(Song song1, Song song2) {
            this.song1 = song1;
            this.song2 = song2;
            this.totalDuration = song1.getDurationInSeconds() + song2.getDurationInSeconds();
        }

        public Song getSong1() {
            return song1;
        }

        public Song getSong2() {
            return song2;
        }

        public int getTotalDuration() {
            return totalDuration;
        }

        @Override
        public String toString() {
            return song1.getTitle() + " (" + song1.getOriginalDuration() + ") + "
                + song2.getTitle() + " (" + song2.getOriginalDuration() + ") = "
                + formatDuration(totalDuration) + " (" + totalDuration + " seconds)";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            SongPair songPair = (SongPair) o;
            return (Objects.equals(song1, songPair.song1) && Objects.equals(song2, songPair.song2))
                || (Objects.equals(song1, songPair.song2) && Objects.equals(song2, songPair.song1));
        }

        @Override
        public int hashCode() {
            return Objects.hash(song1, song2) + Objects.hash(song2, song1);
        }
    }
}
