package com.audiometer.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class ThresholdPoint {

    private final int frequencyHz;
    private final int thresholdDb;
    private final Ear ear;
    private final String formattedTime;

    public ThresholdPoint(int frequencyHz, int thresholdDb, Ear ear) {
        this(frequencyHz, thresholdDb, ear, LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    public ThresholdPoint(int frequencyHz, int thresholdDb, Ear ear, String formattedTime) {
        this.frequencyHz = frequencyHz;
        this.thresholdDb = thresholdDb;
        this.ear = ear;
        this.formattedTime = formattedTime;
    }

    public int getFrequencyHz()  { return frequencyHz; }
    public int getThresholdDb()  { return thresholdDb; }
    public Ear getEar()          { return ear; }
    public String getFormattedTime() { return formattedTime; }

    @Override
    public String toString() {
        return String.format("ThresholdPoint{freq=%dHz, threshold=%ddB, ear=%s}",
                frequencyHz, thresholdDb, ear);
    }

    public enum Ear {
        RIGHT("Right Ear"),
        LEFT("Left Ear");

        private final String displayName;

        Ear(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }
}
