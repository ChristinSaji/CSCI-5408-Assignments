package org.example.service;

public class CleanerService {

    /**
     * Clean text by removing HTML entities and special characters
     * @param text raw text
     * @return cleaned text
     */
    public String cleanText(String text) {
        return text.replaceAll("&[^\\s;]+;", "") // Remove HTML entities
                .replaceAll("[^a-zA-Z0-9\\s]", ""); // Remove other special characters
    }
}
