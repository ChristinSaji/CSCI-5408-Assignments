package org.example.model;

public class News {
    private final String title;
    private final String body;

    /**
     * Constructs a News instance
     * @param title title from reuters
     * @param body body from reuters
     */
    public News(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }
}
