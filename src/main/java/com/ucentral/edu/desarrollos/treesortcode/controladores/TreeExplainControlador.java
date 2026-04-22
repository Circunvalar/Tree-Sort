package com.ucentral.edu.desarrollos.treesortcode.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TreeExplainControlador {

    @GetMapping("/tree-explain")
    public String explain() {
        return "tree-explain";
    }
}

