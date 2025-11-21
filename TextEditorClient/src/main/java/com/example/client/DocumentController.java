package com.example.client;



public class DocumentController {
    private final Document document;
    private final CommandHistory history;
    private String lastRegisteredText;

    public DocumentController(Document document) {
        this.document = document;
        this.history = new CommandHistory();
    }
    
    
    private void executeCommand(ICommand command) {
        command.execute();
        history.push(command);
    }
    
    public void registerTextChange(String newText) {
        String currentText = lastRegisteredText != null ? lastRegisteredText : DocumentTextHelper.getText(document);;
        
        
        if (!currentText.equals(newText)) {
            ICommand command = new TextChangeCommand(document, currentText, newText);
            command.execute();
            history.push(command);
            lastRegisteredText = newText;
        }
    }
    
    
    public String getLastRegisteredText() {
        return lastRegisteredText;
    }
    
    public void insertText(int position, String text) {
        ICommand command = new InsertTextCommand(document, position, text);
        executeCommand(command);
    }
    
    

    
    
    public void deleteText(int start, int end) {
        ICommand command = new DeleteTextCommand(document, start, end);
        executeCommand(command);
    }
    
    
    public void replaceText(int start, int end, String newText) {
        ICommand command = new ReplaceTextCommand(document, start, end, newText);
        executeCommand(command);
    }
    
    

    
    
    public void clearDocument() {
        ICommand command = new ClearDocumentCommand(document);
        executeCommand(command);
    }
    
    
    
    
    
    public void findAndReplace(String findText, String replaceText) {
        ICommand command = new FindAndReplaceCommand(document, findText, replaceText);
        executeCommand(command);
    }
    
    
   
  
   
    
    
    public boolean undo() {
        return history.undo();
    }
    
    
    public boolean redo() {
        return history.redo();
    }
    
    
    public boolean canUndo() {
        return history.canUndo();
    }
    
    
    public boolean canRedo() {
        return history.canRedo();
    }
    
    
    public Document getDocument() {
        return document;
    }
    
    
    public String getText() {
        return DocumentTextHelper.getText(document);
    }
    
    
    public int getTextLength() {
        return DocumentTextHelper.getTextLength(document);
    }
    
    
    public boolean isEmpty() {
        return DocumentTextHelper.isEmpty(document);
    }
    public void setText(String text) {
        ICommand command = new SetTextCommand(document, text);
        executeCommand(command);
    }
    
    public void clearHistory() {
        history.clear();
    }
    
    
    public String getLastUndoCommandName() {
        return history.getLastUndoCommandName();
    }
    
    
    public String getLastRedoCommandName() {
        return history.getLastRedoCommandName();
    }
}