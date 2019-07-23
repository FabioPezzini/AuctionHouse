package Server.Domain;

import Server.People.User;
import Server.Services.AuctionService;
import Server.Services.HibernateUtil;
import Server.Services.LoginService;
import Server.Services.SignUpService;
import org.hibernate.SessionFactory;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

class DBConnection {
    private SessionFactory sessionFactory;

    private AuctionService auctionService;
    private SignUpService signUpService;
    private LoginService loginService;


    /**
     * Create the user and store it in the DB
     */
    void addUser (String username, String password,String email) {
        signUpService.addUser(username,password,email);
    }

    /**
     * Check if the user is registered and logout
     */
    boolean logout(String username) {
        return loginService.logout(username);
    }

    /**
     * Check if the email is alredy registered
     */
    boolean alredyTakenEmail(String email) {
        return signUpService.alredyTakenEmail(email);
    }

    /**
     * Check if the username is alredy registered
     */
    boolean alredyTakenUsername(String userna) {
        return signUpService.alredyTakenUsername(userna);
    }

    /**
     * Return all the open auctions
     */
    String showAllActive() {
        return auctionService.showAllActive();
    }

    /**
     * Return all the closed auctions
     */
    String showAllClosed() {
        return auctionService.showAllClosed();
    }

    /**
     * Create a new auction
     */
    void addAuction(String title, int price, String vendor, LocalDateTime closingTime) {
        auctionService.addAuction(title,price,vendor,closingTime);
    }

    /**
     * Used to get the latest used id for the auction
     */
    int latestId() {
        return auctionService.latestId();
    }

    /**
     * Return an Auction given the id
     */
    Auction getAuction(int id) {
        return auctionService.getAuction(id);
    }

    /**
     * Modify an existing (and not closed) auction
     */
    void modifyAuction(String title, int price,int id) {
        auctionService.modifyAuction(title,price,id);
    }

    /**
     * Return the highest for a specific auction
     */
    int highestOffer (int id) {
        return auctionService.highestOffer(id);
    }

    /**
     * Used to check if the user that is trying to do something is the vendor
     */
    boolean vendorOfAuction(int idAuction,String logged) {
        return auctionService.vendorOfAuction(idAuction,logged);
    }

    /**
     * Save the Timers
     */
    void saveTimer( ArrayList<AuctionDBTimerStrategy> timerTasksDB) {
        auctionService.saveTimer(timerTasksDB);
    }

    /**
     * Reload the Timers
     */
    HashMap<Integer, BigInteger> reloadTimer() {
        return auctionService.reloadTimer();
    }

    /**
     * Delete all the Timers
     */
    void deleteTimer() {
        auctionService.deleteTimer();
    }

    /**
     * Return a list of auction where the user has participated or has sold
     */
    ArrayList<Auction> myAuctionList(String username) {
        return auctionService.myAuctionList(username);
    }

    /**
     * Return a list of favorite auctions for a specific user
     */
    ArrayList<Auction> favoriteAuction(String user) {
        return auctionService.favoriteAuction(user);
    }

    /**
     * Return if the User likes a specific Auction
     */
    boolean userLikeAuction(String username, int id) {
        return auctionService.userLikeAuction(username,id);
    }

    /**
     * Return a list of open auction (used by the GUI)
     */
    ArrayList<Auction> AuctionList() {
        return auctionService.AuctionList();
    }

    /**
     * Return a list of auction that have a specific text in the title
     */
    ArrayList<Auction> searchAuctionList(String textToSearch) {
        return auctionService.searchAuctionList(textToSearch);
    }

    /**
     * Return an User given the username
     */
    User getUser(String username) {
        return auctionService.getUser(username);
    }

    /**
     * Update the User's stats
     */
    void saveUserStateFavorites(User user, Auction au, int choose) {
        auctionService.saveUserStateFavorites(user,au,choose);
    }

    /**
     * Update Auction's state
     */
    void saveAuctionState(Auction auction) {
        auctionService.saveAuctionState(auction);
    }

    String getVendorEmail(String username) {
        return auctionService.getVendorEmail(username);
    }

    boolean changeEmail(String email,String username) {
        return signUpService.changeEmail(email,username);
    }

    boolean changePassword(String password,String username) {
        return signUpService.changePassword(password,username);
    }


    boolean checkActor(String username,int id) {
        return auctionService.checkActor(username,id);
    }

    String getActualWinner(int id) {
        return auctionService.getActualWinner(id);
    }

    /**
     * Check if the auction is closed
     */
    boolean isClosed(int id) {
        return auctionService.isClosed(id);
    }

    /**
     * Close the auction
     */
    void closeAuction(int id) {
        auctionService.closeAuction(id);
    }

    /**
     * Set the winner of the auction (the biggest offer), in addition the method order the bids
     */
    void winner(int id) {
        auctionService.winner(id);
    }

    /**
     * Add a bid to the auction's bids list
     */
    boolean makeBid(String user, int amount,int id) {
        return auctionService.makeBid(user,amount,id);
    }

    /**
     * Check if an auction exists
     */
    boolean checkExistingAuction(int id) {
        return auctionService.checkExistingAuction(id);
    }

    private void deleteAuctions() {
        auctionService.deleteAuctions();
    }

    /**
     * Check if the user is registered and login
     */
    boolean login(String username, String pass) {
        return loginService.login(username,pass);
    }

    void logoutAll() {
        loginService.logoutAll();
    }

    void closeSession(){
        this.sessionFactory.close();
    }

    DBConnection() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
        auctionService = new AuctionService(this.sessionFactory);
        signUpService = new SignUpService(this.sessionFactory);
        loginService = new LoginService(this.sessionFactory);
        deleteAuctions();
    }
}
