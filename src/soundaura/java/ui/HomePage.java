package soundaura.java.ui;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import soundaura.dbms.PlaylistDAO;
import soundaura.dbms.TrackDAO;
import soundaura.java.entities.Playlist;
import soundaura.java.entities.Track;

public interface HomePage {
    public static void homePageMenu(Scanner sc, int user_id) {
        while (true) {
            System.out.println("\nHome Page Menu:");
            System.out.println("1. Continue Listening");
            System.out.println("2. Discover Latest Playlists");
            System.out.println("3. Your Playlists");
            System.out.println("4. Back to Main Menu");

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
                    continueListening(sc, user_id);
                }
                case 2 -> {
                    randomPlaylists(sc, user_id);
                }
                case 3 -> {
                    YourLibrary.yourPlaylists(sc, user_id);
                }
                case 4 -> {
                    return;
                }
                default ->{
                    AttractiveTerminal.printError("INVALID choice. Please try again.");
                }
            }
        }
    }

    public static void continueListening(Scanner sc, int user_id){
        ArrayList<Track> tracks = TrackDAO.getContinuePlayingByUserId(user_id);
        if (tracks.isEmpty()) {
            System.out.println("No tracks listen till now.");
        } 
        else {
            Search.displayTracksAndPlay(tracks, user_id, sc);
        }
    }

    public static void randomPlaylists(Scanner sc, int user_id) {
        ArrayList<Playlist> random_playlists = PlaylistDAO.getLatestPlaylists(5);
        for (Playlist playlist : random_playlists) {
            PlaylistDAO.populatePlaylistTracks(playlist);
        }

        System.out.println("\nLatest Playlists:");
        int i = 1;
        for (Playlist playlist : random_playlists) {
            System.out.println(i + ". " + playlist.getPlaylistName());
            i++;
        }
        int choice;
        while (true) {
            try {
                System.out.print("\nEnter the playlist you want to view (or 0 to go back): ");
                choice = sc.nextInt();
            }
            catch (InputMismatchException e) {
                AttractiveTerminal.printError("Enter Integer datatype only.");
                continue;
            }
            finally{
                sc.nextLine();
            }
            if (choice > 0 && choice <= random_playlists.size()) {
                PlayingTrack.playingTrackInterface(random_playlists.get(choice-1), user_id, sc);
                return;
            } 
            else if (choice == 0) {
                return; 
            }
            else {
                AttractiveTerminal.printError("INVALID choice. Please try again.");
                continue;
            }
        }
    }
}
