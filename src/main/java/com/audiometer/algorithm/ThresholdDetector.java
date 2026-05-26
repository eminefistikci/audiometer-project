package com.audiometer.algorithm;

import com.audiometer.functional.AudiometryRules;

import java.util.ArrayList;
import java.util.List;

public final class ThresholdDetector {

    private ThresholdDetector() {}

    public static ThresholdEvaluation next(
            ThresholdEvaluation current,
            boolean response
    ) {
        List<Boolean> updatedResponses =
                new ArrayList<>(current.getResponsesAtCurrentIntensity());

        updatedResponses.add(response);

        long positiveResponses = updatedResponses.stream()
                .filter(Boolean::booleanValue)
                .count();

        boolean thresholdFound =
                updatedResponses.size() >= 3 && positiveResponses >= 2;

        if (thresholdFound) {
            return new ThresholdEvaluation(
                    current.getFrequency(),
                    current.getIntensity(),
                    updatedResponses,
                    true,
                    current.getIntensity()
            );
        }

        int nextIntensity = response
                ? AudiometryRules.decreaseAfterResponse(current.getIntensity())
                : AudiometryRules.increaseAfterNoResponse(current.getIntensity());

        nextIntensity = AudiometryRules.clampIntensity(nextIntensity);

        boolean sameIntensity = nextIntensity == current.getIntensity();

        return new ThresholdEvaluation(
                current.getFrequency(),
                nextIntensity,
                sameIntensity ? updatedResponses : List.of(),
                false,
                -1
        );
    }
}
