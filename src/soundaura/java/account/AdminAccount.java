package soundaura.java.account;

import java.util.InputMismatchException;
import java.util.Scanner;

import soundaura.dbms.AdminPrivileges;
import soundaura.java.ui.AttractiveTerminal;
// import soundaura.java.entities.User;
import soundaura.java.ui.MainMenu;

public class AdminAccount extends AdminPrivileges implements MainMenu{
    // private User admin; // Holds the active admin session

    public AdminAccount() {

    }

    @Override
    public void mainMenu(Scanner sc) {
        while (true) {
            System.out.println("\nAdmin Main Menu:");
            System.out.println("1. Approve Track");
            System.out.println("2. Approve Artist");
            System.out.println("3. Delete Track");
            System.out.println("4. Delete Artist");
            System.out.println("5. Delete User");
            System.out.println("6. Exit");

            int choice;
            try {
                System.out.print("\nEnter your choice: ");
                choice = sc.nextInt();
            }
            catch (InputMismatchException e) {
                AttractiveTerminal.printError("Enter Integer datatype only.");
                continue;
            }
            finally{
                sc.nextLine();
            }
            switch (choice) {
                case 1 -> {
                    int id;
                    while (true){
                        try {
                            System.out.print("\nEnter Track-id: ");
                            id = sc.nextInt();
                            break;
                        }
                        catch (InputMismatchException e) {
                            AttractiveTerminal.printError("Enter Integer datatype only.");
                            continue;
                        }
                        finally{
                            sc.nextLine();
                        }
                    }
                    boolean approved = false;
                    while (true) {
                        System.out.print("Do you want to approve(Y/N): ");
                        String ans = sc.nextLine();
                        if(ans.equalsIgnoreCase("Y")){
                            approved = true;
                            break;
                        }
                        else if (ans.equalsIgnoreCase("N")){
                            approved = false;
                            break;
                        }
                        else{
                            AttractiveTerminal.printError("INVALID choice. Please enter again.");
                            continue;
                        }
                    }
                    if (getTrackDao().approveTrackById(id, approved)){
                        AttractiveTerminal.printGreen("Track Approved Successfully!");
                    }
                    else{
                        AttractiveTerminal.printError("Something went wrong. Couldn't approve Track.");
                    }
                }
                case 2 -> {
                    int id;
                    while (true) {
                        try {
                            System.out.print("\nEnter Artist-id: ");
                            id = sc.nextInt();
                            break;
                        }
                        catch (InputMismatchException e) {
                            AttractiveTerminal.printError("Enter Integer datatype only.");
                            continue;
                        }
                        finally{
                            sc.nextLine();
                        }
                    }
                    boolean approved = false;
                    while (true) {
                        System.out.print("Do you want to approve(Y/N): ");
                        String ans = sc.nextLine();
                        if(ans.equalsIgnoreCase("Y")){
                            approved = true;
                            break;
                        }
                        else if (ans.equalsIgnoreCase("N")){
                            approved = false;
                            break;
                        }
                        else{
                            AttractiveTerminal.printError("INVALID choice. Please enter again.");
                            continue;
                        }
                    }
                    if (getArtistDao().approveArtistById(id, approved)){
                        AttractiveTerminal.printGreen("Artist Approved Successfully!");
                    }
                    else{
                        AttractiveTerminal.printError("Error: Something went wrong. Couldn't approve Artist.");
                    }
                }
                case 3 -> {
                    int id;
                    while (true) {
                        try {
                            System.out.print("\nEnter Track-id: ");
                            id = sc.nextInt();
                            break;
                        }
                        catch (InputMismatchException e) {
                            AttractiveTerminal.printError("Enter Integer datatype only.");
                            continue;
                        }
                        finally{
                            sc.nextLine();
                        }
                    }
                    if (getTrackDao().deleteTrackById(id)){
                        AttractiveTerminal.printGreen("Track Deleted Successfully!");
                    }
                    else{
                        AttractiveTerminal.printError("Something went wrong. Couldn't delete Track.");
                    }
                }
                case 4 -> {
                    int id;
                    while (true) {
                        try {
                            System.out.print("\nEnter Artist-id: ");
                            id = sc.nextInt();
                            break;
                        }
                        catch (InputMismatchException e) {
                            AttractiveTerminal.printError("Enter Integer datatype only.");
                            continue;
                        }
                        finally{
                            sc.nextLine();
                        }
                    }
                    if (getArtistDao().deleteArtistById(id)){
                        AttractiveTerminal.printGreen("Artist Deleted Successfully!");
                    }
                    else{
                        AttractiveTerminal.printError("Something went wrong. Couldn't delete Artist.");
                    }
                }
                case 5 -> {
                    int id;
                    while (true) {
                        try {
                            System.out.print("\nEnter User-id: ");
                            id = sc.nextInt();
                            break;
                        }
                        catch (InputMismatchException e) {
                            AttractiveTerminal.printError("Enter Integer datatype only.");
                            continue;
                        }
                        finally{
                            sc.nextLine();
                        }
                    }
                    if (id==11){
                        AttractiveTerminal.printError("Admin Account can't be delete.");
                    }
                    else if (getUserDao().deleteUserById(id)){
                        AttractiveTerminal.printGreen("User Deleted Successfully!");
                    }
                    else{
                        AttractiveTerminal.printError("Something went wrong. Couldn't delete User.");
                    }
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
}
