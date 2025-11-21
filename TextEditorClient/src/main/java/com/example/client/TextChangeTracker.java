package com.example.client;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class TextChangeTracker {
    private final JTextComponent textComponent;
    private final DocumentController controller;
    private final Timer timer;
    private boolean isUpdating = false;
    
    public TextChangeTracker(JTextComponent textComponent, DocumentController controller) {
        this.textComponent = textComponent;
        this.controller = controller;
        
        
        this.timer = new Timer(1000, e -> captureChange());
        this.timer.setRepeats(false);
        
        setup();
    }
    
    private void setup() {
        
        textComponent.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (!isUpdating) {
                    timer.restart();
                }
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                if (!isUpdating) {
                    timer.restart();
                }
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                if (!isUpdating) {
                    timer.restart();
                }
            }
        });
        
        
        textComponent.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (timer.isRunning()) {
                    timer.stop();
                    captureChange();
                }
            }
        });
    }
    
    private void captureChange() {
        if (!isUpdating) {
            String currentText = textComponent.getText();
            String lastText = controller.getLastRegisteredText();
            
            if (!currentText.equals(lastText)) {
                controller.registerTextChange(currentText);
            }
        }
    }
    
    public void updateTextFromController() {
        isUpdating = true;
        try {
            textComponent.setText(controller.getText());
        } finally {
            isUpdating = false;
        }
    }
    
    public void stopTimer() {
        if (timer.isRunning()) {
            timer.stop();
        }
    }
    
    public void captureNow() {
        stopTimer();
        captureChange();
    }
}