package com.example.client;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UiClientApplication extends JFrame {

    private static final String SERVER_URL = "http://localhost:8443/api/documents";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private JTextPane contentArea;
    private JTextArea logArea;
    private String currentDocumentId;
    private String currentTitle;

    public UiClientApplication() {
        setTitle("Новий документ - Текстовий редактор");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        initUI();
        createMenuBar();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        contentArea = new JTextPane();
        add(new JScrollPane(contentArea), BorderLayout.CENTER);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setPreferredSize(new Dimension(700, 100));
        add(new JScrollPane(logArea), BorderLayout.SOUTH);
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

        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
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

        contentArea.setText("");
        currentDocumentId = null;
        currentTitle = null;
        setTitle("Новий документ - Текстовий редактор");
        log("Створено новий документ");
    }

    private void showSaveDialog() {
        JDialog dialog = new JDialog(this, "Зберегти документ", true);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.anchor = GridBagConstraints.WEST;

        JTextField titleField = new JTextField(30);
        if (currentTitle != null) {
            titleField.setText(currentTitle);
        }

        c.gridx=0; c.gridy=0; panel.add(new JLabel("Назва: "), c);
        c.gridx=1; panel.add(titleField, c);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Зберегти");
        JButton cancelButton = new JButton("Скасувати");

        saveButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Назва не може бути порожньою.", "Помилка", JOptionPane.ERROR_MESSAGE);
                return;
            }
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
            String[] content = contentArea.getText().split("\n");
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
                currentDocumentId = String.valueOf(saved.getId());
                currentTitle = title;
                setTitle(currentTitle + " - Текстовий редактор");
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
                currentDocumentId = id;
                currentTitle = d.getTitle();
                setTitle(currentTitle + " - Текстовий редактор");
                String[] content = d.getContent();
                contentArea.setText(content == null ? "" : String.join("\n", content));
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