package com.example.client;

import java.util. ArrayList;
import java.util. List;


public class TextDocument {
    private final List<TextCharacter> characters;
    private final FlyweightFactory styleFactory;
    private final String name;

    public TextDocument(String name) {
        this.name = name;
        this.characters = new ArrayList<>();
        this.styleFactory = new FlyweightFactory();
    }

    public void addCharacter(char character, int x, int y,
                           String fontName, int fontSize, String color) {
        CharacterStyle style = styleFactory.getStyle(fontName, fontSize, color);
        TextCharacter ch = new TextCharacter(character, x, y, style);
        characters.add(ch);
    }


    public void addText(String text, int startX, int startY,
                       String fontName, int fontSize, String color) {
        int x = startX;
        int y = startY;
        
        for (char c : text.toCharArray()) {
            if (c == '\n') {
                y += fontSize + 5;
                x = startX;
            } else {
                addCharacter(c, x, y, fontName, fontSize, color);
                x += fontSize / 2;
            }
        }
    }


    public void render() {
        System.out. println("\n=== Рендеринг документа: " + name + " ===");
        for (TextCharacter character : characters) {
            character. render();
        }
    }


    public void printMemoryStatistics() {
        System.out.println("\n=== Статистика документа: " + name + " ===");
        System.out.println("Загальна кількість символів: " + characters.size());
        styleFactory.printStatistics();
        
        long withoutFlyweight = characters.size() * 100;
        long withFlyweight = characters.size() * 20 + styleFactory.getStyleCount() * 80;
        long savedMemory = withoutFlyweight - withFlyweight;
        
        System.out.println("\nОцінка використання пам'яті:");
        System.out.println("Без Flyweight: ~" + withoutFlyweight + " байт");
        System.out. println("З Flyweight: ~" + withFlyweight + " байт");
        System.out.println("Заощаджено: ~" + savedMemory + " байт (" +
                         (savedMemory * 100 / withoutFlyweight) + "%)");
    }

    public int getCharacterCount() {
        return characters.size();
    }

    public String getName() {
        return name;
    }
    
    public List<TextCharacter> getCharacters() {
        return characters;
    }
    
    public FlyweightFactory getStyleFactory() {
        return styleFactory;
    }
    

    public void clear() {
        characters.clear();
        styleFactory.clear();
    }
}