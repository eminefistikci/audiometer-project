package com.audiometer.algorithm;

import com.audiometer.functional.AudiometryRules;

public final class HughsonWestlakeEngine {

    private HughsonWestlakeEngine() {}

    public static HughsonWestlakeStep nextStep(HughsonWestlakeStep currentStep) {

        int nextIntensity = currentStep.hasResponse()
                ? AudiometryRules.decreaseAfterResponse(currentStep.getIntensity())
                : AudiometryRules.increaseAfterNoResponse(currentStep.getIntensity());

        nextIntensity = AudiometryRules.clampIntensity(nextIntensity);

        return new HughsonWestlakeStep(
                currentStep.getFrequency(),
                nextIntensity,
                false
        );
    }
}
