package org.example;

import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;
import org.example.service.StopWordsService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SparkFreqCount {
    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder()
                .appName("SparkFreqCount")
                .getOrCreate();

        String dataFilePath = args.length > 0 ? args[0] : "file:///home/christinsaji01/reut2-009.sgm";
        String stopWordsFilePath = args.length > 1 ? args[1] : "file:///home/christinsaji01/stop-words.txt";

        StopWordsService stopWordsService = new StopWordsService();
        Set<String> stopWords = new HashSet<>(stopWordsService.readStopWords(stopWordsFilePath))
                .stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        Dataset<Row> rawData = spark.read().text(dataFilePath);

        Dataset<String> cleanedData = rawData.map(
                (MapFunction<Row, String>) row -> row.getString(0)
                        .replaceAll("<[^>]+>", "") // Remove HTML tags
                        .replaceAll("&[^\\s;]+;", "") // Remove HTML entities
                        .replaceAll("[^a-zA-Z\\s]", "") // Remove numbers and other special characters
                        .toLowerCase(),
                Encoders.STRING());

        Dataset<String> words = cleanedData.flatMap(
                (FlatMapFunction<String, String>) content -> Arrays.stream(content.split("\\s+"))
                        .iterator(),
                Encoders.STRING());

        Dataset<String> meaningfulWords = words.filter(
                (FilterFunction<String>) word -> !(stopWords.contains(word) || word.trim().isEmpty())
                        && word.length() > 1 && word.length() < 15);

        Dataset<Row> wordCounts = meaningfulWords
                .groupBy("value")
                .count()
                .orderBy(functions.desc("count"));

        System.out.println("Highest frequency words:");
        wordCounts.show(5);

        System.out.println("Lowest frequency words:");
        wordCounts.orderBy(functions.asc("count")).show(5);

        spark.stop();
    }
}
