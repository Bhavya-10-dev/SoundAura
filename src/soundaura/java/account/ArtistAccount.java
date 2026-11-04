package soundaura.java.account;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.Scanner;

// import soundaura.dbms.ArtistDAO;
import soundaura.dbms.ArtistPrivileges;
import soundaura.dbms.TrackDAO;
// import soundaura.dbms.TrackDAO;
import soundaura.java.entities.Artist;
import soundaura.java.entities.Track;
import soundaura.java.ui.AttractiveTerminal;
import soundaura.java.ui.Create;
import soundaura.java.ui.HomePage;
import soundaura.java.ui.MainMenu;
import soundaura.java.ui.Profile;
import soundaura.java.ui.Search;
import soundaura.java.ui.Studio;
import soundaura.java.ui.YourLibrary;

public class ArtistAccount extends ArtistPrivileges implements MainMenu, HomePage, Search, YourLibrary, Profile, Create, Studio{
    private Artist artist; // Holds the active artist session

    public ArtistAccount(Artist artist) {
        this.artist = artist;
    }

    @Override
    public void mainMenu(Scanner sc) {
        while (true) {
            System.out.println("\nMain Menu:");
            System.out.println("1. Home Page");
            System.out.println("2. Search");
            System.out.println("3. Your Library");
            System.out.println("4. Profile");
            System.out.println("5. Create Playlist");
            System.out.println("6. Studio");
            System.out.println("7. Exit");

            int choice;
            try {
                System.out.print("\nEnter your choice: ");
                choice = sc.nextInt();
            }
            catch (InputMismatchException e) {
                AttractiveTerminal.printError("Enter Integer type only.");
                continue;
            }
            finally{
                sc.nextLine();
            }
            switch (choice) {
                case 1 -> {
                    AttractiveTerminal.displayGraphicText("homepage.txt");
                    HomePage.homePageMenu(sc, artist.getUserId());
                }
                case 2 -> {
                    AttractiveTerminal.displayGraphicText("search.txt");
                    Search.searchMenu(sc, artist.getUserId());
                }
                case 3 -> {
                    AttractiveTerminal.displayGraphicText("yourlibrary.txt");
                    YourLibrary.yourLibraryMenu(sc, artist.getUserId());
                }
                case 4 -> {
                    AttractiveTerminal.displayGraphicText("profile.txt");
                    this.profileView();
                }
                case 5 -> {
                    AttractiveTerminal.displayGraphicText("createplaylist.txt");
                    Create.createMenu(sc, artist, super.getPlaylistDao());
                }
                case 6 -> {
                    AttractiveTerminal.displayGraphicText("studio.txt");
                    Studio.studioMenu(sc, artist.getArtistId(), super.getTrackDao());
                }
                case 7 -> {
                    System.out.println("\nExiting the application. Thank You! for using.");
                    return;
                }
                default ->{
                    AttractiveTerminal.printError("INVALID choice. Please try again.");
                }
            }
        }
    }

    public static void uploadTrackMenu(Scanner sc, int artistId, TrackDAO trackDao) {
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
                AttractiveTerminal.printError("Enter Integer type only.");
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
                AttractiveTerminal.printError("INVALID choice. Please enter again.");
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

    @Override
    public void profileView() {
        System.out.println("Artist Profile:");
        System.out.println("Name: " + artist.getUserName());
        System.out.println("Email: " + artist.getEmail());
        String verified = "";
        if (artist.isVerifiedStatus()){
            verified = "Offically Verified";
        }
        else{
            verified = "Not Verified";
        }
        System.out.println("Artist Statuse: " + verified);
        System.out.println("Created At: " + artist.getCreatedAt());
    }
}
