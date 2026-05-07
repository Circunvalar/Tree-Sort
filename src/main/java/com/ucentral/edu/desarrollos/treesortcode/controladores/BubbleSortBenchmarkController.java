package com.ucentral.edu.desarrollos.treesortcode.controladores;

import com.ucentral.edu.desarrollos.treesortcode.servicios.BubbleSortBenchmarkService;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/bubblesort/benchmark")
@CrossOrigin(origins = "*")
public class BubbleSortBenchmarkController {

    private final BubbleSortBenchmarkService service;

    public BubbleSortBenchmarkController(BubbleSortBenchmarkService service) {
        this.service = service;
    }


    @GetMapping("/api/bubblesort/benchmark")
    public Map<Integer, Double> benchmark(
            @RequestParam int size,
            @RequestParam int reps,
            @RequestParam int min,
            @RequestParam int max) {

        Map<Integer, Double> result = new LinkedHashMap<>();

        for (int n = 10; n <= size; n += 10) {

            long total = 0;

            for (int r = 0; r < reps; r++) {
                List<Integer> list = new Random()
                        .ints(n, min, max)
                        .boxed()
                        .toList();

                long start = System.nanoTime();

                service.bubbleSort(list);

                long end = System.nanoTime();
                total += (end - start);
            }

            double avgMs = total / reps / 1_000_000.0;
            result.put(n, avgMs);
        }

        return result;
    }

}