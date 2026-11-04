package soundaura.java.entities;

import java.time.LocalDate;
import java.util.HashMap;

public class Playlist {
    private int playlist_id;
    private int user_id;
    private String playlist_name;
    private HashMap<Integer, Track> original_order;
    private LocalDate created_at;

    public Playlist() {
        
    }

    public Playlist(int user_id, String playlist_name, HashMap<Integer,Track> original_order, LocalDate created_at){
        this.user_id = user_id;
        this.playlist_name = playlist_name;
        this.original_order = original_order;
        this.created_at = created_at;
    }
    
    public Playlist(int playlist_id, int user_id, String playlist_name, HashMap<Integer,Track> original_order, LocalDate created_at) {
        this.playlist_id = playlist_id;
        this.user_id = user_id;
        this.playlist_name = playlist_name;
        this.original_order = original_order;
        this.created_at = created_at;
    }

    public int getPlaylistId() {
        return playlist_id;
    }

    public void setPlaylistId(int playlist_id) {
        this.playlist_id = playlist_id;
    }

    public HashMap<Integer, Track> getOriginalOrder() {
        return original_order;
    }

    public void setOriginalOrder(HashMap<Integer, Track> original_order) {
        this.original_order = original_order;
    }

    public LocalDate getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(LocalDate created_at) {
        this.created_at = created_at;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public String getPlaylistName() {
        return playlist_name;
    }

    public void setPlaylistName(String playlist_name) {
        this.playlist_name = playlist_name;
    }

    public void displayTracks(){
        int i = 1;
        for (Track track: original_order.values()){
            System.out.println(i + ". " + track.getTrackTitle());
            i++;
        }
    }
}
