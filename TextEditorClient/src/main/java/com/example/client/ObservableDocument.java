package com.example.client;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ObservableDocument extends Document implements DocumentSubject {
    private final List<DocumentObserver> observers = new ArrayList<>();

    public ObservableDocument() {
        super();
    }

    public ObservableDocument(String title, String[] content) {
        super(title, content);
    }

    @Override
    public synchronized void setContent(String[] content) {
        String oldText = DocumentTextHelper.getText(this);
        super.setContent(content);
        String newText = DocumentTextHelper.getText(this);
        if (!Objects.equals(oldText, newText)) {
            notifyObservers(new DocumentEvent(DocumentEvent.Type.CONTENT_CHANGED, oldText, newText));
        }
    }

   
    public synchronized void setText(String text) {
        String oldText = DocumentTextHelper.getText(this);
        DocumentTextHelper.setText(this, text);
        String newText = DocumentTextHelper.getText(this);
        if (!Objects.equals(oldText, newText)) {
            notifyObservers(new DocumentEvent(DocumentEvent.Type.CONTENT_CHANGED, oldText, newText));
        }
    }

    @Override
    public synchronized void setTitle(String title) {
        String oldText = getTitle();
        super.setTitle(title);
        String newText = getTitle();
        if (!Objects.equals(oldText, newText)) {
            notifyObservers(new DocumentEvent(DocumentEvent.Type.TITLE_CHANGED, oldText, newText));
        }
    }

    @Override
    public synchronized void addObserver(DocumentObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public synchronized void removeObserver(DocumentObserver observer) {
        observers.remove(observer);
    }

    private synchronized void notifyObservers(DocumentEvent event) {
        
        List<DocumentObserver> snapshot = new ArrayList<>(observers);
        for (DocumentObserver obs : snapshot) {
            try {
                obs.documentChanged(event);
            } catch (Exception ex) {
                
                System.err.println("[ObservableDocument] Observer threw: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}