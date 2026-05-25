package com.audiometer.gui;

import com.audiometer.model.TestSession;
import com.audiometer.serial.SerialManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ConnectionPanel extends JPanel {

    private final SerialManager serialManager;

    private final JComboBox<String> comboPort;
    private final JButton btnConnect;
    private final JButton btnDisconnect;
    private final JButton btnRefresh;
    private final JLabel labelStatus;
    private final JLabel labelDot;

    public ConnectionPanel(SerialManager serialManager) {
        this.serialManager = serialManager;

        setLayout(new FlowLayout(FlowLayout.LEFT, 12, 8));
        setBackground(new Color(248, 250, 252)); // Sleek cool-white background
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)), // Light slate separator line
                BorderFactory.createEmptyBorder(6, 16, 6, 16)
        ));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

        JLabel lblPort = new JLabel("COM Port:");
        lblPort.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPort.setForeground(new Color(71, 85, 105)); // Modern dark slate

        comboPort = new JComboBox<>();
        comboPort.setPreferredSize(new Dimension(140, 30));
        comboPort.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboPort.setBackground(Color.WHITE);
        comboPort.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1));
        refreshPorts();

        btnRefresh = new ModernButton("Refresh", new Color(100, 116, 139), Color.WHITE, 8);
        btnRefresh.setPreferredSize(new Dimension(80, 30));

        btnConnect = new ModernButton("Connect", new Color(34, 197, 94), Color.WHITE, 8);
        btnConnect.setPreferredSize(new Dimension(100, 30));

        btnDisconnect = new ModernButton("Disconnect", new Color(239, 68, 68), Color.WHITE, 8);
        btnDisconnect.setPreferredSize(new Dimension(110, 30));
        btnDisconnect.setEnabled(false);

        labelDot = new JLabel("●");
        labelDot.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        labelDot.setForeground(new Color(239, 68, 68)); // Glowing soft red

        labelStatus = new JLabel("Disconnected");
        labelStatus.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 12));
        labelStatus.setForeground(new Color(148, 163, 184)); // Muted slate

        btnRefresh.addActionListener(e -> refreshPorts());
        btnConnect.addActionListener(e -> onConnect());
        btnDisconnect.addActionListener(e -> onDisconnect());

        serialManager.addConnectionListener((connected, portName) ->
                SwingUtilities.invokeLater(() -> updateConnectionUI(connected, portName)));

        add(lblPort);
        add(comboPort);
        add(btnRefresh);
        add(Box.createHorizontalStrut(6));
        add(btnConnect);
        add(btnDisconnect);
        add(Box.createHorizontalStrut(16));
        add(labelDot);
        add(labelStatus);
    }

    private void onConnect() {
        String selectedPort = (String) comboPort.getSelectedItem();
        if (selectedPort == null || selectedPort.isEmpty() || selectedPort.equals("No ports found")) {
            JOptionPane.showMessageDialog(this,
                    "Please select a valid COM port.",
                    "Port Not Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean success = serialManager.connect(selectedPort);
        if (!success) {
            JOptionPane.showMessageDialog(this,
                    "Could not connect to port '" + selectedPort + "'.\n" +
                            "Make sure the Proteus simulation is running.",
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDisconnect() {
        serialManager.disconnect();
    }

    private void refreshPorts() {
        List<String> ports = serialManager.getAvailablePorts();
        comboPort.removeAllItems();

        if (ports.isEmpty()) {
            comboPort.addItem("No ports found");
        } else {
            for (String port : ports) {
                comboPort.addItem(port);
            }
        }
    }

    private void updateConnectionUI(boolean connected, String portName) {
        if (connected) {
            labelDot.setForeground(new Color(34, 197, 94)); // Emerald Green
            labelStatus.setText("Connected: " + portName);
            labelStatus.setForeground(new Color(21, 128, 61));
            btnConnect.setEnabled(false);
            btnDisconnect.setEnabled(true);
            comboPort.setEnabled(false);
            btnRefresh.setEnabled(false);
        } else {
            labelDot.setForeground(new Color(239, 68, 68)); // Crimson Red
            labelStatus.setText("Disconnected");
            labelStatus.setForeground(new Color(148, 163, 184));
            btnConnect.setEnabled(true);
            btnDisconnect.setEnabled(false);
            comboPort.setEnabled(true);
            btnRefresh.setEnabled(true);
        }
    }
}
