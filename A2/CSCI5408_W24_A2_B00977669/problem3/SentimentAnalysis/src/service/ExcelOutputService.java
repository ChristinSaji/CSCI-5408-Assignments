package service;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ExcelOutputService {

    /**
     * Writes the sentiment analysis results to a CSV file
     * @param fileName name of the file
     * @param results list of sentiment analysis results
     * @throws IOException if an I/O error occurs writing to or creating the file
     */
    public void writeResultsToCSV(String fileName, List<SentimentResult> results) throws IOException {
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(Paths.get(fileName)))) {
            writer.println("News#,Title Content,Match,Score,Polarity");
            for (SentimentResult result : results) {
                String matches = String.join(", ", result.matches());
                writer.println(String.format("%d,\"%s\",\"%s\",%d,%s",
                        result.newsNumber(),
                        result.title(),
                        matches,
                        result.score(),
                        result.polarity()));
            }
        }
    }
}
