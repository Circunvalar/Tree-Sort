package com.ucentral.edu.desarrollos.treesortcode.dtos;

import com.ucentral.edu.desarrollos.treesortcode.modelos.Nodo;
import java.util.List;

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

    public String getType() { return type; }
    public Integer getValue() { return value; }

    public Nodo getTree() { return tree; }
    public List<Integer> getSorted() { return sorted; }
}