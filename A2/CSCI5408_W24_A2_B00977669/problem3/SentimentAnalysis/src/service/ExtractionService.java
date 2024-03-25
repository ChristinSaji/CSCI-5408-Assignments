package service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractionService {
    private final CleanerService cleanerService;

    public ExtractionService(CleanerService cleanerService) {
        this.cleanerService = cleanerService;
    }

    /**
     * Extracts new titles from the given Reuters content
     * @param filterData Reuters content
     * @return list of extracted titles
     */
    public List<String> extractTitles(String filterData) {
        List<String> titles = new ArrayList<>();
        Pattern pattern = Pattern.compile("<REUTERS(.*?)</REUTERS>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(filterData);

        while (matcher.find()) {
            String reuterContent = matcher.group(1);
            String title = extractBetweenTags(reuterContent);
            title = cleanerService.cleanText(title);
            if (!title.isEmpty()) {
                titles.add(title);
            }
        }
        return titles;
    }

    /**
     * Extract content enclosed within Title tags
     * @param content string containing title tags with content
     * @return content between the tags
     */
    private String extractBetweenTags(String content) {
        Pattern tagPattern = Pattern.compile("<" + "TITLE" + ">(.*?)</" + "TITLE" + ">", Pattern.DOTALL);
        Matcher tagMatcher = tagPattern.matcher(content);
        if (tagMatcher.find()) {
            return tagMatcher.group(1);
        }
        return "";
    }
}