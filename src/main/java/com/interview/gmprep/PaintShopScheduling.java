/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.gmprep;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PaintShopScheduling {

    private static final PaintShopScheduling m = new PaintShopScheduling();

    public static void main(String[] args) {
        // red = 0; brown = 2, yellow = 3, green = 4, blue = 5, purple = 6
        int[] carSequence = {1, 4, 2, 7, 5, 9, 3, 6};
        int cleanTime = 2;

        log.info("1          minColorSwitches {}", m.minColorSwitches(carSequence, cleanTime));
        log.info(
                "2 Optimized minColorSwitches {}", m.minColorSwitchesOptimized(carSequence, cleanTime));
        log.info("3     minColorSwitchesStream {}", m.minColorSwitchesStream(carSequence, cleanTime));
    }

    /*
  4. Paint Shop Scheduling
      Problem: Minimize color changeovers in GM's paint shops (where color switches require cleaning).
     */
    public int minColorSwitches(int[] carSequence, int cleanTime) {
        int lastColor = -1;
        int switches = 0;

        for (int color : carSequence) {
            if (color != lastColor) {
                switches++;
                lastColor = color;
            }
        }
        return (switches - 1) * cleanTime;
    }

    public int minColorSwitchesStream(int[] carSequence, int cleanTime) {
        final AtomicInteger lastColor = new AtomicInteger(-1);
        final AtomicInteger switches = new AtomicInteger(0);

        Arrays.stream(carSequence)
                .forEach(
                        e -> {
                            if (e != lastColor.get()) {
                                switches.getAndIncrement();
                                lastColor.set(e);
                            }
                        });

        return switches.decrementAndGet() * cleanTime;
    }

    /*
  Optimization:
      Batch Processing: Group same-color cars first using a priority queue:
     */
    public int minColorSwitchesOptimized(int[] carSequence, int cleanTime) {
        Map<Integer, Integer> colorCount = new HashMap<>();
        for (int color : carSequence) {
            colorCount.put(color, colorCount.getOrDefault(color, 0) + 1);
        }

        PriorityQueue<Integer> maxHeap
                = new PriorityQueue<>((a, b) -> colorCount.get(b) - colorCount.get(a));
        maxHeap.addAll(colorCount.keySet());

        int switches = 0;
        int prev = -1;
        while (!maxHeap.isEmpty()) {
            int color = maxHeap.poll();
            if (color != prev) {
                switches++;
            }
            prev = color;
            colorCount.put(color, colorCount.get(color) - 1);
            if (colorCount.get(color) > 0) {
                maxHeap.offer(color);
            }
        }
        return (switches - 1) * cleanTime;
    }
}
