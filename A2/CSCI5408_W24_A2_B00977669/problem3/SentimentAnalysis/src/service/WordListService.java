package service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class WordListService {

    /**
     * Loads words from a sepecified file
     * @param filePath file path
     * @return set of strings loaded from file
     * @throws IOException I/O errors handling
     */
    public Set<String> loadWordsFromFile(String filePath) throws IOException {
        return new HashSet<>(Files.readAllLines(Paths.get(filePath)));
    }
}
