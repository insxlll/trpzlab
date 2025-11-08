package com.example.client;

public class UpperCaseText implements ITextStrategy {
    @Override
    public void processText(javax.swing.JTextPane textPane) {
        String text = textPane.getText();
        textPane.setText(text.toUpperCase());
    }
    
}
