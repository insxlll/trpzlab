package com.example.client;




public class InsertTextCommand implements ICommand {
    private final Document document;
    private final int position;
    private final String text;
    private boolean executed;
    
    public InsertTextCommand(Document document, int position, String text) {
        this.document = document;
        this.position = position;
        this.text = text;
        this.executed = false;
    }
    
    @Override
    public void execute() {
        String currentText = DocumentTextHelper.getText(document);
        
        if (position < 0 || position > currentText.length()) {
            throw new IndexOutOfBoundsException("Invalid position: " + position);
        }
        
        String newText = currentText.substring(0, position) + text + currentText.substring(position);
        DocumentTextHelper.setText(document, newText);
        executed = true;
    }
    
    @Override
    public void undo() {
        if (executed) {
            String currentText = DocumentTextHelper.getText(document);
            String newText = currentText.substring(0, position) + currentText.substring(position + text.length());
            DocumentTextHelper.setText(document, newText);
        }
    }
    
    @Override
    public void redo() {
        execute();
    }
    
    @Override
    public String getName() {
        return "Insert Text";
    }
}