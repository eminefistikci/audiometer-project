package com.audiometer.algorithm;

public final class HughsonWestlakeStep {

    private final int frequency;
    private final int intensity;
    private final boolean response;

    public HughsonWestlakeStep(int frequency, int intensity, boolean response) {
        this.frequency = frequency;
        this.intensity = intensity;
        this.response = response;
    }

    public int getFrequency() {
        return frequency;
    }

    public int getIntensity() {
        return intensity;
    }

    public boolean hasResponse() {
        return response;
    }

    @Override
    public String toString() {
        return "Step{" +
                "frequency=" + frequency +
                ", intensity=" + intensity +
                ", response=" + response +
                '}';
    }
}
