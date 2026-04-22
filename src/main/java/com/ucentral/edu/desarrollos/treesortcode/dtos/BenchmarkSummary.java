package com.ucentral.edu.desarrollos.treesortcode.dtos;

import java.time.Instant;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class BenchmarkSummary {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("creadoEn")
    private Instant createdAt;

    public BenchmarkSummary(Long id, Instant createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

}

