package com.example.client;


public class UnsavedChangesObserver implements DocumentObserver {
    private volatile boolean dirty = false;

    @Override
    public void documentChanged(DocumentEvent event) {
        if (event.getType() == DocumentEvent.Type.CONTENT_CHANGED
                || event.getType() == DocumentEvent.Type.CLEARED) {
            dirty = true;
        }
        if (event.getType() == DocumentEvent.Type.TITLE_CHANGED) {
            
            dirty = true;
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    
    public void markSaved() {
        dirty = false;
    }
}