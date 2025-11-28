package com.example.appserver;

import java.util.ArrayList;
import java.util.List;


public abstract class SyntaxChecker {
    protected List<String> errors = new ArrayList<>();
    protected String code;


    public final List<String> checkSyntax(String code) {
        this.code = code;
        this.errors.clear();
        
        
        preprocessCode();           
        validateBasicStructure();   
        checkBrackets();            
        checkSpecificSyntax();      
        postProcessErrors();        
        
        return new ArrayList<>(errors);
    }


    protected void preprocessCode() {
 
    }


    protected void validateBasicStructure() {
        if (code == null || code.trim().isEmpty()) {
            errors.add("ПОМИЛКА: Код порожній");
        }
    }


    protected void checkBrackets() {
        int round = 0, square = 0, curly = 0;
        
        for (int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);
            switch (c) {
                case '(': round++; break;
                case ')': round--; break;
                case '[': square++; break;
                case ']': square--; break;
                case '{': curly++; break;
                case '}': curly--; break;
            }
            
            if (round < 0) errors.add("ПОМИЛКА: Зайва закриваюча кругла дужка на позиції " + i);
            if (square < 0) errors.add("ПОМИЛКА: Зайва закриваюча квадратна дужка на позиції " + i);
            if (curly < 0) errors.add("ПОМИЛКА: Зайва закриваюча фігурна дужка на позиції " + i);
        }
        
        if (round > 0) errors.add("ПОМИЛКА: Незакриті круглі дужки: " + round);
        if (square > 0) errors.add("ПОМИЛКА: Незакриті квадратні дужки: " + square);
        if (curly > 0) errors.add("ПОМИЛКА: Незакриті фігурні дужки: " + curly);
    }


    protected abstract void checkSpecificSyntax();


    protected void postProcessErrors() {
        
    }
}