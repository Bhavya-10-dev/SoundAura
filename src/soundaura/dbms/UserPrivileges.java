package soundaura.dbms;

public class UserPrivileges {
    private PlaylistDAO playlist_dao = new PlaylistDAO();

    protected PlaylistDAO getPlaylistDao() {
        return playlist_dao;
    }   
}
