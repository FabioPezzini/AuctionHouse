package Server.Domain;

import Server.People.User;

import javax.persistence.*;

@Entity
@Table(name = "favorites")
public class Favorites {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "idfav", updatable = false, nullable = false)
    private int id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", referencedColumnName = "username")
    private User liker;

    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "id")
    private Auction auctionLiked;

    public User getLiker() {
        return liker;
    }

    public void setLiker(User liker) {
        this.liker = liker;
    }

    public Auction getAuctionLiked() {
        return auctionLiked;
    }

    public void setAuctionLiked(Auction auctionLiked) {
        this.auctionLiked = auctionLiked;
    }

    public Favorites() {
    }
}
