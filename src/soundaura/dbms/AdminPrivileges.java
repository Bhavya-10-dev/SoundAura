package soundaura.dbms;

public class AdminPrivileges {
    private UserDAO user_dao = new UserDAO(); 
    private ArtistDAO artist_dao = new ArtistDAO();
    private TrackDAO track_dao = new TrackDAO();
    
    protected UserDAO getUserDao() {
        return user_dao;
    }
    protected ArtistDAO getArtistDao() {
        return artist_dao;
    }
    protected TrackDAO getTrackDao() {
        return track_dao;
    }
}