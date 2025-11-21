package com.example.client;

public class ChangeCounterObserver implements DocumentObserver {
    private int changeCount = 0;

    @Override
    public void documentChanged(DocumentEvent event) {
        if (event.getType() == DocumentEvent.Type.CONTENT_CHANGED) {
            changeCount++;
        }
    }

 
    public int getChangeCount() {
        return changeCount;
    }

    public void reset() {
        changeCount = 0;
    }
}