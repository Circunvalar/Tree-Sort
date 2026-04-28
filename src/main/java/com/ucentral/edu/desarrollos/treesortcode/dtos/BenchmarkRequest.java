package com.ucentral.edu.desarrollos.treesortcode.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;

@Setter
@Getter
public class BenchmarkRequest {
    @JsonAlias({"modo","mode"})
    private String mode; // "random" | "manual"

    @JsonAlias({"inicioN","startN","start","inicio"})
    private int startN;

    @JsonAlias({"finN","endN","end","fin"})
    private int endN;

    @JsonAlias({"factor"})
    private int factor;

    @JsonAlias({"repeticiones","reps","repetitions"})
    private int repetitions;

    @JsonAlias({"semilla","seed"})
    private Long seed;

    @JsonAlias({"min","minVal","valorMinimo","minimo"})
    private Integer min;

    @JsonAlias({"max","maxVal","valorMaximo","maximo"})
    private Integer max;

    @JsonAlias({"arreglo","array"})
    private List<Integer> array; // optional for manual mode

    public BenchmarkRequest() {}

    public Long getSeed() {
        return seed;
    }

    public int getStartN() {
        return startN;
    }

    public Integer getMin() {
        return min;
    }

    public List<Integer> getArray() {
        return array;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public String getMode() {
        return mode;
    }

    public Integer getMax() {
        return max;
    }

    public int getFactor() {
        return factor;
    }

    public int getEndN() {
        return endN;
    }
}

