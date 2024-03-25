package org.example;

import java.util.List;
import org.example.model.News;
import org.example.service.CleanerService;
import org.example.service.ExtractionService;
import org.example.service.FileOperationService;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class ReutReader {

    /**
     * Main method for reading, extracting, cleaning, and storing
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        CleanerService cleanerService = new CleanerService();
        ExtractionService extractionService = new ExtractionService(cleanerService);
        FileOperationService fileOperationService = new FileOperationService(extractionService);

        String uri = "mongodb+srv://christinsaji01:7JcVg9IfKIeA5oIX@cluster0.cfsnxqh.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("ReuterDb");
            MongoCollection<Document> collection = database.getCollection("news");

            String filePath = "reut2-009.sgm";
            List<News> newsList = fileOperationService.processFile(filePath);

            for (News news : newsList) {
                Document article = new Document("title", news.getTitle()).append("body", news.getBody());
                collection.insertOne(article);
            }
        }
    }
}
