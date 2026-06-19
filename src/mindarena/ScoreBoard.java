package mindarena;

import java.util.ArrayList;
import java.util.List;

/**
 * Tracks the player's score, streaks, and per-question history throughout a quiz session.
 */
public class ScoreBoard {

    private final String playerName;
    private int totalScore;
    private int correctCount;
    private int wrongCount;
    private int skippedCount;          // timed-out questions
    private int currentStreak;
    private int bestStreak;

    // History entry for result summary
    public record Entry(int number, String questionText, char given,
                        char correct, boolean timedOut, int pointsEarned) {}

    private final List<Entry> history = new ArrayList<>();

    public ScoreBoard(String playerName) {
        this.playerName = playerName;
    }

    /**
     * Records a player's answer for one question.
     *
     * @param number      question number (1-based)
     * @param question    the Question that was asked
     * @param given       answer the player gave ('A'-'D'), or '\0' for time-out
     * @param timedOut    true if the question ran out of time
     */
    public void record(int number, Question question, char given, boolean timedOut) {
        boolean correct = !timedOut && question.isCorrect(given);
        int points = correct ? question.getPoints() : 0;

        if (timedOut) {
            skippedCount++;
            currentStreak = 0;
        } else if (correct) {
            totalScore += points;
            correctCount++;
            currentStreak++;
            bestStreak = Math.max(bestStreak, currentStreak);
        } else {
            wrongCount++;
            currentStreak = 0;
        }

        history.add(new Entry(number, question.getText(), given,
                question.getCorrectAnswer(), timedOut, points));
    }

    /** Prints the full end-of-quiz summary to the console. */
    public void printSummary(int totalQuestions) {
        int totalPossible = history.stream()
                .mapToInt(e -> {
                    // find max possible — approximate from points earned or correct answer check
                    // we store pointsEarned only for correct; use difficulty proxy via points
                    return e.pointsEarned() > 0 ? e.pointsEarned() : 10;
                }).sum();

        System.out.println();
        System.out.println(Colour.CYAN + Colour.BOLD
                + "╔══════════════════════════════════════════╗");
        System.out.println("║          🏆  QUIZ RESULTS — MindArena       ║");
        System.out.println("╚══════════════════════════════════════════╝" + Colour.RESET);
        System.out.println();
        System.out.printf("  Player      : %s%s%s%n", Colour.BOLD, playerName, Colour.RESET);
        System.out.printf("  Total Score : %s%d pts%s%n", Colour.GREEN + Colour.BOLD, totalScore, Colour.RESET);
        System.out.printf("  Correct     : %s%d / %d%s%n", Colour.GREEN, correctCount, totalQuestions, Colour.RESET);
        System.out.printf("  Wrong       : %s%d%s%n", Colour.RED, wrongCount, Colour.RESET);
        System.out.printf("  Timed Out   : %s%d%s%n", Colour.YELLOW, skippedCount, Colour.RESET);
        System.out.printf("  Best Streak : %s%d in a row%s%n", Colour.MAGENTA, bestStreak, Colour.RESET);

        System.out.println();
        System.out.println(Colour.BOLD + "  Performance : " + Colour.RESET + getRating(totalQuestions));

        System.out.println();
        System.out.println(Colour.CYAN + "  ── Question Review ──────────────────────" + Colour.RESET);

        for (Entry e : history) {
            String status;
            if (e.timedOut()) {
                status = Colour.YELLOW + "⏰ TIMEOUT" + Colour.RESET;
            } else if (e.given() == e.correct()) {
                status = Colour.GREEN + "✔ CORRECT  (+" + e.pointsEarned() + " pts)" + Colour.RESET;
            } else {
                status = Colour.RED + "✘ WRONG    (correct: " + e.correct() + ")" + Colour.RESET;
            }

            // Truncate long question text for display
            String q = e.questionText().length() > 50
                    ? e.questionText().substring(0, 47) + "..."
                    : e.questionText();

            System.out.printf("  Q%-2d  %-52s  %s%n", e.number(), q, status);
        }

        System.out.println();
        System.out.println(Colour.CYAN + "══════════════════════════════════════════════" + Colour.RESET);
    }

    private String getRating(int totalQuestions) {
        if (totalQuestions == 0) return "N/A";
        double pct = (double) correctCount / totalQuestions * 100;

        if (pct == 100) return Colour.GREEN  + Colour.BOLD + "🌟 Perfect! Genius!" + Colour.RESET;
        if (pct >= 80)  return Colour.GREEN  + "🔥 Excellent!"      + Colour.RESET;
        if (pct >= 60)  return Colour.YELLOW + "👍 Good job!"       + Colour.RESET;
        if (pct >= 40)  return Colour.YELLOW + "📚 Keep practising" + Colour.RESET;
        return                 Colour.RED    + "💪 Don't give up!"  + Colour.RESET;
    }

    // Getters
    public int getTotalScore()   { return totalScore; }
    public int getCorrectCount() { return correctCount; }
    public int getWrongCount()   { return wrongCount; }
    public int getSkippedCount() { return skippedCount; }
    public String getPlayerName(){ return playerName; }
}
