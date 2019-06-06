package Server.People;

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

    @Column(name = "pass")
    private String password;

    @Column(name = "loggedstatus")
    private boolean isLoggedIn;

    @ManyToMany
    @JoinTable(name = "favorites", joinColumns = { @JoinColumn(name = "username")},inverseJoinColumns = {@JoinColumn(name = "id")})
    private List<Auction> favoriteList = new ArrayList<>();



    public boolean equals(Object obj) {
        if (obj instanceof User)
            return username.equals(((User)obj).getUsername());
        else
            return false;
    }

    public boolean checkPassword(String password){
        return this.password.equals(password);
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
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

    public List<Auction> getFavoriteList() { return favoriteList; }

    public void setFavoriteList(List<Auction> favoriteList) { this.favoriteList = favoriteList; }

    public User() {}

    public User(String username,String password) {
        this.username = username;
        this.password = password;
        this.isLoggedIn = false;
    }


}
