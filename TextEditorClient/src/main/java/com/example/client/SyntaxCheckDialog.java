package com.example.client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;


public class SyntaxCheckDialog extends JDialog {
    
    private JTextArea resultArea;
    private JLabel statusLabel;

    public SyntaxCheckDialog(Frame parent) {
        super(parent, "Результати перевірки синтаксису", true);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setSize(600, 400);
        setLocationRelativeTo(getParent());

        
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        statusLabel = new JLabel();
        statusLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.NORTH);

        
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Деталі"));
        add(scrollPane, BorderLayout.CENTER);

        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        JButton closeButton = new JButton("Закрити");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void displayResult(SyntaxCheckResult result) {
        if (result == null) {
            statusLabel.setText("❌ Помилка: результат недоступний");
            statusLabel.setForeground(Color.RED);
            resultArea.setText("Не вдалося отримати результати перевірки.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        
        
        boolean hasRealErrors = result.hasErrors();
        
        if (!hasRealErrors || (result.isSuccess() && result.getErrorCount() == 0)) {
            
            statusLabel.setText("✅ Синтаксис коректний");
            statusLabel.setForeground(new Color(0, 128, 0));
            sb.append("╔════════════════════════════════════════════════════╗\n");
            sb.append("║          ПЕРЕВІРКА ПРОЙШЛА УСПІШНО!               ║\n");
            sb.append("╚════════════════════════════════════════════════════╝\n\n");
            sb.append("✅ Помилок не знайдено!\n\n");
            sb.append("Ваш Java код успішно пройшов перевірку синтаксису.\n");
            sb.append("Код може бути скомпільований без синтаксичних помилок.");
        } else {
            
            statusLabel.setText("❌ Знайдено помилок: " + result.getErrorCount());
            statusLabel.setForeground(Color.RED);
            
            if (result.getMessage() != null && !result.getMessage().isEmpty()) {
                sb.append(result.getMessage()).append("\n\n");
            }
            
            sb.append("═══════════════════════════════════════════════════\n");
            sb.append("СПИСОК ПОМИЛОК ТА ПОПЕРЕДЖЕНЬ:\n");
            sb.append("═══════════════════════════════════════════════════\n\n");
            
            int errorNum = 0;
            int warningNum = 0;
            int infoNum = 0;
            
            List<String> errors = result.getErrors();
            for (int i = 0; i < errors.size(); i++) {
                String error = errors.get(i);
                sb.append(String.format("%d. %s\n", i + 1, error));
                
                if (error.contains("ПОМИЛКА")) errorNum++;
                else if (error.contains("ПОПЕРЕДЖЕННЯ")) warningNum++;
                else if (error.contains("ІНФО")) infoNum++;
                
                sb.append("\n");
            }
            
            sb.append("═══════════════════════════════════════════════════\n");
            sb.append(String.format("Підсумок: Помилок: %d | Попереджень: %d | Інфо: %d\n", 
                                   errorNum, warningNum, infoNum));
            sb.append("═══════════════════════════════════════════════════\n");
        }
        
        resultArea.setText(sb.toString());
        resultArea.setCaretPosition(0);
    }
}