package org.example;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import static org.apache.spark.sql.functions.*;

public class WordCounter {
    public static void main(String[] args) {

        SparkSession spark = SparkSession
                .builder()
                .appName("WordCounter")
                .getOrCreate();

        String filePath = args.length > 0 ? args[0] : "file:///home/christinsaji01/input.txt";

        Dataset<Row> df = spark.read().text(filePath).as("value");
        Dataset<Row> words = df.select(explode(split(col("value"), "\\s+")).as("word"));
        Dataset<Row> wordCounts = words.groupBy("word").count();

        wordCounts.show();

        spark.stop();
    }
}
