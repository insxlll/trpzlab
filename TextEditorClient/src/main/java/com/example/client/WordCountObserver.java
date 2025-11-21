package com.example.client;


public class WordCountObserver implements DocumentObserver {
    private int charCount = 0;
    private int wordCount = 0;

    @Override
    public void documentChanged(DocumentEvent event) {
        if (event.getType() == DocumentEvent.Type.CONTENT_CHANGED) {
            String text = event.getNewText() != null ? event.getNewText() : "";
            charCount = text.length();
            
            String trimmed = text.trim();
            if (trimmed.isEmpty()) {
                wordCount = 0;
            } else {
                wordCount = trimmed.split("\\s+").length;
            }
        }
    }

    public int getCharCount() {
        return charCount;
    }

    public int getWordCount() {
        return wordCount;
    }
}