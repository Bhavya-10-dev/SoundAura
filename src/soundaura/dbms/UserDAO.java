package soundaura.dbms;

import soundaura.java.entities.User;

import java.sql.*;

/*
 * UserDAO handles database operations for the Users table.
 * All methods are static and this class implements no interfaces.
 */

public class UserDAO {
    public static User getUserById(int id) {
        String sql = "SELECT * FROM Users WHERE user_id = ?";
        User user = null;

        try (Connection conn = JdbcConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = mapResultSetToUser(rs);
                }
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static void addUser(User user) {
        String sql = "INSERT INTO Users (username, email, password, role, account_status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = JdbcConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getRole());
            pstmt.setString(5, user.getAccStatus());

            pstmt.executeUpdate();
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void updateUser(User user) {
        String sql = "UPDATE Users SET username = ?, email = ?, password = ?, role = ?, account_status = ? " +
                "WHERE user_id = ?";

        try (Connection conn = JdbcConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getRole());
            pstmt.setString(5, user.getAccStatus());
            pstmt.setInt(6, user.getUserId());

            pstmt.executeUpdate();
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean deleteUserById(int id) {
        String sql = "DELETE FROM Users WHERE user_id = ?";

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

    // Helper method to map a row from the ResultSet to a User object
    private static User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUserName(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setAccStatus(rs.getString("account_status"));
        // Convert SQL TIMESTAMP to Java LocalDate
        Timestamp createdAtTimestamp = rs.getTimestamp("created_at");
        if (createdAtTimestamp != null) {
            user.setCreatedAt(createdAtTimestamp.toLocalDateTime().toLocalDate());
        }
        return user;
    }
}