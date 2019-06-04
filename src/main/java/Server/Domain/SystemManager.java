package Server.Domain;

import Server.People.User;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SystemManager extends UnicastRemoteObject implements Proxy {

    private ConcurrentHashMap<String,User> usersList;
    private ConcurrentHashMap<Integer,Auction> auctionList;
    private HashMap<Integer,Auction> closedAuction;
    private HashMap<LifeCycleAuctionTask, Long> timerTasks;
    private HashMap<LifeCycleAuctionTaskDB, Long> timerTasksDB;
    private int auctionIdCounter = 1; //Valido per FileManager, il valore non e' salvato
    private transient Timer timer;
    private FileManager files;
    private DBManager db;


    public void createUser(String username, String password){
        User user = new User(username,password);
        addUser(user);
    }
    public void createUserDB (String username, String password){
        db.addUser(username,password);
    }


    public boolean logoutS(String username) {
        userListed(username).setLoggedIn(false);
        return true;
    }
    public boolean logoutSDB(String username) {
        return db.logout(username);
    }


    private void addUser(User user){
        usersList.put(user.getUsername(),user);
    }


    /**
     * Il metodo effettua il controllo sull'utilizzo dell'username.
     * Se l'username e' gia' utilizzato rifiuta la creazione dell'utente.
     *
     */
    public boolean alredyTakenUsername(String username){
        return (usersList.containsKey(username));
    }
    public boolean alredyTakenUsernameDB(String username) { return db.alredyTakenUsername(username); }


    public String showAllActiveAuctions() {
        String toPrint = "";
        for (Map.Entry<Integer,Auction> entry : auctionList.entrySet()) {
            Auction entryValue = entry.getValue();
            toPrint =  toPrint + entryValue.auctionInformation();
        }
        if(auctionList.isEmpty()) {
            toPrint = "Nessun Inserzione Esistente" + "\n";
        }
        return toPrint;
    }
    public String showAllActiveAuctionsDB() { return db.showAllActive(); }


    public String showClosedAuctions() {
        String toPrint = "";
        for (Map.Entry<Integer,Auction> entry : closedAuction.entrySet()) {
            Auction entryValue = entry.getValue();
            toPrint =  toPrint + entryValue.closedAuctionInformation();
        }
        if(closedAuction.isEmpty()) {
            toPrint = "Nessun Inserzione Chiusa" + "\n";
        }
        return toPrint;
    }
    public String showClosedAuctionsDB() { return db.showAllClosed(); }


    private void createTimer(LocalDateTime closingTime) {
        ZonedDateTime zdt = closingTime.atZone(ZoneId.of("Europe/Rome"));
        long millis = zdt.toInstant().toEpochMilli();
        LifeCycleAuctionTask t = new LifeCycleAuctionTask(auctionIdCounter,millis);
        t.passArgument(auctionList,closedAuction,timerTasks);
        timer.schedule(t, (millis - System.currentTimeMillis()));
        timerTasks.put(t, millis );
    }

    public synchronized void addAuction(String title, int price, String vendor, LocalDateTime closingTime) {
        Lot lot = new Lot(title,price,vendor);
        Auction au = new Auction(auctionIdCounter,lot,closingTime);
        auctionList.put(auctionIdCounter,au);
        // Timer for ending the auction
        createTimer(closingTime);

        auctionIdCounter++;
    }

    public synchronized void addAuctionDB(String title, int price, String vendor, LocalDateTime closingTime) {
        db.addAuction(title,price,vendor,closingTime);
        int auctionId = db.idOfAuction();
        ZonedDateTime zdt = closingTime.atZone(ZoneId.of("Europe/Rome"));
        long millis = zdt.toInstant().toEpochMilli();
        LifeCycleAuctionTaskDB t = new LifeCycleAuctionTaskDB(auctionId,millis);
        t.passArgument(timerTasksDB,db);
        timer.schedule(t, (millis - System.currentTimeMillis()));
        timerTasksDB.put(t, millis );
    }



    /**
     * Il metodo controlla se e' gia' loggato un utente nel servizio, in tal caso consiglia il logout
     * Il metodo effettua il controllo sulla presenza effettiva nella lista utente, altrimenti non permette il login
     * Se le due condizioni sopra non si avverano allora permette il login
     *
     */
    public boolean checkLogin(String username,String pass) {
        User userToCheck = userListed(username);
        if(userToCheck != null) {
            if(userToCheck.isLoggedIn())
                return false;
            if (userToCheck.checkPassword(pass)) {
                userToCheck.setLoggedIn(true);
                return true;
            }
            return false;
        }

        return false;
    }
    public boolean checkLoginDB(String username,String pass) { return db.login(username,pass); }


    private User userListed(String username) {
        if(usersList.containsKey(username))
            return usersList.get(username);
        else
            return null;
    }


    public boolean checkExistingAuction (int idAuction) {
        return (auctionList.containsKey(idAuction));
    }
    public boolean checkExistingAuctionDB(int idAuction) { return db.checkExistingAuction(idAuction); }


    public int higherOffer(int id) {
        if (auctionList.containsKey(id))
            return auctionList.get(id).getHigherOffer();
        else
            return -1;
    }
    public int higherOfferDB(int id) { return db.higherOffer(id); }


    public boolean vendorOfAuction(int idAuction,String logged) {
        return (auctionListed(idAuction).getLot().getVendor().equalsIgnoreCase(logged));
    }
    public boolean vendorOfAuctionDB(int idAuction,String logged) { return db.vendorOfAuction(idAuction,logged); }


    private Auction auctionListed(int idAuction) {
        if(auctionList.containsKey(idAuction))
            return auctionList.get(idAuction);
        else
            return null;
    }

    public synchronized void makeBid(String user, int amount,int id){
        Bid bid = new Bid(id,user,amount);
        Auction request = auctionListed(id);
        request.addBid(bid);
    }
    public synchronized boolean makeBidDB(String user, int amount,int id) { return db.makeBid(user,amount,id); }

    public LocalDateTime currentiTime() {
        return LocalDateTime.now();
    }

    public void saveAuctionImage(File image) throws RemoteException {

        String pathSave = "src\\main\\java\\Server\\services\\AuctionImages\\" + auctionIdCounter + ".png";

        try {
            BufferedImage bi = ImageIO.read(image);
            File outputfile = new File(pathSave);
            ImageIO.write(bi, "png", outputfile);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Auction> favoriteAuction(String user) {
        return db.favoriteAuction(user);
    }

    public boolean userLikeAuction(String username,int id) {
        return db.userLikeAuction(username,id);
    }

    public ArrayList<Auction> takeAuctionList() {
        return db.AuctionList();
    }

    public Auction getAuction(int id) { return db.getAuction(id);}

    public User getUser(String username) {
        return db.getUser(username);
    }

    public void saveUserStateDB(User user,Auction au,int choose) {
        db.saveUserStateFavorites(user,au,choose);
    }

    public void saveAuctionStateDB(Auction auction) {
        db.saveAuctionState(auction);
    }

    public void probe()  {}

    public void saveState() {
        System.out.println(files.saveState());
    }

    public void loadState() {
        System.out.println(files.loadState());
    }

    public ConcurrentHashMap<String, User> getUsersList() { return usersList; }

    public void setUsersList(ConcurrentHashMap<String, User> usersList) { this.usersList = usersList; }

    public ConcurrentHashMap<Integer, Auction> getAuctionList() { return auctionList; }

    public void setAuctionList(ConcurrentHashMap<Integer, Auction> auctionList) { this.auctionList = auctionList; }

    public HashMap<Integer, Auction> getClosedAuction() { return closedAuction; }

    public void setClosedAuction(HashMap<Integer, Auction> closedAuction) { this.closedAuction = closedAuction; }

    public HashMap<LifeCycleAuctionTask, Long> getTimerTasks() { return timerTasks; }

    public void setTimerTasks(HashMap<LifeCycleAuctionTask, Long> timerTasks) { this.timerTasks = timerTasks; }

    public HashMap<LifeCycleAuctionTaskDB, Long> getTimerTasksDB() { return timerTasksDB; }

    public void setTimerTasksDB(HashMap<LifeCycleAuctionTaskDB, Long> timerTasksDB) { this.timerTasksDB = timerTasksDB; }

    public int getAuctionIdCounter() { return auctionIdCounter; }

    public void setAuctionIdCounter(int auctionIdCounter) { this.auctionIdCounter = auctionIdCounter; }

    public Timer getTimer() { return timer; }

    public void setTimer(Timer timer) { this.timer = timer; }

    public FileManager getFiles() { return files; }

    public void setFiles(FileManager files) { this.files = files; }

    public DBManager getDb() { return db; }

    public void setDb(DBManager db) { this.db = db; }

    public SystemManager() throws RemoteException {
        usersList = new ConcurrentHashMap<>();
        auctionList = new ConcurrentHashMap<>();
        closedAuction = new HashMap<>();
        timer = new Timer();
        timerTasks = new HashMap<>();
        files = new FileManager(this);
        db = new DBManager (this);
        timerTasksDB = new HashMap<>();
    }
}