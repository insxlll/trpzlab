package com.example.appserver;

import java.util.List;

import org.springframework.http.ResponseEntity;

public interface IRepository {
    Document saveDocument(Document document);
    ResponseEntity<Document> getDocument(Long id);
    List<Document> listAll();
    ResponseEntity<Void> delete(Long id);
}
