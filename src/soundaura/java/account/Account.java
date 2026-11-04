package soundaura.java.account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.regex.Pattern;

import soundaura.dbms.JdbcConnection;
import soundaura.java.entities.Artist;
import soundaura.java.entities.User;
import soundaura.java.ui.AttractiveTerminal;

public class Account {

    /**
     * Authenticates the user against the database.
     * Returns a User object (or Artist object via polymorphism) if successful, null otherwise.
     */
    public User login(Scanner sc) {
        System.out.println("\n--- Login to SoundAura ---");
        String identifier = "";

        // Step 1: Loop until a valid username/email is entered or the user cancels
        while (true) {
            System.out.print("\nEnter Email (or type '0' to go back): ");
            identifier = sc.nextLine().trim();

            if (identifier.equals("0")) {
                System.out.println("Returning to SoundAura's Main Menu...");
                return null;
            }

            // Lightweight query to check if the user exists
            String checkSql = "SELECT email FROM users WHERE email = ?";
            boolean exists = false;

            try (Connection conn = JdbcConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(checkSql)) {

                pstmt.setString(1, identifier);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        exists = true;
                    }
                }
            } catch (SQLException e) {
                // Changed from System.err to AttractiveTerminal (System.out)
                AttractiveTerminal.printError(e.getMessage());
                return null;
            }

            if (!exists) {
                // Changed from System.err to AttractiveTerminal to fix the printing order
                AttractiveTerminal.printError("Email does not exist in our system. Please try again.\n");
            } 
            else {
                break; // Identifier is valid, break the loop and move to password
            }
        }

        // Step 2: Prompt for password now that we know the user exists
        System.out.print("Password: ");
        String password = sc.nextLine().trim();

        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (Connection conn = JdbcConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, identifier);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("account_status");

                    if ("ACTIVE".equalsIgnoreCase(status)) {
                        AttractiveTerminal.printGreen("[Success] Login successful! Welcome back, " + rs.getString("username") + ".");
                        return mapResultSetToEntity(rs, conn);
                    } else {
                        AttractiveTerminal.printError("Login failed: Your account is currently " + status + ".");
                        return null;
                    }
                } else {
                    AttractiveTerminal.printError("Login failed: Incorrect password.");
                    return null;
                }
            }
        } catch (SQLException e) {
            AttractiveTerminal.printError(e.getMessage());
        }
        return null;
    }

    /**
     * Registers a new user with updated regex validation, safe transaction rollbacks,
     * and synchronized console error logging. Automatically logs the user in upon success.
     */
    public User signUp(Scanner sc) {
        System.out.println("\n--- Sign Up for SoundAURA ---");

        String username = promptInput(sc, "Enter Username (3-30 chars): ", "^[a-zA-Z0-9_]{3,30}$", "Username must be 3-30 alphanumeric characters.");
        String email = promptInput(sc, "Enter Email: ", "^(?!\\d+@)[A-Za-z0-9+_.-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,6}$", "Invalid email format. Cannot be only numbers before @, and multiple domain extensions are not allowed.");
        String password = getValidPassword(sc);

        System.out.print("Are you registering as an Artist? (Y/N): ");
        boolean isArtist = sc.nextLine().trim().equalsIgnoreCase("Y");
        String role = isArtist ? "ARTIST" : "USER";

        String insertUserSql = "INSERT INTO users (username, email, password, role, account_status, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        String insertArtistSql = "INSERT INTO artists_profile (user_id, verified_status) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = JdbcConnection.getConnection();
            conn.setAutoCommit(false);

            int generatedUserId = -1;
            java.sql.Timestamp currentTimestamp = java.sql.Timestamp.valueOf(java.time.LocalDateTime.now());

            try (PreparedStatement pstmt = conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, username);
                pstmt.setString(2, email);
                pstmt.setString(3, password);
                pstmt.setString(4, role);
                pstmt.setString(5, "ACTIVE");
                pstmt.setTimestamp(6, currentTimestamp);

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating user failed, no rows affected.");
                }

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedUserId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Creating user failed, no ID obtained.");
                    }
                }
            }

            if (isArtist && generatedUserId != -1) {
                try (PreparedStatement pstmtArtist = conn.prepareStatement(insertArtistSql)) {
                    pstmtArtist.setInt(1, generatedUserId);
                    pstmtArtist.setBoolean(2, false);
                    pstmtArtist.executeUpdate();
                }
            }

            conn.commit();
            AttractiveTerminal.printGreen("[Success] Sign up successful! Automatically logging you in...\n");

            // Construct and return the new session object based on the role
            LocalDate creationDate = currentTimestamp.toLocalDateTime().toLocalDate();
            if (isArtist) {
                Artist newArtist = new Artist(generatedUserId, false);
                newArtist.setUserName(username);
                newArtist.setEmail(email);
                newArtist.setPassword(password);
                newArtist.setRole(role);
                newArtist.setAccStatus("ACTIVE");
                newArtist.setCreatedAt(creationDate);
                return newArtist;
            } else {
                return new User(generatedUserId, username, email, password, role, "ACTIVE", creationDate);
            }

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } 
                catch (SQLException ex) {
                    AttractiveTerminal.printError("Database Rollback Error: " + ex.getMessage());
                }
            }
            AttractiveTerminal.printError("Sign Up Error: Username or Email might already exist, or database is unreachable.\n");
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    AttractiveTerminal.printError("Database Connection Error: " + e.getMessage());
                }
            }
        }

        return null;
    }

    static String getValidEmail(Scanner sc) {
        while (true)
        {
            System.out.print("Enter Email (@gmail.com): ");
            String email = sc.nextLine().trim();

            if (!isValid(email)) {
                AttractiveTerminal.printError("Invalid format. Please try again.");
                AttractiveTerminal.printInfo("Note: Ensure one '@', a valid domain, and no double dots.");
                continue; // Restart loop
            }

            try
            {
                PreparedStatement ps = JdbcConnection.getConnection().prepareStatement(
                        "SELECT user_id FROM users WHERE email=?"
                );

                ps.setString(1, email);
                ResultSet rs = ps.executeQuery();

                if (rs.next())
                {
                    System.out.println("Email already exists. Re-enter.");
                }
                else
                {
                    return email;
                }
            }
            catch (SQLException e)
            {
                System.out.println(e.getMessage());
            }
        }
    }

    public static boolean isValid(String email) {
        if (email == null || email.isEmpty())
            return false;

        int firstAt = email.indexOf('@');
        int lastAt = email.lastIndexOf('@');
        if (firstAt == -1 || firstAt != lastAt)
            return false;

        String local = email.substring(0, firstAt);
        String domain = email.substring(firstAt + 1);

        if (local.isEmpty() || domain.isEmpty())
            return false;
        if (local.startsWith(".") || local.endsWith(".") || local.contains(".."))
            return false;
        if (domain.startsWith(".") || domain.endsWith(".") || !domain.contains("."))
            return false;

        int lastDot = domain.lastIndexOf('.');
        if (lastDot > domain.length() - 3)
            return false;

        return isAlphaNumeric(local, true) && isAlphaNumeric(domain, false);
    }

    private static boolean isAlphaNumeric(String str, boolean allowSpecial) {
        for (char c : str.toCharArray()) {
            if (Character.isLetterOrDigit(c))
                continue;
            if (c == '.' || c == '-' || (allowSpecial && (c == '_' || c == '+')))
                continue;
            return false;
        }
        return true;
    }

    static String getValidPassword(Scanner sc) {
        while (true)
        {
            System.out.print("Set Password: ");
            String pass1 = sc.nextLine();

            int b = 0;
            int c = 0;
            int d = 0;

            if (pass1.length() >= 8)
            {
                for (int i = 0; i < pass1.length(); i++)
                {
                    char rem = pass1.charAt(i);

                    if (rem >= '0' && rem <= '9')
                    {
                        b++;
                    }

                    if (rem >= 'A' && rem <= 'Z')
                    {
                        c++;
                    }

                    if (rem == '!' || rem == '@' || rem == '#' || rem == '$' ||
                            rem == '_' || rem == '%' || rem == '^' || rem == '&' || rem == '*')
                    {
                        d++;
                    }
                }

                if (b >= 3 && c >= 1 && d >= 1)
                {
                    AttractiveTerminal.printGreen("Password set successfully");
                    return pass1;
                }
            }

            AttractiveTerminal.printInfo(
                    "Password must contain:\n" +
                            "- Minimum 8 characters\n" +
                            "- At least 3 digits\n" +
                            "- At least 1 uppercase letter\n" +
                            "- At least 1 special character (!@#$_%^&*)"
            );
        }
    }

    /**
     * Helper method to repeatedly prompt the user until their input matches the required Regex format.
     */
    private String promptInput(Scanner sc, String prompt, String regex, String errorMsg) {
        String input;
        Pattern pattern = Pattern.compile(regex);
        while (true) {
            System.out.print(prompt);
            input = sc.nextLine().trim();
            if (pattern.matcher(input).matches()) {
                break;
            }
            System.out.println(errorMsg);
        }
        return input;
    }

    /**
     * Helper method to map the generic ResultSet into either a User or Artist object.
     */
    private User mapResultSetToEntity(ResultSet rs, Connection conn) throws SQLException {
        String role = rs.getString("role");
        LocalDate createdAt = null;
        String createdAtStr = rs.getString("created_at");

        if (createdAtStr != null && !createdAtStr.isEmpty()) {
            createdAt = LocalDate.parse(createdAtStr.substring(0, 10)); // Extract the YYYY-MM-DD portion
        }

        // Dynamically instantiate the correct Sub-Class based on role
        if ("ARTIST".equalsIgnoreCase(role)) {
            boolean isVerified = false;
            String checkArtistSql = "SELECT verified_status FROM artists_profile WHERE artist_id = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(checkArtistSql)) {
                pstmt.setInt(1, rs.getInt("user_id"));
                try (ResultSet artistRs = pstmt.executeQuery()) {
                    if (artistRs.next()) {
                        isVerified = artistRs.getBoolean("verified_status");
                    }
                }
            }

            Artist artist = new Artist(rs.getInt("user_id"), isVerified);
            artist.setUserName(rs.getString("username"));
            artist.setEmail(rs.getString("email"));
            artist.setRole(role);
            artist.setAccStatus(rs.getString("account_status"));
            artist.setCreatedAt(createdAt);

            return artist;
        } 
        else {
            return new User(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password"),
                    role,
                    rs.getString("account_status"),
                    createdAt
            );
        }
    }
}
