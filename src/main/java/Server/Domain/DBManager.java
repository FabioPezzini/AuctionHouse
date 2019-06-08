package Server.Domain;

import Server.People.User;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import Server.services.DBConnection.HibernateUtil;

import java.io.File;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;


public class DBManager {
    private SystemManager sys;
    private SessionFactory sessionFactory;
    private Session s;


    public void addUser(String username, String pass) {
        s = sessionFactory.openSession();
        try {
            s.beginTransaction();
            User u = new User(username, pass);
            s.save(u);
            s.getTransaction().commit();
        } catch (HibernateException e) {
            e.printStackTrace();
        } finally {
            s.close();
        }
    }

   public boolean logout(String userna) {
        s = sessionFactory.openSession();

        try {
            s.beginTransaction();
            String sql = "SELECT COUNT(*) FROM User where User.username= :userna";
            NativeQuery sqlQuery = s.createSQLQuery(sql);
            sqlQuery.setParameter("userna",userna);
            int numb = ((Number)sqlQuery.getSingleResult()).intValue();
            if(numb == 1) {
                User user = s.get(User.class, userna);
                user.setLoggedIn(false);
                s.saveOrUpdate(user);
                s.getTransaction().commit();
                return true;
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            s.close();
        }
        return false;
    }

    public boolean login(String userna, String pass) {
        s = sessionFactory.openSession();

        try {
            s.beginTransaction();
            String sql = "SELECT COUNT(*) FROM User where username= :userna AND pass= :pass AND loggedstatus=false";
            NativeQuery sqlQuery = s.createSQLQuery(sql);
            sqlQuery.setParameter("userna",userna);
            sqlQuery.setParameter("pass", pass);
            int numb = ((Number)sqlQuery.getSingleResult()).intValue();
            if (numb == 1) {
                User u = s.get(User.class,userna);
                u.setLoggedIn(true);
                s.saveOrUpdate(u);
                s.getTransaction().commit();
                return true;
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            s.close();
        }
        return false;
    }

    public void addAuction(String title, int price, String vendor, LocalDateTime closingTime) {
        s = sessionFactory.openSession();

        try {
            s.beginTransaction();
            Lot l = new Lot(title,price);
            User u = s.get(User.class,vendor);
            l.setVendorDB(u);
            Auction au = new Auction(l,closingTime);
            au.setHigherOffer(price);
            s.save(au); //Faccio cosi per fare generare al database la chiave
            s.getTransaction().commit();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            s.close();
        }
    }

    public boolean isClosed(int id) {
        s = sessionFactory.openSession();

        try {
            s.beginTransaction();
            Auction au = s.get(Auction.class,id);
            s.getTransaction().commit();
            return au.isClosed();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
           s.close();
        }
        return false;
    }

    public void closeAuction(int id) {
        s = sessionFactory.openSession();
        try {
            s.beginTransaction();
            Auction au = s.get(Auction.class,id);
            au.setClosed(true);
            s.saveOrUpdate(au);
            s.getTransaction().commit();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            s.close();
        }
    }

    public void winner(int id) {
        s = sessionFactory.openSession();

        try {
            s.beginTransaction();
            Auction au = s.get(Auction.class,id);
            List<Bid> list = au.getBidsList();
            if (list.size() != 0) {
                list.sort(Comparator.comparing(Bid::getAmount)); //ordino in base all'amount
                int lastOne = list.size()-1;
                String winner = list.get(lastOne).getActorDB().getUsername();
                User u = s.get(User.class,winner);
                au.getLot().setWinnerDB(u);
            }
            else {
                au.getLot().setWinner("No Winner!");
            }
            s.saveOrUpdate(au);
            s.getTransaction().commit();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            s.close();
        }
    }

    public synchronized int idOfAuction() {
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


    public boolean vendorOfAuction(int idAuction,String logged) {
       s = sessionFactory.openSession();

       try {
           s.beginTransaction();
           String sql = "FROM Auction where id='" + idAuction + "'";
           Query query = s.createQuery(sql);
           Auction au = (Auction)query.getSingleResult();
           s.getTransaction().commit();
           return (au.getLot().getVendorDB().getUsername().equalsIgnoreCase(logged));
       } catch (Exception e){
           e.printStackTrace();
       } finally {
           s.close();
       }
       return false;
    }


    public  boolean alredyTakenUsername(String userna) {
       s = sessionFactory.openSession();

       try {
           String sql = "SELECT COUNT(*) FROM User where User.username= :usern";
           NativeQuery sqlQuery = s.createSQLQuery(sql);
           sqlQuery.setParameter("usern",userna);
           int numb = ((Number)sqlQuery.getSingleResult()).intValue();
           if (numb == 1)
               return true;
           else
               return false;
       } catch (Exception e){
           e.printStackTrace();
       } finally {
           s.close();
       }
       return false;
    }

    public int higherOffer(int id) {
        s = sessionFactory.openSession();

        try {
            Auction au = s.get(Auction.class,id);
            return au.getHigherOffer();
        } catch (Exception e){
           e.printStackTrace();
        } finally {
           s.close();
        }
        return -1;
    }

    public boolean checkExistingAuction(int id) {
        s = sessionFactory.openSession();

        try {
            String sql = "SELECT COUNT(*) FROM auction where auction.id= :idA AND closed=false";
            NativeQuery sqlQuery = s.createSQLQuery(sql);
            sqlQuery.setParameter("idA",id);
            int numb = ((Number)sqlQuery.getSingleResult()).intValue();
            if (numb == 1) {
                return true;
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            s.close();
        }
        return false;
    }


    public String showAllActive() {
        s = sessionFactory.openSession();
        String toPrint = "";
        String sql = " FROM  Auction where closed= false";

        try {
            Query query = s.createQuery(sql);
            List<Auction> list = (List<Auction>)query.list();

            for(Auction a : list) {
                toPrint = toPrint + a.openAuctionInfoDB();
            }
            if(list.isEmpty()) {
                toPrint = "Nessun Inserzione Esistente" + "\n";
            }
            return toPrint;
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            s.close();
        }
        return "NAN";
    }

    public String showAllClosed() {
        s = sessionFactory.openSession();
        String toPrint = "";
        String sql = " FROM  Auction where closed= true ";

        try {
            Query query = s.createQuery(sql);
            List<Auction> list = (List<Auction>)query.list();

            for(Auction a : list) {
                toPrint = toPrint + a.closedAuctionInfoDB();
            }
            if(list.isEmpty()) {
                toPrint = "Nessun Inserzione Chiusa" + "\n";
            }
            return toPrint;
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            s.close();
        }
        return "NAN";
    }

    public ArrayList<Auction> AuctionList() {
        s = sessionFactory.openSession();
        ArrayList<Auction> Alist = new ArrayList<>();

        String sql = "FROM  Auction where closed= false";
        try {
            Query query = s.createQuery(sql);
            List<Auction> list = (List<Auction>)query.list();

            for (int i = 0; i < list.size() && i <= 9; i++) {
                Auction a = list.get(i);
                File image = new File("src\\main\\java\\Server\\services\\AuctionImages\\" + a.getId() + ".png");
                a.setImage(image);

                Alist.add(a);
            }

            return Alist;
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            s.close();
        }
        return null;
    }

    public ArrayList<Auction> searchAuctionList(String textToSearch) {
        s = sessionFactory.openSession();
        ArrayList<Auction> Alist = new ArrayList<>();

        String sql = "FROM  Auction where closed= false";
        try {
            Query query = s.createQuery(sql);
            List<Auction> list = (List<Auction>)query.list();

            for (int i = 0; i < list.size(); i++) {
                Auction a = list.get(i);
                File image = new File("src\\main\\java\\Server\\services\\AuctionImages\\" + a.getId() + ".png");
                a.setImage(image);
                String title = a.getLot().getDescription().toLowerCase();

                textToSearch = ".*" + textToSearch.toLowerCase() + ".*";
                Pattern PATTERN = Pattern.compile(textToSearch); //Uso la regex     ".*STRINGA.*"      per matchare ogni corrispondenza

                if(PATTERN.matcher(title).matches()) {
                    Alist.add(a);
                }
            }

            return Alist;
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            s.close();
        }
        return null;
    }

    public ArrayList<Auction> favoriteAuction(String user) {
        s = sessionFactory.openSession();
        ArrayList<Auction> Alist = new ArrayList<>();
        User u = s.get(User.class,user);
        String sql = " SELECT a FROM Auction a INNER JOIN Favorites f on a=f.auctionLiked where :u=f.liker ";
        try {
            Hibernate.initialize(u.getFavoriteList());

            Query query = s.createQuery(sql);
            query.setParameter("u", u);
            List<Auction> list = (List<Auction>)query.list();


            for (int i = 0; i < list.size(); i++) {
                Auction a = list.get(i);
                File image = new File("src\\main\\java\\Server\\services\\AuctionImages\\" + a.getId() + ".png");
                a.setImage(image);
                Alist.add(a);
            }


            return Alist;

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            s.close();
        }
        return null;
    }

    public ArrayList<Auction> myAuctionList(String username) {
        s = sessionFactory.openSession();
        ArrayList<Auction> Alist = new ArrayList<>();

        String sql = "FROM  Auction";
        try {
            Query query = s.createQuery(sql);
            List<Auction> list = (List<Auction>)query.list();
            User user = s.get(User.class, username);
            Hibernate.initialize(user.getPartecipantAuction());
            for (int i = 0; i < list.size() && i <= 9; i++) {
                Auction a = list.get(i);
                if(a.getLot().getVendorDB().equals(user) || user.getPartecipantAuction().contains(a)) {
                    File image = new File("src\\main\\java\\Server\\services\\AuctionImages\\" + a.getId() + ".png");
                    a.setImage(image);

                    Alist.add(a);
                }
            }

            return Alist;
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            s.close();
        }
        return null;
    }

    public Auction getAuction(int id) {
        s = sessionFactory.openSession();
        String sql = "FROM Auction where closed=false AND id=:id";
        try {
            Query query = s.createQuery(sql);
            query.setParameter("id",id);
            Auction a = (Auction)query.getSingleResult();
            File image = new File("src\\main\\java\\Server\\services\\AuctionImages\\" + a.getId() + ".png");
            a.setImage(image);
            return a;
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            s.close();
        }
        return null;
    }

    public User getUser(String username) {
        s = sessionFactory.openSession();
        String sql = "FROM User where username=:user";
        try {
            Query query = s.createQuery(sql);
            query.setParameter("user",username);
            User u = (User)query.getSingleResult();

            return u;
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            s.close();
        }
        return null;
    }

    public synchronized void saveUserStateFavorites(User user,Auction au,int choose) {
        s = sessionFactory.openSession();
        try {
            s.beginTransaction();
            User u = s.get(User.class,user.getUsername());
            Auction a = s.get(Auction.class,au.getId());
            Hibernate.initialize(u.getFavoriteList());
            if(choose == 1)
                u.getFavoriteList().add(a);
            else
                u.getFavoriteList().remove(a);
            s.saveOrUpdate(u);

            s.getTransaction().commit();
        }catch (HibernateException e) {
        e.printStackTrace();
        } finally {
        s.close();
        }
    }

    public synchronized void saveAuctionState(Auction auction) {
        s = sessionFactory.openSession();
        try {
            s.beginTransaction();
            s.saveOrUpdate(auction);
            s.getTransaction().commit();
        }catch (HibernateException e) {
            e.printStackTrace();
        } finally {
            s.close();
        }
    }

    public synchronized boolean userLikeAuction(String username, int id) {
        s = sessionFactory.openSession();
        try {
            s.beginTransaction();
            User u = s.get(User.class,username);
            Auction a = s.get(Auction.class,id);
            if(u.getFavoriteList().contains(a))
                return true;
            return false;
        }catch (HibernateException e) {
            e.printStackTrace();
        } finally {
            s.getTransaction().commit();
            s.close();
        }
        return false;
    }

    public void saveTimer( ArrayList<LifeCycleAuctionTaskDB> timerTasksDB) {
        s = sessionFactory.openSession();
        try {
            s.beginTransaction();
                for(LifeCycleAuctionTaskDB timer : timerTasksDB) {
                s.saveOrUpdate(timer);
            }


        }catch (HibernateException e) {
            e.printStackTrace();
        } finally {
            s.getTransaction().commit();
            s.close();
        }
    }

    public HashMap<Integer, BigInteger> reloadTimer() {
        s = sessionFactory.openSession();

        String sql = "SELECT auction FROM timer ";
        String sql2 = "SELECT millis FROM timer ";
        try {
            s.beginTransaction();
            NativeQuery query = s.createSQLQuery(sql);
            List<Integer> a = query.getResultList();
            NativeQuery query2 = s.createSQLQuery(sql2);
            List<BigInteger> b = query2.getResultList();
            HashMap<Integer,BigInteger> timer = new HashMap<>();
            for(int i =0; i < a.size(); i++) {
                timer.put(a.get(i),b.get(i));
            }
            s.getTransaction().commit();
            return timer;
        }catch (HibernateException e) {
            e.printStackTrace();
        } finally {

            s.close();
        }
        return null;
    }
    
    public void deleteTimer() {
        s = sessionFactory.openSession();
        String sql = "DELETE FROM LifeCycleAuctionTaskDB";

        try {
            s.beginTransaction();
            Query query = s.createQuery(sql);
            query.executeUpdate();
        }catch (HibernateException e) {
            e.printStackTrace();
        } finally {
            s.getTransaction().commit();
            s.close();
        }
    }








    public DBManager(SystemManager sys) {
        this.sys = sys;
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }
}
