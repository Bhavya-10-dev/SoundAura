package soundaura.java.account;

import java.util.InputMismatchException;
// import java.util.ArrayList;
import java.util.Scanner;

// import soundaura.dbms.PlaylistDAO;
// import soundaura.dbms.UserDAO;
import soundaura.dbms.UserPrivileges;
import soundaura.java.entities.User;
import soundaura.java.ui.AttractiveTerminal;
import soundaura.java.ui.Create;
import soundaura.java.ui.HomePage;
import soundaura.java.ui.MainMenu;
import soundaura.java.ui.Profile;
import soundaura.java.ui.Search;
import soundaura.java.ui.YourLibrary;

public class UserAccount extends UserPrivileges implements MainMenu, HomePage, Search, YourLibrary, Profile, Create{
    private User user; // Holds the active session

    // Constructor to receive the logged-in user
    public UserAccount(User user) {
        this.user = user;
    }

    @Override
    public void mainMenu(Scanner sc) {
        while (true) {
            System.out.println("\nMain Menu:");
            System.out.println("1. Home Page");
            System.out.println("2. Search");
            System.out.println("3. Your Library");
            System.out.println("4. Profile");
            System.out.println("5. Create Playlist");
            System.out.println("6. Exit");

            int choice;
            try {
                System.out.print("\nEnter your choice: ");
                choice = sc.nextInt();

            }
            catch (InputMismatchException e) {
                System.out.println("Enter Integer type only.");
                continue;
            }
            finally{
                sc.nextLine();
            }
            switch (choice) {
                case 1 -> {
                    AttractiveTerminal.displayGraphicText("homepage.txt");
                    HomePage.homePageMenu(sc, user.getUserId());
                }
                case 2 -> {
                    AttractiveTerminal.displayGraphicText("search.txt");
                    Search.searchMenu(sc, user.getUserId());
                }

                case 3 -> {
                    AttractiveTerminal.displayGraphicText("yourlibrary.txt");
                    YourLibrary.yourLibraryMenu(sc, user.getUserId());
                }
                case 4 -> {
                    AttractiveTerminal.displayGraphicText("profile.txt");
                    this.profileView();
                }
                case 5 -> {
                    AttractiveTerminal.displayGraphicText("createplaylist.txt");
                    Create.createMenu(sc, user, getPlaylistDao());
                }
                case 6 -> {
                    System.out.println("\nExiting the application. Thank You! for using.");
                    return;
                }
                default ->{
                    AttractiveTerminal.printError("INVALID choice. Please try again.");
                }
            } 
        }
    }

    @Override
    public void profileView() {
        System.out.println("\nUser Profile:");
        System.out.println("Name: " + user.getUserName());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Created At: " + user.getCreatedAt());
    }
}
