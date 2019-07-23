package Server.Services;

import Server.People.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;

public class LoginService {
    private SessionFactory sessionFactory;
    private Session s;


    public boolean login(String username, String pass) {
        s = sessionFactory.openSession();

        try {
            s.beginTransaction();
            String sql = "SELECT COUNT(*) FROM User where username= :userna AND pass= :pass AND loggedstatus=false";
            NativeQuery sqlQuery = s.createSQLQuery(sql);
            sqlQuery.setParameter("userna",username);
            sqlQuery.setParameter("pass", pass);
            int numb = ((Number)sqlQuery.getSingleResult()).intValue();
            if (numb == 1) {
                User u = s.get(User.class,username);
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

    public boolean logout(String username) {
        s = sessionFactory.openSession();

        try {
            s.beginTransaction();
            String sql = "SELECT COUNT(*) FROM User where User.username= :username";
            NativeQuery sqlQuery = s.createSQLQuery(sql);
            sqlQuery.setParameter("username",username);
            int numb = ((Number)sqlQuery.getSingleResult()).intValue();
            if(numb == 1) {
                User user = s.get(User.class, username);
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

    public void logoutAll(){
        s = sessionFactory.openSession();

        try {
            s.beginTransaction();
            String sql = "update User set loggedstatus=0";
            NativeQuery sqlQuery = s.createSQLQuery(sql);
            sqlQuery.executeUpdate();
            s.getTransaction().commit();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            s.close();
        }

    }

    public LoginService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
