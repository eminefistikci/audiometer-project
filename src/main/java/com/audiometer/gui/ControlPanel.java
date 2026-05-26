package com.audiometer.gui;

import com.audiometer.model.TestSession;
import com.audiometer.model.ThresholdPoint;
import com.audiometer.serial.SerialManager;
import com.audiometer.functional.AudiometryRules;
import com.audiometer.algorithm.HughsonWestlakeEngine;
import com.audiometer.algorithm.HughsonWestlakeStep;
import com.audiometer.algorithm.ThresholdDetector;
import com.audiometer.algorithm.ThresholdEvaluation;
import com.audiometer.algorithm.FrequencyCycle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ControlPanel extends JPanel {

    private static final int[] STANDARD_FREQUENCIES = { 250, 500, 1000, 2000, 4000, 8000 };

    private static final int DB_MIN = -10;
    private static final int DB_MAX = 120;
    private static final int DB_STEP = 5;

    private final JRadioButton radioRight;
    private final JRadioButton radioLeft;
    private final JComboBox<String> comboFrequency;
    private final JSlider sliderIntensity;
    private final JLabel labelIntensityValue;
    private final JButton btnSendTone;
    private final JButton btnStop;
    private final JButton btnSaveThreshold;
    private final JButton btnAutoTest;
    private final JLabel labelStatus;

    private final TestSession session;
    private final SerialManager serialManager;

    private int lastResponseFrequency = -1;
    private int lastResponseIntensity = -1;
    private ThresholdPoint.Ear lastResponseEar = null;
    private Timer autoTestTimer;
    private boolean autoTestRunning = false;
    private ThresholdEvaluation currentEvaluation;
    private ThresholdPoint.Ear autoTestEar = ThresholdPoint.Ear.RIGHT;
    private boolean autoPatientResponded = false;

    public ControlPanel(TestSession session, SerialManager serialManager) {
        this.session = session;
        this.serialManager = serialManager;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setPreferredSize(new Dimension(260, 530));

        // --- Ear Selection Card ---
        JPanel earPanel = createCardPanel("Ear Selection");
        radioRight = new JRadioButton("Right Ear (O)", true);
        radioLeft  = new JRadioButton("Left Ear (X)");
        radioRight.setOpaque(false);
        radioLeft.setOpaque(false);
        radioRight.setFocusPainted(false);
        radioLeft.setFocusPainted(false);
        radioRight.setFont(new Font("Segoe UI", Font.BOLD, 12));
        radioLeft.setFont(new Font("Segoe UI", Font.BOLD, 12));
        radioRight.setForeground(new Color(220, 38, 38)); // Modern Crimson
        radioLeft.setForeground(new Color(37, 99, 235)); // Modern Blue

        ButtonGroup earGroup = new ButtonGroup();
        earGroup.add(radioRight);
        earGroup.add(radioLeft);
        
        JPanel radioContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0)); // Center radio buttons
        radioContainer.setOpaque(false);
        radioContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        radioContainer.add(radioRight);
        radioContainer.add(radioLeft);
        earPanel.add(radioContainer);

        radioRight.addActionListener(e -> session.setCurrentEar(ThresholdPoint.Ear.RIGHT));
        radioLeft.addActionListener(e -> session.setCurrentEar(ThresholdPoint.Ear.LEFT));

        // --- Frequency Card ---
        JPanel freqPanel = createCardPanel("Frequency");
        String[] freqLabels = {"250 Hz", "500 Hz", "1000 Hz", "2000 Hz", "4000 Hz", "8000 Hz"};
        comboFrequency = new JComboBox<>(freqLabels);
        comboFrequency.setSelectedIndex(2);
        comboFrequency.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        comboFrequency.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboFrequency.setBackground(Color.WHITE);
        comboFrequency.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1));
        comboFrequency.setAlignmentX(Component.CENTER_ALIGNMENT); // Center combo box
        comboFrequency.addActionListener(e -> {
            int freq = STANDARD_FREQUENCIES[comboFrequency.getSelectedIndex()];
            session.setCurrentFrequencyHz(freq);
        });
        freqPanel.add(comboFrequency);

        // --- Intensity Card ---
        JPanel intPanel = createCardPanel("Intensity (dB HL)");

        sliderIntensity = new JSlider(JSlider.VERTICAL, DB_MIN, DB_MAX, 40);
        sliderIntensity.setOpaque(false);
        sliderIntensity.setMajorTickSpacing(20);
        sliderIntensity.setMinorTickSpacing(5);
        sliderIntensity.setPaintTicks(true);
        sliderIntensity.setPaintLabels(true);
        sliderIntensity.setInverted(true);
        sliderIntensity.setPreferredSize(new Dimension(100, 210));
        sliderIntensity.setMinimumSize(new Dimension(100, 160)); // Prevent squishing
        sliderIntensity.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        sliderIntensity.setForeground(new Color(100, 116, 139));
        sliderIntensity.setUI(new ModernSliderUI(sliderIntensity));
        sliderIntensity.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

        labelIntensityValue = new JLabel("40 dB HL", SwingConstants.CENTER);
        labelIntensityValue.setFont(new Font("Segoe UI", Font.BOLD, 15));
        labelIntensityValue.setForeground(new Color(30, 41, 59));
        labelIntensityValue.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        sliderIntensity.addChangeListener(e -> {
            int db = sliderIntensity.getValue();
            db = Math.round((float) db / DB_STEP) * DB_STEP;
            sliderIntensity.setValue(db);
            labelIntensityValue.setText(db + " dB HL");
            session.setCurrentIntensityDb(db);
        });

        intPanel.setLayout(new BorderLayout());
        intPanel.add(labelIntensityValue, BorderLayout.NORTH);
        intPanel.add(sliderIntensity, BorderLayout.CENTER);
        intPanel.setMinimumSize(new Dimension(240, 220)); // Ensure card stays tall

        JPanel quickDbPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        quickDbPanel.setOpaque(false);
        JButton btnDbUp   = new ModernButton("+5 dB", new Color(241, 245, 249), new Color(71, 85, 105), 6);
        JButton btnDbDown = new ModernButton("-5 dB", new Color(241, 245, 249), new Color(71, 85, 105), 6);
        btnDbUp.setPreferredSize(new Dimension(65, 26));
        btnDbDown.setPreferredSize(new Dimension(65, 26));
        btnDbUp.addActionListener(e -> adjustIntensity(+5));
        btnDbDown.addActionListener(e -> adjustIntensity(-5));
        quickDbPanel.add(btnDbDown);
        quickDbPanel.add(btnDbUp);
        intPanel.add(quickDbPanel, BorderLayout.SOUTH);

        // --- Action Control Card ---
        JPanel btnPanel = createCardPanel("Controls");
        btnSendTone = new ModernButton("Send Tone", new Color(34, 197, 94), Color.WHITE, 8);
        btnStop = new ModernButton("Stop Tone", new Color(239, 68, 68), Color.WHITE, 8);
        btnSaveThreshold = new ModernButton("Save Threshold", new Color(59, 130, 246), Color.WHITE, 8);
        btnSaveThreshold.setEnabled(false);
        btnAutoTest = new ModernButton("Auto Threshold Test", new Color(168, 85, 247), Color.WHITE, 8);

        Dimension btnSize = new Dimension(220, 35);
        btnSendTone.setMaximumSize(btnSize);
        btnStop.setMaximumSize(btnSize);
        btnSaveThreshold.setMaximumSize(btnSize);
        btnAutoTest.setMaximumSize(btnSize);
        btnSendTone.setPreferredSize(btnSize);
        btnStop.setPreferredSize(btnSize);
        btnSaveThreshold.setPreferredSize(btnSize);
        btnAutoTest.setPreferredSize(btnSize);


        btnSendTone.addActionListener(this::onSendTone);
        btnStop.addActionListener(this::onStop);
        btnSaveThreshold.addActionListener(this::onSaveThreshold);
        btnAutoTest.addActionListener(this::onAutoTest);

        btnSendTone.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnStop.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSaveThreshold.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAutoTest.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
        btnPanel.add(btnSendTone);
        btnPanel.add(Box.createVerticalStrut(4));
        btnPanel.add(btnStop);
        btnPanel.add(Box.createVerticalStrut(4));
        btnPanel.add(btnSaveThreshold);
        btnPanel.add(Box.createVerticalStrut(4));
        btnPanel.add(btnAutoTest);

        labelStatus = new JLabel("System Ready", SwingConstants.CENTER);
        labelStatus.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 11));
        labelStatus.setForeground(new Color(148, 163, 184));
        labelStatus.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        labelStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(earPanel);
        add(Box.createVerticalStrut(6)); // Reduced spacing to give slider more height
        add(freqPanel);
        add(Box.createVerticalStrut(6));
        add(intPanel);
        add(Box.createVerticalStrut(6));
        add(btnPanel);
        add(Box.createVerticalStrut(4));
        add(labelStatus);

        serialManager.addResponseListener(this::onResponseReceived);
    }

    private void onSendTone(ActionEvent e) {
        if (!serialManager.isConnected()) {
            setStatus("⚠ Port not connected!", new Color(239, 68, 68));
            return;
        }

        int freq = STANDARD_FREQUENCIES[comboFrequency.getSelectedIndex()];
        int db = sliderIntensity.getValue();

        if (!AudiometryRules.isValidFrequency(freq)) {
            setStatus("Invalid frequency!", new Color(239, 68, 68));
            return;
        }

        if (!AudiometryRules.isValidIntensity(db)) {
            setStatus("Invalid intensity!", new Color(239, 68, 68));
            return;
        }

        boolean sent = serialManager.sendToneCommand(freq, db);
        
        
        if (sent) {
            setStatus(String.format("Tone: %d Hz @ %d dB sent", freq, db), new Color(22, 163, 74));
            session.setTestRunning(true);
            btnSaveThreshold.setEnabled(false);
            lastResponseFrequency = -1;
            lastResponseIntensity = -1;
            lastResponseEar = null;
        } else {
            setStatus("Command failed to send!", new Color(239, 68, 68));
        }
    }

    private void onStop(ActionEvent e) {
        serialManager.sendStopCommand();
        session.setTestRunning(false);
        setStatus("Tone stopped.", new Color(100, 116, 139));
    }

    public void onResponseReceived() {
        if (autoTestRunning) {
            autoPatientResponded = true;
        }
        lastResponseFrequency = STANDARD_FREQUENCIES[comboFrequency.getSelectedIndex()];
        lastResponseIntensity = sliderIntensity.getValue();
        lastResponseEar = radioRight.isSelected() ? ThresholdPoint.Ear.RIGHT : ThresholdPoint.Ear.LEFT;

        SwingUtilities.invokeLater(() -> {
            setStatus("Patient responded!", new Color(22, 163, 74));
            btnSaveThreshold.setEnabled(true);
        });
    }

    private void onSaveThreshold(ActionEvent e) {
        if (lastResponseFrequency < 0 || lastResponseEar == null)
            return;

        ThresholdPoint point = new ThresholdPoint(lastResponseFrequency, lastResponseIntensity, lastResponseEar);
        session.addThreshold(point);

        setStatus(String.format("Saved: %d Hz @ %d dB HL (%s)",
                lastResponseFrequency, lastResponseIntensity, lastResponseEar.getDisplayName()),
                new Color(37, 99, 235));

        btnSaveThreshold.setEnabled(false);
        lastResponseFrequency = -1;
        lastResponseIntensity = -1;
        lastResponseEar = null;
    }

    private void onAutoTest(ActionEvent e) {
    if (autoTestRunning) {
        stopAutoTest();
    } else {
        startAutoTest();
    }
}

private void startAutoTest() {
    if (!serialManager.isConnected()) {
        setStatus("⚠ Port not connected!", new Color(239, 68, 68));
        return;
    }

    autoTestRunning = true;
    autoPatientResponded = false;

    int startingFrequency = 1000;

    currentEvaluation =
        ThresholdEvaluation.initial(startingFrequency);

    autoTestEar =
        radioRight.isSelected()
                ? ThresholdPoint.Ear.RIGHT
                : ThresholdPoint.Ear.LEFT;

    btnAutoTest.setText("Stop Auto Test");
    btnSendTone.setEnabled(false);
    btnSaveThreshold.setEnabled(false);

    setStatus("Auto test started...", new Color(168, 85, 247));

    autoTestTimer = new Timer(2500, event -> runAutoTestStep());
    autoTestTimer.setInitialDelay(0);
    autoTestTimer.start();
}

private void runAutoTestStep() {
    if (!autoTestRunning) {
        return;
    }

    sliderIntensity.setValue(currentEvaluation.getIntensity());

    session.setCurrentFrequencyHz(currentEvaluation.getFrequency());
    session.setCurrentIntensityDb(currentEvaluation.getIntensity());
    session.setCurrentEar(autoTestEar);

    boolean sent =
        serialManager.sendToneCommand(
                currentEvaluation.getFrequency(),
                currentEvaluation.getIntensity()
        );

    if (!sent) {
        stopAutoTest();
        setStatus("Auto test command failed!", new Color(239, 68, 68));
        return;
    }

    setStatus(
        String.format(
                "Auto: %d Hz @ %d dB",
                currentEvaluation.getFrequency(),
                currentEvaluation.getIntensity()
        ),
        new Color(168, 85, 247)
);

currentEvaluation =
        ThresholdDetector.next(
                currentEvaluation,
                autoPatientResponded
        );

if (currentEvaluation.isThresholdFound()) {

    ThresholdPoint point =
            new ThresholdPoint(
                    currentEvaluation.getFrequency(),
                    currentEvaluation.getThresholdDb(),
                    autoTestEar
            );

    session.addThreshold(point);

    int nextFrequency =
            FrequencyCycle.nextFrequency(
                    currentEvaluation.getFrequency()
            );

    if (nextFrequency == -1) {
        stopAutoTest();

        setStatus(
                "Automatic audiometry completed.",
                new Color(22, 163, 74)
        );

        return;
    }

    currentEvaluation =
            ThresholdEvaluation.initial(nextFrequency);
}

    autoPatientResponded = false;
}

private void stopAutoTest() {
    autoTestRunning = false;

    if (autoTestTimer != null) {
        autoTestTimer.stop();
    }

    serialManager.sendStopCommand();

    btnAutoTest.setText("Auto Threshold Test");
    btnSendTone.setEnabled(true);

    setStatus("Auto test stopped.", new Color(100, 116, 139));
}

    private void adjustIntensity(int delta) {
        int newVal = Math.max(DB_MIN, Math.min(DB_MAX, sliderIntensity.getValue() + delta));
        sliderIntensity.setValue(newVal);
    }

    private void setStatus(String message, Color color) {
        labelStatus.setText(message);
        labelStatus.setForeground(color);
    }

    private JPanel createCardPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(226, 232, 240), 12, 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12) // Thinner padding to free up space
        ));

        if (title != null && !title.isEmpty()) {
            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblTitle.setForeground(new Color(100, 116, 139));
            lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT); // Centered horizontally
            lblTitle.setHorizontalAlignment(SwingConstants.CENTER); // Centered text
            lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
            panel.add(lblTitle);
        }
        return panel;
    }


    private static class ModernSliderUI extends javax.swing.plaf.basic.BasicSliderUI {
        public ModernSliderUI(JSlider b) { super(b); }

        @Override
        public void paintTrack(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(226, 232, 240));

            int width = 8;
            int x = trackRect.x + (trackRect.width - width) / 2;
            g2.fillRoundRect(x, trackRect.y, width, trackRect.height, 4, 4);

            int thumbY = thumbRect.y + thumbRect.height / 2;
            g2.setColor(new Color(59, 130, 246));
            g2.fillRoundRect(x, trackRect.y, width, thumbY - trackRect.y, 4, 4);
            g2.dispose();
        }

        @Override
        public void paintThumb(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(59, 130, 246));
            g2.fillOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);

            g2.setColor(new Color(29, 78, 216));
            g2.setStroke(new BasicStroke(1f));
            g2.drawOval(thumbRect.x, thumbRect.y, thumbRect.width - 1, thumbRect.height - 1);

            g2.setColor(Color.WHITE);
            int size = 6;
            g2.fillOval(thumbRect.x + (thumbRect.width - size) / 2, thumbRect.y + (thumbRect.height - size) / 2, size, size);
            g2.dispose();
        }

        @Override
        protected Dimension getThumbSize() { return new Dimension(16, 16); }
    }
}
