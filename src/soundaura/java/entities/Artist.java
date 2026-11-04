package soundaura.java.entities;

public class Artist extends User{
    protected int artist_id;
    // public String bio;
    protected boolean verified_status;
    // public long total_followers;

    public Artist() {
        
    }
    
    public Artist(int artist_id, boolean verified_status) {
        super();
        super.user_id = 12;
        this.artist_id = artist_id;
        this.verified_status = verified_status;
        // this.total_followers = total_followers;
    }

    public int getArtistId() {
        return artist_id;
    }

    public void setArtistId(int artist_id) {
        this.artist_id = artist_id;
    }

    // public String getBio() {
    //     return bio;
    // }

    // public void setBio(String bio) {
    //     this.bio = bio;
    // }

    public boolean isVerifiedStatus() {
        return verified_status;
    }

    public void setVerifiedStatus(boolean verified_status) {
        this.verified_status = verified_status;
    }

    // public long getTotalFollowers() {
    //     return total_followers;
    // }

    // public void setTotalFollowers(long total_followers) {
    //     this.total_followers = total_followers;
    // }
}
