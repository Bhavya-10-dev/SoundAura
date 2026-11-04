package soundaura.java.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class AttractiveTerminal {
    static final String CYAN = "\u001B[36m";
    static final String LIGHT_CYAN = "\033[96m";
    // static final String LIGHT_YELLOW = "\033[93m";
    public static final String RED = "\033[31m";
    public static final String GREEN = "\033[32m";
    public static final String YELLOW = "\033[33m";
    static final String LIGHT_MAGNENTA = "\033[95m";
    // static final String WHITE = "\033[97m";
    // static final String LIGHT_BLUE = "\033[94m";
    static final String LIGHT_GREEN = "\033[92m";
    static final String RESET = "\u001B[0m";

    public static void displayGraphicText(String path){
        System.out.println("\n"+LIGHT_MAGNENTA);
        File file = new File("graphic_text/"+path);
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while(line!=null)
            {
                System.out.println(line);
                line = br.readLine();
            }
            br.close();
            fr.close();
        } 
        catch (FileNotFoundException e) {
            System.out.println("File Not Found.");
        }
        catch (IOException e) {
            System.out.println("Unable to read from File.");
        }
        System.out.println(RESET);
    }

    public static void displayAnimationText(String path){
        System.out.println("\n"+LIGHT_GREEN);
        File file = new File("graphic_text/"+path);
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while(line!=null)
            {
                System.out.println(line);
                line = br.readLine();
                Thread.sleep(100);
            }
            br.close();
            fr.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("File Not Found.");
        }
        catch (IOException e) {
            System.out.println("Unable to read from File.");
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println(RESET);
    }

    /**
     * Prints a success message in green.
     */
    public static void printGreen(String message) {
        System.out.println(GREEN + message + RESET);
    }

    /*
     * Prints an error message in red.
     */

    public static void printError(String message) {
        System.out.println(RED + "[Error] " + message + RESET);
    }

    /*
     * Prints an informational or system message in yellow.
     */

    public static void printInfo(String message) {
        System.out.println(YELLOW + "[System] " + message + RESET);
    }

    /*
     * Clears the console screen for a cleaner UI experience.
     */
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
