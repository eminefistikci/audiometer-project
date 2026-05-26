package com.audiometer.algorithm;

import java.util.List;

public final class FrequencyCycle {

    private FrequencyCycle() {}

    public static final List<Integer> CLINICAL_ORDER = List.of(
            1000,
            2000,
            4000,
            8000,
            500,
            250
    );

    public static int nextFrequency(int currentFrequency) {

        int index = CLINICAL_ORDER.indexOf(currentFrequency);

        if (index < 0 || index == CLINICAL_ORDER.size() - 1) {
            return -1;
        }

        return CLINICAL_ORDER.get(index + 1);
    }

    public static boolean isLastFrequency(int frequency) {
        return frequency == CLINICAL_ORDER.get(CLINICAL_ORDER.size() - 1);
    }
}
