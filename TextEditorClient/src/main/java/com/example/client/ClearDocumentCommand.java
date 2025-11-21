package com.example.client;




public class ClearDocumentCommand implements ICommand {
    private final Document document;
    private String previousText;
    
    public ClearDocumentCommand(Document document) {
        this.document = document;
    }
    
    @Override
    public void execute() {
        previousText = DocumentTextHelper.getText(document);
        DocumentTextHelper.setText(document, "");
    }
    
    @Override
    public void undo() {
        if (previousText != null) {
            DocumentTextHelper.setText(document, previousText);
        }
    }
    
    @Override
    public void redo() {
        DocumentTextHelper.setText(document, "");
    }
    
    @Override
    public String getName() {
        return "Clear Document";
    }
}