package com.ucentral.edu.desarrollos.treesortcode.modelos;

import java.util.List;

public class BubbleSortStep {

    private String type;
    private Integer i;
    private Integer j;
    private List<Integer> array;

    // 🔥 IMPORTANTE: constructor vacío (Jackson lo necesita)
    public BubbleSortStep() {}

    public BubbleSortStep(String type, Integer i, Integer j, List<Integer> array) {
        this.type = type;
        this.i = i;
        this.j = j;
        this.array = array;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getI() { return i; }
    public void setI(Integer i) { this.i = i; }

    public Integer getJ() { return j; }
    public void setJ(Integer j) { this.j = j; }

    public List<Integer> getArray() { return array; }
    public void setArray(List<Integer> array) { this.array = array; }
}