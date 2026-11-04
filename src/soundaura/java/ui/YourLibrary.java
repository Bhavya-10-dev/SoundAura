package soundaura.java.ui;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import soundaura.dbms.PlaylistDAO;
import soundaura.dbms.TrackDAO;
import soundaura.java.entities.Playlist;
import soundaura.java.entities.Track;

public interface YourLibrary {
    public static void yourLibraryMenu(Scanner sc, int user_id){
        while (true) {
            System.out.println("\nYour Library Menu:");
            System.out.println("1. Liked Tracks");
            System.out.println("2. Your Playlists");
            System.out.println("3. Back to Main Menu");

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
                    likedTracks(sc, user_id);
                }
                case 2 -> {
                    yourPlaylists(sc, user_id);
                }
                case 3 -> {
                    return; 
                }
                default ->{
                    AttractiveTerminal.printError("INVALID choice. Please try again.");
                }
            }
        }
    }

    public static void likedTracks(Scanner sc, int user_id){
        ArrayList<Track> liked_tracks = TrackDAO.getLikeTrackByUserId(user_id);

        if (liked_tracks.isEmpty()){
            System.out.println("You have not Like any track till now.");
            return;
        }

        System.out.println("\nLiked Tracks:");
        int i = 1;
        for (Track track : liked_tracks) {
            System.out.println(i + ". " + track.getTrackTitle());
            i++;
        }
        int choice;
        while (true) {
            try {
                System.out.print("\nEnter the playlist you want to view (or 0 to go back): ");
                choice = sc.nextInt();
                break;
            }
            catch (InputMismatchException e) {
                AttractiveTerminal.printError("Enter Integer datatype only.");
                continue;
            }
            finally{
                sc.nextLine();
            }
        }
        if (choice > 0 && choice <= liked_tracks.size()) {
            PlayingTrack.playingTrackInterface(liked_tracks.get(choice-1), user_id, sc);
        } 
        else if (choice == 0) {
            return; 
        }
        else {
            AttractiveTerminal.printError("INVALID choice. Please try again.");
        }
    }

    public static void yourPlaylists(Scanner sc, int user_id){
        ArrayList<Playlist> your_playlists = PlaylistDAO.getPlaylistByUserId(user_id);

        if (your_playlists.isEmpty()){
            System.out.println("You have not made any Playlist till now.");
            return;
        }

        System.out.println("\nYour Playlists:");
        int i = 1;
        for (Playlist playlist : your_playlists) {
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
            if (choice > 0 && choice <= your_playlists.size()) {
                Playlist playlist = your_playlists.get(choice-1);
                PlaylistDAO.populatePlaylistTracks(playlist);
                PlayingTrack.playingTrackInterface(playlist, user_id, sc);
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
