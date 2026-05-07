package com.ucentral.edu.desarrollos.treesortcode.servicios;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BubbleSortBenchmarkService {

    public Map<Integer, Long> runBenchmark(int size, int reps, int min, int max) {

        Map<Integer, Long> results = new LinkedHashMap<>();
        Random random = new Random();

        for (int n = 10; n <= size; n += size / 10) {

            long totalTime = 0;

            for (int r = 0; r < reps; r++) {

                List<Integer> arr = new ArrayList<>();

                for (int i = 0; i < n; i++) {
                    arr.add(random.nextInt(max - min + 1) + min);
                }

                long start = System.nanoTime();

                bubbleSort(arr);

                long end = System.nanoTime();

                totalTime += (end - start);
            }

            results.put(n, totalTime / reps);
        }

        return results;
    }

    public void bubbleSort(List<Integer> arr) {
        int n = arr.size();

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {

                if (arr.get(j) > arr.get(j + 1)) {
                    int temp = arr.get(j);
                    arr.set(j, arr.get(j + 1));
                    arr.set(j + 1, temp);
                }
            }
        }
    }
}