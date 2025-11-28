package com.example.client;

import java.util.HashMap;
import java.util.Map;


public class FlyweightFactory {
    private final Map<String, CharacterStyle> stylePool;

    public FlyweightFactory() {
        this.stylePool = new HashMap<>();
    }

    public CharacterStyle getStyle(String fontName, int fontSize, String color) {
        String key = createKey(fontName, fontSize, color);
        
        CharacterStyle style = stylePool.get(key);
        
        if (style == null) {
            style = new CharacterStyle(fontName, fontSize, color);
            stylePool.put(key, style);
            System.out.println("Створено новий стиль: " + style);
        } else {
            System.out.println("Використано існуючий стиль: " + style);
        }
        
        return style;
    }


    private String createKey(String fontName, int fontSize, String color) {
        return fontName + "_" + fontSize + "_" + color;
    }


    public int getStyleCount() {
        return stylePool.size();
    }


    public void printStatistics() {
        System.out.println("=== Статистика FlyweightFactory ===");
        System.out.println("Кількість унікальних стилів у пулі: " + stylePool.size());
        System.out.println("Стилі в пулі:");
        for (CharacterStyle style : stylePool.values()) {
            System.out. println("  - " + style);
        }
    }
    

    public void clear() {
        stylePool.clear();
    }
}