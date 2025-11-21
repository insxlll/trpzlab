package com.example.client;

import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class AutoSaveObserver implements DocumentObserver {
    private final ObservableDocument document;
    private final DocumentSaveHandler saveHandler;
    private final ScheduledExecutorService scheduler;
    private final long delayMs;
    private ScheduledFuture<?> future;

 
    public AutoSaveObserver(ObservableDocument document, DocumentSaveHandler saveHandler, long delayMs) {
        this.document = Objects.requireNonNull(document);
        this.saveHandler = Objects.requireNonNull(saveHandler);
        this.delayMs = Math.max(100L, delayMs);
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "AutoSaveObserver");
            t.setDaemon(true);
            return t;
        });
    }

    @Override
    public synchronized void documentChanged(DocumentEvent event) {
        if (event.getType() == DocumentEvent.Type.CONTENT_CHANGED || event.getType() == DocumentEvent.Type.CLEARED) {
            
            if (future != null && !future.isDone()) {
                future.cancel(false);
            }
            future = scheduler.schedule(() -> {
                try {
                    saveHandler.save(document);
                } catch (Exception ex) {
                    System.err.println("[AutoSaveObserver] save handler failed: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }, delayMs, TimeUnit.MILLISECONDS);
        }
    }

    public void shutdown() {
        try {
            scheduler.shutdownNow();
        } catch (Exception ignored) {}
    }
}