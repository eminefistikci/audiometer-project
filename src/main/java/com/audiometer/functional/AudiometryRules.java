package com.audiometer.functional;

import java.util.List;

public final class AudiometryRules {

    private AudiometryRules() {}

    public static final List<Integer> VALID_FREQUENCIES =
            List.of(250, 500, 1000, 2000, 4000, 8000);

    public static boolean isValidFrequency(int frequencyHz) {
        return VALID_FREQUENCIES.contains(frequencyHz);
    }

    public static boolean isValidIntensity(int intensityDb) {
        return intensityDb >= -10 && intensityDb <= 120 && intensityDb % 5 == 0;
    }

    public static int decreaseAfterResponse(int currentDb) {
        return currentDb - 10;
    }

    public static int increaseAfterNoResponse(int currentDb) {
        return currentDb + 5;
    }

    public static int clampIntensity(int db) {
        return Math.max(-10, Math.min(120, db));
    }
}
