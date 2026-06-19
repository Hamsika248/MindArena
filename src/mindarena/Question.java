package mindarena;

/**
 * Represents a single quiz question with 4 options, a correct answer, category, and difficulty.
 */
public class Question {

    private final String category;
    private final String difficulty;   // EASY | MEDIUM | HARD
    private final String text;
    private final String[] options;    // Always 4 options: A, B, C, D
    private final char correctAnswer;  // 'A', 'B', 'C', or 'D'

    public Question(String category, String difficulty, String text,
                    String optionA, String optionB, String optionC, String optionD,
                    char correctAnswer) {
        this.category = category;
        this.difficulty = difficulty;
        this.text = text;
        this.options = new String[]{optionA, optionB, optionC, optionD};
        this.correctAnswer = Character.toUpperCase(correctAnswer);
    }

    public String getCategory()       { return category; }
    public String getDifficulty()     { return difficulty; }
    public String getText()           { return text; }
    public String[] getOptions()      { return options; }
    public char getCorrectAnswer()    { return correctAnswer; }

    /**
     * Returns points based on difficulty.
     * EASY=5, MEDIUM=10, HARD=15
     */
    public int getPoints() {
        return switch (difficulty.toUpperCase()) {
            case "HARD"   -> 15;
            case "MEDIUM" -> 10;
            default       -> 5;
        };
    }

    public boolean isCorrect(char answer) {
        return Character.toUpperCase(answer) == correctAnswer;
    }

    @Override
    public String toString() {
        return String.format("[%s | %s] %s", category, difficulty, text);
    }
}
