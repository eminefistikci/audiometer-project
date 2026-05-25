package com.audiometer.functional;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AudiometryRulesTest {

    @Test
    void validFrequenciesShouldBeAccepted() {
        assertTrue(AudiometryRules.isValidFrequency(250));
        assertTrue(AudiometryRules.isValidFrequency(1000));
        assertTrue(AudiometryRules.isValidFrequency(8000));
    }

    @Test
    void invalidFrequenciesShouldBeRejected() {
        assertFalse(AudiometryRules.isValidFrequency(300));
        assertFalse(AudiometryRules.isValidFrequency(9000));
    }

    @Test
    void intensityShouldBeBetweenMinus10And120AndMultipleOfFive() {
        assertTrue(AudiometryRules.isValidIntensity(-10));
        assertTrue(AudiometryRules.isValidIntensity(40));
        assertTrue(AudiometryRules.isValidIntensity(120));

        assertFalse(AudiometryRules.isValidIntensity(-15));
        assertFalse(AudiometryRules.isValidIntensity(125));
        assertFalse(AudiometryRules.isValidIntensity(42));
    }

    @Test
    void hughsonWestlakeDbRulesShouldWork() {
        assertEquals(30, AudiometryRules.decreaseAfterResponse(40));
        assertEquals(45, AudiometryRules.increaseAfterNoResponse(40));
    }

    @Test
    void intensityShouldBeClampedToMedicalRange() {
        assertEquals(-10, AudiometryRules.clampIntensity(-50));
        assertEquals(60, AudiometryRules.clampIntensity(60));
        assertEquals(120, AudiometryRules.clampIntensity(140));
    }
}
