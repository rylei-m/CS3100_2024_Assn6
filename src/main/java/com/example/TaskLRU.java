package com.example;

import java.util.*;

public class TaskLRU implements Runnable {
    private final int[] sequence;
    private final int maxMemoryFrames;
    private final int[] pageFaults;

    public TaskLRU(int[] sequence, int maxMemoryFrames, int maxPageReference, int[] pageFaults) {
        this.sequence = sequence;
        this.maxMemoryFrames = maxMemoryFrames;
        this.pageFaults = pageFaults;
    }

    @Override
    public void run() {
        LinkedHashSet<Integer> memory = new LinkedHashSet<>();
        int faults = 0;

        for (int page : sequence) {
            if (!memory.contains(page)) {
                faults++;
                if (memory.size() == maxMemoryFrames) {
                    // Remove the least recently used (first element)
                    int lru = memory.iterator().next();
                    memory.remove(lru);
                }
            } else {
                // Re-accessed page; remove and re-insert to update order
                memory.remove(page);
            }
            memory.add(page);
        }
        pageFaults[maxMemoryFrames] = faults;
    }
}
