package soundaura.java.ui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

import soundaura.dbms.PlaylistDAO;
import soundaura.dbms.TrackDAO;
import soundaura.java.entities.Playlist;
import soundaura.java.entities.Track;
import soundaura.java.entities.User;

public interface Create {
    public static void createMenu(Scanner sc, User user, PlaylistDAO playlist_dao){
        while (true) {
            System.out.println("\nCreate Menu:");
            System.out.println("1. Add Playlist");
            System.out.println("2. Update Playlist");
            System.out.println("3. Delete Playlist");
            System.out.println("4. Exit");

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
                    Create.addPlaylist(sc, user.getUserId(), playlist_dao);
                }
                case 2 -> {
                    Create.updatePlaylist(sc, user.getUserId(), playlist_dao);
                }
                case 3 -> {
                    Create.deletePlaylist(sc, user.getUserId(), playlist_dao);
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

    public static void addPlaylist(Scanner sc, int u_id, PlaylistDAO playlist_dao) {
        String playlistName = "";
        int user_id = u_id;
        HashMap<Integer, Track> tracks = new HashMap<>();
        LocalDate created_at = LocalDate.now();
        int no_of_tracks = 0;

        System.out.println("\nCreating a new playlist...");
        System.out.print("Enter the name of the playlist: ");
        playlistName = sc.nextLine();

        ArrayList<Track> all_tracks = TrackDAO.getAllTracks();
        int pages = all_tracks.size()/10;
        int page = 1;

        if (all_tracks.size()%10 != 0){
            pages++;
        }

        while(true){
            int start = ((page-1)*10) + 1;
            int end = Math.min(page*10, all_tracks.size());
            System.out.println("\nTracks in page - " + page);
            for (int i=start; i<=end; i++){
                System.out.println(i + ". " + all_tracks.get(i-1).getTrackTitle()); // fixed: was 'start'
            }
            System.out.println("\nAdd Menu: ");
            System.out.println("1. Adding tracks");
            System.out.println("2. Previous Page");
            System.out.println("3. Next Page");
            System.out.println("4. Exit");

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
                    int iterations;
                    try {
                        System.out.print("Enter how many track you need to add from this page: ");
                        iterations = sc.nextInt();
                    }
                    catch (InputMismatchException e) {
                        AttractiveTerminal.printError("Enter Integer datatype only.");
                        continue;
                    }
                    finally{
                        sc.nextLine();
                    }
                    for (int i=0; i<iterations; i++){
                        int track_no;
                        try {
                            System.out.print("Enter track no.: ");
                            track_no = sc.nextInt();
                        }
                        catch (InputMismatchException e) {
                            AttractiveTerminal.printError("Enter Integer datatype only.");
                            continue;
                        }
                        finally{
                            sc.nextLine();
                        }

                        if (track_no < 1 || track_no > all_tracks.size()) {
                            AttractiveTerminal.printError("INVALID choice. Please enter again.");
                            continue;
                        }

                        no_of_tracks++;
                        tracks.put(no_of_tracks, all_tracks.get(track_no-1));
                    }
                }
                case 2 -> {
                    page = page-1;
                    if (page<1){
                        AttractiveTerminal.printError("Page doesn't not Exists.");
                        page = 1;
                        continue;
                    }
                }
                case 3 -> {
                    page = page+1;
                    if (page>pages){
                        AttractiveTerminal.printError("Page doesn't not Exists.");
                        page = pages;
                        continue;
                    }
                }
                case 4 -> {
                    while (true) {
                        System.out.print("\nDo you want to save the playlist(Y/N): ");
                        String ans = sc.nextLine();
                        if (ans.equalsIgnoreCase("Y")){
                            if (!tracks.isEmpty()){
                                Playlist playlist = new Playlist(user_id, playlistName, tracks, created_at);
                                boolean saved = playlist_dao.addPlaylist(playlist);
                                if (saved){
                                    AttractiveTerminal.printGreen("Playlist saved.");
                                }
                                else{
                                    AttractiveTerminal.printError("Failed to save playlist.");
                                }
                                return;
                            }
                            System.out.println("Playlist Not Saved.");
                            return;
                        }
                        else if (ans.equalsIgnoreCase("N")){
                            System.out.println("Playlist Not Saved.");
                            return;
                        }
                        else{
                            AttractiveTerminal.printError("INVALID choice. Please enter again.");
                            continue;
                        }
                    }
                }
                default ->{
                    AttractiveTerminal.printError("INVALID choice. Please try again.");
                }
            }
        }  
    }

    public static void updatePlaylist(Scanner sc, int user_id, PlaylistDAO playlist_dao){
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

        int choice = -1;
        while(true){
            try {
                System.out.print("\nEnter the playlist no.(or 0 to go back): ");
                choice = sc.nextInt();
            }
            catch (InputMismatchException e) {
                AttractiveTerminal.printError("Enter Integer datatype only.");
                continue;
            }
            finally{
                sc.nextLine();
            }
            if (choice < 0 || choice > your_playlists.size()) {
                AttractiveTerminal.printError("INVALID choice. Please try again.");
                continue;
            }
            else if (choice == 0) return;
            else{
                break;
            }
        }


        Playlist playlist = your_playlists.get(choice-1);
        System.out.println("\n"+playlist.getPlaylistName());
        playlist.displayTracks();

        while (true){
            System.out.println("\nUpdate Menu:");
            System.out.println("1. Add Track");
            System.out.println("2. Delete Track");
            System.out.println("3. Exit");

            int action_choice;
            try {
                System.out.print("\nEnter your choice: ");
                action_choice = sc.nextInt();
            }
            catch (InputMismatchException e) {
                AttractiveTerminal.printError("Enter Integer datatype only.");
                continue;
            }
            finally{
                sc.nextLine();
            }

            switch (action_choice) {
                case 1 -> {
                    System.out.print("Enter Track Title to search: ");
                    String title = sc.nextLine();
                    ArrayList<Track> results = TrackDAO.getTrackArrayByTitle(title);

                    if (results.isEmpty()) {
                        System.out.println("No tracks found.");
                        continue;
                    }
                    int j = 1;
                    for (Track t : results) {
                        System.out.println(j + ". " + t.getTrackTitle());
                        j++;
                    }

                    int pick;
                    try {
                        System.out.print("Enter track no. to add (or 0 to cancel): ");
                        pick = sc.nextInt();
                    }
                    catch (InputMismatchException e) {
                        AttractiveTerminal.printError("Enter Integer datatype only.");
                        continue;
                    }
                    finally{
                        sc.nextLine();
                    }

                    if (pick > 0 && pick <= results.size()) {
                        Track chosen = results.get(pick-1);
                        boolean added = playlist_dao.addTrackToPlaylist(playlist.getPlaylistId(), chosen.getTrackId());
                        if (added) {
                            int nextKey = playlist.getOriginalOrder().size() + 1;
                            playlist.getOriginalOrder().put(nextKey, chosen); // keep in-memory copy synced
                            AttractiveTerminal.printGreen("[Success] Track added.");
                        } else {
                            AttractiveTerminal.printError("Failed to add track.");
                        }
                    }
                }
                case 2 -> {
                    playlist.displayTracks();
                    int pick;
                    try {
                        System.out.print("Enter track no. to delete (or 0 to cancel): ");
                        pick = sc.nextInt();
                    }
                    catch (InputMismatchException e) {
                        AttractiveTerminal.printError("Enter Integer datatype only.");
                        continue;
                    }
                    finally{
                        sc.nextLine();
                    }

                    if (pick > 0 && pick <= playlist.getOriginalOrder().size()) {
                        Track track = playlist.getOriginalOrder().get(pick); // same key used throughout
                        if (track != null) {
                            boolean removed = playlist_dao.removeTrackFromPlaylist(playlist.getPlaylistId(), track.getTrackId());
                            if (removed) {
                                playlist.getOriginalOrder().remove(pick); // remove by the SAME key
                                AttractiveTerminal.printGreen("[Success] Track removed.");
                            } else {
                                AttractiveTerminal.printError("Failed to remove track.");
                            }
                        }
                    }
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

    public static void deletePlaylist(Scanner sc, int user_id, PlaylistDAO playlist_dao){
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
        int choice = -1;
        while(true){
            try {
                System.out.print("\nEnter the playlist no.(or 0 to go back): ");
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
                playlist_dao.deletePlaylistById(your_playlists.get(choice-1).getPlaylistId());
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
