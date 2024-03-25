package service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class FileOperationService {
    private final ExtractionService extractionService;

    public FileOperationService(ExtractionService extractionService) {
        this.extractionService = extractionService;
    }

    /**
     * Reads the content from the given file and extracts titles
     * @param filePath file path
     * @return list of extracted titles
     */
    public List<String> extractTitlesFromFile(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
        List<String> titles = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
            titles = extractionService.extractTitles(contentBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return titles;
    }
}