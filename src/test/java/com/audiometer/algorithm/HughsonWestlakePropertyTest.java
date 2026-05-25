package com.audiometer.algorithm;

import com.audiometer.functional.AudiometryRules;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class HughsonWestlakePropertyTest {

    @Test
    void intensityShouldAlwaysStayWithinMedicalRangeForRandomSteps() {
        Random random = new Random(42);

        for (int i = 0; i < 1000; i++) {
            int randomDb = random.nextInt(181) - 30; // -30 ile 150 arası
            boolean response = random.nextBoolean();

            HughsonWestlakeStep current =
                    new HughsonWestlakeStep(1000, randomDb, response);

            HughsonWestlakeStep next =
                    HughsonWestlakeEngine.nextStep(current);

            assertTrue(next.getIntensity() >= -10);
            assertTrue(next.getIntensity() <= 120);
        }
    }

    @Test
    void validFrequenciesShouldAlwaysBeRecognized() {
        for (int frequency : AudiometryRules.VALID_FREQUENCIES) {
            assertTrue(AudiometryRules.isValidFrequency(frequency));
        }
    }

    @Test
    void invalidRandomFrequenciesShouldBeRejected() {
        Random random = new Random(24);

        for (int i = 0; i < 1000; i++) {
            int frequency = random.nextInt(10000);

            if (!AudiometryRules.VALID_FREQUENCIES.contains(frequency)) {
                assertFalse(AudiometryRules.isValidFrequency(frequency));
            }
        }
    }

    @Test
    void validIntensityValuesShouldBeMultiplesOfFiveWithinRange() {
        for (int db = -10; db <= 120; db += 5) {
            assertTrue(AudiometryRules.isValidIntensity(db));
        }
    }

    @Test
    void randomNonMultipleOfFiveIntensityValuesShouldBeRejected() {
        Random random = new Random(99);

        for (int i = 0; i < 1000; i++) {
            int db = random.nextInt(131) - 10;

            if (db % 5 != 0) {
                assertFalse(AudiometryRules.isValidIntensity(db));
            }
        }
    }
}
