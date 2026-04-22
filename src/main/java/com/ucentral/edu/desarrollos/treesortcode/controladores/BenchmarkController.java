package com.ucentral.edu.desarrollos.treesortcode.controladores;

import com.ucentral.edu.desarrollos.treesortcode.dtos.BenchmarkRequest;
import com.ucentral.edu.desarrollos.treesortcode.modelos.BenchmarkRun;
import com.ucentral.edu.desarrollos.treesortcode.servicios.BenchmarkService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.ucentral.edu.desarrollos.treesortcode.repos.BenchmarkRunRepository;
import com.ucentral.edu.desarrollos.treesortcode.servicios.AiAnalysisService;


@Controller
public class BenchmarkController {

    private final BenchmarkService benchmarkService;
    private final BenchmarkRunRepository runRepository;
    private final AiAnalysisService aiService;

    public BenchmarkController(BenchmarkService benchmarkService, BenchmarkRunRepository runRepository, AiAnalysisService aiService) {
        this.benchmarkService = benchmarkService;
        this.runRepository = runRepository;
        this.aiService = aiService;
    }

    @GetMapping("/benchmark")
    public String benchPage() { return "bench"; }

    @PostMapping("/api/treesort/benchmark")
    @ResponseBody
    public BenchmarkRun runBenchmark(@RequestBody BenchmarkRequest req) throws Exception {
        return benchmarkService.runAndSave(req);
    }

    @GetMapping("/api/treesort/benchmarks")
    @ResponseBody
    public java.util.List<com.ucentral.edu.desarrollos.treesortcode.dtos.BenchmarkSummary> listRuns() {
        // Return lightweight summaries (id + createdAt) to avoid loading large LOB fields into memory
        return runRepository.findAllSummaries();
    }

    @GetMapping("/api/treesort/benchmark/{id}")
    @ResponseBody
    public BenchmarkRun getRun(@PathVariable Long id) {
        return runRepository.findById(id).orElse(null);
    }

    @GetMapping("/api/treesort/benchmarks/aggregate")
    @ResponseBody
    public java.util.Map<String, Object> aggregateRuns() {
        java.util.List<String> jsons = runRepository.findAllResultJsons();
        com.fasterxml.jackson.core.JsonFactory factory = new com.fasterxml.jackson.core.JsonFactory();

        java.util.Map<Integer, java.util.List<Double>> acc = new java.util.HashMap<>();

        for (String js : jsons) {
            if (js == null) continue;
            try (com.fasterxml.jackson.core.JsonParser p = factory.createParser(js)) {
                // stream parse and find "points" array without building full tree
                while (p.nextToken() != null) {
                    com.fasterxml.jackson.core.JsonToken t = p.currentToken();
                    if (t == com.fasterxml.jackson.core.JsonToken.FIELD_NAME && "points".equals(p.getCurrentName())) {
                        // move to start of array
                        if (p.nextToken() == com.fasterxml.jackson.core.JsonToken.START_ARRAY) {
                            while (p.nextToken() != com.fasterxml.jackson.core.JsonToken.END_ARRAY) {
                                // each element is an object
                                int n = -1; double mean = Double.NaN;
                                if (p.currentToken() == com.fasterxml.jackson.core.JsonToken.START_OBJECT) {
                                    while (p.nextToken() != com.fasterxml.jackson.core.JsonToken.END_OBJECT) {
                                        String field = p.getCurrentName();
                                        p.nextToken();
                                        if ("n".equals(field)) {
                                            n = p.getIntValue();
                                        } else if ("mean".equals(field)) {
                                            mean = p.getDoubleValue();
                                        } else {
                                            p.skipChildren();
                                        }
                                    }
                                    if (n != -1 && !Double.isNaN(mean)) {
                                        acc.computeIfAbsent(n, k -> new java.util.ArrayList<>()).add(mean);
                                    }
                                } else {
                                    p.skipChildren();
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // log and continue with next
                System.err.println("Warning: failed to parse resultJson for one run: " + e.getMessage());
            }
        }

        java.util.List<java.util.Map<String,Object>> pointsOut = new java.util.ArrayList<>();
        acc.entrySet().stream().sorted(java.util.Map.Entry.comparingByKey()).forEach(entry -> {
            int n = entry.getKey();
            java.util.List<Double> list = entry.getValue();
            double sum=0; for (double v: list) sum+=v;
            double mean = list.isEmpty() ? 0 : sum / list.size();
            java.util.Map<String,Object> m = new java.util.HashMap<>();
            m.put("n", n);
            m.put("meanNs", mean);
            m.put("runs", list.size());
            pointsOut.add(m);
        });

        java.util.Map<String,Object> out = new java.util.HashMap<>();
        out.put("points", pointsOut);
        return out;
    }

    @PostMapping("/api/treesort/benchmark/{id}/analyze")
    @ResponseBody
    public BenchmarkRun analyzeRun(@PathVariable Long id) throws Exception {
        BenchmarkRun run = runRepository.findById(id).orElse(null);
        if (run == null) return null;
        String analysis = aiService.analyzeRun(run);
        run.setAiAnalysis(analysis);
        return runRepository.save(run);
    }
}

