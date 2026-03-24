package com.ucentral.edu.desarrollos.treesortcode.servicios;

import com.ucentral.edu.desarrollos.treesortcode.dtos.PasoDto;
import com.ucentral.edu.desarrollos.treesortcode.modelos.Nodo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TreeSortServicio {

    private Nodo root;
    private final List<PasoDto> steps = new ArrayList<>();

    public List<PasoDto> generarPasos(List<Integer> input) {
        root = null;
        steps.clear();
        List<Integer> sorted = new ArrayList<>();

        // Inserción
        for (int val : input) {
            root = insert(root, val);

            steps.add(new PasoDto(
                    "insert",
                    val,
                    cloneTree(root),
                    new ArrayList<>(sorted)
            ));
        }

        // Inorder
        inorder(root, sorted);

        steps.add(new PasoDto("done", null, cloneTree(root), sorted));

        return steps;
    }

    private Nodo insert(Nodo node, int value) {
        if (node == null) return new Nodo(value);

        if (value < node.getValue())
            node.setLeft(insert(node.getLeft(), value));
        else
            node.setRight(insert(node.getRight(), value));

        return node;
    }

    private void inorder(Nodo node, List<Integer> sorted) {
        if (node == null) return;

        inorder(node.getLeft(), sorted);

        sorted.add(node.getValue());
        steps.add(new PasoDto(
                "visit",
                node.getValue(),
                cloneTree(root),
                new ArrayList<>(sorted)
        ));

        inorder(node.getRight(), sorted);
    }

    private Nodo cloneTree(Nodo node) {
        if (node == null) return null;

        Nodo copy = new Nodo(node.getValue());
        copy.setLeft(cloneTree(node.getLeft()));
        copy.setRight(cloneTree(node.getRight()));
        return copy;
    }
}