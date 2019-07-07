package Server.Domain;

import Server.People.User;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "LOT")
public class Lot implements Serializable {

    @Column(name = "title", nullable = false)
    private String description;

    @Column(name = "baseprice", updatable = false, nullable = false)
    private int basePrice;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor", referencedColumnName = "username")
    private User vendorDB;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner", referencedColumnName = "username")
    private User winnerDB;

    @Id
    @OneToOne
    @JoinColumn(name = "auctionid", referencedColumnName = "id")
    private Auction auL;

    @Transient
    private String pathImage;

    @Transient
    private String winner;

    @Transient
    private String vendor;


    public int getBasePrice() {
        return basePrice;
    }

    @Override
    public int hashCode() {
        return 100;
    }

    public String getDescription() { return description; }

    public String information() {
        return "Name:" + description + "\t" + "Vendor: " + vendor + "\t" + "Base Price:" + basePrice + "\n";
    }
    public String informationDB() {
        return "Name:" + description + "\t" + "Vendor: " + vendorDB.getUsername() + "\t" + "Base Price:" + basePrice + "\n";
    }


    public String closedInformation() {
        return "Name:" + description + "\t" + "Vendor: " + vendor + "\t" + "Base Price:" + basePrice + "\t\t" + "Winner:" + winner + "\n";
    }

    public String closedInformationDB() {
        return "Name:" + description + "\t" + "Vendor: " + vendorDB.getUsername() + "\t" + "Base Price:" + basePrice + "\t\t" + "Winner:" + valuateWinner() + "\n";
    }

    public String getWinner() {
        return winner;
    }

    private String valuateWinner() {
        if(winnerDB == null)
            return "No Winner!";
        else
            return winnerDB.getUsername();
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public User getVendorDB() {
        return vendorDB;
    }

    public void setVendorDB(User vendorDB) {
        this.vendorDB = vendorDB;
    }

    public User getWinnerDB() {
        return winnerDB;
    }

    public void setWinnerDB(User winnerDB) {
        this.winnerDB = winnerDB;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBasePrice(int basePrice) {
        this.basePrice = basePrice;
    }

    public Auction getAuL() {
        return auL;
    }

    public void setAuL(Auction auL) {
        this.auL = auL;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Auction)
            return (obj)==this.auL;
        else
            return false;
    }

    public Lot() {}

    public Lot(String description, int basePrice) {
        this.description = description;
        this.basePrice = basePrice;
    }

    public Lot(String description, int basePrice, String vendor) {
        this.description = description;
        this.basePrice = basePrice;
        this.vendor = vendor;
    }
}
