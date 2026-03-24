package com.ucentral.edu.desarrollos.treesortcode.dtos;

import com.ucentral.edu.desarrollos.treesortcode.modelos.Nodo;

import java.util.List;

public class TreeSortResponse {

    private List<Integer> original;
    private List<Integer> sorted;
    private Nodo tree;

    public TreeSortResponse(List<Integer> original, List<Integer> sorted, Nodo tree) {
        this.original = original;
        this.sorted = sorted;
        this.tree = tree;
    }

    public List<Integer> getOriginal() { return original; }
    public List<Integer> getSorted() { return sorted; }
    public Nodo getTree() { return tree; }
}