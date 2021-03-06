package Server.Domain;

import Server.People.User;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

class FileManager {
    private FacadeServer s;
    private static final String USERS_FILE = "./src/main/resources/StoreFiles/utenti.bin";
    private static final String AUCTION_FILE = "./src/main/resources/StoreFiles/auctions.bin";
    private static final String TIMERS_FILE = "./src/main/resources/StoreFiles/timers.bin";
    private static final String CLOSED_AUCTION = "./src/main/resources/StoreFiles/closedAuction.bin";

    /**
     * Used from the Textual Application (not DB) to save the Server's state
     */
    String saveState()  {
        String result = "";
        try {
            ObjectOutputStream o = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(AUCTION_FILE)));
            o.writeObject(s.getAuctionList());
            o.close();
            ObjectOutputStream o2 = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(USERS_FILE)));
            o2.writeObject(s.getUsersList());
            o2.close();
            ObjectOutputStream o3 = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(TIMERS_FILE)));
            o3.writeObject(s.getTimerTasks());
            o3.close();
            ObjectOutputStream o4 = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(CLOSED_AUCTION)));
            o4.writeObject(s.getClosedAuction());
            o4.close();

            result = "Auction state saved in: " + AUCTION_FILE + "\nUsers state saved in: " + USERS_FILE;
        }catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Used from the Textual Application (not DB) to load the Server's state
     */
    String loadState() {
        String result = "";
        loadAuction();
        loadUser();
        loadTimer();
        loadClosedAuction();
        reloadTimer();

        result = "Auction state restored from: " + AUCTION_FILE + "\nUsers state restored from: " + USERS_FILE;

        return result;
    }

    private void loadAuction() {
        try {
            ObjectInputStream i = new ObjectInputStream(new BufferedInputStream(new FileInputStream(AUCTION_FILE)));
            s.setAuctionList((ConcurrentHashMap<Integer, Auction>) i.readObject());
        }catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadTimer() {
        try {
            ObjectInputStream i3 = new ObjectInputStream(new BufferedInputStream(new FileInputStream(TIMERS_FILE)));
            s.setTimerTasks((HashMap<AuctionTimerStrategy, Long>) i3.readObject());
        }catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadUser() {
        try {
            ObjectInputStream i2 = new ObjectInputStream(new BufferedInputStream(new FileInputStream(USERS_FILE)));
            s.setUsersList((ConcurrentHashMap<String, User>) i2.readObject());
        }catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadClosedAuction() {
        try {
            ObjectInputStream i4 = new ObjectInputStream(new BufferedInputStream(new FileInputStream(CLOSED_AUCTION)));
            s.setClosedAuction((HashMap<Integer, Auction>) i4.readObject());
        }catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used from the Textual Application (not DB) to re-load the timers
     */
    private void reloadTimer() {
        Timer timer = new Timer();
        for (Map.Entry<AuctionTimerStrategy, Long> t: s.getTimerTasks().entrySet()) {
            // Reschedule task to initial value subtracted how much has already elapsed
            t.getKey().passArgument(s.getAuctionList(),s.getClosedAuction(),s.getTimerTasks()); // lo faccio per riallineare i riferimenti
            long timeLeft = t.getKey().getTimeLeft();
            if(timeLeft < 0) {
                t.getKey().run();
            }
            else {
                timer.schedule(t.getKey(), timeLeft);
            }
        }
    }

    FileManager(FacadeServer s) {
        this.s = s;
    }
}
