package com.audiometer.serial;

import java.util.ArrayList;
import java.util.List;

/**
 * =================================================================================
 * TO BE IMPLEMENTED BY THE SERIAL PORT TEAM MEMBER.
 * This class is currently a STUB / MOCK implementation designed to allow the GUI
 * to compile, run, and be tested independently without requiring hardware or serial ports.
 * =================================================================================
 *
 * Class managing serial port communications.
 *
 * Responsibilities :
 *   1. List available COM ports using jSerialComm.
 *   2. Connect to and disconnect from a selected port.
 *   3. Send frequency & intensity commands (e.g. "FREQ:1000,INT:40\n").
 *   4. Notify the GUI when a "RESPONSE\n" message is received from Arduino.
 */
public class SerialManager {

    /** Active connection state */
    private boolean connected = false;
    private String activePortName = null;

    /** Subscribed listeners for patient RESPONSE events */
    private final List<ResponseListener> responseListeners = new ArrayList<>();

    /** Subscribed listeners for connection state changes */
    private final List<ConnectionListener> connectionListeners = new ArrayList<>();

    /** Thread to simulate patient response timer */
    private Thread simulationThread;

    /**
     * Lists system port names.
     * MOCKED: Returns virtual mock ports for easy testing.
     *
     * @return List of available port names.
     */
    public List<String> getAvailablePorts() {
        List<String> mockPorts = new ArrayList<>();
        mockPorts.add("COM1 (Virtual Mock)");
        mockPorts.add("COM2 (Virtual Mock)");
        mockPorts.add("COM3 (Virtual Mock)");
        return mockPorts;
    }

    /**
     * Connects to a specific COM port.
     * MOCKED: Simulates successful connection.
     *
     * @param portName The target port name.
     * @return true if connection succeeded.
     */
    public boolean connect(String portName) {
        if (connected) {
            disconnect();
        }
        
        // Simulating connection delay
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        connected = true;
        activePortName = portName;
        notifyConnectionChanged(true, portName);
        return true;
    }

    /**
     * Closes the active serial port connection.
     * MOCKED: Simulates disconnection.
     */
    public void disconnect() {
        if (connected) {
            connected = false;
            String disconnectedPort = activePortName;
            activePortName = null;
            notifyConnectionChanged(false, disconnectedPort);
            
            if (simulationThread != null && simulationThread.isAlive()) {
                simulationThread.interrupt();
            }
        }
    }

    /**
     * Sends a tone generation command to Arduino.
     * MOCKED: Simulates writing the command and automatically triggers a patient 
     * response after 1.5 seconds to allow full GUI testing without hardware.
     *
     * @param frequencyHz Target frequency in Hz.
     * @param intensityDb Hearing level intensity in dB HL.
     * @return true if command was successfully sent.
     */
    public boolean sendToneCommand(int frequencyHz, int intensityDb) {
        if (!connected) {
            System.err.println("Command failed to send: Port is not connected.");
            return false;
        }

        System.out.println(String.format("[MOCK SERIAL SENT] FREQ:%d,INT:%d\\n", frequencyHz, intensityDb));

        // Trigger simulation thread to mimic patient response
        if (simulationThread != null && simulationThread.isAlive()) {
            simulationThread.interrupt();
        }

        simulationThread = new Thread(() -> {
            try {
                // Simulate patient delay of 1.5 seconds before pressing the button
                Thread.sleep(1500);
                notifyResponse();
            } catch (InterruptedException e) {
                // Simulation interrupted by user action (e.g. stop tone)
            }
        });
        simulationThread.start();

        return true;
    }

    /**
     * Sends a tone stop command to Arduino.
     * MOCKED: Interrupts the pending patient response simulation.
     */
    public boolean sendStopCommand() {
        if (!connected) return false;
        System.out.println("[MOCK SERIAL SENT] STOP\\n");
        
        if (simulationThread != null && simulationThread.isAlive()) {
            simulationThread.interrupt();
        }
        return true;
    }

    /** Checks if the serial port is currently connected and open */
    public boolean isConnected() {
        return connected;
    }

    // --- Listener Management ---

    public void addResponseListener(ResponseListener listener)     { responseListeners.add(listener); }
    public void addConnectionListener(ConnectionListener listener) { connectionListeners.add(listener); }

    private void notifyResponse() {
        for (ResponseListener l : responseListeners) l.onResponseReceived();
    }

    private void notifyConnectionChanged(boolean connected, String portName) {
        for (ConnectionListener l : connectionListeners) l.onConnectionChanged(connected, portName);
    }

    /** Triggered when the patient button response is received */
    public interface ResponseListener {
        void onResponseReceived();
    }

    /** Triggered when the serial connection state changes */
    public interface ConnectionListener {
        void onConnectionChanged(boolean connected, String portName);
    }
}
