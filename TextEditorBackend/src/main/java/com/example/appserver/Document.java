package com.example.appserver;

import jakarta.persistence.*;

@Entity
@Table(name = "documents")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'Новий текстовий документ'")
    private String title;

    @Column(columnDefinition = "TEXT[]")
    private String[] content;

    public Document() {}

    public Document(String title, String[] content) {
        this.title = title;
        this.content = content;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String[] getContent() { return content; }
    public void setContent(String[] content) { this.content = content; }
}
