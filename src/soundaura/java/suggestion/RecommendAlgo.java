package soundaura.java.suggestion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import soundaura.java.entities.Playlist;
import soundaura.java.entities.Track;

//Recommendation
public class RecommendAlgo {
    public ArrayList<Track> recommend(ArrayList<Track> recent_history, ArrayList<Track> all_tracks, Playlist playlist) {

        HashMap<String, Integer> genre_count = new HashMap<>();
        HashMap<Integer, Integer> artist_count = new HashMap<>();

        for (Track t : recent_history) {
            genre_count.merge(t.getGenre(), 1, Integer::sum);
            artist_count.merge(t.getArtistId(), 1, Integer::sum);
        }

        HashSet<Integer> exclude_ids = new HashSet<>();
        for (Track t : playlist.getOriginalOrder().values()) {
            exclude_ids.add(t.getTrackId());
        }
        
        PriorityQueue<Track> candidates = new PriorityQueue<>(Comparator.comparingDouble((Track t) -> score(t, genre_count, artist_count)).reversed());

        for (Track t : all_tracks) {
            if (exclude_ids.contains(t.getTrackId())) {
                continue;
            }
            candidates.add(t);
        }
        
        ArrayList<Track> top_recommendations = new ArrayList<>();
        for (int i = 0; i < 10 && !candidates.isEmpty(); i++) {
            Track track = candidates.poll();
            top_recommendations.add(track);
        }

        return top_recommendations;
    }

    private double score(Track t, HashMap<String, Integer> genre_count, HashMap<Integer, Integer> artist_count) {
        double s = 0;
        s += genre_count.getOrDefault(t.getGenre(), 0) * 2.0;
        s += artist_count.getOrDefault(t.getArtistId(), 0) * 3.0;
        s += Math.log(1 + t.getTotalViews()) * 0.5;
        return s;
    }
}