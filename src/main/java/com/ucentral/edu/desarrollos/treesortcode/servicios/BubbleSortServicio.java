package com.ucentral.edu.desarrollos.treesortcode.servicios;

import com.ucentral.edu.desarrollos.treesortcode.dtos.BubbleDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BubbleSortServicio {

    public List<BubbleDTO> generarPasos(List<Integer> input) {

        List<BubbleDTO> pasos = new ArrayList<>();
        List<Integer> arr = new ArrayList<>(input);

        int n = arr.size();

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {

                pasos.add(new BubbleDTO(
                        "compare",
                        j,
                        j + 1,
                        new ArrayList<>(arr)
                ));

                if (arr.get(j) > arr.get(j + 1)) {

                    int temp = arr.get(j);
                    arr.set(j, arr.get(j + 1));
                    arr.set(j + 1, temp);

                    pasos.add(new BubbleDTO(
                            "swap",
                            j,
                            j + 1,
                            new ArrayList<>(arr)
                    ));
                }
            }
        }

        pasos.add(new BubbleDTO("done", -1, -1, new ArrayList<>(arr)));

        return pasos;
    }
}