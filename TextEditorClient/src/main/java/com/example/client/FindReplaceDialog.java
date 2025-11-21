package com.example.client;

import javax.swing.*;
import java.awt.*;


public class FindReplaceDialog extends JDialog {
    private final JTextField findField;
    private final JTextField replaceField;
    private boolean confirmed = false;
    
    public FindReplaceDialog(Frame parent) {
        super(parent, "Знайти та замінити", true);
        
        findField = new JTextField(25);
        replaceField = new JTextField(25);
        
        initComponents();
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        fieldsPanel.add(new JLabel("Знайти:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        fieldsPanel.add(findField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        fieldsPanel.add(new JLabel("Замінити на:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        fieldsPanel.add(replaceField, gbc);
        
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton replaceButton = new JButton("Замінити все");
        replaceButton.addActionListener(e -> {
            if (findField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Поле 'Знайти' не може бути порожнім",
                    "Помилка",
                    JOptionPane.ERROR_MESSAGE);
            } else {
                confirmed = true;
                dispose();
            }
        });
        
        JButton cancelButton = new JButton("Скасувати");
        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
        
        buttonsPanel.add(replaceButton);
        buttonsPanel.add(cancelButton);
        
        
        add(fieldsPanel, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
        
        
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        
        getRootPane().setDefaultButton(replaceButton);
    }
    
    public String getFindText() {
        return findField.getText();
    }
    
    public String getReplaceText() {
        return replaceField.getText();
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}