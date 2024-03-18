// TerminalEmulator.java
package com.editor.texteditor;

import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TerminalEmulator {

    private final TextArea terminalArea;

    public TerminalEmulator(TextArea terminalArea) {
        this.terminalArea = terminalArea;
    }

    public void executeCommand(String command) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", command);
        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                terminalArea.appendText("\n" + line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}