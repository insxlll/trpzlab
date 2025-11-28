package com.example.client;


public class CharacterStyle {
    private final String fontName;
    private final int fontSize;
    private final String color;

    public CharacterStyle(String fontName, int fontSize, String color) {
        this.fontName = fontName;
        this.fontSize = fontSize;
        this.color = color;
    }

    public String getFontName() {
        return fontName;
    }

    public int getFontSize() {
        return fontSize;
    }

    public String getColor() {
        return color;
    }

    public void display(char character, int x, int y) {
        System.out.println("Символ '" + character + "' на позиції (" + x + ", " + y + 
                         ") з шрифтом: " + fontName + ", розміром: " + fontSize + 
                         ", кольором: " + color);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        CharacterStyle that = (CharacterStyle) obj;
        return fontSize == that.fontSize &&
               fontName.equals(that.fontName) &&
               color.equals(that.color);
    }

    @Override
    public int hashCode() {
        int result = fontName.hashCode();
        result = 31 * result + fontSize;
        result = 31 * result + color. hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CharacterStyle{fontName='" + fontName + "', fontSize=" + fontSize + 
               ", color='" + color + "'}";
    }
}