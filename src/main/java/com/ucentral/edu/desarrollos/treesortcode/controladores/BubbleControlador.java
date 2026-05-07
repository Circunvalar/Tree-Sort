package com.ucentral.edu.desarrollos.treesortcode.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@Controller
public class BubbleControlador {

    @GetMapping("/bubble")
    public String bubble() {
        return "bubblesort";
    }

    @GetMapping("/bubble-benchmark")
    public String benchmark() {
        return "bubblesortbenchmark";
    }

}