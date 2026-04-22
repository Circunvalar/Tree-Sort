package com.ucentral.edu.desarrollos.treesortcode.servicios;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucentral.edu.desarrollos.treesortcode.dtos.BenchmarkRequest;
import com.ucentral.edu.desarrollos.treesortcode.modelos.BenchmarkRun;
import com.ucentral.edu.desarrollos.treesortcode.repos.BenchmarkRunRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class BenchmarkService {

    private final TreeSortServicio treeSortServicio;
    private final BenchmarkRunRepository repo;
    private final ObjectMapper mapper = new ObjectMapper();

    public BenchmarkService(TreeSortServicio treeSortServicio, BenchmarkRunRepository repo) {
        this.treeSortServicio = treeSortServicio;
        this.repo = repo;
    }

    public BenchmarkRun runAndSave(BenchmarkRequest req) throws Exception {
        List<Map<String, Object>> points = new ArrayList<>();

        Random rnd = req.getSeed() != null ? new Random(req.getSeed()) : new Random();

        for (int n = req.getStartN(); n <= req.getEndN(); n = Math.max(n * req.getFactor(), n + 1)) {
            List<Long> times = new ArrayList<>();
            List<Integer> sampleArray = null;
            List<?> sampleSteps = null;

            for (int r = 0; r < Math.max(1, req.getRepetitions()); r++) {
                List<Integer> arr;
                if ("manual".equalsIgnoreCase(req.getMode()) && req.getArray() != null && !req.getArray().isEmpty()) {
                    arr = req.getArray();
                } else {
                    arr = new ArrayList<>();
                    // respect optional min/max parameters (defaults: 0..1000)
                    int min = req.getMin() != null ? req.getMin() : 0;
                    int max = req.getMax() != null ? req.getMax() : 1000;
                    if (max < min) { int t = min; min = max; max = t; }
                    int range = Math.max(1, max - min + 1);
                    for (int i = 0; i < n; i++) arr.add(min + rnd.nextInt(range));
                }

                if (r == 0) sampleArray = new ArrayList<>(arr);

                long t0 = System.nanoTime();
                // run tree sort steps; we don't need full steps for timing but we can collect if requested
                List<?> steps = treeSortServicio.generarPasos(arr);
                long t1 = System.nanoTime();
                times.add(t1 - t0);

                if (r == 0) sampleSteps = steps;
            }

            double mean = times.stream().mapToLong(Long::longValue).average().orElse(0.0);
            double variance = times.stream().mapToDouble(t -> Math.pow(t - mean, 2)).average().orElse(0.0);
            double std = Math.sqrt(variance);

            Map<String, Object> point = new LinkedHashMap<>();
            point.put("n", n);
            point.put("times", times);
            point.put("mean", mean);
            point.put("std", std);
            point.put("sampleArray", sampleArray);
            point.put("sampleSteps", sampleSteps);
            points.add(point);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("points", points);
        result.put("createdAt", Instant.now().toString());

        String resultJson = mapper.writeValueAsString(result);

        BenchmarkRun run = new BenchmarkRun();
        run.setCreatedAt(Instant.now());
        run.setParamsJson(mapper.writeValueAsString(req));
        run.setResultJson(resultJson);

        return repo.save(run);
    }
}

