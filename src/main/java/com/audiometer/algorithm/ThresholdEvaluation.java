package com.audiometer.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ThresholdEvaluation {

    private final int frequency;
    private final int intensity;
    private final List<Boolean> responsesAtCurrentIntensity;
    private final boolean thresholdFound;
    private final int thresholdDb;

    public ThresholdEvaluation(
            int frequency,
            int intensity,
            List<Boolean> responsesAtCurrentIntensity,
            boolean thresholdFound,
            int thresholdDb
    ) {
        this.frequency = frequency;
        this.intensity = intensity;
        this.responsesAtCurrentIntensity =
                Collections.unmodifiableList(new ArrayList<>(responsesAtCurrentIntensity));
        this.thresholdFound = thresholdFound;
        this.thresholdDb = thresholdDb;
    }

    public static ThresholdEvaluation initial(int frequency) {
        return new ThresholdEvaluation(
                frequency,
                30,
                Collections.emptyList(),
                false,
                -1
        );
    }

    public int getFrequency() {
        return frequency;
    }

    public int getIntensity() {
        return intensity;
    }

    public List<Boolean> getResponsesAtCurrentIntensity() {
        return responsesAtCurrentIntensity;
    }

    public boolean isThresholdFound() {
        return thresholdFound;
    }

    public int getThresholdDb() {
        return thresholdDb;
    }
}
