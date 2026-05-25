package com.audiometer.gui;

import com.audiometer.audiogram.AudiogramPanel;
import com.audiometer.model.TestSession;
import com.audiometer.serial.SerialManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainWindow extends JFrame {
    private final TestSession session = new TestSession();

    public MainWindow() {
        super("Audiometer Test System — Ankara University");

        SerialManager serial = new SerialManager();

        ConnectionPanel connectionPanel = new ConnectionPanel(serial);
        LogPanel logPanel = new LogPanel(session);
        ControlPanel controlPanel = new ControlPanel(session, serial);
        AudiogramPanel audiogramPanel = new AudiogramPanel(session);

        serial.addResponseListener(() -> logPanel.appendLog("<- RESPONSE received (patient pressed button)"));
        serial.addConnectionListener((connected, port) -> logPanel.appendLog(connected
                ? "Connected -> " + port
                : "Disconnected <- " + port));

        JMenuBar menuBar = buildMenuBar(session, logPanel);
        setJMenuBar(menuBar);

        getContentPane().setBackground(new Color(248, 250, 252));
        setLayout(new BorderLayout(0, 0));

        add(connectionPanel, BorderLayout.NORTH);

        JSplitPane centerSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, controlPanel, audiogramPanel);
        centerSplit.setBackground(new Color(248, 250, 252));
        centerSplit.setDividerLocation(290);
        centerSplit.setDividerSize(6);
        centerSplit.setContinuousLayout(true);
        centerSplit.setBorder(null);

        JSplitPane mainSplit = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT, centerSplit, logPanel);
        mainSplit.setBackground(new Color(248, 250, 252));
        mainSplit.setDividerLocation(530);
        mainSplit.setDividerSize(6);
        mainSplit.setContinuousLayout(true);
        mainSplit.setBorder(null);

        add(mainSplit, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onExit(serial);
            }
        });

        setMinimumSize(new Dimension(1000, 720));
        setPreferredSize(new Dimension(1150, 780));
        pack();
        setLocationRelativeTo(null);
    }

    private JMenuBar buildMenuBar(TestSession session, LogPanel logPanel) {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuFile = new JMenu("File");

        JMenuItem itemReset = new JMenuItem("Reset Session");
        itemReset.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "All threshold data will be deleted. Do you want to continue?",
                    "Reset Session", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                session.clearAll();
            }
        });

        JMenuItem itemExit = new JMenuItem("Exit");
        itemExit.addActionListener(e -> onExit(null));

        menuFile.add(itemReset);
        menuFile.addSeparator();
        menuFile.add(itemExit);

        JMenu menuHelp = new JMenu("Help");
        JMenuItem itemAbout = new JMenuItem("About");
        itemAbout.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Audiometer Test System\n" +
                        "Ankara University Faculty of Engineering\n" +
                        "Multidisciplinary Project — 2025-2026 Spring\n\n" +
                        "Computer Engineering Team\n" +
                        "Component: GUI & Serial Port Communication",
                "About", JOptionPane.INFORMATION_MESSAGE));
        menuHelp.add(itemAbout);

        menuBar.add(menuFile);
        menuBar.add(menuHelp);
        return menuBar;
    }

    private void onExit(SerialManager serial) {
        if (serial != null && serial.isConnected()) {
            serial.disconnect();
        }
        System.exit(0);
    }

    public TestSession getSession() {
        return session;
    }
}
