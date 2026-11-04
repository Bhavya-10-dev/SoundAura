package soundaura.java.player;

import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import soundaura.java.entities.Track;
import soundaura.java.exception.CustomException;

public class MusicPlayer {
    private static String current_file_path;
    private static MediaPlayer media_player;
    // private static Runnable on_track_end_callback; // NEW

    // public static void setOnSongEndCallback(Runnable callback) { // NEW
    //     on_track_end_callback = callback;
    // }

    // ---------- PLAY ----------
    public static void play(Track track) {
        if (track == null) {
            throw new CustomException("No Track Available.");
        }

        // If it's the same file and already loaded, just resume
        if (track.getAbsAudioPath().equals(current_file_path) && media_player != null) {
            resume();
            return;
        }

        stop(); // clean up whatever was playing before

        current_file_path = track.getAbsAudioPath();
        File audio_file = new File(current_file_path);

        if (!audio_file.exists()) {
            throw new CustomException("Audio file not found at path: " + current_file_path);
        }

        Media media = new Media(audio_file.toURI().toString());
        media_player = new MediaPlayer(media);

        media_player.setOnReady(() -> media_player.play());

        // media_player.setOnEndOfMedia(() -> { // NEW
        //     if (on_track_end_callback != null) {
        //         on_track_end_callback.run();
        //     }
        // });

        media_player.setOnError(() -> {
            throw new CustomException("Playback error occurred.", media_player.getError());
        });
    }

    // ---------- PAUSE ----------
    public static void pause() {
        if (media_player != null) {
            media_player.pause();
        }
    }

    // ---------- RESUME ----------
    public static void resume() {
        if (media_player != null) {
            media_player.play();
        }
    }

    // ---------- STOP ----------
    private static void stop() {
        if (media_player != null) {
            media_player.stop();
            media_player.dispose();
            media_player = null;
        }
        current_file_path = null;
    }

    // ---------- RESTART CURRENT SONG ----------
    public static void restart() {
        if (media_player != null) {
            media_player.seek(Duration.ZERO);
            media_player.play();
        }
    }

    // ---------- FORWARD / BACKWARD ----------
    public static void seekForward(int seconds) {
        seekBy(seconds);
    }

    public static void seekBackward(int seconds) {
        seekBy(-seconds);
    }

    private static void seekBy(int seconds) {
        if (media_player == null) return;

        Duration current = media_player.getCurrentTime();
        Duration total = media_player.getTotalDuration();
        Duration target = current.add(Duration.seconds(seconds));

        if (target.lessThan(Duration.ZERO)) {
            target = Duration.ZERO;
        }
        else if (total != null && target.greaterThan(total)) {
            target = total;
        }

        media_player.seek(target);
    }

    // ---------- VOLUME ----------
    public static void setVolume(double volume) { // 0.0 to 1.0
        if (media_player != null) {
            media_player.setVolume(volume);
        }
    }

    // ---------- STATUS ----------
    public static boolean isPlaying() {
        return media_player != null && media_player.getStatus() == MediaPlayer.Status.PLAYING;
    }

    public static Duration getCurrentTime() {
        return media_player != null ? media_player.getCurrentTime() : Duration.ZERO;
    }

    public static Duration getTotalDuration() {
        if (media_player != null) {
            return media_player.getTotalDuration(); //add this time in database.
        } else {
            throw new CustomException("No track is being played. Cannot retrieve total duration.");
        }
    }
}