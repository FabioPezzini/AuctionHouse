package Server.Domain;

import Server.People.User;
import Server.Services.AuctionService;
import Server.Services.HibernateUtil;
import Server.Services.LoginService;
import Server.Services.SignUpService;
import org.hibernate.SessionFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FacadeServer extends UnicastRemoteObject implements Proxy {

    private ConcurrentHashMap<String,User> usersList;
    private ConcurrentHashMap<Integer,Auction> auctionList;
    private HashMap<Integer,Auction> closedAuction;
    private HashMap<AuctionTimerStrategy, Long> timerTasks;
    private ArrayList<AuctionDBTimerStrategy> timerTasksDB;
    private int auctionIdCounter = 1; //Valido per FileManager, il valore non e' salvato
    private transient Timer timer;
    private FileManager files;
    private Registry reg = null;
    private DBConnection db;



    public void createUser(String username, String password){
        User user = new User(username,password,"");
        addUser(user);
    }

    public void createUserDB (String username, String password,String email){ db.addUser(username,password,email); }
    public void changeEmail(String email,String username){
        db.changeEmail(email,username);
    }
    public void changePassword(String psw,String username){
        db.changePassword(psw,username);
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

    public boolean alredyTakenEmailDB(String email) { return db.alredyTakenEmail(email); }



    public String showAllActiveAuctions() {
        StringBuilder toPrint2=new StringBuilder();
        for (Map.Entry<Integer,Auction> entry : auctionList.entrySet()) {
            Auction entryValue = entry.getValue();
            toPrint2.append(entryValue.closedAuctionInformation());
        }
        if(auctionList.isEmpty()) {
            return  "Nessun Inserzione Esistente" + "\n";
        }
        return toPrint2.toString();
    }

    public String showAllActiveAuctionsDB() { return db.showAllActive(); }


    public String showClosedAuctions() {
        StringBuilder toPrint2=new StringBuilder();
        for (Map.Entry<Integer,Auction> entry : closedAuction.entrySet()) {
            Auction entryValue = entry.getValue();
            toPrint2.append(entryValue.closedAuctionInformation());
        }
        if(closedAuction.isEmpty()) {
            return  "Nessun Inserzione Chiusa" + "\n";
        }
        return toPrint2.toString();
    }

    public String showClosedAuctionsDB() { return db.showAllClosed(); }


    private void createTimer(LocalDateTime closingTime) {
        ZonedDateTime zdt = closingTime.atZone(ZoneId.of("Europe/Rome"));
        long millis = zdt.toInstant().toEpochMilli();
        AuctionTimerStrategy t = new AuctionTimerStrategy(auctionIdCounter,millis);
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
        int auctionId = db.latestId();
        ZonedDateTime zdt = closingTime.atZone(ZoneId.of("Europe/Rome"));
        long millis = zdt.toInstant().toEpochMilli();
        AuctionDBTimerStrategy t = new AuctionDBTimerStrategy(db.getAuction(auctionId),millis);
        t.passArgument(timerTasksDB,db);
        timer.schedule(t, (millis - System.currentTimeMillis()));
        timerTasksDB.add(t);
    }

    public synchronized void modifyAuctionDB(String title, int price, int id) {
        db.modifyAuction(title,price,id);
    }

    public synchronized void closeAuction(int id) {
        db.closeAuction(id);
    }

    public void closeActiveAuction(int id){
        Auction auction=auctionList.get(id);
        if(!auction.isClosed()){
            auction.setClosed(true);
            if (!auction.getBidsList().isEmpty()){
                auction.setWinner(auction.getLastActor());
            }
            else{
                auction.setWinner("No winner");
            }
        }
    }

    public boolean isClosed(int id) { return db.isClosed(id);}





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
            if (userToCheck.checkPassword2(pass)) {
                userToCheck.setLoggedIn(true);
                return true;
            }
            return false;
        }

        return false;
    }
    public boolean checkLoginDB(String username,String pass) { return db.login(username,pass); }


    User userListed(String username) {
        return usersList.getOrDefault(username,null);
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
    public int higherOfferDB(int id) { return db.highestOffer(id); }


    public boolean vendorOfAuction(int idAuction,String logged) {
        //protected variation
        return (auctionListed(idAuction).getVendor().equalsIgnoreCase(logged));
        //Auction chiede a Lot di restituirgli la stringa del venditore
    }
    public boolean vendorOfAuctionDB(int idAuction,String logged) { return db.vendorOfAuction(idAuction,logged); }


    private Auction auctionListed(int idAuction) {
        return auctionList.getOrDefault(idAuction,null);
    }

    public synchronized void makeBid(String user, int amount,int id){
        Bid bid = new Bid(id,user,amount);
        Auction request = auctionListed(id);
        if (request.getBidsList().isEmpty() || !request.getLastActor().equals(user)) {
                    request.addBid(bid);
        }
    }
    public synchronized boolean makeBidDB(String user, int amount,int id) { return db.makeBid(user,amount,id); }

    public LocalDateTime currentiTime() {
        return LocalDateTime.now();
    }

    public synchronized void saveAuctionImage(File image) {
        auctionIdCounter = db.latestId();
        String pathSave = "src\\main\\resources\\Images\\" + auctionIdCounter + ".png";

        try {
            BufferedImage bi = ImageIO.read(image);
            File outputfile = new File(pathSave);
            ImageIO.write(bi, "png", outputfile);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveTimerStats() {
        db.saveTimer(timerTasksDB);

    }

    public void refreshTimerStats() {
        HashMap<Integer, BigInteger> timerValue;
        timerValue = db.reloadTimer();

        for (Map.Entry<Integer, BigInteger> entry : timerValue.entrySet()) {
            AuctionDBTimerStrategy t = new AuctionDBTimerStrategy(db.getAuction(entry.getKey()), entry.getValue().longValue());
            t.passArgument(timerTasksDB, db);
            if (entry.getValue().longValue() - System.currentTimeMillis() > 0) {
                timer.schedule(t, (entry.getValue().longValue() - System.currentTimeMillis()));
                timerTasksDB.add(t);
            }
            else {
                t.run();
            }
        }

        for(Map.Entry<Integer, BigInteger> entry : timerValue.entrySet()) {
            Integer key = entry.getKey();
            Long value = entry.getValue().longValue();
            System.out.println(key);
            System.out.println(value);
        }

        db.deleteTimer();
    }

    public void closeServer() throws RemoteException {
        try {
            saveTimerStats();
            //protected variations
            db.closeSession();
            //Facade interagisce con InterpreterRDB per chiudere Session Factory
            reg.unbind("progettok19");
            UnicastRemoteObject.unexportObject(this,true);


        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

    public void init() throws RemoteException {
        reg = LocateRegistry.createRegistry(1002);
        reg.rebind("progettok19", this);
        db.logoutAll();
    }

    public void reloadImages() {
        setAuctionIdCounter(db.latestId());
    }

    public ArrayList<Auction> myAuctionList(String username) { return db.myAuctionList(username); }

    public ArrayList<Auction> favoriteAuction(String user) {
        return db.favoriteAuction(user);
    }

    public boolean userLikeAuction(String username,int id) {
        return db.userLikeAuction(username,id);
    }

    public ArrayList<Auction> takeAuctionList() {
        return db.AuctionList();
    }

    public ArrayList<Auction> searchAuctionList(String textToSearch) { return db.searchAuctionList(textToSearch); }

    public Auction getAuction(int id) { return db.getAuction(id);}

    public User getUser(String username) {
        return db.getUser(username);
    }

    public void saveUserStateDB(User user,Auction au,int choose) { db.saveUserStateFavorites(user,au,choose); }

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

    void setUsersList(ConcurrentHashMap<String, User> usersList) { this.usersList = usersList; }

    public ConcurrentHashMap<Integer, Auction> getAuctionList() { return auctionList; }

    void setAuctionList(ConcurrentHashMap<Integer, Auction> auctionList) { this.auctionList = auctionList; }

    HashMap<Integer, Auction> getClosedAuction() { return closedAuction; }

    void setClosedAuction(HashMap<Integer, Auction> closedAuction) { this.closedAuction = closedAuction; }

    HashMap<AuctionTimerStrategy, Long> getTimerTasks() { return timerTasks; }

    void setTimerTasks(HashMap<AuctionTimerStrategy, Long> timerTasks) { this.timerTasks = timerTasks; }

    public ArrayList<AuctionDBTimerStrategy> getTimerTasksDB() {
        return timerTasksDB;
    }

    public void setTimerTasksDB(ArrayList<AuctionDBTimerStrategy> timerTasksDB) {
        this.timerTasksDB = timerTasksDB;
    }

    public int getAuctionIdCounter() { return auctionIdCounter; }

    private void setAuctionIdCounter(int auctionIdCounter) { this.auctionIdCounter = auctionIdCounter; }

    public Timer getTimer() { return timer; }

    public void setTimer(Timer timer) { this.timer = timer; }

    public FileManager getFiles() { return files; }

    public void setFiles(FileManager files) { this.files = files; }

    public String getVendorEmail(String username){ return db.getVendorEmail(username); }

    public boolean checkActor(String username,int id){
        return db.checkActor(username,id);
    }
    public String getActualWinner(int id){
        return db.getActualWinner( id);
    }


    public FacadeServer() throws RemoteException {
        usersList = new ConcurrentHashMap<>();
        auctionList = new ConcurrentHashMap<>();
        closedAuction = new HashMap<>();
        timer = new Timer();
        timerTasks = new HashMap<>();
        files = new FileManager(this);
        timerTasksDB = new ArrayList<>();
        db = new DBConnection();

    }
}