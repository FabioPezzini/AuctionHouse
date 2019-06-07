package Server.Domain;

import Server.People.User;

import javax.persistence.*;

@Entity
@Table(name = "PARTECIPANTS")
public class Partecipants {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "idPartecip", updatable = false, nullable = false)
    private int id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", referencedColumnName = "username")
    private User partecipant;

    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "id")
    private Auction auctionReferred;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getPartecipant() {
        return partecipant;
    }

    public void setPartecipant(User partecipant) {
        this.partecipant = partecipant;
    }

    public Auction getAuctionReferred() {
        return auctionReferred;
    }

    public void setAuctionReferred(Auction auctionReferred) {
        this.auctionReferred = auctionReferred;
    }

    public Partecipants() {
    }
}
