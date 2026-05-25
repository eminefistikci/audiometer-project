package com.audiometer.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestSession {

    private final List<ThresholdPoint> thresholds = new ArrayList<>();
    private final List<TestSessionListener> listeners = new ArrayList<>();
    private ThresholdPoint.Ear currentEar = ThresholdPoint.Ear.RIGHT;
    private int currentFrequencyHz = 1000;
    private int currentIntensityDb = 40;
    private boolean testRunning = false;

    public void addThreshold(ThresholdPoint point) {
        thresholds.removeIf(p -> p.getEar() == point.getEar() && p.getFrequencyHz() == point.getFrequencyHz());
        thresholds.add(point);
        thresholds.sort((p1, p2) -> Integer.compare(p1.getFrequencyHz(), p2.getFrequencyHz()));
        for (TestSessionListener listener : listeners) {
            listener.onThresholdAdded(point);
        }
    }

    public void clearAll() {
        thresholds.clear();
        for (TestSessionListener listener : listeners) {
            listener.onSessionCleared();
        }
    }

    public List<ThresholdPoint> getThresholds() {
        return Collections.unmodifiableList(thresholds);
    }

    public void addListener(TestSessionListener listener) {
        listeners.add(listener);
    }

    public ThresholdPoint.Ear getCurrentEar()       { return currentEar; }
    public void setCurrentEar(ThresholdPoint.Ear e) { this.currentEar = e; }

    public int getCurrentFrequencyHz()              { return currentFrequencyHz; }
    public void setCurrentFrequencyHz(int f)        { this.currentFrequencyHz = f; }

    public int getCurrentIntensityDb()              { return currentIntensityDb; }
    public void setCurrentIntensityDb(int db)       { this.currentIntensityDb = db; }

    public boolean isTestRunning()                  { return testRunning; }
    public void setTestRunning(boolean running)     { this.testRunning = running; }

    public interface TestSessionListener {
        void onThresholdAdded(ThresholdPoint point);
        void onSessionCleared();
    }
}
