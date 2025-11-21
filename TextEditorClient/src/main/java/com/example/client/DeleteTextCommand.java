package com.example.client;




public class DeleteTextCommand implements ICommand {
    private final Document document;
    private final int start;
    private final int end;
    private String deletedText;
    
    public DeleteTextCommand(Document document, int start, int end) {
        this.document = document;
        this.start = start;
        this.end = end;
    }
    
    @Override
    public void execute() {
        String currentText = DocumentTextHelper.getText(document);
        
        if (start < 0 || end > currentText.length() || start > end) {
            throw new IndexOutOfBoundsException("Invalid range: " + start + " to " + end);
        }
        
        deletedText = currentText.substring(start, end);
        String newText = currentText.substring(0, start) + currentText.substring(end);
        DocumentTextHelper.setText(document, newText);
    }
    
    @Override
    public void undo() {
        if (deletedText != null) {
            String currentText = DocumentTextHelper.getText(document);
            String newText = currentText.substring(0, start) + deletedText + currentText.substring(start);
            DocumentTextHelper.setText(document, newText);
        }
    }
    
    @Override
    public void redo() {
        if (deletedText != null) {
            String currentText = DocumentTextHelper.getText(document);
            String newText = currentText.substring(0, start) + currentText.substring(start + deletedText.length());
            DocumentTextHelper.setText(document, newText);
        }
    }
    
    @Override
    public String getName() {
        return "Delete Text";
    }
}