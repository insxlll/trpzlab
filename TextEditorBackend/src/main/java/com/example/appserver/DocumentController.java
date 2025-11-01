package com.example.appserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController implements IRepository {

    @Autowired
    private DocumentRepository repository;

    // Create or update (if id present)
    @PostMapping
    public Document saveDocument(@RequestBody Document document) {
        System.out.println("[Backend] Save request: " + document.getTitle());
        return repository.save(document);
    }

    // Get by id
    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocument(@PathVariable Long id) {
        System.out.println("[Backend] Get request for id: " + id);
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // List all
    @GetMapping
    public List<Document> listAll() {
        return repository.findAll();
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
