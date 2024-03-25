package service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SentimentAnalysisService {
    private final Set<String> positiveWords;
    private final Set<String> negativeWords;

    public SentimentAnalysisService(Set<String> positiveWords, Set<String> negativeWords) {
        this.positiveWords = positiveWords;
        this.negativeWords = negativeWords;
    }

    /**
     * Analyze the sentiment of a given title by counting the frequency of positive and negative words
     * @param title title content to analyze
     * @param newsNumber number associated with bag of words
     * @return a SentimentResult object
     */
    public SentimentResult analyzeSentiment(String title, int newsNumber) {
        String[] words = title.split("\\s+");
        int score = 0;
        List<String> matches = new ArrayList<>();

        for (String word : words) {
            word = word.toLowerCase();
            if (positiveWords.contains(word)) {
                score++;
                matches.add(word);
            } else if (negativeWords.contains(word)) {
                score--;
                matches.add(word);
            }
        }

        String polarity = score > 0 ? "Positive" : score < 0 ? "Negative" : "Neutral";
        return new SentimentResult(newsNumber, title, String.join(", ", matches), score, polarity);
    }
}
