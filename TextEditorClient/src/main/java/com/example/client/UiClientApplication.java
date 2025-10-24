package com.example.client;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UiClientApplication extends JFrame {

    private static final String SERVER_URL = "http://localhost:8080/api/documents";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private JTextField idField;
    private JTextField titleField;
    private JTextArea contentArea;
    private JTextArea logArea;

    public UiClientApplication() {
        setTitle("Text Editor Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel top = new JPanel(new BorderLayout());
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.anchor = GridBagConstraints.WEST;

        c.gridx=0; c.gridy=0; form.add(new JLabel("ID (for Open):"), c);
        c.gridx=1; idField = new JTextField(10); form.add(idField, c);

        c.gridx=0; c.gridy=1; form.add(new JLabel("Title:"), c);
        c.gridx=1; titleField = new JTextField(40); form.add(titleField, c);

        c.gridx=0; c.gridy=2; form.add(new JLabel("Content:"), c);
        c.gridx=1; contentArea = new JTextArea(10,40); form.add(new JScrollPane(contentArea), c);

        top.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        JButton saveBtn = new JButton("Save (POST/PUT)");
        JButton openBtn = new JButton("Open by ID (GET)");
        JButton listBtn = new JButton("List All");
        JButton deleteBtn = new JButton("Delete by ID");

        saveBtn.addActionListener(e -> saveDocument());
        openBtn.addActionListener(e -> openById());
        listBtn.addActionListener(e -> listAll());
        deleteBtn.addActionListener(e -> deleteById());

        buttons.add(saveBtn);
        buttons.add(openBtn);
        buttons.add(listBtn);
        buttons.add(deleteBtn);

        top.add(buttons, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        add(new JScrollPane(logArea), BorderLayout.CENTER);
    }

    private void log(String s) {
        logArea.append(s + "\n");
    }

    private void saveDocument() {
        try {
            Document doc = new Document(titleField.getText(), contentArea.getText());
            String json = objectMapper.writeValueAsString(doc);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(SERVER_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            Document saved = objectMapper.readValue(resp.body(), Document.class);
            idField.setText(String.valueOf(saved.getId()));
            log("Saved: ID=" + saved.getId());
        } catch (Exception ex) {
            log("Error saving: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void openById() {
        try {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                log("Enter ID to open.");
                return;
            }
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(SERVER_URL + "/" + id))
                    .GET()
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() == 200) {
                Document d = objectMapper.readValue(resp.body(), Document.class);
                titleField.setText(d.getTitle());
                contentArea.setText(d.getContent());
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
            log("List response: " + resp.body());
        } catch (Exception ex) {
            log("Error listing: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void deleteById() {
        try {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                log("Enter ID to delete.");
                return;
            }
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(SERVER_URL + "/" + id))
                    .DELETE()
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            log("Delete status: " + resp.statusCode());
        } catch (Exception ex) {
            log("Error deleting: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UiClientApplication ui = new UiClientApplication();
            ui.setVisible(true);
        });
    }
}
