package com.audiometer.algorithm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ThresholdDetectorTest {

    @Test
    void initialThresholdEvaluationShouldStartAtThirtyDb() {
        ThresholdEvaluation initial =
                ThresholdEvaluation.initial(1000);

        assertEquals(1000, initial.getFrequency());
        assertEquals(30, initial.getIntensity());
        assertFalse(initial.isThresholdFound());
        assertEquals(-1, initial.getThresholdDb());
        assertTrue(initial.getResponsesAtCurrentIntensity().isEmpty());
    }

    @Test
    void shouldDecreaseTenDbAfterResponse() {
        ThresholdEvaluation current =
                ThresholdEvaluation.initial(1000);

        ThresholdEvaluation next =
                ThresholdDetector.next(current, true);

        assertEquals(20, next.getIntensity());
        assertFalse(next.isThresholdFound());
    }

    @Test
    void shouldIncreaseFiveDbAfterNoResponse() {
        ThresholdEvaluation current =
                ThresholdEvaluation.initial(1000);

        ThresholdEvaluation next =
                ThresholdDetector.next(current, false);

        assertEquals(35, next.getIntensity());
        assertFalse(next.isThresholdFound());
    }

    @Test
    void shouldDetectThresholdWhenTwoOfThreeResponsesArePositiveAtSameDb() {
        ThresholdEvaluation current =
                new ThresholdEvaluation(
                        1000,
                        40,
                        java.util.List.of(true, false),
                        false,
                        -1
                );

        ThresholdEvaluation next =
                ThresholdDetector.next(current, true);

        assertTrue(next.isThresholdFound());
        assertEquals(40, next.getThresholdDb());
    }

    @Test
    void shouldNotDetectThresholdWhenLessThanTwoPositiveResponsesExist() {
        ThresholdEvaluation current =
                new ThresholdEvaluation(
                        1000,
                        40,
                        java.util.List.of(false, false),
                        false,
                        -1
                );

        ThresholdEvaluation next =
                ThresholdDetector.next(current, true);

        assertFalse(next.isThresholdFound());
    }
}
