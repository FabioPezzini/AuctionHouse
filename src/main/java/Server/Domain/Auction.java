package Server.Domain;

import Server.People.User;

import javax.persistence.*;
import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "auction")
public class Auction implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @Column(name = "higheroffer")
    private int higherOffer;

    @OneToOne(mappedBy = "auL",cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Lot lot;

    @OneToMany(mappedBy = "au",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Bid> bidsList;

    @ManyToMany(mappedBy = "partecipantAuction")
    private List<User> partecipantsList = new ArrayList<>();

    @Column(name = "closingdate")
    private LocalDateTime closingDate;

    @Column(name = "closed")
    private boolean closed;

    @ManyToMany(mappedBy = "favoriteList")
    private List<User> userLike = new ArrayList<>();

    @OneToOne(mappedBy = "auction",cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private AuctionDBTimerStrategy timer;

    @Transient
    private File image;

    /**
     * Add bid to the auction
     */
    public void addBid(Bid bid) {
        if(bid.getAmount()>higherOffer) {
            bidsList.add(bid);
            higherOffer = bid.getAmount();
        }
    }

    public void addBidDB(Bid bid) {
        if(bid != null) {
            bidsList.add(bid);
            bid.setAu(this);
        }
    }

    /**
     * Print info of if the auction is open
     */
    public String auctionInformation() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String closeDate = closingDate.format(formatter);
        return "Id:"+ id + "\t" + "Current value:" + higherOffer + "\t" + lot.information() + "\t"  + "Data Fine:" + closeDate + "\n";
    }

    public String openAuctionInfoDB() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String closeDate = closingDate.format(formatter);
        return "Id:"+ id + "\t" + "Current value:" + higherOffer + "\t" + lot.informationDB() + "\t"  + "Data Fine:" + closeDate + "\n";
    }

    /**
     * Print info of if the auction is closed
     */
    String closedAuctionInformation() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        closingDate.format(formatter);
        return "Id:"+ id + "\t" + "Current value:" + higherOffer + "\t" + lot.closedInformation() + "\n";
    }

    public String closedAuctionInfoDB() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        closingDate.format(formatter);
        return "Id:"+ id + "\t" + "Current value:" + higherOffer + "\t" + lot.closedInformationDB() + "\n";
    }

    /**
     * Return the latest bid
     */
    public Bid getLastBid() {
        if(bidsList.size() != 0) {
            return bidsList.get((bidsList.size()-1));
        }
        else
            return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Auction)
            return ((Auction)obj).getId()==this.id;
        else
            return false;
    }

    public void addFavourite(User a) {
        userLike.add(a);
    }

    public void removeFavourite(User a) {
        userLike.remove(a);
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getHigherOffer() { return higherOffer; }

    public void setHigherOffer(int higherOffer) { this.higherOffer = higherOffer; }

    public Lot getLot() { return lot; }

    public void setLot(Lot lot) { this.lot = lot; }

    public List<Bid> getBidsList() { return bidsList; }

    public void setBidsList(List<Bid> bidsList) { this.bidsList = bidsList; }

    public LocalDateTime getClosingDate() { return closingDate; }

    public void setClosingDate(LocalDateTime closingDate) { this.closingDate = closingDate; }

    public boolean isClosed() { return closed; }

    public void setClosed(boolean closed) { this.closed = closed; }

    public List<User> getPartecipantsList() { return partecipantsList; }

    public void setPartecipantsList(List<User> partecipantsList) { this.partecipantsList = partecipantsList; }

    public AuctionDBTimerStrategy getTimer() { return timer; }

    public void setTimer(AuctionDBTimerStrategy timer) { this.timer = timer; }

    public File getImage() {
        return image;
    }

    public void setImage(File image) {
        this.image = image;
    }

    public List<User> getUserLike() { return userLike; }

    public void setUserLike(List<User> userLike) { this.userLike = userLike; }

    public Auction() {}

    public String getVendor(){
        return this.lot.getVendor();
    }
    public User getVendorDB(){
        return this.lot.getVendorDB();
    }
    

    public String getLastActor(){
        if (!bidsList.isEmpty())
            return this.getLastBid().getActor();
        else
            return "";
    }

    public int getLastBidAmount(){
        return this.getLastBid().getAmount();
    }

    public String getDescriptionLot(){
        return this.lot.getDescription();
    }

    public void setDescriptionLot(String title){
         this.lot.setDescription(title);
    }
    public void setBasePriceLot(int price){
         this.lot.setBasePrice(price);
    }
    public String getUsernameVendorDB(){
        //protected variations
        return this.lot.getUsernamenVendorDB();
        //Lot chiede a User di restituire la stringa username
    }

    public String getEmailVendor(){
        return this.lot.getEmailVendor();
    }


    public void setWinner(String winner){
        this.lot.setWinner(winner);
    }
    public void setWinnerDB(User winner){
        this.lot.setWinnerDB(winner);
    }

    public String getWinner(){
        return this.lot.getWinner();
    }
    public Auction(Lot lot, LocalDateTime closingDate) {
        this.lot = lot;
        this.closingDate = closingDate;
        this.higherOffer=lot.getBasePrice();
        this.bidsList=new ArrayList<>();
    }
    public int getLastBidID(){
       return this.getLastBid().getId();
    }

    public Auction(int id, Lot lot, LocalDateTime closingDate) {
        this.id = id;
        this.partecipantsList=new ArrayList<>();
        this.closingDate = closingDate;
        this.lot=lot;
        this.higherOffer=lot.getBasePrice();
        this.bidsList=new ArrayList<>();
    }
}
