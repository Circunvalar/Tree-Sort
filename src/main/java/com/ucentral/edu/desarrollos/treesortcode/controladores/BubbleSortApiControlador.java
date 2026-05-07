package com.ucentral.edu.desarrollos.treesortcode.controladores;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/bubblesort")
@CrossOrigin
public class BubbleSortApiControlador {

    @GetMapping("/benchmark")
    public Map<Integer, Double> benchmark(
            @RequestParam int size,
            @RequestParam int reps,
            @RequestParam int min,
            @RequestParam int max) {

        Map<Integer, Double> result = new LinkedHashMap<>();
        Random rand = new Random();

        for (int n = 10; n <= size; n += 10) {

            long total = 0;

            for (int r = 0; r < reps; r++) {

                List<Integer> list = new ArrayList<>(
                        rand.ints(n, min, max)
                                .boxed()
                                .toList()
                );

                long start = System.nanoTime();

                bubbleSort(list);

                long end = System.nanoTime();
                total += (end - start);
            }

            double avgMs = total / reps / 1_000_000.0;
            result.put(n, avgMs);
        }

        return result;
    }

    private void bubbleSort(List<Integer> list) {
        int n = list.size();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (list.get(j) > list.get(j + 1)) {
                    int temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }
        }
    }
}