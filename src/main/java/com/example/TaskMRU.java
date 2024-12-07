package com.example;

import java.util.*;

public class TaskMRU implements Runnable {
    private final int[] sequence;
    private final int maxMemoryFrames;
    private final int[] pageFaults;

    public TaskMRU(int[] sequence, int maxMemoryFrames, int maxPageReference, int[] pageFaults) {
        this.sequence = sequence;
        this.maxMemoryFrames = maxMemoryFrames;
        this.pageFaults = pageFaults;
    }

    @Override
    public void run() {
        LinkedHashSet<Integer> memory = new LinkedHashSet<>();
        int faults = 0;
        int mostRecentlyUsed = -1; // Tracks the most recently used page

        for (int page : sequence) {
            if (!memory.contains(page)) {
                faults++;
                if (memory.size() == maxMemoryFrames) {
                    // Evict the most recently used page
                    memory.remove(mostRecentlyUsed);
                }
                memory.add(page);
            } else {
                // Re-accessed page; just update the MRU tracker
                memory.remove(page);
                memory.add(page);
            }
            mostRecentlyUsed = page; // Update MRU
        }
        pageFaults[maxMemoryFrames] = faults;
    }
}
