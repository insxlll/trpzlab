package com.example.client;


public class TextCharacter {
    private final char character;
    private final int x;
    private final int y;
    private final CharacterStyle style;

    public TextCharacter(char character, int x, int y, CharacterStyle style) {
        this.character = character;
        this.x = x;
        this.y = y;
        this.style = style;
    }

    public char getCharacter() {
        return character;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public CharacterStyle getStyle() {
        return style;
    }


    public void render() {
        style.display(character, x, y);
    }

    @Override
    public String toString() {
        return "TextCharacter{char='" + character + "', position=(" + x + ", " + y + 
               "), style=" + style + "}";
    }
}