package soundaura.dbms;

public class ArtistPrivileges {
    private TrackDAO track_dao = new TrackDAO();
    private PlaylistDAO playlist_dao = new PlaylistDAO();

    protected TrackDAO getTrackDao() {
        return track_dao;
    }
    protected PlaylistDAO getPlaylistDao() {
        return playlist_dao;
    }   
}
