package com.ucentral.edu.desarrollos.treesortcode.controladores;

import com.ucentral.edu.desarrollos.treesortcode.services.BucketSortService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/bucketsort")
@CrossOrigin
public class BucketSortController {

    private final BucketSortService service;

    public BucketSortController(BucketSortService service) {
        this.service = service;
    }

    // ===================== STEPS =====================
    @PostMapping("/steps")
    public List<BucketSortService.Step> steps(
            @RequestBody List<Double> input
    ) {
        return service.bucketSortSteps(input);
    }

    // ===================== BENCHMARK =====================
    @GetMapping("/benchmark")
    public Map<Integer, Double> benchmark(
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(defaultValue = "3") int reps,
            @RequestParam(defaultValue = "0") double min,
            @RequestParam(defaultValue = "1") double max
    ) {

        Map<Integer, Double> result = new LinkedHashMap<>();

        for (int n = 10; n <= size; n += 10) {

            double total = 0;

            for (int r = 0; r < reps; r++) {

                List<Double> arr = generateRandom(n, min, max);

                long start = System.nanoTime();

                service.bucketSortSteps(arr);

                long end = System.nanoTime();

                total += (end - start) / 1_000_000.0;
            }

            result.put(n, total / reps);
        }

        return result;
    }

    // ===================== RANDOM =====================
    private List<Double> generateRandom(int size, double min, double max) {

        Random random = new Random();

        List<Double> arr = new ArrayList<>();

        for (int i = 0; i < size; i++) {

            arr.add(min + (max - min) * random.nextDouble());
        }

        return arr;
    }
}