package Server.Services;

import Server.Domain.AuctionDBTimerStrategy;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class TimerRepositoryImpl {
    private SessionFactory sessionFactory;
    private Session s;


    void saveTimer( ArrayList<AuctionDBTimerStrategy> timerTasksDB) {
        s = sessionFactory.openSession();

        try {
            s.beginTransaction();
            for(AuctionDBTimerStrategy timer : timerTasksDB) {
                s.saveOrUpdate(timer);
            }

        }catch (HibernateException e) {
            e.printStackTrace();
        } finally {
            s.getTransaction().commit();
            s.close();
        }
    }

    HashMap<Integer, BigInteger> reloadTimer() {
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

    void deleteTimer() {
        s = sessionFactory.openSession();
        String sql = "DELETE FROM AuctionDBTimerStrategy";

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

    TimerRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
