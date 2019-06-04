package Server.Domain;

import Server.People.User;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "BID")
public class Bid implements Serializable {
    @Id
    //@GeneratedValue(strategy=GenerationType.IDENTITY) C'e' un bug con Hibernate non ancora risolto, non posso usare reflection su generated valuehttps://hibernate.atlassian.net/browse/HHH-10956
    @Column(name = "id", updatable = false)
    private int id;

    @Transient
    private int idAu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auctionid")
    private Auction au;


    @Transient
    private String actor;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offerer", referencedColumnName = "username")
    private User actorDB;


    @Column(name = "amount", updatable = false)
    private int amount;

    public String getActor() {
        return actor;
    }

    public int getAmount() {
        return amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Auction getAu() {
        return au;
    }

    public void setAu(Auction au) {
        this.au = au;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getIdAu() {
        return idAu;
    }

    public void setIdAu(int idAu) {
        this.idAu = idAu;
    }

    public User getActorDB() {
        return actorDB;
    }

    public void setActorDB(User actorDB) {
        this.actorDB = actorDB;
    }

    public Bid() {}

    public Bid(int idAu, String actor, int amount) {
        this.idAu = idAu;
        this.actor = actor;
        this.amount = amount;
    }

    public Bid(int idAu, User actorDB, int amount) {
        this.idAu = idAu;
        this.actorDB = actorDB;
        this.amount = amount;
    }

    public Bid(String actor, int amount) {

        this.actor = actor;
        this.amount = amount;
    }

}
