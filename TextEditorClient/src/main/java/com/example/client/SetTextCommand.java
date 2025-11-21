package com.example.client;




public class SetTextCommand implements ICommand {
    private final Document document;
    private final String newText;
    private String oldText;
    
    public SetTextCommand(Document document, String newText) {
        this.document = document;
        this.newText = newText;
    }
    
    @Override
    public void execute() {
        oldText = DocumentTextHelper.getText(document);
        DocumentTextHelper.setText(document, newText);
    }
    
    @Override
    public void undo() {
        if (oldText != null) {
            DocumentTextHelper.setText(document, oldText);
        }
    }
    
    @Override
    public void redo() {
        DocumentTextHelper.setText(document, newText);
    }
    
    @Override
    public String getName() {
        return "Set Text";
    }
}