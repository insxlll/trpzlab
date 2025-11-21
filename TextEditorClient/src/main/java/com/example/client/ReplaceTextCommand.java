package com.example.client;



public class ReplaceTextCommand implements ICommand {
    private final Document document;
    private final int start;
    private final int end;
    private final String newText;
    private String oldText;
    
    public ReplaceTextCommand(Document document, int start, int end, String newText) {
        this.document = document;
        this.start = start;
        this.end = end;
        this.newText = newText;
    }
    
    @Override
    public void execute() {
        String currentText = DocumentTextHelper.getText(document);
        
        if (start < 0 || end > currentText.length() || start > end) {
            throw new IndexOutOfBoundsException("Invalid range: " + start + " to " + end);
        }
        
        oldText = currentText.substring(start, end);
        String resultText = currentText.substring(0, start) + newText + currentText.substring(end);
        DocumentTextHelper.setText(document, resultText);
    }
    
    @Override
    public void undo() {
        if (oldText != null) {
            String currentText = DocumentTextHelper.getText(document);
            String resultText = currentText.substring(0, start) + oldText + currentText.substring(start + newText.length());
            DocumentTextHelper.setText(document, resultText);
        }
    }
    
    @Override
    public void redo() {
        String currentText = DocumentTextHelper.getText(document);
        String resultText = currentText.substring(0, start) + newText + currentText.substring(start + oldText.length());
        DocumentTextHelper.setText(document, resultText);
    }
    
    @Override
    public String getName() {
        return "Replace Text";
    }
}