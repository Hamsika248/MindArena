package mindarena;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Per-question countdown timer running on a background thread.
 * Displays a live ticking countdown on the same console line.
 * When time runs out it sets a flag so QuizEngine can detect it.
 */
public class QuizTimer {

    private final int seconds;
    private final AtomicBoolean timeUp = new AtomicBoolean(false);
    private final AtomicBoolean stopped = new AtomicBoolean(false);
    private Thread timerThread;

    public QuizTimer(int seconds) {
        this.seconds = seconds;
    }

    /** Starts the countdown in a daemon background thread. */
    public void start() {
        timeUp.set(false);
        stopped.set(false);

        timerThread = new Thread(() -> {
            try {
                for (int remaining = seconds; remaining >= 0; remaining--) {
                    if (stopped.get()) return;

                    // Colour: green > 5s, yellow 3-5s, red <= 2s
                    String colour;
                    if (remaining > 5)      colour = Colour.GREEN;
                    else if (remaining > 2) colour = Colour.YELLOW;
                    else                    colour = Colour.RED;

                    // \r rewrites the same line
                    System.out.print("\r  " + colour + "⏱  Time remaining: "
                            + remaining + "s   " + Colour.RESET);
                    System.out.flush();

                    if (remaining == 0) break;
                    Thread.sleep(1000);
                }

                if (!stopped.get()) {
                    timeUp.set(true);
                    System.out.println("\r  " + Colour.RED
                            + "⏰  Time's up!              " + Colour.RESET);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        timerThread.setDaemon(true);
        timerThread.start();
    }

    /** Stops the timer (called when the player submits an answer). */
    public void stop() {
        stopped.set(true);
        if (timerThread != null) timerThread.interrupt();
        // Clear the timer line
        System.out.print("\r" + " ".repeat(40) + "\r");
        System.out.flush();
    }

    /** Returns true if the countdown reached zero before stop() was called. */
    public boolean isTimeUp() {
        return timeUp.get();
    }

    /** Waits for the timer thread to finish (used to sync output). */
    public void waitForFinish() {
        if (timerThread != null) {
            try { timerThread.join(100); } catch (InterruptedException ignored) {}
        }
    }
}
