package com.ucentral.edu.desarrollos.treesortcode.servicios;

import com.ucentral.edu.desarrollos.treesortcode.modelos.BenchmarkRun;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AiAnalysisService {

    private final ObjectMapper mapper = new ObjectMapper();

    public AiAnalysisService() {}

    // If OPENAI_API_KEY is configured we could call external API. For now provide heuristic analysis.
    public String analyzeRun(BenchmarkRun run) {
        try {
            Map<String, Object> result = mapper.readValue(run.getResultJson(), Map.class);
            List<Map<String, Object>> points = (List<Map<String, Object>>) result.get("points");
            if (points == null || points.isEmpty()) return "No hay datos para analizar.";

            // build arrays
            double[] ns = new double[points.size()];
            double[] means = new double[points.size()];
            for (int i = 0; i < points.size(); i++) {
                Map<String,Object> p = points.get(i);
                ns[i] = ((Number)p.get("n")).doubleValue();
                means[i] = ((Number)p.get("mean")).doubleValue();
            }

            // Fit c for n*log(n) and n^2 using least squares: c = sum(mean*f)/sum(f^2)
            double sumF2 = 0, sumFM = 0;
            for (int i = 0; i < ns.length; i++) {
                double f = ns[i] * Math.log(ns[i]);
                sumF2 += f*f; sumFM += f * means[i];
            }
            double cNLogN = sumF2 == 0 ? 0 : sumFM / sumF2;

            double sumF22 = 0, sumFM2 = 0;
            for (int i = 0; i < ns.length; i++) {
                double f = ns[i] * ns[i];
                sumF22 += f*f; sumFM2 += f * means[i];
            }
            double cN2 = sumF22 == 0 ? 0 : sumFM2 / sumF22;

            StringBuilder sb = new StringBuilder();
            sb.append("Análisis heurístico:\n");
            sb.append(String.format("Ajuste para n·log(n): c = %.6f (tiempo en ns ≈ c·n·log n)\n", cNLogN));
            sb.append(String.format("Ajuste para n^2: c = %.6f (tiempo en ns ≈ c·n^2)\n", cN2));

            sb.append("Interpretación: ");
            if (cNLogN > 0 && cNLogN < cN2) sb.append("Los datos se ajustan mejor a n·log(n) en magnitud relativa.\n");
            else sb.append("La dependencia con n es más pronunciada, posiblemente acercándose a n^2 en este rango.\n");

            sb.append("Recomendación: comparar visualmente la curva medida con ambas líneas teóricas y ejecutar más repeticiones o variar la entrada para confirmar.\n");
            return sb.toString();
        } catch (Exception e) {
            return "Error analizando: " + e.getMessage();
        }
    }
}

