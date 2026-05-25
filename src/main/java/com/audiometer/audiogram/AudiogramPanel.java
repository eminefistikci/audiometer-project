package com.audiometer.audiogram;

import com.audiometer.model.TestSession;
import com.audiometer.model.ThresholdPoint;
import com.audiometer.gui.RoundedBorder;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.List;

public class AudiogramPanel extends JPanel implements TestSession.TestSessionListener {

    private static final int MARGIN_LEFT   = 75;
    private static final int MARGIN_RIGHT  = 40;
    private static final int MARGIN_TOP    = 45;
    private static final int MARGIN_BOTTOM = 55;

    private static final int[] FREQUENCIES = {250, 500, 1000, 2000, 4000, 8000};

    private static final int DB_MIN = -10;
    private static final int DB_MAX = 120;
    private static final int DB_STEP = 10;

    private static final int SYMBOL_SIZE = 12;

    private static final Color COLOR_RIGHT_EAR = new Color(239, 68, 68); // Modern Crimson
    private static final Color COLOR_LEFT_EAR  = new Color(37, 99, 235);  // Modern Cobalt Blue
    private static final Color COLOR_GRID      = new Color(226, 232, 240);  // Slate 200
    private static final Color COLOR_NORMAL_ZONE = new Color(240, 253, 244, 180); // Soft green tint

    private final TestSession session;

    public AudiogramPanel(TestSession session) {
        this.session = session;
        session.addListener(this);

        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(700, 450));
        setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(226, 232, 240), 12, 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawNormalZone(g2);
        drawGrid(g2);
        drawAxes(g2);
        drawAxisLabels(g2);
        drawDataPoints(g2);
        drawLegend(g2);
    }

    private void drawNormalZone(Graphics2D g2) {
        int y0  = dbToY(0);
        int y25 = dbToY(25);
        int x0  = MARGIN_LEFT;
        int x1  = getWidth() - MARGIN_RIGHT;

        g2.setColor(COLOR_NORMAL_ZONE);
        g2.fillRect(x0, y0, x1 - x0, y25 - y0);

        g2.setColor(new Color(22, 163, 74, 200)); // Darker green
        g2.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 10));
        g2.drawString("Normal Hearing Range (0 - 25 dB)", x0 + 8, y25 - 6);
    }

    private void drawGrid(Graphics2D g2) {
        for (int db = DB_MIN; db <= DB_MAX; db += DB_STEP) {
            int y = dbToY(db);
            if (db == 0) {
                g2.setStroke(new BasicStroke(1.5f));
                g2.setColor(new Color(148, 163, 184)); // Highlight 0 dB line (Slate 400)
            } else {
                g2.setStroke(new BasicStroke(1.0f));
                g2.setColor(COLOR_GRID);
            }
            g2.draw(new Line2D.Float(MARGIN_LEFT, y, getWidth() - MARGIN_RIGHT, y));
        }

        g2.setStroke(new BasicStroke(1.0f));
        g2.setColor(COLOR_GRID);
        for (int freq : FREQUENCIES) {
            int x = freqToX(freq);
            g2.draw(new Line2D.Float(x, MARGIN_TOP, x, getHeight() - MARGIN_BOTTOM));
        }
    }

    private void drawAxes(Graphics2D g2) {
        g2.setColor(new Color(148, 163, 184)); // Modern Slate 400
        g2.setStroke(new BasicStroke(1.5f));
        
        // Draw solid enclosing box for the graph area
        int w = getWidth() - MARGIN_LEFT - MARGIN_RIGHT;
        int h = getHeight() - MARGIN_TOP - MARGIN_BOTTOM;
        g2.drawRect(MARGIN_LEFT, MARGIN_TOP, w, h);
    }

    private void drawAxisLabels(Graphics2D g2) {
        g2.setColor(new Color(71, 85, 105)); // Slate 700
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        FontMetrics fm = g2.getFontMetrics();

        // Draw frequency labels (X)
        for (int freq : FREQUENCIES) {
            int x = freqToX(freq);
            String label = freq >= 1000
                    ? (freq / 1000) + " kHz"
                    : freq + " Hz";
            int labelW = fm.stringWidth(label);
            g2.drawString(label, x - labelW / 2, getHeight() - MARGIN_BOTTOM + 20);
        }

        // Draw X Axis Title
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        String xTitle = "Frequency (Hz / kHz)";
        int xTitleW = fm.stringWidth(xTitle);
        g2.drawString(xTitle, MARGIN_LEFT + (getWidth() - MARGIN_LEFT - MARGIN_RIGHT - xTitleW) / 2, getHeight() - 10);

        // Draw Hearing Level labels (Y)
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        for (int db = DB_MIN; db <= DB_MAX; db += DB_STEP) {
            int y = dbToY(db);
            String label = String.valueOf(db);
            int labelW = fm.stringWidth(label);
            g2.drawString(label, MARGIN_LEFT - labelW - 8, y + fm.getAscent() / 2 - 1);
        }

        // Draw Y Axis Title (Rotated)
        Graphics2D g2r = (Graphics2D) g2.create();
        g2r.rotate(-Math.PI / 2);
        g2r.setFont(new Font("Segoe UI", Font.BOLD, 12));
        String yTitle = "Hearing Level (dB HL)";
        int yTitleW = fm.stringWidth(yTitle);
        g2r.drawString(yTitle, -(MARGIN_TOP + (getHeight() - MARGIN_TOP - MARGIN_BOTTOM + yTitleW) / 2), 22);
        g2r.dispose();
    }

    private void drawDataPoints(Graphics2D g2) {
        List<ThresholdPoint> points = session.getThresholds();

        drawConnectingLines(g2, points, ThresholdPoint.Ear.RIGHT, COLOR_RIGHT_EAR);
        drawConnectingLines(g2, points, ThresholdPoint.Ear.LEFT,  COLOR_LEFT_EAR);

        for (ThresholdPoint point : points) {
            int x = freqToX(point.getFrequencyHz());
            int y = dbToY(point.getThresholdDb());

            if (point.getEar() == ThresholdPoint.Ear.RIGHT) {
                drawCircleSymbol(g2, x, y, COLOR_RIGHT_EAR);
            } else {
                drawXSymbol(g2, x, y, COLOR_LEFT_EAR);
            }
        }
    }

    private void drawConnectingLines(Graphics2D g2, List<ThresholdPoint> points,
                                      ThresholdPoint.Ear ear, Color color) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(2.0f));

        ThresholdPoint prev = null;
        for (ThresholdPoint point : points) {
            if (point.getEar() != ear) continue;

            if (prev != null) {
                int x1 = freqToX(prev.getFrequencyHz());
                int y1 = dbToY(prev.getThresholdDb());
                int x2 = freqToX(point.getFrequencyHz());
                int y2 = dbToY(point.getThresholdDb());
                g2.drawLine(x1, y1, x2, y2);
            }
            prev = point;
        }
    }

    private void drawCircleSymbol(Graphics2D g2, int cx, int cy, Color color) {
        int r = SYMBOL_SIZE;
        g2.setColor(color);
        g2.setStroke(new BasicStroke(3.0f));
        g2.drawOval(cx - r / 2, cy - r / 2, r, r);
    }

    private void drawXSymbol(Graphics2D g2, int cx, int cy, Color color) {
        int r = SYMBOL_SIZE / 2;
        g2.setColor(color);
        g2.setStroke(new BasicStroke(3.0f));
        g2.drawLine(cx - r, cy - r, cx + r, cy + r);
        g2.drawLine(cx + r, cy - r, cx - r, cy + r);
    }

    private void drawLegend(Graphics2D g2) {
        int lx = getWidth() - MARGIN_RIGHT - 140;
        int ly = MARGIN_TOP + 15;

        g2.setColor(new Color(255, 255, 255, 240));
        g2.fillRoundRect(lx - 8, ly - 8, 135, 60, 10, 10);
        g2.setColor(new Color(226, 232, 240));
        g2.setStroke(new BasicStroke(1.0f));
        g2.drawRoundRect(lx - 8, ly - 8, 135, 60, 10, 10);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));

        drawCircleSymbol(g2, lx + 12, ly + 10, COLOR_RIGHT_EAR);
        g2.setColor(COLOR_RIGHT_EAR);
        g2.drawString("Right Ear (O)", lx + 28, ly + 14);

        drawXSymbol(g2, lx + 12, ly + 34, COLOR_LEFT_EAR);
        g2.setColor(COLOR_LEFT_EAR);
        g2.drawString("Left Ear (X)", lx + 28, ly + 38);
    }

    private int freqToX(int freqHz) {
        int chartWidth = getWidth() - MARGIN_LEFT - MARGIN_RIGHT;

        double logMin = Math.log10(FREQUENCIES[0]);
        double logMax = Math.log10(FREQUENCIES[FREQUENCIES.length - 1]);
        double logFreq = Math.log10(freqHz);

        double ratio = (logFreq - logMin) / (logMax - logMin);
        return MARGIN_LEFT + (int) (ratio * chartWidth);
    }

    private int dbToY(int db) {
        int chartHeight = getHeight() - MARGIN_TOP - MARGIN_BOTTOM;
        double ratio = (double)(db - DB_MIN) / (DB_MAX - DB_MIN);
        return MARGIN_TOP + (int)(ratio * chartHeight);
    }

    @Override
    public void onThresholdAdded(ThresholdPoint point) {
        SwingUtilities.invokeLater(this::repaint);
    }

    @Override
    public void onSessionCleared() {
        SwingUtilities.invokeLater(this::repaint);
    }
}
