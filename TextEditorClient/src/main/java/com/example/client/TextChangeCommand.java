package com.example.client;



public class TextChangeCommand implements ICommand {
    private final Document document;
    private final String oldText;
    private final String newText;
    
    public TextChangeCommand(Document document, String oldText, String newText) {
        this.document = document;
        this.oldText = oldText;
        this.newText = newText;
    }
    
    @Override
    public void execute() {
        DocumentTextHelper.setText(document, newText);
    }
    
    @Override
    public void undo() {
        DocumentTextHelper.setText(document, oldText);
    }
    
    @Override
    public void redo() {
        DocumentTextHelper.setText(document, newText);
    }
    
    @Override
    public String getName() {
        return "Text Change";
    }
}