package com.example.client;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UiClientApplication extends JFrame {

    private static final String SERVER_URL = "http://localhost:8443/api/documents";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private JStrategyTextPane contentArea;
    private JTextArea logArea;

    
    private ObservableDocument currentDocument;
    private DocumentController documentController;
    private TextChangeTracker textChangeTracker;
    private DocumentObserver uiDocumentObserver;

    
    private JPanel statsPanel;
    private JLabel wordCountLabel;
    private JLabel charCountLabel;
    private DocumentObserver uiStatsObserver;

    
    private ChangeCounterObserver changeCounterObserver;
    private UnsavedChangesObserver unsavedChangesObserver;
    private WordCountObserver wordCountObserver;
    private VersionHistoryObserver versionHistoryObserver;
    private AutoSaveObserver autoSaveObserver;
    private DocumentSaveHandler saveHandler;

    private String currentDocumentId;
    private String currentTitle;

    public UiClientApplication() {
        setTitle("Новий документ - Текстовий редактор");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        initUI();
        createMenuBar();

        
        initCommandController();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        contentArea = new JStrategyTextPane();
        add(new JScrollPane(contentArea), BorderLayout.CENTER);

        
        statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        wordCountLabel = new JLabel("Слів: 0");
        charCountLabel = new JLabel("Символів: 0");
        statsPanel.add(wordCountLabel);
        statsPanel.add(Box.createHorizontalStrut(10));
        statsPanel.add(charCountLabel);

        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(statsPanel, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setPreferredSize(new Dimension(700, 100));
        bottomPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Файл");

        JMenuItem newItem = new JMenuItem("Новий документ");
        JMenuItem saveItem = new JMenuItem("Зберегти");
        JMenuItem openItem = new JMenuItem("Відкрити");
        JMenuItem listItem = new JMenuItem("Показати всі документи");
        JMenuItem deleteItem = new JMenuItem("Видалити");

        newItem.addActionListener(e -> createNewDocument());
        saveItem.addActionListener(e -> showSaveDialog());
        openItem.addActionListener(e -> showOpenDialog());
        listItem.addActionListener(e -> listAll());
        deleteItem.addActionListener(e -> showDeleteDialog());

        fileMenu.add(newItem);
        fileMenu.addSeparator();
        fileMenu.add(saveItem);
        fileMenu.add(openItem);
        fileMenu.add(listItem);
        fileMenu.add(deleteItem);

        JMenu editMenu = new JMenu("Редагувати");

        JMenuItem clearItem = new JMenuItem("Очистити текст");
       /*/ JMenuItem lowerCaseItem = new JMenuItem("До нижнього регістру");
        JMenuItem upperCaseItem = new JMenuItem("До верхнього регістру");
        JMenuItem sentenceCaseItem = new JMenuItem("З великої літери"); */

        
        clearItem.addActionListener(e -> {
            if (textChangeTracker != null) textChangeTracker.captureNow();
            documentController.clearDocument();
            if (textChangeTracker != null) textChangeTracker.updateTextFromController();
        });

        

        editMenu.add(clearItem);
       /*/ editMenu.add(lowerCaseItem);
        editMenu.add(upperCaseItem);
        editMenu.add(sentenceCaseItem);*/

        
        JMenuItem undoItem = new JMenuItem("Скасувати");
        JMenuItem redoItem = new JMenuItem("Повторити");
        JMenuItem findReplaceItem = new JMenuItem("Знайти та замінити");

        undoItem.addActionListener(e -> performUndo());
        redoItem.addActionListener(e -> performRedo());
        findReplaceItem.addActionListener(e -> performFindReplace());

        
        JMenuItem restoreLastVersion = new JMenuItem("Відновити останню локальну версію");
        restoreLastVersion.addActionListener(e -> {
            if (versionHistoryObserver != null && currentDocument != null) {
                String restored = versionHistoryObserver.restoreLast(currentDocument);
                if (restored != null) {
                    
                    if (textChangeTracker != null) textChangeTracker.updateTextFromController();
                    log("Restored last local version.");
                } else {
                    log("No local versions to restore.");
                }
            }
        });

        editMenu.addSeparator();
        editMenu.add(undoItem);
        editMenu.add(redoItem);
        editMenu.addSeparator();
        editMenu.add(findReplaceItem);
        editMenu.addSeparator();
        editMenu.add(restoreLastVersion);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        setJMenuBar(menuBar);
    }

    
    private void unregisterObservers() {
        if (currentDocument == null) return;
        if (uiDocumentObserver != null) currentDocument.removeObserver(uiDocumentObserver);
        if (uiStatsObserver != null) currentDocument.removeObserver(uiStatsObserver);
        if (changeCounterObserver != null) currentDocument.removeObserver(changeCounterObserver);
        if (unsavedChangesObserver != null) currentDocument.removeObserver(unsavedChangesObserver);
        if (wordCountObserver != null) currentDocument.removeObserver(wordCountObserver);
        if (versionHistoryObserver != null) currentDocument.removeObserver(versionHistoryObserver);
        if (autoSaveObserver != null) {
            currentDocument.removeObserver(autoSaveObserver);
            autoSaveObserver.shutdown();
            autoSaveObserver = null;
        }
    }

    private void registerObservers() {
        if (currentDocument == null) return;

        
        if (changeCounterObserver == null) changeCounterObserver = new ChangeCounterObserver();
        if (unsavedChangesObserver == null) unsavedChangesObserver = new UnsavedChangesObserver();
        if (wordCountObserver == null) wordCountObserver = new WordCountObserver();
        if (versionHistoryObserver == null) versionHistoryObserver = new VersionHistoryObserver(20);

        
        if (saveHandler == null) {
            saveHandler = document -> {
                
                String titleToSave = currentTitle != null && !currentTitle.isBlank()
                        ? currentTitle
                        : "Автозбережено " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                
                SwingUtilities.invokeLater(() -> {
                    try {
                        
                        if (textChangeTracker != null) textChangeTracker.captureNow();
                        saveDocument(titleToSave);
                        
                        if (unsavedChangesObserver != null) unsavedChangesObserver.markSaved();
                    } catch (Exception ex) {
                        log("Auto-save error: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                });
            };
        }

        
        uiDocumentObserver = event -> SwingUtilities.invokeLater(() -> {
            if (textChangeTracker != null) textChangeTracker.updateTextFromController();
            log("Document event: " + event.getType());
        });

        
        uiStatsObserver = event -> SwingUtilities.invokeLater(() -> {
            if (event.getType() == DocumentEvent.Type.CONTENT_CHANGED || event.getType() == DocumentEvent.Type.CLEARED) {
                String text = event.getNewText() != null ? event.getNewText() : "";
                updateStatsFromText(text);
            }
        });

        
        currentDocument.addObserver(uiDocumentObserver);
        currentDocument.addObserver(uiStatsObserver);
        currentDocument.addObserver(changeCounterObserver);
        currentDocument.addObserver(unsavedChangesObserver);
        currentDocument.addObserver(wordCountObserver);
        currentDocument.addObserver(versionHistoryObserver);

        
        if (autoSaveObserver == null) {
            autoSaveObserver = new AutoSaveObserver(currentDocument, saveHandler, 2000);
        }
        currentDocument.addObserver(autoSaveObserver);
    }

    
    private void initCommandController() {
        if (currentDocument == null) {
            currentDocument = new ObservableDocument("Новий документ", new String[0]);
        }
        documentController = new DocumentController(currentDocument);
        
        textChangeTracker = new TextChangeTracker((JTextComponent) contentArea, documentController);

        
        registerObservers();

        
        textChangeTracker.updateTextFromController();
        
        updateStatsFromText(documentController.getText());
    }

    private void createNewDocument() {
        if (!contentArea.getText().isEmpty() || (currentTitle != null && !currentTitle.isEmpty())) {
            int result = JOptionPane.showConfirmDialog(this,
                    "Бажаєте зберегти поточний документ?",
                    "Новий документ",
                    JOptionPane.YES_NO_CANCEL_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                showSaveDialog();
            } else if (result == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }

        
        unregisterObservers();

        
        currentDocument = new ObservableDocument("Новий документ", new String[0]);
        currentDocumentId = null;
        currentTitle = null;
        setTitle("Новий документ - Текстовий редактор");

        
        documentController = new DocumentController(currentDocument);
        textChangeTracker = new TextChangeTracker((JTextComponent) contentArea, documentController);

        
        registerObservers();

        
        contentArea.setText("");
        updateStatsFromText("");
        log("Створено новий документ");
    }

    private void showSaveDialog() {
        JDialog dialog = new JDialog(this, "Зберегти документ", true);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;

        JTextField titleField = new JTextField(30);
        if (currentTitle != null) {
            titleField.setText(currentTitle);
        }

        c.gridx = 0;
        c.gridy = 0;
        panel.add(new JLabel("Назва: "), c);
        c.gridx = 1;
        panel.add(titleField, c);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Зберегти");
        JButton cancelButton = new JButton("Скасувати");

        saveButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Назва не може бути порожньою.", "Помилка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (textChangeTracker != null) textChangeTracker.captureNow();
            saveDocument(title);
            dialog.dispose();
        });
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showOpenDialog() {
        String id = JOptionPane.showInputDialog(this,
                "Введіть id документа:",
                "Відкрити документ",
                JOptionPane.QUESTION_MESSAGE);

        if (id != null && !id.trim().isEmpty()) {
            openById(id.trim());
        }
    }

    private void showDeleteDialog() {
        String id = JOptionPane.showInputDialog(this,
                "Введіть id документа для видалення:",
                "Видалити документ",
                JOptionPane.WARNING_MESSAGE);

        if (id != null && !id.trim().isEmpty()) {
            if (JOptionPane.showConfirmDialog(this,
                    "Ви справді хочете видалити документ № " + id + "?",
                    "Підтвердити видалення",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                deleteById(id.trim());
            }
        }
    }

    private void log(String s) {
        logArea.append(s + "\n");
    }

    private void saveDocument(String title) {
        try {
            
            if (textChangeTracker != null) textChangeTracker.captureNow();

            String[] content = documentController.getText().split("\n", -1);
            Document doc = new Document(title, content);

            HttpRequest req;
            if (currentDocumentId != null && !currentDocumentId.isEmpty()) {

                try {
                    doc.setId(Long.parseLong(currentDocumentId));
                } catch (NumberFormatException nfe) {
                    log("Невірний формат ID для оновлення.");
                    return;
                }
                String json = objectMapper.writeValueAsString(doc);
                req = HttpRequest.newBuilder()
                        .uri(URI.create(SERVER_URL + "/" + currentDocumentId))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(json))
                        .build();
            } else {

                String json = objectMapper.writeValueAsString(doc);
                req = HttpRequest.newBuilder()
                        .uri(URI.create(SERVER_URL))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();
            }

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() == 200 || resp.statusCode() == 201) {
                Document saved = objectMapper.readValue(resp.body(), Document.class);
                
                unregisterObservers();
                ObservableDocument od = new ObservableDocument(saved.getTitle(), saved.getContent());
                od.setId(saved.getId());
                currentDocument = od;
                currentDocumentId = String.valueOf(saved.getId());
                currentTitle = title;
                setTitle(currentTitle + " - Текстовий редактор");

                
                documentController = new DocumentController(currentDocument);
                textChangeTracker = new TextChangeTracker((JTextComponent) contentArea, documentController);
                registerObservers();
                textChangeTracker.updateTextFromController();

                
                if (unsavedChangesObserver != null) unsavedChangesObserver.markSaved();

                
                updateStatsFromText(documentController.getText());

                log("Saved: ID=" + saved.getId());
            } else if (resp.statusCode() == 404) {
                log("Document not found for update (status=404).");
            } else {
                log("Error saving (status=" + resp.statusCode() + "): " + resp.body());
            }
        } catch (Exception ex) {
            log("Error saving: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void openById(String id) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(SERVER_URL + "/" + id))
                    .GET()
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() == 200) {
                Document d = objectMapper.readValue(resp.body(), Document.class);

                
                unregisterObservers();
                ObservableDocument od = new ObservableDocument(d.getTitle(), d.getContent());
                od.setId(d.getId());
                currentDocument = od;
                currentDocumentId = id;
                currentTitle = d.getTitle();
                setTitle(currentTitle + " - Текстовий редактор");

                documentController = new DocumentController(currentDocument);
                textChangeTracker = new TextChangeTracker((JTextComponent) contentArea, documentController);
                registerObservers();
                textChangeTracker.updateTextFromController();

                
                updateStatsFromText(documentController.getText());

                log("Loaded ID=" + d.getId());
            } else {
                log("Document not found (status=" + resp.statusCode() + ")");
            }
        } catch (Exception ex) {
            log("Error loading: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void listAll() {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(SERVER_URL))
                    .GET()
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() == 200) {
                log("All documents: " + resp.body());
            } else {
                log("Error listing (status=" + resp.statusCode() + "): " + resp.body());
            }
        } catch (Exception ex) {
            log("Error listing: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void deleteById(String id) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(SERVER_URL + "/" + id))
                    .DELETE()
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            log("Delete status: " + resp.statusCode());
            if (resp.statusCode() == 200 || resp.statusCode() == 204) {
                if (id.equals(currentDocumentId)) {
                    contentArea.setText("");
                    currentDocumentId = null;
                    currentTitle = null;
                    setTitle("Новий документ - Текстовий редактор");
                    updateStatsFromText("");
                }
                log("Deleted ID=" + id);
            } else {
                log("Error deleting (status=" + resp.statusCode() + "): " + resp.body());
            }
        } catch (Exception ex) {
            log("Error deleting: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    
    private void performUndo() {
        
        if (textChangeTracker != null) textChangeTracker.captureNow();

        if (documentController != null && documentController.canUndo()) {
            documentController.undo();
            if (textChangeTracker != null) textChangeTracker.updateTextFromController();
            
            updateStatsFromText(documentController.getText());
            log("Undo performed");
        } else {
            log("Nothing to undo");
        }
    }

    private void performRedo() {
        if (textChangeTracker != null) textChangeTracker.captureNow();

        if (documentController != null && documentController.canRedo()) {
            documentController.redo();
            if (textChangeTracker != null) textChangeTracker.updateTextFromController();
            
            updateStatsFromText(documentController.getText());
            log("Redo performed");
        } else {
            log("Nothing to redo");
        }
    }

    private void performFindReplace() {
        try {
            if (textChangeTracker != null) textChangeTracker.captureNow();

            FindReplaceDialog dialog = new FindReplaceDialog(this);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                String findText = dialog.getFindText();
                String replaceText = dialog.getReplaceText();
                if (findText != null && !findText.isEmpty()) {
                    documentController.findAndReplace(findText, replaceText != null ? replaceText : "");
                    if (textChangeTracker != null) textChangeTracker.updateTextFromController();
                    
                    updateStatsFromText(documentController.getText());
                    log("Replaced '" + findText + "' with '" + replaceText + "'");
                }
            }
        } catch (Exception ex) {
            log("Error in Find&Replace: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

   
    private void updateStatsFromText(String text) {
        if (text == null) text = "";
        final String t = text;
        SwingUtilities.invokeLater(() -> {
            int charCount = t.length();
            String trimmed = t.trim();
            int wordCount = trimmed.isEmpty() ? 0 : trimmed.split("\\s+").length;
            wordCountLabel.setText("Слів: " + wordCount);
            charCountLabel.setText("Символів: " + charCount);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                UiClientApplication ui = new UiClientApplication();
                ui.setVisible(true);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
        });
    }
}