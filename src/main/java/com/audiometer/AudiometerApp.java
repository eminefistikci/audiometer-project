package com.audiometer;

import com.audiometer.gui.MainWindow;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.audiometer.model.TestSession;
import com.audiometer.model.ThresholdPoint;

public class AudiometerApp {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("System L&F could not be loaded, using default: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}
