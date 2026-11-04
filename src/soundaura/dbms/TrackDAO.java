package soundaura.dbms;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import soundaura.java.entities.Track;

/*
 * TrackDAO handles database operations for the Tracks, Liked_Songs, and Play_History tables.
 * All methods are static and this class implements no interfaces.
 */

public class TrackDAO {
    public static ArrayList<Track> getAllTracks() {
        String sql = "SELECT * FROM Tracks";
        ArrayList<Track> tracks = new ArrayList<>();

        try (Connection conn = JdbcConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                tracks.add(mapResultSetToTrack(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tracks;
    }

    public static Track getTrackById(int id) {
        String sql = "SELECT * FROM Tracks WHERE track_id = ?";
        Track track = null;

        try (Connection conn = JdbcConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    track = mapResultSetToTrack(rs);
                }
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return track;
    }

    public static Track getTrackByTitle(String title) {
        String sql = "SELECT * FROM Tracks WHERE title=?";
        Track track = null;

        try (Connection conn = JdbcConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    track = mapResultSetToTrack(rs);
                }
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return track;
    }

    public static ArrayList<Track> getTrackArrayByTitle(String title) {
        String sql = "SELECT * FROM Tracks WHERE title LIKE ?";
        Track track = null;
        ArrayList<Track> name_playlists = new ArrayList<>();

        try (Connection conn = JdbcConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%"+title+"%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    track = mapResultSetToTrack(rs);
                    name_playlists.add(track);
                    track = mapResultSetToTrack(rs);
                }
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return name_playlists;
    }

    public static ArrayList<String> getGenres(String genre) {
        String sql = "SELECT genre FROM Tracks WHERE genre LIKE ? GROUP BY genre";
        ArrayList<String> genres = new ArrayList<>();

        try (Connection conn = JdbcConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%"+genre+"%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    genres.add(rs.getString("genre"));
                }
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return genres;
    }
    
    public static ArrayList<Track> getTracksByGenre(String genre) {
        String sql = "SELECT * FROM Tracks WHERE genre LIKE ?";
        Track track = null;
        ArrayList<Track> genre_tracks = new ArrayList<>();

        try (Connection conn = JdbcConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%"+genre+"%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    track = mapResultSetToTrack(rs);
                    genre_tracks.add(track);
                }
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return genre_tracks;
    }

    public static ArrayList<Track> getLatestTracks(int size){
        String sql = "SELECT * FROM Tracks ORDER BY release_date DESC LIMIT ?";
        Track track = null;
        ArrayList<Track> latest_track = new ArrayList<>();

        try (Connection conn = JdbcConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, size);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    track = mapResultSetToTrack(rs);
                    latest_track.add(track);
                }
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return latest_track;
    }

    public void addTrack(Track track) {
        String sql = "INSERT INTO Tracks (title, artist_id, genre, duration_seconds, lyrics, file_path, release_date, total__views, is_approved) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = JdbcConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, track.getTrackTitle());
            pstmt.setInt(2, track.getArtistId());
            pstmt.setString(3, track.getGenre());
            pstmt.setLong(4, track.getDuration());
            pstmt.setString(5, track.getLyrics());
            pstmt.setString(6, track.getRelAudioPath());

            // Convert LocalDate to java.sql.Date
            if (track.getReleaseDate() != null) {
                pstmt.setDate(7, Date.valueOf(track.getReleaseDate()));
            } else {
                pstmt.setNull(7, java.sql.Types.DATE);
            }

            pstmt.setLong(8, track.getTotalViews());
            pstmt.setBoolean(9, track.isApproved());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateTrack(Track track) {
        String sql = "UPDATE Tracks SET title = ?, artist_id = ?, genre = ?, duration_seconds = ?, lyrics = ?, file_path = ?, release_date = ?, total__views = ?, is_approved = ? WHERE track_id = ?";

        try (Connection conn = JdbcConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, track.getTrackTitle());
            pstmt.setInt(2, track.getArtistId());
            pstmt.setString(3, track.getGenre());
            pstmt.setLong(4, track.getDuration());
            pstmt.setString(5, track.getLyrics());
            pstmt.setString(6, track.getRelAudioPath());

            if (track.getReleaseDate() != null) {
                pstmt.setDate(7, Date.valueOf(track.getReleaseDate()));
            } else {
                pstmt.setNull(7, java.sql.Types.DATE);
            }

            pstmt.setLong(8, track.getTotalViews());
            pstmt.setBoolean(9, track.isApproved());
            pstmt.setInt(10, track.getTrackId());

            pstmt.executeUpdate();
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean uploadTrack(Track track) {
        // track_id omitted — AUTO_INCREMENT handles it
        // artist_id, release_date, total__views, is_approved are all set here, not by the artist
        String sql = "INSERT INTO tracks (title, artist_id, genre, duration_seconds, lyrics, file_path, release_date, total__views, is_approved) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = JdbcConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, track.getTrackTitle());
            ps.setInt(2, track.getArtistId());
            ps.setString(3, track.getGenre());
            ps.setLong(4, track.getDuration());
            ps.setString(5, track.getLyrics());
            ps.setString(6, track.getRelAudioPath());
            ps.setString(7, track.getReleaseDate().toString()); // release_date, e.g. "2026-08-05"
            ps.setInt(8, 0); // total__views starts at 0
            ps.setString(9, "f"); // is_approved defaults to false — pending admin review

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteTrackById(int track_id) {
        String sql = "DELETE FROM tracks WHERE track_id = ?";

        try (Connection conn = JdbcConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, track_id);
            int n = pstmt.executeUpdate();

            if (n>0){
                return true;
            }
            else{
                return false;
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean likeTrack(int user_id, int track_id, boolean liked) {
        String check_like = "SELECT user_id, track_id FROM liked_songs "+
        "WHERE user_id=? AND track_id=?";

        if(liked){
            try (Connection conn = JdbcConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(check_like)) {

                pstmt.setInt(1, user_id);
                pstmt.setInt(2, track_id);
                
                ResultSet has_liked = pstmt.executeQuery();
                if (has_liked.next()){
                    System.out.println("Already liked the track.");
                    return true;
                }
            } 
            catch (SQLException e) {
                e.printStackTrace();
            }
        }

        String sql = liked ? "INSERT INTO  liked_songs (user_id, track_id, liked_at) "+
        "VALUES (?, ?, ?)" : "DELETE FROM liked_songs WHERE user_id = ? AND track_id = ?";
        boolean success = false;

        try (Connection conn = JdbcConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, user_id);
            pstmt.setInt(2, track_id);

            if (liked) {
            pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            }
            
            int rowsAffected = pstmt.executeUpdate();
            success = rowsAffected > 0;
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }
    
    public static ArrayList<Track> getLikeTrackByUserId(int userId){
        ArrayList<Track> liked_tracks = new ArrayList<>();
        String sql = "SELECT track_id FROM liked_songs WHERE user_id = ? ORDER BY liked_at DESC";

        try (Connection conn = JdbcConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                liked_tracks.add(getTrackById(rs.getInt("track_id")));
            }
        }
        catch (SQLException e1) {
            e1.printStackTrace();
        }
        return liked_tracks;
    }

    public boolean approveTrackById(int track_id, boolean is_approved) {
        String sql = "UPDATE Tracks SET is_approved = ? WHERE track_id = ?";

        try (Connection conn = JdbcConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, is_approved);
            pstmt.setInt(2, track_id);

            int n = pstmt.executeUpdate();
            
            if (n>0){
                return true;
            }
            else{
                return false;
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void updateTotalViewsById(int track_id) {
        String sql = "UPDATE Tracks SET total__views = total__views + 1 WHERE track_id = ?";

        try (Connection conn = JdbcConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, track_id);
            pstmt.executeUpdate();
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateHistory(int user_id, int track_id) {
        String sql = "INSERT INTO Play_History (user_id, track_id, played_at) VALUES (?, ?, ?)";

        try (Connection conn = JdbcConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, user_id);
            pstmt.setInt(2, track_id);
            pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.executeUpdate();
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Track> getHistoryTrackByUserId(int userId) {
        ArrayList<Track> history_tracks = new ArrayList<>();
        String sql = "SELECT track_id FROM play_history WHERE user_id = ? ORDER BY played_at DESC";

        try (Connection conn = JdbcConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                history_tracks.add(getTrackById(rs.getInt("track_id")));
            }
        }
        catch (SQLException e1) {
            e1.printStackTrace();
        }
        return history_tracks;
    }

    public static ArrayList<Track> getContinuePlayingByUserId(int userId) {
        ArrayList<Track> history_tracks = new ArrayList<>();
        String sql = "SELECT track_id, MAX(played_at) AS last_played FROM play_history WHERE user_id = ? GROUP BY track_id ORDER BY last_played DESC LIMIT 5";

        try (Connection conn = JdbcConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                history_tracks.add(getTrackById(rs.getInt("track_id")));
            }
        }
        catch (SQLException e1) {
            e1.printStackTrace();
        }
        return history_tracks;
    }

    // Helper method to map a row from the ResultSet to a Track object
    private static Track mapResultSetToTrack(ResultSet rs) throws SQLException {
        LocalDate releaseDate = null;
        if (rs.getDate("release_date") != null) {
            releaseDate = rs.getDate("release_date").toLocalDate();
        }

        // Utilizing the full constructor provided in the Track class
        return new Track(
                rs.getInt("track_id"),
                rs.getString("title"),
                rs.getInt("artist_id"),
                rs.getString("genre"),
                rs.getLong("duration_seconds"),
                rs.getString("lyrics"),
                rs.getString("file_path"),
                releaseDate,
                rs.getLong("total__views"),
                rs.getBoolean("is_approved")
        );
    }
}
