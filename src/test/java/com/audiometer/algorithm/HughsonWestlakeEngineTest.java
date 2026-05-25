package com.audiometer.algorithm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HughsonWestlakeEngineTest {

    @Test
    void shouldDecreaseTenDbAfterPatientResponse() {
        HughsonWestlakeStep current =
                new HughsonWestlakeStep(1000, 40, true);

        HughsonWestlakeStep next =
                HughsonWestlakeEngine.nextStep(current);

        assertEquals(1000, next.getFrequency());
        assertEquals(30, next.getIntensity());
        assertFalse(next.hasResponse());
    }

    @Test
    void shouldIncreaseFiveDbAfterNoResponse() {
        HughsonWestlakeStep current =
                new HughsonWestlakeStep(1000, 40, false);

        HughsonWestlakeStep next =
                HughsonWestlakeEngine.nextStep(current);

        assertEquals(45, next.getIntensity());
    }

    @Test
    void shouldNotExceedMaximumIntensity() {
        HughsonWestlakeStep current =
                new HughsonWestlakeStep(1000, 120, false);

        HughsonWestlakeStep next =
                HughsonWestlakeEngine.nextStep(current);

        assertEquals(120, next.getIntensity());
    }

    @Test
    void shouldNotGoBelowMinimumIntensity() {
        HughsonWestlakeStep current =
                new HughsonWestlakeStep(1000, -10, true);

        HughsonWestlakeStep next =
                HughsonWestlakeEngine.nextStep(current);

        assertEquals(-10, next.getIntensity());
    }
}
