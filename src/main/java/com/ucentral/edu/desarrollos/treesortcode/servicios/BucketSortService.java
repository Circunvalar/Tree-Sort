package com.ucentral.edu.desarrollos.treesortcode.services;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class BucketSortService {

    // ================= STEP =================
    public static class Step {

        public String type;
        public Double value;
        public int bucketIndex;

        public List<List<Double>> buckets;
        public List<Double> sorted;

        public Step(
                String type,
                Double value,
                int bucketIndex,
                List<List<Double>> buckets,
                List<Double> sorted
        ) {
            this.type = type;
            this.value = value;
            this.bucketIndex = bucketIndex;
            this.buckets = buckets;
            this.sorted = sorted;
        }
    }


    public List<Step> bucketSortSteps(List<Double> arr) {

        List<Step> steps = new ArrayList<>();

        if (arr == null || arr.isEmpty()) {
            return steps;
        }

        int n = arr.size();

        List<List<Double>> buckets = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            buckets.add(new ArrayList<>());
        }

        double max = Collections.max(arr);

        if (max == 0) {
            max = 1;
        }

        // ================= INSERTAR EN BUCKETS =================
        for (Double value : arr) {

            int bucketIndex = (int) ((value / max) * (n - 1));

            buckets.get(bucketIndex).add(value);

            steps.add(new Step(
                    "insert_bucket",
                    value,
                    bucketIndex,
                    copyBuckets(buckets),
                    null
            ));
        }

        // ================= ORDENAR BUCKETS =================
        for (int i = 0; i < buckets.size(); i++) {

            Collections.sort(buckets.get(i));

            steps.add(new Step(
                    "sort_bucket",
                    null,
                    i,
                    copyBuckets(buckets),
                    null
            ));
        }

        // ================= UNIR RESULTADO =================
        List<Double> sorted = new ArrayList<>();

        for (int i = 0; i < buckets.size(); i++) {

            for (Double value : buckets.get(i)) {

                sorted.add(value);

                steps.add(new Step(
                        "merge",
                        value,
                        i,
                        copyBuckets(buckets),
                        new ArrayList<>(sorted)
                ));
            }
        }

        // ================= FINAL =================
        steps.add(new Step(
                "done",
                null,
                -1,
                copyBuckets(buckets),
                sorted
        ));

        return steps;
    }

    // ================= COPIA SEGURA =================
    private List<List<Double>> copyBuckets(List<List<Double>> original) {

        List<List<Double>> copy = new ArrayList<>();

        for (List<Double> bucket : original) {
            copy.add(new ArrayList<>(bucket));
        }

        return copy;
    }
}