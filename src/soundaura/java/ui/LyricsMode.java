package soundaura.java.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import soundaura.java.entities.Track;

public class LyricsMode {
    private static String lyrics;
    private static ArrayList<String> lyricsLines;
    private static double charDelaySeconds = 0.05; // delay between characters
    private static double lineDelaySeconds = 0.2; // delay between lines
    private static AtomicBoolean stopRequested = new AtomicBoolean(false);

    public static void lyrics(Track track, Scanner sc) {
        System.out.println("\nLyrics Mode");
        System.out.println();
        lyrics = track.getLyrics();

        if (lyrics == null || lyrics.isEmpty()) {
            System.out.println("No lyrics available for this track.");
        } 
        else {
            lyricsLines = new ArrayList<>(Arrays.asList(lyrics.split("\n")));
            LyricsPrinter lp = new LyricsPrinter(lyricsLines, charDelaySeconds, lineDelaySeconds);
            Thread printThread = new Thread(lp);
            printThread.start();
            while (printThread.isAlive()) {
                if (sc.hasNextLine()) {
                    String input = sc.nextLine().trim();
                    if (input.equals("q") || input.equals("0")) {
                        stopRequested.set(true);
                        printThread.interrupt();
                        break;
                    }
                }
            }
            //E.H in threads
            try {
                printThread.join(); // wait for print thread to actually finish/stop
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            System.out.println(); // clean newline before returning to menu
        }
    }
}
