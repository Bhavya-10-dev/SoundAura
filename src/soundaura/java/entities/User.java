package soundaura.java.entities;

import java.time.LocalDate;
import java.util.HashMap;

public class User {
    protected int user_id;
    protected String user_name;
    protected String email;
    protected String password;
    protected String role;
    protected String acc_status;
    protected LocalDate created_at;
    protected HashMap<Integer, Playlist> playlists;

    public User() {
        
    }
    
    public User(int user_id, String user_name, String email, String password, String role, String acc_status, LocalDate created_at) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.acc_status = acc_status;
        this.created_at = created_at;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public String getUserName() {
        return user_name;
    }

    public void setUserName(String user_name) {
        this.user_name = user_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAccStatus() {
        return acc_status;
    }

    public void setAccStatus(String acc_status) {
        this.acc_status = acc_status;
    }

    public LocalDate getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(LocalDate created_at) {
        this.created_at = created_at;
    }



    //
}
