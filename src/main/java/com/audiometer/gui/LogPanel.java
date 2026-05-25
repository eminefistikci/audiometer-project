package com.audiometer.gui;

import com.audiometer.model.TestSession;
import com.audiometer.model.ThresholdPoint;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LogPanel extends JPanel implements TestSession.TestSessionListener {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final TestSession session;
    private final DefaultTableModel tableModel;
    private final JTable thresholdTable;
    private final JTextArea logArea;

    public LogPanel(TestSession session) {
        this.session = session;
        session.addListener(this);

        setLayout(new BorderLayout(0, 8));
        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createEmptyBorder(6, 12, 12, 12));
        setPreferredSize(new Dimension(0, 140));

        String[] columns = {"Frequency (Hz)", "Threshold (dB HL)", "Ear", "Recorded Time"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        
        thresholdTable = new JTable(tableModel);
        thresholdTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        thresholdTable.setRowHeight(26);
        thresholdTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        thresholdTable.setGridColor(new Color(226, 232, 240));
        thresholdTable.setShowHorizontalLines(true);
        thresholdTable.setShowVerticalLines(false);

        // Header Styling
        thresholdTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        thresholdTable.getTableHeader().setBackground(new Color(241, 245, 249));
        thresholdTable.getTableHeader().setForeground(new Color(71, 85, 105));
        thresholdTable.getTableHeader().setReorderingAllowed(false);
        thresholdTable.getTableHeader().setResizingAllowed(true);

        JScrollPane tableScroll = new JScrollPane(thresholdTable);
        tableScroll.getViewport().setBackground(Color.WHITE);
        tableScroll.setBackground(Color.WHITE);
        tableScroll.setBorder(BorderFactory.createTitledBorder(
                new RoundedBorder(new Color(226, 232, 240), 12, 1),
                "Recorded Thresholds",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 11),
                new Color(71, 85, 105)
        ));

        // Log Area (Console Style)
        logArea = new JTextArea(5, 0);
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logArea.setBackground(new Color(15, 23, 42)); // Slate 900 (Dark Console)
        logArea.setForeground(new Color(148, 163, 184)); // Slate 400 text
        logArea.setMargin(new Insets(6, 8, 6, 8));

        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBackground(new Color(15, 23, 42));
        logScroll.getViewport().setBackground(new Color(15, 23, 42));
        logScroll.setBorder(BorderFactory.createTitledBorder(
                new RoundedBorder(new Color(226, 232, 240), 12, 1),
                "System Log",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 11),
                new Color(71, 85, 105)
        ));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScroll, logScroll);
        splitPane.setBackground(new Color(248, 250, 252));
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(6);
        splitPane.setBorder(null);

        add(splitPane, BorderLayout.CENTER);

        appendLog("System initialized. Select COM port and click 'Connect'.");
    }

    public void appendLog(String message) {
        String timestamped = String.format("[%s] %s%n",
                LocalTime.now().format(TIME_FORMAT), message);

        if (SwingUtilities.isEventDispatchThread()) {
            logArea.append(timestamped);
            logArea.setCaretPosition(logArea.getDocument().getLength());
        } else {
            SwingUtilities.invokeLater(() -> {
                logArea.append(timestamped);
                logArea.setCaretPosition(logArea.getDocument().getLength());
            });
        }
    }

    @Override
    public void onThresholdAdded(ThresholdPoint point) {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
            for (ThresholdPoint p : session.getThresholds()) {
                tableModel.addRow(new Object[]{
                        p.getFrequencyHz() + " Hz",
                        p.getThresholdDb() + " dB HL",
                        p.getEar().getDisplayName(),
                        p.getFormattedTime()
                });
            }

            appendLog(String.format("Threshold saved -> %d Hz @ %d dB HL (%s)",
                    point.getFrequencyHz(), point.getThresholdDb(), point.getEar().getDisplayName()));
        });
    }

    @Override
    public void onSessionCleared() {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
            appendLog("Session reset. All threshold data cleared.");
        });
    }
}
