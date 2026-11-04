package soundaura;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

import javafx.application.Platform;
import soundaura.dbms.JdbcConnection;
import soundaura.java.account.Account;
import soundaura.java.account.AdminAccount;
import soundaura.java.account.ArtistAccount;
import soundaura.java.account.UserAccount;
import soundaura.java.entities.Artist;
import soundaura.java.entities.User;
import soundaura.java.ui.AttractiveTerminal;

public class Main {
    public static void main(String[] args) {
        if (JdbcConnection.getConnection() == null){
            AttractiveTerminal.printError("Couldn't connect to Server.");
            return;
        }

        AttractiveTerminal.printGreen("\n[Success] Connection established successfully!\n");
        Scanner sc = new Scanner(System.in);

        // Step 1: just initialize the JavaFX toolkit — nothing else happens here
        Platform.startup(() -> {
            Platform.setImplicitExit(false); // keep JavaFX alive even with no Stage/window
        });

        // Step 2: run your actual console app on the NORMAL main thread,
        // NOT inside the Platform.startup() callback.
        // Blocking Scanner calls here are totally fine — they no longer
        // stall the JavaFX Application Thread, which stays free to handle
        // MediaPlayer's internal runLater() calls immediately.
        try {
            Thread.sleep(2000);
        } 
        catch (InterruptedException e) {

        }

        AttractiveTerminal.clearScreen();
        AttractiveTerminal.displayAnimationText("soundaura.txt");
        while (true) {
            Account sessionAccount = new Account();
            User loggedInUser = null;
            boolean isAuthenticated = false;

            while (!isAuthenticated) {
                System.out.println("\nSoundAura's Main Menu");
                System.out.println("1. Login");
                System.out.println("2. Sign Up");
                System.out.println("3. Exit Application");
                System.out.print("\nEnter your choice: ");

                try {
                    int choice = sc.nextInt();
                    sc.nextLine();

                    switch (choice) {
                        case 1 -> {
                            AttractiveTerminal.displayGraphicText("login.txt");
                            loggedInUser = sessionAccount.login(sc);
                            if (loggedInUser != null) {
                                isAuthenticated = true;
                            }
                        }
                        case 2 -> {
                            AttractiveTerminal.displayGraphicText("signup.txt");
                            loggedInUser = sessionAccount.signUp(sc);
                            if (loggedInUser != null){
                                isAuthenticated = true;
                            } 
                        }
                        case 3 -> {
                            System.out.println("\nExiting SoundAura. Thank You! for using it.");
                            sc.close();
                            Platform.exit();
                            System.exit(0);
                        }
                        default -> AttractiveTerminal.printError("Please enter 1, 2, or 3.\n");
                    }
                } catch (InputMismatchException e) {
                    AttractiveTerminal.printError("INVALID input. Please enter a number.\n");
                    sc.nextLine();
                }
            }

            // Route to the correct Account Interface
            AttractiveTerminal.printGreen("[Success] Authentication successful. Loading Dashboard...");
            String userRole = loggedInUser.getRole();

            if ("ARTIST".equalsIgnoreCase(userRole)) {
                // System.out.println(loggedInUser.getUserId());
                // ArrayList<Integer> valid = new ArrayList<>();
                int i = 0;
                String sql = "SELECT user_id FROM users WHERE email=?";
                    try (Connection conn = JdbcConnection.getConnection();
                        PreparedStatement pstmt = conn.prepareStatement(sql)) {

                        pstmt.setString(1, loggedInUser.getEmail());

                        try (ResultSet rs = pstmt.executeQuery()) {
                            if (rs.next()) {
                                i = rs.getInt("user_id");
                            }
                        }
                    } 
                    catch (SQLException e) {
                        AttractiveTerminal.printError(e.getMessage());
                    }

                    // if (!flag){
                    //     AttractiveTerminal.printError("User Id does not exits. Please enter again.");
                    //     continue;
                    // }
                    // else{
                    //     break;
                    // }
                loggedInUser.setUserId(i);
                ArtistAccount artistAcc = new ArtistAccount((Artist) loggedInUser);
                artistAcc.mainMenu(sc);
            } 
            else if ("ADMIN".equalsIgnoreCase(userRole)) {
                AdminAccount adminAcc = new AdminAccount();
                adminAcc.mainMenu(sc);
            } 
            else {
                UserAccount userAcc = new UserAccount(loggedInUser);
                userAcc.mainMenu(sc);
            }

            AttractiveTerminal.clearScreen();
        }
    }
}