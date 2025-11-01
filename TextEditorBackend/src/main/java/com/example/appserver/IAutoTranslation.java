package com.example.appserver;

public interface IAutoTranslation {
    Document translateDocument(Integer documentId, String targetLanguage);
}
