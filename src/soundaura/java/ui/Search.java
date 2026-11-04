package soundaura.java.ui;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import soundaura.dbms.PlaylistDAO;
import soundaura.dbms.TrackDAO;
import soundaura.java.entities.Playlist;
import soundaura.java.entities.Track;

public interface Search {
    public static void searchMenu(Scanner sc, int user_id){
        while (true) {
            System.out.println("\nSearch Menu:");
            System.out.println("1. Search by Track Title");
            // System.out.println("2. Search by Artist");
            System.out.println("2. Search by Playlist");
            System.out.println("3. Search by Genre");
            System.out.println("4. Latest Tracks");
            System.out.println("5. Back to Main Menu");
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
                    System.out.print("\nEnter Track Title: ");
                    String track_title = sc.nextLine();  
                    ArrayList<Track> tracks = TrackDAO.getTrackArrayByTitle(track_title);

                    if (tracks.isEmpty()) {
                        System.out.println("No tracks found for this genre.");
                    } 
                    else {
                        displayTracksAndPlay(tracks, user_id, sc);
                    }
                }
                // case 2 -> {
                //     System.out.print("Enter Artist Name: ");
                //     String artist_name = sc.nextLine();  
                //     ArrayList<Track> tracks = TrackDAO.getTracksByArtistName(artist_name);

                //     if (tracks.isEmpty()) {
                //         System.out.println("No tracks found for this artist.");
                //     } 
                //     else {
                //         displayTracksAndPlay(tracks, sc);
                //     }
                // }
                case 2 -> {
                    System.out.print("Enter Playlist Name: ");
                    String playlist_name = sc.nextLine();  
                    ArrayList<Playlist> playlists = PlaylistDAO.getPlaylistByName(playlist_name);
                    for (Playlist playlist : playlists) {
                        PlaylistDAO.populatePlaylistTracks(playlist);
                    }

                    if (playlists.isEmpty()) {
                        System.out.println("No playlists found.");
                    } 
                    else {
                        int i = 1;
                        for (Playlist p : playlists) {
                            System.out.println(i + ". " + p.getPlaylistName());
                            i++;
                        }
                        int playlist_choice;
                        while (true) {
                            try {
                                System.out.print("\nEnter playlist no. to play (or 0 to go back): ");
                                playlist_choice = sc.nextInt();
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
                        if (playlist_choice > 0 && playlist_choice <= playlists.size()) {
                            PlayingTrack.playingTrackInterface(playlists.get(playlist_choice - 1), user_id, sc);
                        } 
                        else if (playlist_choice == 0) {
                            continue;
                        }
                        else{
                            AttractiveTerminal.printError("INVALID choice. Please try again.");
                        }
                    }
                }
                case 3 -> {System.out.print("Enter Genre: ");
                    String genre = sc.nextLine();  
                    ArrayList<String> genres = TrackDAO.getGenres(genre);

                    int i = 1;
                    for (String g : genres) {
                        System.out.println(i + ". " + g);
                        i++;
                    }

                    int ans = -1;
                    String chosen_genre = "";
                    while (true) {
                        try {
                            System.out.print("\nEnter genre no.: ");
                            ans = sc.nextInt();
                        } 
                        catch (InputMismatchException e) {
                            AttractiveTerminal.printError("Enter Integer datatype only.");
                            continue;
                        }
                        finally{
                            sc.nextLine();
                        }
                        if (ans > 0 && ans <= genres.size()) {
                            chosen_genre = genres.get(ans-1);
                            break;
                        }
                        else {
                            AttractiveTerminal.printError("INVALID choice. Please try again.");
                            continue;
                        }
                    }
                    
                    ArrayList<Track> tracks = TrackDAO.getTracksByGenre(chosen_genre);

                    if (tracks.isEmpty()) {
                        System.out.println("No tracks found for this genre.");
                    } 
                    else {
                        displayTracksAndPlay(tracks, user_id, sc);
                    }
                }
                case 4 -> {
                    ArrayList<Track> tracks = TrackDAO.getLatestTracks(10);

                    if (tracks.isEmpty()) {
                        System.out.println("No tracks found.");
                    }
                    else {
                        displayTracksAndPlay(tracks, user_id, sc);
                    }
                }
                case 5 -> {
                    return; 
                }
                default -> {
                    AttractiveTerminal.printError("INVALID choice. Please try again.");
                }
            }
        }
    }

    static void displayTracksAndPlay(ArrayList<Track> tracks, int user_id, Scanner sc) {
        int i = 1;
        for (Track t : tracks) {
            System.out.println(i + ". " + t.getTrackTitle());
            i++;
        }
        int track_choice = -1;
        while (true) {
            try {
                System.out.print("\nEnter track no. to play (or 0 to go back): ");
                track_choice = sc.nextInt();
            }
            catch (InputMismatchException e) {
                AttractiveTerminal.printError("Enter Integer datatype only.");
                continue;
            }
            finally{
                sc.nextLine();
            }
            if (track_choice > 0 && track_choice <= tracks.size()) {
                PlayingTrack.playingTrackInterface(tracks.get(track_choice - 1), user_id, sc);
                return;
            } 
            else if (track_choice == 0) {
                return;
            }
            else{
                AttractiveTerminal.printError("INVALID choice. Please try again.");
                continue;
            }
        }
    }
}
