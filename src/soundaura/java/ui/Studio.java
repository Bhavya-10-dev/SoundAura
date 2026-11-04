package soundaura.java.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.Scanner;

import soundaura.dbms.TrackDAO;
import soundaura.java.entities.Track;

public interface Studio {
    public static void studioMenu(Scanner sc, int artistId, TrackDAO trackDAO){
        while (true) {
            System.out.println("\nStudio Menu:");
            System.out.println("1. Upload Track");
            System.out.println("2. Exit");

            int choice;
            try {
                System.out.print("\nEnter your choice: ");
                choice = sc.nextInt();
            }
            catch (InputMismatchException e) {
                AttractiveTerminal.printError("Enter Integer datatype only.");
                continue;
            }
            finally{
                sc.nextLine();
            }
            switch (choice) {
                case 1 -> {
                    Studio.uploadTrack(sc, artistId, trackDAO);
                }
                case 2 -> {
                    return;
                }
                default ->{
                    AttractiveTerminal.printError("INVALID choice. Please try again.");
                }
            }
        }
    }

    public static void uploadTrack(Scanner sc, int artistId, TrackDAO trackDao) {
        System.out.println("\nUpload a New Track");

        System.out.print("Enter track title: ");
        String title = sc.nextLine(); //E.H

        int artist_id = artistId;

        System.out.print("Enter genre: ");
        String genre = sc.nextLine(); //E.H

        long durationSeconds;
        while (true) {
            try {
                System.out.print("Enter duration in seconds: ");
                durationSeconds = sc.nextLong();
                sc.nextLine();
                break;
            } 
            catch (InputMismatchException e) {
                AttractiveTerminal.printError("Enter Integer datatype only.");
                sc.nextLine();
            }
        }

        // File-path approach for audio, matching the rest of the project
        File audioFile;
        String filePath;
        while (true) {
            System.out.print("Enter path to the MP3 file: ");
            filePath = sc.nextLine(); //E.H
            audioFile = new File("artist/track/"+filePath);
            if (audioFile.exists()) {
                break;
            }
            AttractiveTerminal.printError("File not found at that path. Please check and try again.");
        }

        String lyrics = "";
        while (true) {
            System.out.print("Do you want to add lyrics now? (Y/N): ");
            String addLyrics = sc.nextLine(); //E.H
            if (addLyrics.equalsIgnoreCase("Y")) {
                System.out.print("Enter path to the lyrics .txt file: ");
                String lyricsPath = sc.nextLine(); //E.H
                try {
                    lyricsPath = "artist/lyrics/"+lyricsPath;
                    BufferedReader br = new BufferedReader(new FileReader(lyricsPath));
                    String line = br.readLine();
                    while (line!=null) {
                        lyrics = lyrics+line;
                        line = br.readLine();
                    }
                    br.close();
                    break;
                } 
                catch (IOException e) {
                    AttractiveTerminal.printError("Could not read lyrics file — uploading track without lyrics.");
                    continue;
                }
            }
            else if (addLyrics.equalsIgnoreCase("N")){
                System.out.println("Uploading track without lyrics.");
                break;
            }
            else{
                AttractiveTerminal.printError("INVALID choice. Please Enter Again.");
                continue;
            }
            
        }

        LocalDate releaseDate = LocalDate.now();
                
        Track track = new Track(title, artist_id, genre, durationSeconds, lyrics, filePath, releaseDate);

        boolean success = trackDao.uploadTrack(track);
        if (success) {
            AttractiveTerminal.printGreen("[Success] Track uploaded successfully! It will appear once approved by an admin.");
        } 
        else {
            AttractiveTerminal.printError("Failed to upload track. Please try again.");
        }
    }
}
