/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.misc;

import com.interview.TwoDimSongPairFinder;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.jupiter.api.Test; //Junit 5
//import org.junit.Test;

/**
 *
 * @author javau
 */
public class SongPairFinderTest {
    private TwoDimSongPairFinder finder = new TwoDimSongPairFinder();

    //@Test
    public void testFindSongPairsWithExactMatches() {
        String[][] testPlaytimes = {
            {"Song A", "3.30"}, // 210 seconds
            {"Song B", "3.30"}, // 210 seconds
            {"Song C", "4.00"}, // 240 seconds
            {"Song D", "3.00"} // 180 seconds
        };

        List<TwoDimSongPairFinder.SongPair> pairs = finder.findSongPairs(testPlaytimes);

        // Should find Song A + Song B (210 + 210 = 420)
        assertEquals(1, pairs.size());
        assertEquals(420, pairs.get(0).getTotalDuration());
    }

    //@Test
    public void testFindSongPairsWithDifferentDurations() {
        String[][] testPlaytimes = {
            {"Song A", "4.00"}, // 240 seconds
            {"Song B", "3.00"}, // 180 seconds
            {"Song C", "2.30"}, // 150 seconds
            {"Song D", "4.30"} // 270 seconds
        };

        List<TwoDimSongPairFinder.SongPair> pairs = finder.findSongPairs(testPlaytimes);

        // Should find Song A + Song B (240 + 180 = 420)
        assertEquals(1, pairs.size());
        assertEquals(420, pairs.get(0).getTotalDuration());
    }

    @Test
    public void testParseDuration() {
        assertEquals(335, finder.parseDuration("5.35"));  // 5*60 + 35 = 335
        assertEquals(95, finder.parseDuration("1.35"));   // 1*60 + 35 = 95
        assertEquals(275, finder.parseDuration("4.35"));  // 4*60 + 35 = 275
        assertEquals(420, finder.parseDuration("7.00"));  // 7*60 + 00 = 420
    }

    @Test
    public void testFormatDuration() {
        assertEquals("5.35", finder.formatDuration(335));
        assertEquals("1.35", finder.formatDuration(95));
        assertEquals("7.00", finder.formatDuration(420));
    }

    @Test
    public void testNoPairsFound() {
        String[][] testPlaytimes = {
            {"Song A", "1.00"},
            {"Song B", "1.00"},
            {"Song C", "1.00"}
        };

        List<TwoDimSongPairFinder.SongPair> pairs = finder.findSongPairs(testPlaytimes);
        assertTrue(pairs.isEmpty());
    }
}
