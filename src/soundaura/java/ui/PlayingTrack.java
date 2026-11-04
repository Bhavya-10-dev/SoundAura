package soundaura.java.ui;

import java.util.InputMismatchException;
import java.util.Scanner;

import soundaura.dbms.TrackDAO;
import soundaura.java.entities.Playlist;
import soundaura.java.entities.Track;
import soundaura.java.player.MusicPlayer;
import soundaura.java.player.PlaybackQueue;

public interface PlayingTrack {
    public static void playingTrackInterface(Object obj,int user_id, Scanner sc) {
        if (obj == null) {
            AttractiveTerminal.printError("No object was passed (null)."); 
            return;
        }

        PlaybackQueue pb_queue = new PlaybackQueue();

        if (obj instanceof Track){
            Track track = (Track) obj;
            pb_queue.loadContext(track, user_id);
            System.out.print("\nPlaying ");
            AttractiveTerminal.printGreen(track.getTrackTitle() +"...");
        }
        else if (obj instanceof Playlist){
            Playlist playlist = (Playlist) obj;
            pb_queue.loadContext(playlist, user_id);
            System.out.print("Playing ");
            AttractiveTerminal.printGreen(playlist.getPlaylistName()+"...");
        }
        else{
            AttractiveTerminal.printError("Invalid Object Passed - got " + obj.getClass().getName()); //Exception Handling
            return;
        }

        if (pb_queue.getCurrentTrack() == null) {
            System.out.println("This playlist/track has no playable tracks.");
            return;
        }

        MusicPlayer.play(pb_queue.getCurrentTrack());
        while (true) {
            System.out.print("\nPlaying ");
            AttractiveTerminal.printGreen(pb_queue.getCurrentTrack().getTrackTitle()+"...");
            System.out.println("1. Previous");
            System.out.println("2. Play");
            System.out.println("3. Pause");
            System.out.println("4. Resume");
            System.out.println("5. Next");
            System.out.println("6. Forward 10 sec");
            System.out.println("7. Backward 10 sec");
            // System.out.println("8. Seek by Timestamp");
            if (!pb_queue.getCurrentTrack().isLiked()){
                System.out.println("8. Like");
            }
            else{
                System.out.println("8. Unlike");
            }
            System.out.println("9. Lyrics Mode");
            System.out.println("10. Queue");
            if (!pb_queue.isShuffled()){
                System.out.println("11. Shuffle");
            }
            else{
                System.out.println("11. Undo Shuffle");
            }
            System.out.println("12. Volume Level");
            System.out.println("13. Exit");

            int choice;
            try {
                System.out.print("\nEnter your choice: ");
                choice = sc.nextInt();
            }
            catch (InputMismatchException e) {
                AttractiveTerminal.printError("Enter Integer type only.");
                continue;
            }
            finally{
                sc.nextLine();
            }
            switch (choice) {
                case 1 -> {
                    Track prev = pb_queue.previous(user_id);
                    if (prev != null){
                        MusicPlayer.play(prev);
                    }
                    else{
                        System.out.println("No Track Available.");
                        // current track keeps playing as-is, no further calls needed
                    }
                }
                case 2 -> {
                    MusicPlayer.play(pb_queue.getCurrentTrack());
                }
                case 3 -> {
                    MusicPlayer.pause();
                }
                case 4 -> {
                    MusicPlayer.restart();
                }
                case 5 -> {
                    Track next = pb_queue.next(user_id);
                    if (next != null){
                        MusicPlayer.play(next);
                    }
                    else{
                        System.out.println("No Track Available.");
                    }
                }
                case 6 -> {
                    MusicPlayer.seekForward(10);
                }
                case 7 -> {
                    MusicPlayer.seekBackward(10);
                }
                // case 8 -> {
                //     enterTimeStamp(pb_queue.getCurrentTrack(), sc);
                // }
                case 8 -> {
                    boolean liked = pb_queue.getCurrentTrack().isLiked();
                    if (!liked){
                        liked = pb_queue.getCurrentTrack().like();
                        TrackDAO.likeTrack(user_id, pb_queue.getCurrentTrack().getTrackId(), liked);
                    }
                    else{
                        liked = pb_queue.getCurrentTrack().unlike();
                        TrackDAO.likeTrack(user_id, pb_queue.getCurrentTrack().getTrackId(), liked);
                    }
                }
                case 9 -> {
                    System.out.println("Menu:");
                    System.out.println("1. Animation Mode(Press q or 0 then enter to stop)");
                    System.out.println("2. Display Full Lyrics");
                    int ans = -1;
                    while (true) {
                        try {
                            System.out.print("\nEnter your choice: ");
                            ans = sc.nextInt();
                        } 
                        catch (InputMismatchException e) {
                            System.out.println("Enter Interger datatype only.");
                            continue;
                        }
                        finally{
                            sc.nextLine();
                        }
                        if (ans==1){
                            LyricsMode.lyrics(pb_queue.getCurrentTrack(), sc);
                            break;
                        }
                        else if (ans==2){
                            String RESET = "\033[0m";
                            String CYAN = "\033[96m";
                            System.out.println("\n" + CYAN + pb_queue.getCurrentTrack().getLyrics() + RESET);
                            break;
                        }
                        else{
                            AttractiveTerminal.printError("INVALID choice. Please enter again.");
                            continue;
                        }
                    }
                }
                case 10 -> {
                    queueInterface(pb_queue, user_id, sc);
                }
                case 11 -> {
                    pb_queue.toggleShuffle();
                }
                case 12 -> {
                    enterVolumeLevel(sc);
                }
                case 13 -> {
                    MusicPlayer.pause();
                    return;
                }
                default -> {
                    AttractiveTerminal.printError("INVALID choice. Please enter again."); 
                }
            }
        }
    }

    // private static void enterTimeStamp(Track track, Scanner sc) {
    //     System.out.println("\nTotal Duration of the track: " + track.getDuration());
    //     System.out.print("Enter TimeStamp: ");
    //     int duration = 0;
    //     try {
    //         duration = sc.nextInt(); 
    //     } 
    //     catch (InputMismatchException e) {
    //         AttractiveTerminal.printError("Enter Integer type only.");
    //         sc.nextLine();
    //         enterTimeStamp(track, sc);
    //     }
    //     sc.nextLine();
    //     MusicPlayer.seekBy(duration);
    // }

    private static void enterVolumeLevel(Scanner sc) {
        double vol;
        while (true) {          
            System.out.print("\nEnter Volume Level(0.0 to 1.0): ");
            vol = 0.0;
            try {
                vol = sc.nextDouble();
            } 
            catch (InputMismatchException e) {
                AttractiveTerminal.printError("Enter Double Type Only.");
                continue;
            }  
            finally{
                sc.nextLine();
            }
            if (vol<0.0 || vol>1.0){
                AttractiveTerminal.printError("Enter volume level between the range of 0.0 to 1.0.");
                continue;
            }
            else{
                break;
            }
        }
        MusicPlayer.setVolume(vol);
    }

    private static void showQueue(PlaybackQueue pbq){
        System.out.println("\nQueue");
        System.out.println("\nNow Playing:");
        System.out.println("\t"+pbq.getCurrentTrack().getTrackTitle());
        if (!pbq.getUpNext().isEmpty()){
            System.out.println("\n0. Next in Queue:");
            for (int i=0; i<pbq.getUpNext().size(); i++){
                System.out.println("\t"+(i+1)+". "+pbq.getUpNext().get(i).getTrackTitle());
            }
        }
        System.out.println("\n1. Next from: "+pbq.getPlaylistName());
        for (int i=0; i<pbq.getContextQueue().size(); i++){
            System.out.println("\t"+(i+1)+". "+pbq.getContextQueue().get(i).getTrackTitle());
        }
        System.out.println("\n2. Recommendations:");
        for (int i=0; i<pbq.getRecomQueue().size(); i++){
            System.out.println("\t"+(i+1)+". "+pbq.getRecomQueue().get(i).getTrackTitle());
        }
    }

    private static void queueInterface(PlaybackQueue pbq, int user_id, Scanner sc) {
        int instr_count = 0;
        while(true){
            showQueue(pbq);
            String instruction = """
                    \nUse Track-Action Code for quick insertion and deletion of track in queue.
                    'Track-Action Code' (For eg: 1-10-1)
                    Where '-' is seperator.
                    Source No.(First Set of numbers)- From where the track is being added or removed from the Queue.
                    eg: 
                        0. Next in Queue
                        1. Current Playlist
                        2. Recommendation
                    Track No.(Second Set of numbers)- Simply a track number.
                    Action No.(Third Set of numbers)- What action to be perform on the track.
                    """;
            if (instr_count<3){
                AttractiveTerminal.printInfo(instruction);
                instr_count++;
            }
            System.out.println("\nQueue MENU:");
            System.out.println("1. Add to Queue");
            System.out.println("2. Delete from Queue");
            System.out.println("3. Exit");
            
            int choice;
            try {
                System.out.print("\nEnter Your Choice: ");
                choice = sc.nextInt();
            }
            catch (InputMismatchException e) {
                AttractiveTerminal.printError("Enter Integer type only.");
                continue;
            }
            finally{
                sc.nextLine();
            }
            switch (choice) {
                case 1 -> {
                    addToQueue(pbq, sc);
                }
                case 2 -> {
                    deleteFromQueue(pbq, user_id, sc);
                }
                case 3 -> {
                    return;
                }
                default -> {
                    AttractiveTerminal.printError("INVALID choice. Please enter again.");  //E.H
                }
            }
        }
    }

    private static void addToQueue(PlaybackQueue pbq, Scanner sc){
        String add_menu = """
                Add Action MENU:
                1. Next in Queue
                2. Play Now
                3. Exit
                """;
        int add_menu_count = 0;
        if (add_menu_count<3){
            System.out.println("\n"+add_menu);
            add_menu_count++;
        }

        System.out.print("\nEnter Track-Action Code: ");
        String trk_act_code = sc.nextLine();  //E.H may be or little bit

        String[] codes = trk_act_code.split("-");
        int code1 = -1;
        int code2 = -1;
        int code3 = -1;
        String source = "";
        int idx = -1;
        String action = "";

        if (codes.length>3 || codes.length==0){
            AttractiveTerminal.printError("INVALID choice. Please enter again."); //E.H
            addToQueue(pbq, sc);
        }
        else if (trk_act_code.equalsIgnoreCase("3")){
            return;
        }
        else{
            //E.H also for ArrayIndexOutOfBoundsException
            try {
                code1 = Integer.parseInt(codes[0]);
                code2 = Integer.parseInt(codes[1]);
                code3 = Integer.parseInt(codes[2]);
            } 
            catch (NumberFormatException e) {
                AttractiveTerminal.printError("INVALID choice. Please enter again."); 
                addToQueue(pbq, sc);
            }
            catch (ArrayIndexOutOfBoundsException e){
                AttractiveTerminal.printError("INVALID choice. Please enter again."); 
                addToQueue(pbq, sc);
            }


            //source validation
            if (code1==1){
                source = "playlist";
                //index validation
                if (code2>=1 && code2<=pbq.getContextQueue().size()){
                    idx = code2-1;
                }
                else{
                    AttractiveTerminal.printError("INVALID choice. Please enter again."); 
                    addToQueue(pbq, sc);
                }
            }
            else if (code1==2){
                source = "recommended";
                //index validation
                if (code2>=1 && code2<=pbq.getRecomQueue().size()){
                    idx = code2-1;
                }
                else{
                    AttractiveTerminal.printError("INVALID choice. Please enter again."); 
                    addToQueue(pbq, sc);
                }
            }
            else{
                AttractiveTerminal.printError("INVALID choice. Please enter again."); //E.H Can be better? in all the other validations also
                addToQueue(pbq, sc);
            }

            //action validation
            if (code3==1){
                action = "next_in_queue";
            }
            else if (code3==2){
                action = "play_now";
            }
            else{
                AttractiveTerminal.printError("INVALID choice. Please enter again."); 
                addToQueue(pbq, sc);
            }

            pbq.insertInQueue(idx, source, action);
            return;
        }
    }

    private static void deleteFromQueue(PlaybackQueue pbq, int user_id, Scanner sc){
        String delete_menu = """
                \nNOTE: For delteing there is no source no. only track no and action no. For eg: 10-1
                Delete Action MENU:
                0. Clear Queue
                1. Delete particular Track From Queue
                2. Exit
                """;
        int delete_menu_count = 0;
        if (delete_menu_count<3){
            AttractiveTerminal.printInfo("\n"+delete_menu);
            delete_menu_count++;
        }

        System.out.print("\nEnter Track-Action Code: ");
        String trk_act_code = sc.nextLine();   //E.H may be or little bit 

        if (trk_act_code.equalsIgnoreCase("0")){
            clearQueueConfirmation(pbq, user_id, sc);
        }
        else if (trk_act_code.equalsIgnoreCase("2")){
            return;
        }
        else{
            String[] codes = trk_act_code.split("-");
            int code1 = -1;
            int code2 = -1;
            int idx = -1;

            if (codes.length>2 || codes.length==0){
                AttractiveTerminal.printError("INVALID choice. Please enter again."); //E.H
                deleteFromQueue(pbq, user_id, sc);
            }
            else{
                //E.H also for ArrayIndexOutOfBoundsException
                try {
                    code1 = Integer.parseInt(codes[0]);
                    code2 = Integer.parseInt(codes[1]);
                } 
                catch (NumberFormatException e) {
                    AttractiveTerminal.printError("INVALID choice. Please enter again."); 
                    deleteFromQueue(pbq, user_id, sc);
                }
                catch (ArrayIndexOutOfBoundsException e){
                    AttractiveTerminal.printError("INVALID choice. Please enter again."); 
                    deleteFromQueue(pbq, user_id, sc);
                }

                //index validation
                if (code1>=1 && code1<=pbq.getUpNext().size()){
                    idx = code2-1;
                    //action validation
                    if (code2==0){
                        clearQueueConfirmation(pbq, user_id, sc);
                    }
                    else if (code2==1){
                        if (code1==1)
                        pbq.deleteInQueue(idx, user_id);
                        return;
                    }
                    else{
                        AttractiveTerminal.printError("INVALID choice. Please enter again."); 
                        deleteFromQueue(pbq, user_id, sc);
                    }
                }
                else{
                    AttractiveTerminal.printError("INVALID choice. Please enter again."); 
                    deleteFromQueue(pbq, user_id, sc);
                }
            }
        }
    }

    private static void clearQueueConfirmation(PlaybackQueue pbq, int user_id, Scanner sc){
        System.out.print("Enter are you sure you want clear Queue(Y/N): ");
        String choice = sc.nextLine();  //E.H
        choice = choice.toUpperCase();
        switch (choice) {
            case "Y" -> {
                pbq.getUpNext().clear();
                MusicPlayer.play(pbq.next(user_id));
            }
            case "N" -> {
                return;
            }
            default -> {
                AttractiveTerminal.printError("INVALID choice. Please enter again."); //E.H
                deleteFromQueue(pbq, user_id, sc);
            }
        }
    }
}
