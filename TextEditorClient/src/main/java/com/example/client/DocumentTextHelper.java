package com.example.client;




public class DocumentTextHelper {
    
    
    public static String getText(Document document) {
        String[] content = document.getContent();
        if (content == null || content.length == 0) {
            return "";
        }
        return String.join("\n", content);
    }
    
    
    public static void setText(Document document, String text) {
        if (text == null || text.isEmpty()) {
            document.setContent(new String[0]);
        } else {
            document.setContent(text.split("\n", -1)); 
        }
    }
    
    
    public static int getTextLength(Document document) {
        return getText(document).length();
    }
    
    
    public static boolean isEmpty(Document document) {
        String[] content = document.getContent();
        return content == null || content.length == 0 || getText(document).isEmpty();
    }
}