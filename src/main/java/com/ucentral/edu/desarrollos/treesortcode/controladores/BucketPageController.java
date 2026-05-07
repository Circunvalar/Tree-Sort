package com.ucentral.edu.desarrollos.treesortcode.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BucketPageController {

    @GetMapping("/bucket")
    public String bucketPage() {
        return "bucket";
    }

    @GetMapping("/bucketbenchmark")
    public String bucketBenchmark() {
        return "bucket-benchmark";
    }
}