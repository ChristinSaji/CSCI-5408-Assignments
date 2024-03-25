package org.example.service;

import org.example.model.News;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ExtractionService {
    private final CleanerService cleanerService;

    /**
     * Constructs an ExtractionService
     * @param cleanerService CleanerService instance
     */
    public ExtractionService(CleanerService cleanerService) {
        this.cleanerService = cleanerService;
    }

    /**
     * Parses the text and extracts news information into a list of News object
     * @param filterData text containing multiple new entries
     * @return list of news objects
     */
    public List<News> parse(String filterData) {
        List<News> newsList = new ArrayList<>();
        Pattern pattern = Pattern.compile("<REUTERS(.*?)</REUTERS>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(filterData);

        while (matcher.find()) {
            String reuterContent = matcher.group(1);
            String title = extractBetweenTags(reuterContent, "TITLE");
            String body = extractBetweenTags(reuterContent, "BODY");
            title = cleanerService.cleanText(title);
            body = cleanerService.cleanText(body);

            newsList.add(new News(title, body));
        }
        return newsList;
    }

    /**
     * Extracts content enclosed between TITLE and BODY tags
     * @param content string containing tags and content
     * @param tag tag name whose content is to be extracted
     * @return content found between tags
     */
    private String extractBetweenTags(String content, String tag) {
        Pattern tagPattern = Pattern.compile("<" + tag + ">(.*?)</" + tag + ">", Pattern.DOTALL);
        Matcher tagMatcher = tagPattern.matcher(content);
        if (tagMatcher.find()) {
            return tagMatcher.group(1);
        }
        return "";
    }
}
