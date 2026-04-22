package com.ucentral.edu.desarrollos.treesortcode.dtos;

import com.ucentral.edu.desarrollos.treesortcode.modelos.Nodo;
import lombok.Getter;

import java.util.List;

@Getter
public class PasoDto {

    private String type; // insert | visit | done
    private Integer value;
    private Nodo tree;
    private List<Integer> sorted;

    public PasoDto(String type, Integer value, Nodo tree, List<Integer> sorted) {
        this.type = type;
        this.value = value;
        this.tree = tree;
        this.sorted = sorted;
    }

}