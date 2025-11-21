package com.example.client;

/**
 * Подія, що описує зміну документа (текст/заголовок/повне очищення тощо)
 */
public class DocumentEvent {
    public enum Type {
        CONTENT_CHANGED,
        TITLE_CHANGED,
        CLEARED,
        OTHER
    }

    private final Type type;
    private final String oldText;
    private final String newText;

    public DocumentEvent(Type type, String oldText, String newText) {
        this.type = type;
        this.oldText = oldText != null ? oldText : "";
        this.newText = newText != null ? newText : "";
    }

    public Type getType() {
        return type;
    }

    public String getOldText() {
        return oldText;
    }

    public String getNewText() {
        return newText;
    }

    @Override
    public String toString() {
        return "DocumentEvent{" +
                "type=" + type +
                ", oldText='" + oldText + '\'' +
                ", newText='" + newText + '\'' +
                '}';
    }
}