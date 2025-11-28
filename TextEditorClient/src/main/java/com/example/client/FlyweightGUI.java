package com.example.client;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;


public class FlyweightGUI extends JFrame {
    private TextDocument document;
    private DocumentCanvas canvas;
    private JTextArea statisticsArea;
    private JTextField textInput;
    private JComboBox<String> fontComboBox;
    private JSpinner fontSizeSpinner;
    private JButton colorButton;
    private Color selectedColor;
    private JLabel characterCountLabel;
    private JLabel styleCountLabel;
    private JLabel memoryLabel;

    public FlyweightGUI() {
        document = new TextDocument("GUI Document");
        selectedColor = Color.BLACK;
        
        initializeUI();
        showExample();
    }

    private void initializeUI() {
        setTitle("Flyweight Pattern Demo - Текстовий Редактор (Swing)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        add(createTopPanel(), BorderLayout. NORTH);
        
        
        add(createControlPanel(), BorderLayout.WEST);
        
        
        add(createCanvasPanel(), BorderLayout.CENTER);
        
        
        add(createStatisticsPanel(), BorderLayout.EAST);
        
        
        add(createBottomPanel(), BorderLayout.SOUTH);
        
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout. Y_AXIS));
        topPanel.setBorder(new EmptyBorder(15, 10, 15, 10));
        topPanel.setBackground(new Color(245, 245, 245));
        
        JLabel titleLabel = new JLabel("Демонстрація патерну 'Пристосуванець' (Flyweight)");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Оптимізація пам'яті в текстовому редакторі");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        topPanel. add(titleLabel);
        topPanel.add(Box.createVerticalStrut(5));
        topPanel.add(subtitleLabel);
        
        return topPanel;
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel. setBorder(new CompoundBorder(
            new EmptyBorder(10, 10, 10, 10),
            new TitledBorder(BorderFactory.createLineBorder(Color.GRAY), 
                           "Панель керування", 
                           TitledBorder.LEFT, 
                           TitledBorder.TOP,
                           new Font("Arial", Font.BOLD, 14))
        ));
        controlPanel.setPreferredSize(new Dimension(300, 0));
        controlPanel.setBackground(new Color(240, 240, 240));
        
        
        JLabel textLabel = new JLabel("Введіть текст:");
        textLabel.setFont(new Font("Arial", Font. PLAIN, 12));
        textLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        textInput = new JTextField();
        textInput.setMaximumSize(new Dimension(280, 35));
        textInput.setFont(new Font("Arial", Font. PLAIN, 13));
        textInput.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        
        JLabel fontLabel = new JLabel("Шрифт:");
        fontLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        fontLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        String[] fonts = {"Arial", "Times New Roman", "Courier New", 
                         "Verdana", "Georgia", "Comic Sans MS"};
        fontComboBox = new JComboBox<>(fonts);
        fontComboBox. setMaximumSize(new Dimension(280, 30));
        fontComboBox. setAlignmentX(Component.LEFT_ALIGNMENT);
        
        
        JLabel sizeLabel = new JLabel("Розмір шрифту:");
        sizeLabel.setFont(new Font("Arial", Font. PLAIN, 12));
        sizeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(14, 8, 48, 2);
        fontSizeSpinner = new JSpinner(spinnerModel);
        fontSizeSpinner.setMaximumSize(new Dimension(280, 30));
        fontSizeSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        
        JLabel colorLabel = new JLabel("Колір:");
        colorLabel.setFont(new Font("Arial", Font. PLAIN, 12));
        colorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        colorPanel.setMaximumSize(new Dimension(280, 35));
        colorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        colorPanel.setBackground(new Color(240, 240, 240));
        
        colorButton = new JButton("Обрати колір");
        colorButton.setPreferredSize(new Dimension(150, 30));
        colorButton.setBackground(selectedColor);
        colorButton.setForeground(Color.WHITE);
        colorButton.addActionListener(e -> chooseColor());
        
        JPanel colorPreview = new JPanel();
        colorPreview.setPreferredSize(new Dimension(30, 30));
        colorPreview.setBackground(selectedColor);
        colorPreview. setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        colorPanel. add(colorButton);
        colorPanel.add(Box.createHorizontalStrut(10));
        colorPanel.add(colorPreview);
        
        
        JButton addButton = new JButton("Додати текст");
        addButton. setMaximumSize(new Dimension(280, 45));
        addButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        addButton. setFont(new Font("Arial", Font. BOLD, 14));
        addButton.setBackground(new Color(76, 175, 80));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> addText());
        
        
        JButton exampleButton = new JButton("Показати приклад");
        exampleButton.setMaximumSize(new Dimension(280, 40));
        exampleButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        exampleButton.setFont(new Font("Arial", Font.BOLD, 12));
        exampleButton.setBackground(new Color(33, 150, 243));
        exampleButton.setForeground(Color.WHITE);
        exampleButton.setFocusPainted(false);
        exampleButton.addActionListener(e -> showExample());
        
        
        JButton clearButton = new JButton("Очистити документ");
        clearButton.setMaximumSize(new Dimension(280, 40));
        clearButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        clearButton.setFont(new Font("Arial", Font. BOLD, 12));
        clearButton.setBackground(new Color(244, 67, 54));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton. addActionListener(e -> clearDocument());
        
        
        controlPanel.add(textLabel);
        controlPanel.add(Box.createVerticalStrut(5));
        controlPanel.add(textInput);
        controlPanel.add(Box.createVerticalStrut(15));
        
        controlPanel.add(fontLabel);
        controlPanel.add(Box.createVerticalStrut(5));
        controlPanel.add(fontComboBox);
        controlPanel.add(Box.createVerticalStrut(15));
        
        controlPanel.add(sizeLabel);
        controlPanel.add(Box.createVerticalStrut(5));
        controlPanel. add(fontSizeSpinner);
        controlPanel.add(Box.createVerticalStrut(15));
        
        controlPanel.add(colorLabel);
        controlPanel.add(Box.createVerticalStrut(5));
        controlPanel.add(colorPanel);
        controlPanel.add(Box.createVerticalStrut(20));
        
        controlPanel.add(addButton);
        controlPanel.add(Box.createVerticalStrut(15));
        
        controlPanel.add(new JSeparator());
        controlPanel.add(Box.createVerticalStrut(15));
        
        controlPanel. add(exampleButton);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(clearButton);
        controlPanel.add(Box.createVerticalGlue());
        
        return controlPanel;
    }

    private JPanel createCanvasPanel() {
        JPanel canvasPanel = new JPanel(new BorderLayout(5, 5));
        canvasPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel canvasLabel = new JLabel("Область відображення документа");
        canvasLabel.setFont(new Font("Arial", Font. BOLD, 14));
        canvasLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        canvas = new DocumentCanvas();
        canvas.setPreferredSize(new Dimension(600, 550));
        canvas. setBorder(BorderFactory.createLineBorder(Color. GRAY, 2));
        
        canvasPanel.add(canvasLabel, BorderLayout.NORTH);
        canvasPanel.add(canvas, BorderLayout.CENTER);
        
        return canvasPanel;
    }

    private JPanel createStatisticsPanel() {
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBorder(new CompoundBorder(
            new EmptyBorder(10, 10, 10, 10),
            new TitledBorder(BorderFactory.createLineBorder(Color.GRAY), 
                           "Статистика патерну Flyweight", 
                           TitledBorder.LEFT, 
                           TitledBorder.TOP,
                           new Font("Arial", Font.BOLD, 13))
        ));
        statsPanel.setPreferredSize(new Dimension(320, 0));
        statsPanel.setBackground(new Color(249, 249, 249));
        
        
        characterCountLabel = new JLabel("Символів: 0");
        characterCountLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        characterCountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        styleCountLabel = new JLabel("Унікальних стилів: 0");
        styleCountLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        styleCountLabel. setAlignmentX(Component. LEFT_ALIGNMENT);
        
        memoryLabel = new JLabel("Економія пам'яті: 0%");
        memoryLabel.setFont(new Font("Arial", Font.BOLD, 13));
        memoryLabel.setForeground(new Color(76, 175, 80));
        memoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel detailsLabel = new JLabel("Детальна статистика:");
        detailsLabel.setFont(new Font("Arial", Font.BOLD, 12));
        detailsLabel. setAlignmentX(Component. LEFT_ALIGNMENT);
        
        statisticsArea = new JTextArea();
        statisticsArea.setEditable(false);
        statisticsArea.setFont(new Font("Courier New", Font.PLAIN, 11));
        statisticsArea.setLineWrap(true);
        statisticsArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(statisticsArea);
        scrollPane.setPreferredSize(new Dimension(300, 400));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        statsPanel.add(characterCountLabel);
        statsPanel. add(Box.createVerticalStrut(8));
        statsPanel.add(styleCountLabel);
        statsPanel.add(Box.createVerticalStrut(8));
        statsPanel.add(memoryLabel);
        statsPanel.add(Box.createVerticalStrut(15));
        statsPanel.add(new JSeparator());
        statsPanel.add(Box.createVerticalStrut(15));
        statsPanel.add(detailsLabel);
        statsPanel.add(Box.createVerticalStrut(8));
        statsPanel.add(scrollPane);
        statsPanel.add(Box.createVerticalGlue());
        
        return statsPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        bottomPanel.setBackground(new Color(224, 224, 224));
        
        JLabel infoLabel = new JLabel("💡 Патерн Flyweight дозволяє ефективно використовувати пам'ять, " +
                                     "повторно використовуючи спільні дані (стилі) між об'єктами (символами)");
        infoLabel.setFont(new Font("Arial", Font. PLAIN, 11));
        
        bottomPanel.add(infoLabel);
        
        return bottomPanel;
    }

    private void chooseColor() {
        Color newColor = JColorChooser.showDialog(this, "Оберіть колір тексту", selectedColor);
        if (newColor != null) {
            selectedColor = newColor;
            colorButton.setBackground(selectedColor);
            
            
            Container parent = colorButton.getParent();
            if (parent != null && parent. getComponentCount() > 2) {
                Component preview = parent.getComponent(2);
                if (preview instanceof JPanel) {
                    preview.setBackground(selectedColor);
                }
            }
            
            
            int brightness = (selectedColor.getRed() + selectedColor.getGreen() + selectedColor.getBlue()) / 3;
            colorButton.setForeground(brightness > 127 ? Color.BLACK : Color. WHITE);
        }
    }

    private void addText() {
        String text = textInput. getText();
        if (text == null || text.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Будь ласка, введіть текст!", 
                "Помилка", 
                JOptionPane. WARNING_MESSAGE);
            return;
        }
        
        String fontName = (String) fontComboBox.getSelectedItem();
        int fontSize = (Integer) fontSizeSpinner.getValue();
        String colorHex = String.format("#%02X%02X%02X", 
            selectedColor.getRed(), 
            selectedColor.getGreen(), 
            selectedColor.getBlue());
        
        
        int startX = 10;
        int startY = document.getCharacterCount() > 0 ? 
                    getLastYPosition() + fontSize + 10 : 30;
        
        
        document.addText(text, startX, startY, fontName, fontSize, colorHex);
        
        
        canvas.repaint();
        updateStatistics();
        
        
        textInput.setText("");
    }

    private void clearDocument() {
        int result = JOptionPane.showConfirmDialog(this,
            "Ви впевнені, що хочете очистити документ? ",
            "Підтвердження",
            JOptionPane.YES_NO_OPTION);
            
        if (result == JOptionPane.YES_OPTION) {
            document.clear();
            canvas.repaint();
            updateStatistics();
        }
    }

    private void showExample() {
        document.clear();
        
        
        document.addText("Привіт!  ", 10, 30, "Arial", 18, "#000000");
        document.addText("Це демонстрація ", 100, 30, "Arial", 18, "#FF0000");
        document.addText("патерну Flyweight!", 280, 30, "Arial", 18, "#0000FF");
        
        document.addText("Зверніть увагу: ", 10, 65, "Times New Roman", 15, "#008000");
        document.addText("один стиль використовується багато разів!", 170, 65, 
                        "Times New Roman", 15, "#008000");
        
        
        StringBuilder largeText = new StringBuilder();
        for (int i = 0; i < 60; i++) {
            largeText.append("ТРПЗ ");
        }
        document.addText(largeText.toString(), 10, 105, "Courier New", 13, "#800080");
        
        document.addText("Патерн Flyweight економить пам'ять!", 10, 210, 
                        "Verdana", 16, "#FF6600");
        
        document. addText("Завдяки цьому патерну тисячі символів", 10, 250, 
                        "Georgia", 14, "#000080");
        document.addText("посилаються лише на декілька об'єктів стилів!", 10, 275, 
                        "Georgia", 14, "#000080");
        
        canvas.repaint();
        updateStatistics();
    }

    private void updateStatistics() {
        int charCount = document.getCharacterCount();
        int styleCount = document.getStyleFactory().getStyleCount();
        
        
        characterCountLabel.setText("Символів: " + charCount);
        styleCountLabel. setText("Унікальних стилів: " + styleCount);
        
        
        if (charCount > 0) {
            long withoutFlyweight = charCount * 100;
            long withFlyweight = charCount * 20 + styleCount * 80;
            long savedMemory = withoutFlyweight - withFlyweight;
            int savedPercent = (int)((savedMemory * 100) / withoutFlyweight);
            
            memoryLabel.setText("Економія пам'яті: " + savedPercent + "% (~" + 
                              savedMemory + " байт)");
            
            
            StringBuilder stats = new StringBuilder();
            stats. append("═══════════════════════════════════\n");
            stats.append("  СТАТИСТИКА ДОКУМЕНТА\n");
            stats. append("═══════════════════════════════════\n\n");
            stats.append("📊 Загальна інформація:\n");
            stats.append("   • Символів: ").append(charCount).append("\n");
            stats.append("   • Унікальних стилів: ").append(styleCount).append("\n\n");
            
            stats.append("💾 Використання пам'яті:\n");
            stats.append("   • Без Flyweight:\n");
            stats.append("     ~").append(withoutFlyweight).append(" байт\n");
            stats.append("   • З Flyweight:\n");
            stats.append("     ~").append(withFlyweight).append(" байт\n");
            stats.append("   • Заощаджено:\n");
            stats.append("     ~").append(savedMemory).append(" байт\n");
            stats.append("   • Економія: ").append(savedPercent). append("%\n\n");
            
            stats.append("🎨 Співвідношення:\n");
            if (styleCount > 0) {
                float ratio = (float)charCount / styleCount;
                stats.append("   • ").append(String.format("%.1f", ratio))
                     .append(" символів на 1 стиль\n");
            }
            
            stats.append("\n═══════════════════════════════════\n");
            stats.append("✅ Патерн Flyweight\n");
            stats.append("   успішно працює!\n");
            stats.append("═══════════════════════════════════\n");
            
            statisticsArea. setText(stats.toString());
        } else {
            memoryLabel.setText("Економія пам'яті: 0%");
            statisticsArea.setText("Документ порожній.\n\nДодайте текст для\nперегляду статистики.");
        }
    }

    private int getLastYPosition() {
        if (document.getCharacters().isEmpty()) {
            return 0;
        }
        
        int maxY = 0;
        for (TextCharacter ch : document.getCharacters()) {
            if (ch.getY() > maxY) {
                maxY = ch.getY();
            }
        }
        return maxY;
    }

    
    private class DocumentCanvas extends JPanel {
        public DocumentCanvas() {
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            
            
            g2d. setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                               RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                               RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            
            for (TextCharacter ch : document.getCharacters()) {
                CharacterStyle style = ch.getStyle();
                
                
                g2d.setFont(new Font(style.getFontName(), Font.PLAIN, style.getFontSize()));
                
                
                g2d.setColor(Color.decode(style.getColor()));
                
                
                g2d.drawString(String.valueOf(ch.getCharacter()), ch.getX(), ch.getY());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FlyweightGUI());
    }
}