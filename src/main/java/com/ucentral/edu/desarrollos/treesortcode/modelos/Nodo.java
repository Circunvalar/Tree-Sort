package com.ucentral.edu.desarrollos.treesortcode.modelos;

public class Nodo {

    private int value;
    private Nodo left;
    private Nodo right;

    public Nodo(int value) {
        this.value = value;
    }

    public int getValue() { return value; }
    public Nodo getLeft() { return left; }
    public Nodo getRight() { return right; }

    public void setLeft(Nodo left) { this.left = left; }
    public void setRight(Nodo right) { this.right = right; }
}