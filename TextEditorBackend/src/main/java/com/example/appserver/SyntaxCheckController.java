package com.example.appserver;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/syntax")
@CrossOrigin(origins = "*")
public class SyntaxCheckController {

    @PostMapping("/check")
    public ResponseEntity<SyntaxCheckResponse> checkSyntax(@RequestBody SyntaxCheckRequest request) {
        try {
            String code = request.getCode();
            String language = request.getLanguage();

            if (code == null || code.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new SyntaxCheckResponse(
                        List.of("Код порожній"),
                        false,
                        "Неможливо перевірити порожній код"
                    ));
            }

            
            SyntaxChecker checker = getSyntaxChecker(language);
            
            
            List<String> errors = checker.checkSyntax(code);

            
            boolean hasRealErrors = errors.stream()
                .anyMatch(e -> e.contains("ПОМИЛКА"));
            
            boolean success = !hasRealErrors;

            String message = errors.isEmpty() || success
                ? "Синтаксис коректний" 
                : "Знайдено проблеми у коді";

            return ResponseEntity.ok(new SyntaxCheckResponse(errors, success, message));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                .body(new SyntaxCheckResponse(
                    List.of("Помилка сервера: " + e.getMessage()),
                    false,
                    "Внутрішня помилка сервера"
                ));
        }
    }

    private SyntaxChecker getSyntaxChecker(String language) {
        if (language == null) {
            language = "java";
        }
        
        switch (language.toLowerCase()) {
            case "java":
                return new JavaSyntaxChecker();
            default:
                throw new IllegalArgumentException("Непідтримувана мова: " + language);
        }
    }


    public static class SyntaxCheckRequest {
        private String code;
        private String language;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }
    }

    public static class SyntaxCheckResponse {
        private List<String> errors;
        private boolean success;
        private String message;

        public SyntaxCheckResponse(List<String> errors, boolean success, String message) {
            this.errors = errors;
            this.success = success;
            this.message = message;
        }

        public List<String> getErrors() {
            return errors;
        }

        public void setErrors(List<String> errors) {
            this.errors = errors;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}