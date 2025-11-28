package com.example.appserver;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ExternalServicesImpl implements IExternalServices {

    @Override
    public Document checkSyntax(Integer documentId, String language) {
        
        Document document = getDocumentById(documentId);
        
        if (document == null) {
            throw new IllegalArgumentException("Документ не знайдено: " + documentId);
        }

        
        SyntaxChecker checker = getSyntaxChecker(language);
        
        
        String code = String.join("\n", document.getContent());
        
        
        List<String> errors = checker.checkSyntax(code);
        
        
        if (errors.isEmpty()) {
            document.setTitle(document.getTitle() + " [Синтаксис OK]");
        } else {
            document.setTitle(document.getTitle() + " [Помилки: " + errors.size() + "]");
            
            String[] newContent = new String[document.getContent().length + errors.size() + 1];
            System.arraycopy(document.getContent(), 0, newContent, 0, document.getContent().length);
            newContent[document.getContent().length] = "\n// === РЕЗУЛЬТАТИ ПЕРЕВІРКИ СИНТАКСИСУ ===";
            for (int i = 0; i < errors.size(); i++) {
                newContent[document.getContent().length + 1 + i] = "// " + errors.get(i);
            }
            document.setContent(newContent);
        }
        
        return document;
    }

    @Override
    public Document translateDocument(Integer documentId, String targetLanguage) {
        
        throw new UnsupportedOperationException("Автопереклад ще не реалізовано");
    }

    private SyntaxChecker getSyntaxChecker(String language) {
        switch (language.toLowerCase()) {
            case "java":
                return new JavaSyntaxChecker();
            
            default:
                throw new IllegalArgumentException("Непідтримувана мова: " + language);
        }
    }

    private Document getDocumentById(Integer documentId) {
        
        
        return null;
    }
}