package com.example.client;

public interface DocumentSubject {
    void addObserver(DocumentObserver observer);
    void removeObserver(DocumentObserver observer);
}