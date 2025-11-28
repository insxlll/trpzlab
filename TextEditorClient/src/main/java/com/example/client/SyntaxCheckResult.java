package com.example.client;

import java.util.ArrayList;
import java.util.List;

public class SyntaxCheckResult {
    private List<String> errors;
    private boolean success;
    private String message;

    public SyntaxCheckResult() {
        this.errors = new ArrayList<>();
        this.success = false;
    }

    public SyntaxCheckResult(List<String> errors, boolean success, String message) {
        this.errors = errors != null ? errors : new ArrayList<>();
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

    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }

    public int getErrorCount() {
        return errors != null ? errors.size() : 0;
    }
}