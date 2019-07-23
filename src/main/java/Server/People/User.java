package Server.People;

import Client.Exceptions.IncorrectPasswordException;
import Server.Domain.Auction;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "USER")
public class User implements Serializable {
    @Id
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name = "username", updatable = false, nullable = false)
    private String username;

    @Column(name="email",nullable=false)
    private String email;

    @Column(name = "pass")
    private String password;

    @Column(name = "loggedstatus")
    private boolean isLoggedIn;

    @ManyToMany
    @JoinTable(name = "favorites", joinColumns = { @JoinColumn(name = "username")},inverseJoinColumns = {@JoinColumn(name = "id")})
    private List<Auction> favoriteList = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "partecipants", joinColumns = { @JoinColumn(name = "username")},inverseJoinColumns = {@JoinColumn(name = "id")})
    private List<Auction> partecipantAuction = new ArrayList<>();


    public boolean equals(Object obj) {
        if (obj instanceof User)
            return username.equals(((User)obj).getUsername());
        else
            return false;
    }

    public void checkPassword(String password){
        if(!this.password.equals(password))
            throw new IncorrectPasswordException();
    }
    public boolean checkPassword2(String password){
        return this.password.equals(password);
    }
    public boolean isLoggedIn() {
        return isLoggedIn;
    }
    
    public boolean isAPartecipant(Auction auction){
     return partecipantAuction.contains(auction);
    }

    public boolean isFavourite(Auction auction){
     return favoriteList.contains(auction);
    }

    public List<Auction> getPartecipantAuction() {
        return partecipantAuction;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public List<Auction> getFavoriteList() { return favoriteList; }

    public void setFavoriteList(List<Auction> favoriteList) { this.favoriteList = favoriteList; }

    public List<Auction> getAuction() {
        return partecipantAuction;
    }


    public void setPartecipantAuction(List<Auction> partecipantAuction) {
        this.partecipantAuction = partecipantAuction;
    }

    public User() {}

    public User(String username,String password, String email) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.isLoggedIn = false;
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.isLoggedIn = false;
    }
}

