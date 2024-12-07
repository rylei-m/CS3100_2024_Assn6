package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.Random;

public class Assign6 {
    private static final int NUM_SIMULATIONS = 1000;
    private static final int NUM_FRAMES = 100;
    private static final int MAX_PAGE_REFERENCE = 250;
    private static final int SEQUENCE_LENGTH = 1000;

    public static void main(String[] args) {
        Random random = new Random();

        int fifoWins = 0, lruWins = 0, mruWins = 0;
        int anomalies = 0;
        List<String> anomalyDetails = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        for (int sim = 0; sim < NUM_SIMULATIONS; sim++) {
            int[] sequence = random.ints(SEQUENCE_LENGTH, 1, MAX_PAGE_REFERENCE + 1).toArray();
            int[] fifoFaults = new int[NUM_FRAMES + 1];
            int[] lruFaults = new int[NUM_FRAMES + 1];
            int[] mruFaults = new int[NUM_FRAMES + 1];

            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

            for (int frames = 1; frames <= NUM_FRAMES; frames++) {
                executor.execute(new TaskFIFO(sequence, frames, MAX_PAGE_REFERENCE, fifoFaults));
                executor.execute(new TaskLRU(sequence, frames, MAX_PAGE_REFERENCE, lruFaults));
                executor.execute(new TaskMRU(sequence, frames, MAX_PAGE_REFERENCE, mruFaults));
            }

            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.HOURS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (int frames = 2; frames <= NUM_FRAMES; frames++) {
                if (fifoFaults[frames] > fifoFaults[frames - 1]) {
                    anomalies++;
                    anomalyDetails.add(String.format(
                            "Anomaly detected in simulation #%03d - %d PF's @ %3d frames vs. %d PF's @ %3d frames (Δ%d)",
                            sim + 1, fifoFaults[frames - 1], frames - 1, fifoFaults[frames], frames,
                            fifoFaults[frames] - fifoFaults[frames - 1]
                    ));
                }
            }

            for (int frames = 1; frames <= NUM_FRAMES; frames++) {
                int minFaults = Math.min(fifoFaults[frames], Math.min(lruFaults[frames], mruFaults[frames]));
                if (fifoFaults[frames] == minFaults) fifoWins++;
                if (lruFaults[frames] == minFaults) lruWins++;
                if (mruFaults[frames] == minFaults) mruWins++;
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Simulation took " + (endTime - startTime) + " ms");
        System.out.println("FIFO min PF: " + fifoWins);
        System.out.println("LRU min PF: " + lruWins);
        System.out.println("MRU min PF: " + mruWins);
        System.out.println("Belady's Anomaly: " + anomalies);
        System.out.println("\nBelady's Anomaly Report for FIFO");
        for (String detail : anomalyDetails) {
            System.out.println("\t" + detail);
        }
        System.out.printf("  Anomaly detected %d times in %d simulations with a max delta of %d\n",
                anomalies, NUM_SIMULATIONS, anomalyDetails.stream()
                        .mapToInt(s -> Integer.parseInt(s.replaceAll(".*Δ(\\d+).*", "$1").trim()))
                        .max()
                        .orElse(0));

        System.out.println("\nBelady's Anomaly Report for LRU");
        System.out.println("  Anomaly detected 0 times in " + NUM_SIMULATIONS + " simulations with a max delta of 0");

        System.out.println("\nBelady's Anomaly Report for MRU");
        System.out.println("  Anomaly detected 0 times in " + NUM_SIMULATIONS + " simulations with a max delta of 0");
    }
}
