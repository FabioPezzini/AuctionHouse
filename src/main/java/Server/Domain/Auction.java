package Server.Domain;

import Server.People.User;

import javax.persistence.*;
import javax.transaction.Transactional;
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

    @Transient
    private List<String> partecipantsList;

    @Transient
    private File image;

    @OneToOne(mappedBy = "auL",cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Lot lot;

    @OneToMany(mappedBy = "au",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Bid> bidsList;

    @Column(name = "closingdate")
    private LocalDateTime closingDate;

    @Column(name = "closed")
    private boolean closed;

    @ManyToMany(mappedBy = "favoriteList")
    private List<User> userLike = new ArrayList<>();



    /**
     * Aggiunge offerta all'asta
     * @param bid
     */
    public void addBid(Bid bid) {
        bidsList.add(bid);
        higherOffer = bid.getAmount();
    }

    public void addBidDB(Bid bid) {
        if(bid != null) {
            if(bidsList == null) {
                bidsList = new ArrayList<Bid>();
            }
            bidsList.add(bid);
            bid.setAu(this);
        }
    }

    /**
     * Stampa informazioni su asta aperta
     * @return
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
     * Stampa informazioni su asta chiusa
     * @return
     */
    public String closedAuctionInformation() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        closingDate.format(formatter);
        return "Id:"+ id + "\t" + "Current value:" + higherOffer + "\t" + lot.closedInformation() + "\n";
    }

    /**
     * Stampa informazioni su asta chiusa
     * @return
     */
    public String closedAuctionInfoDB() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        closingDate.format(formatter);
        return "Id:"+ id + "\t" + "Current value:" + higherOffer + "\t" + lot.closedInformationDB() + "\n";
    }

    /**
     * Ottiene l'attuale Offerta piu' alta
     * @return
     */
    public Bid getLastBid() {
        if(bidsList.size() != 0) {
            return bidsList.get((bidsList.size()-1));
        }
        else
            return null;
    }

    public void addFavourite(User a) {
        userLike.add(a);
    }

    public void removeFavourite(User a) {
        userLike.remove(a);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Auction)
            return ((Auction)obj).getId()==this.id;
        else
            return false;
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
    public List<String> getPartecipantsList() {
        return partecipantsList;
    }

    public void setPartecipantsList(List<String> partecipantsList) {
        this.partecipantsList = partecipantsList;
    }

    public File getImage() {
        return image;
    }

    public void setImage(File image) {
        this.image = image;
    }

    public List<User> getUserLike() { return userLike; }

    public void setUserLike(List<User> userLike) { this.userLike = userLike; }

    public Auction() {}

    public Auction(Lot lot, LocalDateTime closingDate) {
        this.lot = lot;
        this.closingDate = closingDate;
        this.higherOffer=lot.getBasePrice();
    }

    public Auction(int id, Lot lot, LocalDateTime closingDate) {
        this.id = id;
        this.partecipantsList=new ArrayList<>();
        this.closingDate = closingDate;
        this.lot=lot;
        this.higherOffer=lot.getBasePrice();
    }
}