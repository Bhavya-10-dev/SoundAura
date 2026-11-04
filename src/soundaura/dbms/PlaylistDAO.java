package soundaura.dbms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import soundaura.java.entities.Track;
import soundaura.java.entities.Playlist;

/*
 * PlaylistDAO handles database operations for the Playlists and Playlist_Tracks tables.
 * All methods are static and this class implements no interfaces.
 */

public class PlaylistDAO {
    public static ArrayList<Playlist> getAllPlaylist() {
        String sql = "SELECT * FROM Playlists";
        ArrayList<Playlist> playlists = new ArrayList<>();

        try (Connection conn = JdbcConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                playlists.add(mapResultSetToPlaylist(rs));
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return playlists;
    }

    public static Playlist getPlaylistById(int id) {
        String sql = "SELECT * FROM Playlists WHERE playlist_id = ?";
        Playlist playlist = null;

        try (Connection conn = JdbcConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    playlist = mapResultSetToPlaylist(rs);
                }
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return playlist;
    }

    public static ArrayList<Playlist> getPlaylistByUserId(int id) {
        String sql = "SELECT * FROM Playlists WHERE user_id = ?";
        ArrayList<Playlist> playlists = new ArrayList<>();

        try (Connection conn = JdbcConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    playlists.add(mapResultSetToPlaylist(rs));
                }
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return playlists;
    }

    public static ArrayList<Playlist> getPlaylistByName(String name) {
        String sql = "SELECT * FROM Playlists WHERE name LIKE ?";
        Playlist playlist = null;
        ArrayList<Playlist> name_playlists = new ArrayList<>();

        try (Connection conn = JdbcConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%"+name+"%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    playlist = mapResultSetToPlaylist(rs);
                    name_playlists.add(playlist);
                }
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return name_playlists;
    }

    public static ArrayList<Playlist> getLatestPlaylists(int size){
        String sql = "SELECT * FROM Playlists ORDER BY created_at DESC LIMIT ?";
        Playlist playlist = null;
        ArrayList<Playlist> latest_playlist = new ArrayList<>();

        try (Connection conn = JdbcConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, size);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    playlist = mapResultSetToPlaylist(rs);
                    latest_playlist.add(playlist);
                }
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return latest_playlist;
    }

    // public boolean addPlaylist(Playlist playlist) {
    //     // 'name' maps to playlist.getPlaylistName() as per the schema
    //     String sql = "INSERT INTO Playlists (user_id, name) VALUES (?, ?)";

    //     try (Connection conn = JdbcConnection.getConnection();
    //          PreparedStatement pstmt = conn.prepareStatement(sql)) {

    //         pstmt.setInt(1, playlist.getUserId());
    //         pstmt.setString(2, playlist.getPlaylistName());

    //         int n = pstmt.executeUpdate();

    //         if (n>0){
    //             return true;
    //         }
    //         else{
    //             return false;
    //         }
    //     } 
    //     catch (SQLException e) {
    //         e.printStackTrace();
    //         return false;
    //     }
    // }

    public boolean addPlaylist(Playlist playlist) {
        // created_at is NOT set here — the DB fills it via DEFAULT current_timestamp(6)
        String insertPlaylistSql = "INSERT INTO playlists (user_id, name) VALUES (?, ?)";
        String insertTrackSql = "INSERT INTO playlists_tracks (playlist_id, track_id, track_order) VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            conn = JdbcConnection.getConnection();
            conn.setAutoCommit(false);

            int newPlaylistId;
            try (PreparedStatement ps = conn.prepareStatement(insertPlaylistSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, playlist.getUserId());
                ps.setString(2, playlist.getPlaylistName());
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        newPlaylistId = keys.getInt(1); // AUTO_INCREMENT value MySQL just generated
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(insertTrackSql)) {
                for (Map.Entry<Integer, Track> entry : playlist.getOriginalOrder().entrySet()) {
                    ps.setInt(1, newPlaylistId);
                    ps.setInt(2, entry.getValue().getTrackId());
                    ps.setInt(3, entry.getKey()); // key = track_order
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
            return true;

        } 
        catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } 
        finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    // public void updatePlaylistById(int playlist_id, Playlist playlist) {
    //     String sql = "UPDATE Playlists SET name = ?, user_id = ? WHERE playlist_id = ?";

    //     try (Connection conn = JdbcConnection.getConnection();
    //          PreparedStatement pstmt = conn.prepareStatement(sql)) {

    //         pstmt.setString(1, playlist.getPlaylistName());
    //         pstmt.setInt(2, playlist.getUserId());
    //         pstmt.setInt(3, playlist_id);

    //         pstmt.executeUpdate();
    //     } 
    //     catch (SQLException e) {
    //         e.printStackTrace();
    //     }
    // }

    // ---------- UPDATE ----------

    public boolean addTrackToPlaylist(int playlistId, int trackId) {
        // Optional but recommended: guard against duplicate tracks, since your schema
        // has no unique constraint on (playlist_id, track_id) to stop this at the DB level
        if (trackAlreadyInPlaylist(playlistId, trackId)) {
            System.out.println("Track is already in this playlist.");
            return false;
        }

        String sql = "INSERT INTO playlists_tracks (playlist_id, track_id, track_order) " +
                     "VALUES (?, ?, (SELECT nxt FROM (SELECT COALESCE(MAX(track_order), 0) + 1 AS nxt " +
                     "FROM playlists_tracks WHERE playlist_id = ?) AS t))";
        try (Connection conn = JdbcConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playlistId);
            ps.setInt(2, trackId);
            ps.setInt(3, playlistId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean trackAlreadyInPlaylist(int playlistId, int trackId) {
        String sql = "SELECT 1 FROM playlists_tracks WHERE playlist_id = ? AND track_id = ?";
        try (Connection conn = JdbcConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playlistId);
            ps.setInt(2, trackId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeTrackFromPlaylist(int playlistId, int trackId) {
        String sql = "DELETE FROM playlists_tracks WHERE playlist_id = ? AND track_id = ?";
        try (Connection conn = JdbcConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playlistId);
            ps.setInt(2, trackId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean renamePlaylist(int playlistId, String newName) {
        // created_at will auto-update via ON UPDATE current_timestamp(6) — no need to touch it
        String sql = "UPDATE playlists SET name = ? WHERE playlist_id = ?";
        try (Connection conn = JdbcConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newName);
            ps.setInt(2, playlistId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // public boolean deletePlaylistById(int id) {
    //     String sql = "DELETE FROM Playlists WHERE playlist_id = ?";

    //     try (Connection conn = JdbcConnection.getConnection();
    //         PreparedStatement pstmt = conn.prepareStatement(sql)) {

    //         pstmt.setInt(1, id);
    //         int n = pstmt.executeUpdate();
            
    //         if (n>0){
    //             return true;
    //         }
    //         else{
    //             return false;
    //         }
    //     } 
    //     catch (SQLException e) {
    //         e.printStackTrace();
    //         return false;
    //     }
    // }

    
    // ---------- DELETE ----------
    public boolean deletePlaylistById(int playlistId) {
        String deleteTracksSql = "DELETE FROM playlists_tracks WHERE playlist_id = ?";
        String deletePlaylistSql = "DELETE FROM playlists WHERE playlist_id = ?";

        Connection conn = null;
        try {
            conn = JdbcConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(deleteTracksSql)) {
                ps.setInt(1, playlistId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(deletePlaylistSql)) {
                ps.setInt(1, playlistId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    /*
     * Helper method to map a row from the ResultSet to a Playlist object
     * Note: As per instructions, original_order is NOT initialized here.
     */
    private static Playlist mapResultSetToPlaylist(ResultSet rs) throws SQLException {
        LocalDate createdAtDate = null;
        Timestamp createdAtTimestamp = rs.getTimestamp("created_at");
        if (createdAtTimestamp != null) {
            createdAtDate = createdAtTimestamp.toLocalDateTime().toLocalDate();
        }

        // Utilizing the full constructor provided in the Playlist class.
        // original_order is passed as an empty HashMap per the handwritten note.
        return new Playlist(
                rs.getInt("playlist_id"),
                rs.getInt("user_id"),
                rs.getString("name"),
                new HashMap<Integer, Track>(), // Not populated automatically
                createdAtDate
        );
    }

    /**
     * Helper method strictly for interacting with the Playlist_Tracks table.
     * Use this after getPlaylistById or getPlaylistByUserId to populate the original_order HashMap.
     *
     * @param playlist The playlist object to populate.
     */
    public static void populatePlaylistTracks(Playlist playlist) {
        String sql = "SELECT pt.track_order, t.* " +
                "FROM Playlists_Tracks pt " +
                "JOIN Tracks t ON pt.track_id = t.track_id " +
                "WHERE pt.playlist_id = ? " +
                "ORDER BY pt.track_order ASC";

        HashMap<Integer, Track> orderedTracks = new HashMap<>();

        try (Connection conn = JdbcConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, playlist.getPlaylistId());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int trackOrder = rs.getInt("track_order");

                    // Manually mapping the track to avoid circular dependencies with TrackDAO
                    LocalDate releaseDate = rs.getDate("release_date") != null ? rs.getDate("release_date").toLocalDate() : null;
                    Track track = new Track(
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

                    orderedTracks.put(trackOrder, track);
                }
            }
            playlist.setOriginalOrder(orderedTracks);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
