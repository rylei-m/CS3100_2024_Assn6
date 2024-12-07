package com.example;

import java.util.*;

public class TaskFIFO implements Runnable {
    private final int[] sequence;
    private final int maxMemoryFrames;
    private final int[] pageFaults;

    public TaskFIFO(int[] sequence, int maxMemoryFrames, int maxPageReference, int[] pageFaults) {
        this.sequence = sequence;
        this.maxMemoryFrames = maxMemoryFrames;
        this.pageFaults = pageFaults;
    }

    @Override
    public void run() {
        Set<Integer> memory = new HashSet<>();
        Queue<Integer> fifoQueue = new LinkedList<>();
        int faults = 0;

        for (int page : sequence) {
            if (!memory.contains(page)) {
                faults++;
                if (memory.size() == maxMemoryFrames) {
                    memory.remove(fifoQueue.poll());
                }
                memory.add(page);
                fifoQueue.add(page);
            }
        }
        pageFaults[maxMemoryFrames] = faults;
    }
}
