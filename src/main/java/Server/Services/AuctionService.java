package Server.Services;

import Server.Domain.Auction;
import Server.Domain.AuctionDBTimerStrategy;
import Server.Domain.Bid;
import Server.People.User;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;

import java.io.File;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class AuctionService {
    private SessionFactory sessionFactory;
    private Session s;

    private AuctionRepositoryImpl auctionRepository;
    private UserRepositoryImpl userRepository;
    private TimerRepositoryImpl timerRepository;

    public void closeAuction(int id) {
        s = sessionFactory.openSession();

        try {
            s.beginTransaction();
            Auction au = s.get(Auction.class,id);
            au.setClosed(true);
            s.saveOrUpdate(au);
            s.getTransaction().commit();
            ArrayList<Integer> numbers=new ArrayList<>();
            numbers.add(au.getId());
            deleteImages(numbers);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            s.close();
        }
    }

    public synchronized int latestId() {
        s = sessionFactory.openSession();

        String sql = "SELECT max(id) FROM auction";
        try {
            s.beginTransaction();
            NativeQuery sqlQuery = s.createSQLQuery(sql);
            if(sqlQuery.getSingleResult() != null) {
                int id = ((Number) sqlQuery.getSingleResult()).intValue();
                s.getTransaction().commit();
                return id;
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            s.close();
        }
        return  1;
    }

    public synchronized boolean makeBid(String user, int amount,int id) {
        s = sessionFactory.openSession();

        try {
            s.beginTransaction();
            User u = s.get(User.class,user);
            Bid b = new Bid();
            b.setActorDB(u);
            b.setAmount(amount);
            Auction a = s.get(Auction.class,id);
            Hibernate.initialize(u.getPartecipantAuction());
            if(amount> a.getHigherOffer()) {
                a.addBidDB(b);
                a.setHigherOffer(amount);
                u.getPartecipantAuction().add(a);
                s.saveOrUpdate(a);
                s.getTransaction().commit();
                return true;
            }
            return false;
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            s.close();
        }
        return false;
    }

    public boolean checkExistingAuction(int id) {
        s = sessionFactory.openSession();

        try {
            String sql = "SELECT COUNT(*) FROM auction where auction.id= :idA AND closed=false";
            NativeQuery sqlQuery = s.createSQLQuery(sql);
            sqlQuery.setParameter("idA",id);
            int numb = ((Number)sqlQuery.getSingleResult()).intValue();
            if (numb == 1)
                return true;
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            s.close();
        }
        return false;
    }

    public void winner(int id) { auctionRepository.winner(id); }

    public void deleteAuctions() { auctionRepository.deleteAuctions(); }

    public String showAllActive() {
        return auctionRepository.showAllActive();
    }

    public String showAllClosed() {
        return auctionRepository.showAllClosed();
    }

    public void addAuction(String title, int price, String vendor, LocalDateTime closingTime) { auctionRepository.addAuction(title,price,vendor,closingTime); }

    public Auction getAuction(int id) {
        return auctionRepository.getAuction(id);
    }

    public void modifyAuction(String title, int price,int id) { auctionRepository.modifyAuction(title,price,id); }

    public int highestOffer (int id) {
        return auctionRepository.highestOffer(id);
    }

    public boolean vendorOfAuction(int idAuction,String logged) { return auctionRepository.vendorOfAuction(idAuction,logged); }

    public void saveTimer( ArrayList<AuctionDBTimerStrategy> timerTasksDB) {
        timerRepository.saveTimer(timerTasksDB);
    }

    public HashMap<Integer, BigInteger> reloadTimer() {
        return timerRepository.reloadTimer();
    }

    public void deleteTimer() {
        timerRepository.deleteTimer();
    }

    public ArrayList<Auction> myAuctionList(String username) {
        return auctionRepository.myAuctionList(username);
    }

    public ArrayList<Auction> favoriteAuction(String user) {
        return auctionRepository.favoriteAuction(user);
    }

    public boolean userLikeAuction(String username, int id) {
        return auctionRepository.userLikeAuction(username,id);
    }

    public ArrayList<Auction> AuctionList() {
        return auctionRepository.AuctionList();
    }

    public ArrayList<Auction> searchAuctionList(String textToSearch) { return auctionRepository.searchAuctionList(textToSearch); }

    public User getUser(String username) {
        return userRepository.getUser(username);
    }

    public void saveUserStateFavorites(User user, Auction au, int choose) { userRepository.saveUserStateFavorites(user,au,choose); }

    public void saveAuctionState(Auction auction) { auctionRepository.saveAuctionState(auction); }

    public String getVendorEmail(String username) { return userRepository.getVendorEmail(username); }

    public boolean checkActor(String username,int id) { return userRepository.checkActor(username,id); }

    public String getActualWinner(int id) { return userRepository.getActualWinner(id); }

    public boolean isClosed(int id) { return auctionRepository.isClosed(id); }

    private void deleteImages(ArrayList<Integer> numbers){
        for(Integer id:numbers){
            File image = new File("src\\main\\resources\\Images\\" + id + ".png");
            image.delete();
        }
    }

    public AuctionService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        auctionRepository = new AuctionRepositoryImpl(this.sessionFactory);
        userRepository = new UserRepositoryImpl(this.sessionFactory);
        timerRepository = new TimerRepositoryImpl(this.sessionFactory);

    }
}
