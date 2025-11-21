package com.example.client;

public interface ICommand {
    void execute();
    void undo();
    void redo();
    public String getName();
}
