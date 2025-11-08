package com.example.client;

import javax.swing.JTextPane;

public class JStrategyTextPane extends JTextPane {
    private ITextStrategy textStrategy;
    void setTextStrategy(ITextStrategy textStrategy) {
        this.textStrategy = textStrategy;
    }
    void executeTextStrategy() {
        if (textStrategy != null) {
            textStrategy.processText(this);
        }
    }
}
