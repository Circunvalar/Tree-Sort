package com.ucentral.edu.desarrollos.treesortcode.modelos;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Nodo {
    @Setter
    @Getter
    private int value;
    @Setter
    @Getter
    private Nodo left;
    @Setter
    @Getter
    private Nodo right;

    public Nodo(int value) {
        this.value = value;
    }

    public Nodo getLeft() {
        return left;
    }

    public Nodo getRight() {
        return right;
    }

    public int getValue() {
        return value;
    }

    public void setRight(Nodo right) {
        this.right = right;
    }

    public void setLeft(Nodo left) {
        this.left = left;
    }
}