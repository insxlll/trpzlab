package com.example.client;

import java.time.LocalTime;

/**
 * Простейший приклад DocumentObserver — просто логуватиме події
 */
public class LogDocumentObserver implements DocumentObserver {
    private final String name;

    public LogDocumentObserver(String name) {
        this.name = name;
    }

    @Override
    public void documentChanged(DocumentEvent event) {
        System.out.println("[" + LocalTime.now() + "] [" + name + "] Document changed: " + event);
    }
}