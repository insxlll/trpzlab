package com.example.client;

public class SentenceCaseText implements ITextStrategy {
    @Override
    public void processText(javax.swing.JTextPane textPane) {
        String text = textPane.getText().toLowerCase();
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;
        for (char c : text.toCharArray()) {
            if (capitalizeNext && Character.isLetter(c)) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }
            if (c == '.' || c == '!' || c == '?' || c == '\n') {
                capitalizeNext = true;
            }
        }
        text = result.toString();
        textPane.setText(text);
    }
    
}
