package Server.Services;

import Server.Domain.Auction;
import Server.Domain.Bid;
import Server.People.User;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.Comparator;
import java.util.List;

class UserRepositoryImpl {
    private SessionFactory sessionFactory;
    private Session s;


    void addUser(String username, String pass,String email) {
        s = sessionFactory.openSession();

        try {
            s.beginTransaction();
            User u = new User(username, pass,email);
            s.save(u);
            s.getTransaction().commit();
        } catch (HibernateException e) {
            e.printStackTrace();
        } finally {
            s.close();
        }
    }

    User getUser(String username) {
        s = sessionFactory.openSession();
        String sql = "FROM User where username=:user";

        try {
            Query query = s.createQuery(sql);
            query.setParameter("user",username);

            return (User)query.getSingleResult();
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            s.close();
        }
        return null;
    }

    String getVendorEmail(String username) {
        s = sessionFactory.openSession();
        String sql = "FROM User where username=:user";

        try {
            Query query = s.createQuery(sql);
            query.setParameter("user",username);
            return ((User)query.getSingleResult()).getEmail();
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            s.close();
        }
        return null;
    }

    synchronized void saveUserStateFavorites(User user, Auction au, int choose) {
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

    boolean checkActor(String username,int id){
        s = sessionFactory.openSession();
        try {
            s.beginTransaction();
            Auction au = s.get(Auction.class,id);
            List<Bid> list = au.getBidsList();
            if (!list.isEmpty()) {
                list.sort(Comparator.comparing(Bid::getAmount)); //ordino in base all'amount
                int lastOne = list.size() - 1;
                String lastActor = list.get(lastOne).getActorDBUsername();
                if (lastActor.equals(username))
                    return true;
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            s.close();
        }
        return false;
    }

    String getActualWinner(int id) {
        s = sessionFactory.openSession();
        try {
            s.beginTransaction();
            Auction au = s.get(Auction.class,id);
            List<Bid> list = au.getBidsList();
            if (list.size() != 0) {
                list.sort(Comparator.comparing(Bid::getAmount)); //ordino in base all'amount
                int lastOne = list.size()-1;
                return list.get(lastOne).getActorDBUsername();
            }
            else {
                return "No Winner!";
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            s.close();
        }
        return null;
    }


    UserRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
