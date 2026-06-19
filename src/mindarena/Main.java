package mindarena;

import java.io.IOException;
import java.util.*;

/**
 * MindArena — Console Quiz Application
 * Entry point: handles welcome screen, category/difficulty selection, and game loop.
 */
public class Main {

    // Path to questions file (relative to where the jar / class is run from)
    private static final String QUESTIONS_FILE = "resources/questions.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        printBanner();

        // Load questions
        QuestionLoader loader = new QuestionLoader(QUESTIONS_FILE);
        List<Question> allQuestions;
        try {
            allQuestions = loader.loadAll();
        } catch (IOException e) {
            System.out.println(Colour.RED + "  Error: Could not load questions file at '"
                    + QUESTIONS_FILE + "'" + Colour.RESET);
            System.out.println("  Make sure you run the program from the MindArena root folder.");
            return;
        }

        if (allQuestions.isEmpty()) {
            System.out.println(Colour.RED + "  No questions found. Check your questions.txt file." + Colour.RESET);
            return;
        }

        boolean playAgain = true;
        while (playAgain) {

            // 1. Get player name
            System.out.print("\n  Enter your name: ");
            String playerName = scanner.nextLine().trim();
            if (playerName.isEmpty()) playerName = "Player";

            // 2. Choose category
            List<String> categories;
            try {
                categories = loader.getCategories();
            } catch (IOException e) {
                categories = new ArrayList<>();
            }

            System.out.println();
            System.out.println(Colour.CYAN + "  ── Choose a Category ───────────────────" + Colour.RESET);
            System.out.println("  0) All Categories");
            for (int i = 0; i < categories.size(); i++) {
                System.out.printf("  %d) %s%n", i + 1, categories.get(i));
            }
            System.out.print("\n  Your choice: ");

            int catChoice = readInt(scanner, 0, categories.size());
            List<Question> filtered;

            if (catChoice == 0) {
                filtered = new ArrayList<>(allQuestions);
            } else {
                String chosen = categories.get(catChoice - 1);
                filtered = allQuestions.stream()
                        .filter(q -> q.getCategory().equalsIgnoreCase(chosen))
                        .toList();
            }

            // 3. Choose difficulty
            System.out.println();
            System.out.println(Colour.CYAN + "  ── Choose Difficulty ───────────────────" + Colour.RESET);
            System.out.println("  0) All Difficulties");
            System.out.println("  1) Easy");
            System.out.println("  2) Medium");
            System.out.println("  3) Hard");
            System.out.print("\n  Your choice: ");

            int diffChoice = readInt(scanner, 0, 3);
            String[] diffMap = {"", "EASY", "MEDIUM", "HARD"};

            if (diffChoice != 0) {
                String chosenDiff = diffMap[diffChoice];
                filtered = filtered.stream()
                        .filter(q -> q.getDifficulty().equalsIgnoreCase(chosenDiff))
                        .toList();
            }

            if (filtered.isEmpty()) {
                System.out.println(Colour.YELLOW
                        + "\n  No questions found for that combination. Try again." + Colour.RESET);
                continue;
            }

            // 4. Choose number of questions
            int maxQ = Math.min(filtered.size(), 20);
            System.out.printf("%n  How many questions? (1–%d): ", maxQ);
            int numQ = readInt(scanner, 1, maxQ);

            filtered = new ArrayList<>(filtered);
            Collections.shuffle(filtered);
            filtered = filtered.subList(0, numQ);

            // 5. Run the quiz
            QuizEngine engine = new QuizEngine(filtered, playerName, scanner);
            engine.run();

            // 6. Play again?
            System.out.println();
            System.out.print("  Play again? (Y/N): ");
            String again = scanner.nextLine().trim().toUpperCase();
            playAgain = again.startsWith("Y");
        }

        System.out.println();
        System.out.println(Colour.CYAN + Colour.BOLD
                + "  Thanks for playing MindArena! See you next time. 👋"
                + Colour.RESET);
        System.out.println();
        scanner.close();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private static void printBanner() {
        System.out.println();
        System.out.println(Colour.CYAN + Colour.BOLD + """
                  ███╗   ███╗██╗███╗   ██╗██████╗      █████╗ ██████╗ ███████╗███╗   ██╗ █████╗
                  ████╗ ████║██║████╗  ██║██╔══██╗    ██╔══██╗██╔══██╗██╔════╝████╗  ██║██╔══██╗
                  ██╔████╔██║██║██╔██╗ ██║██║  ██║    ███████║██████╔╝█████╗  ██╔██╗ ██║███████║
                  ██║╚██╔╝██║██║██║╚██╗██║██║  ██║    ██╔══██║██╔══██╗██╔══╝  ██║╚██╗██║██╔══██║
                  ██║ ╚═╝ ██║██║██║ ╚████║██████╔╝    ██║  ██║██║  ██║███████╗██║ ╚████║██║  ██║
                  ╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝╚═════╝     ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝╚═╝  ╚═══╝╚═╝  ╚═╝
                """ + Colour.RESET);
        System.out.println(Colour.MAGENTA
                + "                        🧠  Test Your Knowledge. Claim the Arena.  🧠"
                + Colour.RESET);
        System.out.println();
    }

    /** Reads an integer from stdin in the range [min, max], re-prompting on invalid input. */
    private static int readInt(Scanner scanner, int min, int max) {
        while (true) {
            try {
                if (!scanner.hasNextLine()) {
                    return min; // fallback for non-interactive environments
                }
                String line = scanner.nextLine().trim();
                int val = Integer.parseInt(line);
                if (val >= min && val <= max) return val;
                System.out.printf("  Please enter a number between %d and %d: ", min, max);
            } catch (NumberFormatException e) {
                System.out.printf("  Invalid input. Enter a number (%d–%d): ", min, max);
            }
        }
    }
}
