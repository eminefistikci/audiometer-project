package com.audiometer.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ModernButton extends JButton {
    private final Color normalColor;
    private final Color hoverColor;
    private final Color pressedColor;
    private final int cornerRadius;

    public ModernButton(String text, Color bg, Color fg) {
        this(text, bg, fg, 10);
    }

    public ModernButton(String text, Color bg, Color fg, int cornerRadius) {
        super(text);
        this.normalColor = bg;
        this.hoverColor = adjustColor(bg, 1.15f);
        this.pressedColor = adjustColor(bg, 0.85f);
        this.cornerRadius = cornerRadius;

        setBackground(bg);
        setForeground(fg);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setFont(new Font("Segoe UI", Font.BOLD, 12));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                if (isEnabled()) {
                    setBackground(hoverColor);
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                if (isEnabled()) {
                    setBackground(normalColor);
                    repaint();
                }
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                if (isEnabled() && SwingUtilities.isLeftMouseButton(evt)) {
                    setBackground(pressedColor);
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                if (isEnabled() && SwingUtilities.isLeftMouseButton(evt)) {
                    if (getBounds().contains(evt.getPoint())) {
                        setBackground(hoverColor);
                    } else {
                        setBackground(normalColor);
                    }
                    repaint();
                }
            }
        });
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (!isEnabled()) {
            g2.setColor(new Color(218, 224, 233));
        } else {
            g2.setColor(getBackground());
        }

        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(getText())) / 2;
        int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

        if (!isEnabled()) {
            g2.setColor(new Color(150, 160, 175));
        } else {
            g2.setColor(getForeground());
        }
        g2.drawString(getText(), x, y);
        g2.dispose();
    }

    private Color adjustColor(Color color, float factor) {
        int r = Math.min(255, Math.max(0, (int) (color.getRed() * factor)));
        int g = Math.min(255, Math.max(0, (int) (color.getGreen() * factor)));
        int b = Math.min(255, Math.max(0, (int) (color.getBlue() * factor)));
        return new Color(r, g, b, color.getAlpha());
    }
}
