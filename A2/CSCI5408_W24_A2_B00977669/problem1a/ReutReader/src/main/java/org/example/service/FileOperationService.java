package org.example.service;

import org.example.model.News;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class FileOperationService {
    private final ExtractionService extractionService;

    /**
     * Constructs a FileOperationService
     * @param extractionService ExtractionService instance
     */
    public FileOperationService(ExtractionService extractionService) {
        this.extractionService = extractionService;
    }

    /**
     * Processes the given file path to extract News object
     * @param filePath path to news data file
     * @return list of News object
     */
    public List<News> processFile(String filePath) {
        List<News> newsList = new ArrayList<>();
        StringBuilder contentBuilder = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }

            newsList = extractionService.parse(contentBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newsList;
    }
}
