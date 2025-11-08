package com.example.client;

import javax.swing.JTextPane;

public class ClearText implements ITextStrategy {
    @Override
    public void processText(JTextPane textPane) {
        textPane.setText("");
    }
    
}
