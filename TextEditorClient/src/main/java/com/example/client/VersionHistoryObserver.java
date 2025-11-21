package com.example.client;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;


public class VersionHistoryObserver implements DocumentObserver {
    private final Deque<String> history;
    private final int maxSize;

   
    public VersionHistoryObserver(int maxSize) {
        this.history = new ArrayDeque<>(maxSize + 1);
        this.maxSize = Math.max(1, maxSize);
    }

    @Override
    public synchronized void documentChanged(DocumentEvent event) {
        if (event.getType() == DocumentEvent.Type.CONTENT_CHANGED) {
            String oldText = event.getOldText() != null ? event.getOldText() : "";
            
            if (history.isEmpty() || !Objects.equals(history.peekLast(), oldText)) {
                history.addLast(oldText);
                if (history.size() > maxSize) {
                    history.removeFirst();
                }
            }
        }
    }

  
    public synchronized List<String> getVersions() {
        return new ArrayList<>(history);
    }

 
    public synchronized String restoreLast(ObservableDocument document) {
        if (history.isEmpty()) return null;
        String last = history.peekLast();
        if (last != null) {
            document.setText(last);
        }
        return last;
    }

 
    public synchronized void clear() {
        history.clear();
    }
}