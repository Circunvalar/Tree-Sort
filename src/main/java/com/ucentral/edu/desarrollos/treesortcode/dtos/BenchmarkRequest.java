package com.ucentral.edu.desarrollos.treesortcode.dtos;

import lombok.Getter;
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

}

