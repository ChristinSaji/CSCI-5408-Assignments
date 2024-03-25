import service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SentimentAnalysis {

    /**
     * Entry point of the application for sentiment analysis
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        CleanerService cleanerService = new CleanerService();
        ExtractionService extractionService = new ExtractionService(cleanerService);
        FileOperationService fileOperationService = new FileOperationService(extractionService);
        WordListService wordListService = new WordListService();
        ExcelOutputService excelService = new ExcelOutputService();

        String filePath = "reut2-009.sgm";

        try {
            Set<String> positiveWords = wordListService.loadWordsFromFile("positive-words.txt");
            Set<String> negativeWords = wordListService.loadWordsFromFile("negative-words.txt");

            List<String> titles = fileOperationService.extractTitlesFromFile(filePath);

            SentimentAnalysisService sentimentService = new SentimentAnalysisService(positiveWords, negativeWords);

            List<SentimentResult> sentimentResults = new ArrayList<>();
            for (int i = 0; i < titles.size(); i++) {
                SentimentResult result = sentimentService.analyzeSentiment(titles.get(i), i + 1);
                sentimentResults.add(result);
            }

            excelService.writeResultsToCSV("sentiment-analysis.csv", sentimentResults);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
