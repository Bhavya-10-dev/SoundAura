package soundaura.java.ui;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class LyricsPrinter implements Runnable {
    private static final String RESET = "\033[0m";
    private static final String CYAN = "\033[96m"; // pick any single color, or randomize if you want

    private ArrayList<String> lyrics;
    private double charDelaySeconds;
    private double lineDelaySeconds;
    private AtomicBoolean stopRequested;

    public LyricsPrinter(ArrayList<String> lyrics, double charDelaySeconds, double lineDelaySeconds) {
        this.lyrics = lyrics;
        this.charDelaySeconds = charDelaySeconds;
        this.lineDelaySeconds = lineDelaySeconds;
        this.stopRequested = new AtomicBoolean(false);
    }

    @Override
    public void run() {
        //E.H in threads
        try {
            for (String line : lyrics) {
                if (stopRequested.get()) break;
                for (char c : line.toCharArray()) {
                    if (stopRequested.get()) break;
                    System.out.print(CYAN + c + RESET);
                    System.out.flush();
                    Thread.sleep((long) (charDelaySeconds * 1000));
                }
                System.out.println();
                if (stopRequested.get()) break;
                Thread.sleep((long) (lineDelaySeconds * 1000));
            }
        } 
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
