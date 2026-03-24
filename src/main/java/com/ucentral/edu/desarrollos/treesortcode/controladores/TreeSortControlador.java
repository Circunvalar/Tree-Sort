package com.ucentral.edu.desarrollos.treesortcode.controladores;

import com.ucentral.edu.desarrollos.treesortcode.dtos.PasoDto;
import com.ucentral.edu.desarrollos.treesortcode.servicios.TreeSortServicio;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/treesort")
@CrossOrigin
public class TreeSortControlador {

    private final TreeSortServicio service;

    public TreeSortControlador(TreeSortServicio service) {
        this.service = service;
    }

    @PostMapping("/steps")
    public List<PasoDto> pasos(@RequestBody List<Integer> input) {
        return service.generarPasos(input);
    }
}