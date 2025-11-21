package com.example.client;

import java.util.Stack;


public class CommandHistory {
    private final Stack<ICommand> undoStack;
    private final Stack<ICommand> redoStack;
    private final int maxHistorySize;
    
    public CommandHistory() {
        this(100);
    }
    
    public CommandHistory(int maxHistorySize) {
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
        this.maxHistorySize = maxHistorySize;
    }
    
    public void push(ICommand command) {
        redoStack.clear();
        
        if (undoStack.size() >= maxHistorySize) {
            undoStack.remove(0);
        }
        
        undoStack.push(command);
    }
    
    public boolean undo() {
        if (undoStack.isEmpty()) {
            return false;
        }
        
        ICommand command = undoStack.pop();
        command.undo();
        redoStack.push(command);
        return true;
    }
    
    public boolean redo() {
        if (redoStack.isEmpty()) {
            return false;
        }
        
        ICommand command = redoStack.pop();
        command.redo();
        undoStack.push(command);
        return true;
    }
    
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }
    
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }
    
    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }
    
    public int getUndoSize() {
        return undoStack.size();
    }
    
    public int getRedoSize() {
        return redoStack.size();
    }
    
    public String getLastUndoCommandName() {
        if (undoStack.isEmpty()) {
            return null;
        }
        return undoStack.peek().getName();
    }
    
    public String getLastRedoCommandName() {
        if (redoStack.isEmpty()) {
            return null;
        }
        return redoStack.peek().getName();
    }
}