package service;

/**
 * Represents the sentiment analysis result
 * @param newsNumber unique identifier
 * @param title title of news
 * @param matches comma-seperated list of words
 * @param score sentiment score
 * @param polarity overall sentiment, can be "Positive", "Negative", or "Neutral"
 */
public record SentimentResult(int newsNumber, String title, String matches, int score, String polarity) {
}
