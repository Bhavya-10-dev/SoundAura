package soundaura.java.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Stack;

import soundaura.java.entities.Track;
import soundaura.java.suggestion.RecommendAlgo;
import soundaura.dbms.TrackDAO;
import soundaura.ds.TrackDeque;
import soundaura.java.entities.Playlist;

public class PlaybackQueue {
    private ArrayList<Track> context_queue = new ArrayList<>();
    private ArrayList<Track> recom_queue = new ArrayList<>();
    private TrackDeque up_next = new TrackDeque();
    private Stack<Track> history = new Stack<>();
    private boolean shuffled = false;
    private HashMap<Integer, Track> original_order = new HashMap<>(); // initialized here too, as a safety net
    private String playlist_name;

    public PlaybackQueue(){

    }

    public void loadContext(Track track, int user_id) {
        this.clear();
        HashMap<Integer, Track> single_track_map = new HashMap<>();
        single_track_map.put(1, track); // build a fresh map containing just this track
        Playlist single_track_playlist = new Playlist(-1, -1, "Unknown", single_track_map, null);
        loadContext(single_track_playlist, user_id);
    }

    public void loadContext(Playlist playlist, int user_id) {
        this.clear();
        this.playlist_name = playlist.getPlaylistName();
        original_order.putAll(playlist.getOriginalOrder());
        context_queue = new ArrayList<>(original_order.values());

        if (!context_queue.isEmpty()) {
            Track first = context_queue.removeFirst();
            up_next.nextInQueue(first);
            updateHistoryTable(first, user_id); // updates the very first track in history immediately — fixes the single-song case
        }

        RecommendAlgo recom_algo = new RecommendAlgo();
        ArrayList<Track> recent_history = TrackDAO.getHistoryTrackByUserId(user_id);
        ArrayList<Track> all_tracks = TrackDAO.getAllTracks();
        recom_queue = recom_algo.recommend(recent_history, all_tracks, playlist);
    }

    public void clear(){
        original_order = new HashMap<>();
        context_queue = new ArrayList<>();
        recom_queue = new ArrayList<>();
        history = new Stack<>();
        up_next = new TrackDeque(); // now reset too — no stale tracks carried over
        shuffled = false;
    }

    public Track getCurrentTrack() {
        return up_next.peekFirst();
    }

    public Track next(int user_id) {
        boolean hasNext = up_next.size() > 1 || !context_queue.isEmpty();
        if (!hasNext) {
            return null; // nothing to advance to — no DB writes, no state changes
        }

        addToHistory();

        if (up_next.isEmpty()) {
            Track track = context_queue.removeFirst();
            up_next.nextInQueue(track);
        }

        Track newCurrent = getCurrentTrack();
        updateHistoryTable(newCurrent, user_id); // update to table whatever just BECAME current
        return newCurrent;
    }

    public Track previous(int user_id) {
        if (history.isEmpty()) {
            return null; // nothing to go back to — no DB writes, no state changes
        }

        Track track = history.pop();
        up_next.addFirst(track);

        Track newCurrent = getCurrentTrack();
        updateHistoryTable(newCurrent, user_id);
        return newCurrent;
    }

    public void insertInQueue(int idx, String source, String action) {
        if (idx==-1 || source.isEmpty() || action.isEmpty()){
            return;
        }

        Track track;
        if (source.equalsIgnoreCase("playlist")){
            track = context_queue.remove(idx);
        }
        else if (source.equalsIgnoreCase("recommended")){
            track = recom_queue.remove(idx);
        }
        else{
            System.out.println("Error: Something went wrong");
            return;
        }

        if (action.equalsIgnoreCase("next_in_queue")){
            up_next.nextInQueue(track);
        }
        else if (action.equalsIgnoreCase("play_now")){
            up_next.addFirst(track);
            System.out.println("Playing " + getCurrentTrack().getTrackTitle());
            MusicPlayer.play(getCurrentTrack());
            return;
        }
        else{
            System.out.println("Error: Something went wrong");
            return;
        }
    }

    public void deleteInQueue(int idx, int user_id) {
        if (idx==0){
            next(user_id);
            MusicPlayer.play(getCurrentTrack());
        }
        else{
            up_next.deleteByNo(idx);
        }
    }

    public void addToHistory(){
        history.push(up_next.removeFromFront());
    }

    private void updateHistoryTable(Track track, int user_id) {
        if (track == null) {
            return;
        }
        try {
            track.setTotalViews(track.getTotalViews() + 1);
            TrackDAO.updateTotalViewsById(track.getTrackId());
            TrackDAO.updateHistory(user_id, track.getTrackId());
        } 
        catch (Exception e) {
        }
    }

    public void toggleShuffle() {
        if (!shuffled) {
            Collections.shuffle(context_queue);
            shuffled = true;
            System.out.println("Added Shuffle.");
        } 
        else {
            if (context_queue.size()==original_order.size()){
                context_queue = new ArrayList<>(original_order.values());
            }
            else{
                ArrayList<Track> temp = new ArrayList<>();
                temp.addAll(original_order.values());
                temp.removeAll(history);
                context_queue = temp;
            }
            System.out.println("Removed Shuffle.");
            shuffled = false;
        }
    }

    public TrackDeque getUpNext() {
        return up_next;
    }

    public ArrayList<Track> getContextQueue() {
        return context_queue;
    }

    public ArrayList<Track> getRecomQueue() {
        return recom_queue;
    }

    public String getPlaylistName() {
        return playlist_name;
    }

    public boolean isShuffled() {
        return shuffled;
    }
}