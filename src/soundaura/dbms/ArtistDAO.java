package soundaura.dbms;

import java.sql.*;

import soundaura.java.entities.Artist;

/*
 * ArtistDAO handles database operations specifically for users with the 'ARTIST' role.
 * It queries both the Users and Artists_Profile tables to construct a complete Artist object.
 * All methods are static and this class implements no interfaces.
 */

public class ArtistDAO {
    public static Artist getArtistById(int id) {
        String sql = "SELECT u.user_id, u.username, u.email, u.password, u.role, u.account_status, u.created_at, " +
                "a.verified_status " +
                "FROM Users u " +
                "JOIN Artists_Profile a ON u.user_id = a.user_id " +
                "WHERE u.user_id = ?";

        Artist artist = null;

        try (Connection conn = JdbcConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    artist = mapResultSetToArtist(rs);
                }
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return artist;
    }

    public void addArtist(Artist artist) {
        String sql = "INSERT INTO Artists_Profile (artist_id, bio, verified_status, total_followers) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = JdbcConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // artist_id links directly to Users.user_id[cite: 1]
            pstmt.setInt(1, artist.getArtistId());
            // pstmt.setString(2, artist.getBio());
            pstmt.setBoolean(3, artist.isVerifiedStatus());
            // pstmt.setInt(4, artist.getTotalFollowers());

            pstmt.executeUpdate();
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateArtist(Artist artist) {
        String updateUsersSql = "UPDATE Users SET username = ?, email = ?, password = ?, role = ?, account_status = ? WHERE user_id = ?";
        String updateArtistSql = "UPDATE Artists_Profile SET verified_status = ? WHERE artist_id = ?";

        try (Connection conn = JdbcConnection.getConnection()) {
            // Update inherited User fields
            try (PreparedStatement pstmtUser = conn.prepareStatement(updateUsersSql)) {
                pstmtUser.setString(1, artist.getUserName());
                pstmtUser.setString(2, artist.getEmail());
                pstmtUser.setString(3, artist.getPassword());
                pstmtUser.setString(4, artist.getRole());
                pstmtUser.setString(5, artist.getAccStatus());
                pstmtUser.setInt(6, artist.getArtistId());
                pstmtUser.executeUpdate();
            }

            // Update Artist-specific fields
            try (PreparedStatement pstmtArtist = conn.prepareStatement(updateArtistSql)) {
                pstmtArtist.setBoolean(1, artist.isVerifiedStatus());
                pstmtArtist.setInt(2, artist.getArtistId());
                pstmtArtist.executeUpdate();
            }

        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean deleteArtistById(int id) {
        String sql = "DELETE FROM Artists_Profile WHERE artist_id = ?";

        try (Connection conn = JdbcConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
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

    public boolean approveArtistById(int artist_id, boolean is_approved) {
        String sql = "UPDATE Artists_Profile SET is_approved = ? WHERE artist_id = ?";

        try (Connection conn = JdbcConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, is_approved);
            pstmt.setInt(2, artist_id);

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

    // Helper method to map a row from the ResultSet to an Artist object
    private static Artist mapResultSetToArtist(ResultSet rs) throws SQLException {
        // Initialize with Artist-specific constructor fields
        Artist artist = new Artist(
                rs.getInt("user_id"), 
                rs.getBoolean("verified_status")
        );

        // Populate inherited User attributes via setter methods
        artist.setUserId(rs.getInt("user_id"));
        artist.setUserName(rs.getString("username"));
        artist.setEmail(rs.getString("email"));
        artist.setPassword(rs.getString("password"));
        artist.setRole(rs.getString("role"));
        artist.setAccStatus(rs.getString("account_status"));
        Timestamp createdAtTimestamp = rs.getTimestamp("created_at");
        if (createdAtTimestamp != null) {
            artist.setCreatedAt(createdAtTimestamp.toLocalDateTime().toLocalDate());
        }

        return artist;
    }
}