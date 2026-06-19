package mindarena;

import java.util.*;

/**
 * Core game loop for MindArena.
 * Handles question display, user input, timer integration, and score tracking.
 */
public class QuizEngine {

    private static final int TIME_PER_QUESTION_SECONDS = 15;

    private final List<Question> questions;
    private final ScoreBoard scoreBoard;
    private final Scanner scanner;

    public QuizEngine(List<Question> questions, String playerName, Scanner scanner) {
        // Shuffle for variety every session
        this.questions = new ArrayList<>(questions);
        Collections.shuffle(this.questions);
        this.scoreBoard = new ScoreBoard(playerName);
        this.scanner = scanner;
    }

    /** Runs the full quiz and returns the ScoreBoard with results. */
    public ScoreBoard run() {
        int total = questions.size();

        printQuizHeader(total);

        for (int i = 0; i < total; i++) {
            Question q = questions.get(i);
            System.out.println();
            printDivider();
            printQuestion(i + 1, total, q);

            QuizTimer timer = new QuizTimer(TIME_PER_QUESTION_SECONDS);
            timer.start();

            char answer = readAnswer(timer);
            timer.stop();
            timer.waitForFinish();

            if (timer.isTimeUp()) {
                System.out.println("  " + Colour.YELLOW
                        + "No answer recorded. Correct answer was: "
                        + Colour.BOLD + q.getCorrectAnswer() + Colour.RESET);
                scoreBoard.record(i + 1, q, '\0', true);
            } else {
                boolean correct = q.isCorrect(answer);
                if (correct) {
                    System.out.println("  " + Colour.GREEN + Colour.BOLD
                            + "✔  Correct! +" + q.getPoints() + " pts" + Colour.RESET);
                } else {
                    System.out.println("  " + Colour.RED
                            + "✘  Wrong! Correct answer was: "
                            + Colour.BOLD + q.getCorrectAnswer() + Colour.RESET);
                }
                scoreBoard.record(i + 1, q, answer, false);
            }

            // Brief pause between questions
            sleep(800);
        }

        scoreBoard.printSummary(total);
        return scoreBoard;
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private void printQuizHeader(int total) {
        System.out.println();
        System.out.println(Colour.MAGENTA + Colour.BOLD
                + "  🎯  Starting MindArena Quiz — " + total + " questions  "
                + "(" + TIME_PER_QUESTION_SECONDS + "s per question)"
                + Colour.RESET);
        System.out.println("  Type A, B, C, or D and press Enter to answer.");
    }

    private void printDivider() {
        System.out.println(Colour.BLUE + "  ──────────────────────────────────────────" + Colour.RESET);
    }

    private void printQuestion(int number, int total, Question q) {
        // Category & difficulty badge
        String diffColour = switch (q.getDifficulty().toUpperCase()) {
            case "HARD"   -> Colour.RED;
            case "MEDIUM" -> Colour.YELLOW;
            default       -> Colour.GREEN;
        };

        System.out.printf("  %sQ%d / %d%s   %s[%s]%s   %s(%s)%s%n",
                Colour.BOLD + Colour.CYAN, number, total, Colour.RESET,
                Colour.MAGENTA, q.getCategory(), Colour.RESET,
                diffColour, q.getDifficulty(), Colour.RESET);
        System.out.println();
        System.out.println("  " + Colour.BOLD + q.getText() + Colour.RESET);
        System.out.println();

        String[] options = q.getOptions();
        char[] labels = {'A', 'B', 'C', 'D'};
        for (int i = 0; i < options.length; i++) {
            System.out.printf("    %s%c)%s  %s%n",
                    Colour.CYAN, labels[i], Colour.RESET, options[i]);
        }
        System.out.println();
    }

    /**
     * Reads a valid A/B/C/D answer from stdin.
     * Returns '\0' immediately if the timer runs out before the user types.
     */
    private char readAnswer(QuizTimer timer) {
        System.out.print("  Your answer: ");
        System.out.flush();

        // Poll for input while timer is still running
        while (!timer.isTimeUp()) {
            try {
                if (System.in.available() > 0) {
                    String line = scanner.nextLine().trim().toUpperCase();
                    if (!line.isEmpty()) {
                        char ch = line.charAt(0);
                        if (ch == 'A' || ch == 'B' || ch == 'C' || ch == 'D') {
                            return ch;
                        } else {
                            System.out.print("  Invalid! Enter A, B, C, or D: ");
                            System.out.flush();
                        }
                    }
                }
                Thread.sleep(100);
            } catch (Exception e) {
                break;
            }
        }
        return '\0'; // time up
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
