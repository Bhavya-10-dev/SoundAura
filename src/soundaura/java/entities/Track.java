package soundaura.java.entities;

import java.io.*;
// import java.time.Duration;
import java.time.LocalDate;

import soundaura.java.ui.AttractiveTerminal;

import java.lang.System;

public class Track {
    private int track_id;
    private String track_title;
    private int artist_id;
    private String genre;
    private long duration; //in seconds  
    private String lyrics;  
    private String rel_audio_path;
    private static String sys_audio_path;  
    private String abs_audio_path;
    private LocalDate release_date;
    private long total_views;
    private boolean is_approved;
    private boolean is_liked = false;

    static {
        sys_audio_path = System.getProperty("user.dir") + File.separator + "tracks";
    }

    public Track() {
        
    }

    public Track(String track_title, int artist_id, String genre, long duration, String lyrics, String relAudioPath, LocalDate release_date) {
        this.track_title = track_title;
        this.artist_id = artist_id;
        this.genre = genre;
        this.duration = duration;
        this.lyrics = lyrics;
        this.rel_audio_path = relAudioPath;
        this.release_date = release_date;
    }

    public Track(int track_id, String track_title, int artist_id, String genre, long duration, String lyrics, String relAudioPath, LocalDate release_date, long total_views, boolean is_approved) {
        this.track_id = track_id;
        this.track_title = track_title;
        this.artist_id = artist_id;
        this.genre = genre;
        this.duration = duration;
        this.lyrics = lyrics;
        this.rel_audio_path = relAudioPath;
        this.abs_audio_path = sys_audio_path + File.separator + relAudioPath;
        // this.audio_file = new File(abs_audio_path);
        this.release_date = release_date;
        this.total_views = total_views; 
        this.is_approved = is_approved;
    }

    public int getTrackId() {
        return track_id;
    }

    public void setTrackId(int track_id) {
        this.track_id = track_id;
    }

    public String getTrackTitle() {
        return track_title;
    }

    public void setTrackTitle(String track_title) {
        this.track_title = track_title;
    }

    public int getArtistId() {
        return artist_id;
    }

    public void setArtistId(int artist_id) {
        this.artist_id = artist_id;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getRelAudioPath() {
        return rel_audio_path;
    }

    public void setRelAudioPath(String relAudioPath) {
        this.rel_audio_path = relAudioPath;
    }

    public String getAbsAudioPath() {
        return abs_audio_path;
    }

    public void setAbsAudioPath(String absAudioPath) {
        this.abs_audio_path = absAudioPath;
    }

    // public File getAudioFile() {
    //     return audio_file;
    // }

    // public void setAudioFile(File audioFile) {
    //     this.audio_file = audioFile;
    // }

    public LocalDate getReleaseDate() {
        return release_date;
    }

    public void setReleaseDate(LocalDate release_date) {
        this.release_date = release_date;
    }

    public long getTotalViews() {
        return total_views;
    }

    public void setTotalViews(long total_views) {
        this.total_views = total_views;
    }

    public boolean isApproved() {
        return is_approved;
    }

    public void setApproved(boolean is_approved) {
        this.is_approved = is_approved;
    }

    public boolean isLiked(){
        return is_liked;
    }

    public boolean like() {
        AttractiveTerminal.printGreen("Liked " + track_title + "!");
        is_liked = true;
        return isLiked();
    }  

    public boolean unlike() {
        System.out.println("Unliked " + track_title + ".");
        is_liked = false;
        return isLiked();
    }  
}
