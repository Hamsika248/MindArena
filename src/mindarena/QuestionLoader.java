package mindarena;

import java.io.*;
import java.util.*;

/**
 * Loads questions from a plain-text file.
 *
 * File format (one question block, blank line between questions):
 *   CATEGORY|DIFFICULTY|Question text
 *   A) Option A
 *   B) Option B
 *   C) Option C
 *   D) Option D
 *   ANSWER: X
 *
 * Lines starting with '#' are treated as comments and ignored.
 */
public class QuestionLoader {

    private final String filePath;

    public QuestionLoader(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all questions from the file and returns them as a list.
     */
    public List<Question> loadAll() throws IOException {
        List<Question> questions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            List<String> block = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Skip comments
                if (line.startsWith("#")) continue;

                if (line.isEmpty()) {
                    // End of a block — try to parse it
                    if (!block.isEmpty()) {
                        Question q = parseBlock(block);
                        if (q != null) questions.add(q);
                        block.clear();
                    }
                } else {
                    block.add(line);
                }
            }

            // Parse the last block (no trailing blank line needed)
            if (!block.isEmpty()) {
                Question q = parseBlock(block);
                if (q != null) questions.add(q);
            }
        }

        return questions;
    }

    /**
     * Loads questions filtered by category (case-insensitive).
     */
    public List<Question> loadByCategory(String category) throws IOException {
        List<Question> all = loadAll();
        List<Question> filtered = new ArrayList<>();
        for (Question q : all) {
            if (q.getCategory().equalsIgnoreCase(category)) {
                filtered.add(q);
            }
        }
        return filtered;
    }

    /**
     * Returns all unique categories found in the file.
     */
    public List<String> getCategories() throws IOException {
        List<Question> all = loadAll();
        List<String> categories = new ArrayList<>();
        for (Question q : all) {
            if (!categories.contains(q.getCategory())) {
                categories.add(q.getCategory());
            }
        }
        return categories;
    }

    /**
     * Parses a single question block (6 lines) into a Question object.
     * Returns null if the block is malformed.
     */
    private Question parseBlock(List<String> block) {
        if (block.size() < 6) return null;

        try {
            // Line 0: CATEGORY|DIFFICULTY|Question text
            String[] header = block.get(0).split("\\|", 3);
            if (header.length < 3) return null;

            String category   = header[0].trim();
            String difficulty = header[1].trim();
            String text       = header[2].trim();

            // Lines 1-4: A) ... B) ... C) ... D) ...
            String optA = parseOption(block.get(1));
            String optB = parseOption(block.get(2));
            String optC = parseOption(block.get(3));
            String optD = parseOption(block.get(4));

            // Line 5: ANSWER: X
            String answerLine = block.get(5).toUpperCase();
            if (!answerLine.startsWith("ANSWER:")) return null;
            char answer = answerLine.replace("ANSWER:", "").trim().charAt(0);

            return new Question(category, difficulty, text, optA, optB, optC, optD, answer);

        } catch (Exception e) {
            System.err.println("Warning: Skipping malformed question block: " + block.get(0));
            return null;
        }
    }

    /**
     * Strips the leading "A) ", "B) " etc. prefix from an option line.
     */
    private String parseOption(String line) {
        // Handles formats: "A) text", "A. text", "A: text"
        if (line.length() > 2 && line.charAt(1) == ')' || line.charAt(1) == '.' || line.charAt(1) == ':') {
            return line.substring(2).trim();
        }
        return line.trim();
    }
}
