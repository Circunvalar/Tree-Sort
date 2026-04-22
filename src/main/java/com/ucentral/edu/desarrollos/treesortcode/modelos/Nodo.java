package com.ucentral.edu.desarrollos.treesortcode.modelos;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Nodo {

    private int value;
    @Setter
    private Nodo left;
    @Setter
    private Nodo right;

    public Nodo(int value) {
        this.value = value;
    }

}