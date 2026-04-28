package com.ucentral.edu.desarrollos.treesortcode.modelos;

import jakarta.persistence.*;
import java.time.Instant;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class BenchmarkRun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Fecha/hora en que se creó esta ejecución de benchmark. */
    @JsonProperty("creadoEn")
    @JsonAlias({"createdAt","created_at","created"})
    private Instant createdAt;

    /** Parámetros de la ejecución (JSON): startN, endN, factor, repetitions, mode, etc. */
    @JsonProperty("parametrosJson")
    @JsonAlias({"paramsJson","params","parameters"})
    private String paramsJson; // request params as JSON

    // Map large JSON text as DB native TEXT instead of a JDBC Large Object (LOB).
    // This avoids PostgreSQL "Large Objects may not be used in auto-commit mode" errors
    // because TEXT is stored as TOASTed text, not via the Large Object API.
    @Column(columnDefinition = "text")
    @JsonProperty("resultadoJson")
    @JsonAlias({"resultJson","resultado","resultado_json"})
    private String resultJson; // aggregated result as JSON (puntos, estadísticas, sampleSteps opcional)

    @Column(columnDefinition = "text")
    @JsonProperty("analisisAi")
    @JsonAlias({"aiAnalysis","analisis","analisis_ai"})
    private String aiAnalysis;

    public BenchmarkRun() {}

    public void setAiAnalysis(String analysis) {
    }

    public void setParamsJson(String paramsJson) {
        this.paramsJson = paramsJson;
    }

    public void setResultJson(String resultJson) {
        this.resultJson = resultJson;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getResultJson() {
        return resultJson;
    }

    public String getAiAnalysis() {
        return aiAnalysis;
    }

    public String getParamsJson() {
        return paramsJson;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Long getId() {
        return id;
    }
}

