package service;

public class CleanerService {

    /**
     * Cleans given text by removing HTML entities and special characters
     * @param text text to clean
     * @return cleaned text
     */
    public String cleanText(String text) {
        return text.replaceAll("&[^\\s;]+;", "") // Remove HTML entities
                .replaceAll("[^a-zA-Z0-9\\s]", ""); // Remove other special characters
    }
}
