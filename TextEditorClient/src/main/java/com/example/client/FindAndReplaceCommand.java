package com.example.client;



public class FindAndReplaceCommand implements ICommand {
    private final Document document;
    private final String findText;
    private final String replaceText;
    private String oldText;
    
    public FindAndReplaceCommand(Document document, String findText, String replaceText) {
        this.document = document;
        this.findText = findText;
        this.replaceText = replaceText;
    }
    
    @Override
    public void execute() {
        oldText = DocumentTextHelper.getText(document);
        String newText = oldText.replace(findText, replaceText);
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
        if (oldText != null) {
            String newText = oldText.replace(findText, replaceText);
            DocumentTextHelper.setText(document, newText);
        }
    }
    
    @Override
    public String getName() {
        return "Find and Replace";
    }
}