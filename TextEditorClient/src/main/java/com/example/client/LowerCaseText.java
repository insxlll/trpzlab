package com.example.client;

import javax.swing.JTextPane;

public class LowerCaseText implements ITextStrategy {
    @Override
    public void processText(JTextPane textPane) {
        String text = textPane.getText();
        textPane.setText(text.toLowerCase());
    }
    
}
