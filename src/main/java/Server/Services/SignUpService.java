package Server.Services;

import Server.People.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;

public class SignUpService {
    private SessionFactory sessionFactory;
    private Session s;

    private UserRepositoryImpl userRepository;


    public void addUser (String username, String password,String email) {
        userRepository.addUser(username,password,email);
    }

    public boolean alredyTakenUsername(String userna) {
        s = sessionFactory.openSession();

        try {
            String sql = "SELECT COUNT(*) FROM User where User.username= :usern";
            NativeQuery sqlQuery = s.createSQLQuery(sql);
            sqlQuery.setParameter("usern",userna);
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

    public boolean alredyTakenEmail(String email) {
        s = sessionFactory.openSession();

        try {
            String sql = "SELECT COUNT(*) FROM User where User.email= :email";
            NativeQuery sqlQuery = s.createSQLQuery(sql);
            sqlQuery.setParameter("email",email);
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

    public boolean changeEmail(String email,String username){ s = sessionFactory.openSession();
        try {
            s.beginTransaction();
            String sql = "SELECT COUNT(*) FROM User where User.username= :username";
            NativeQuery sqlQuery = s.createSQLQuery(sql);
            sqlQuery.setParameter("username",username);
            int numb = ((Number)sqlQuery.getSingleResult()).intValue();
            if(numb == 1) {
                User user = s.get(User.class, username);
                user.setEmail(email);
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

    public boolean changePassword(String password,String username){
        s = sessionFactory.openSession();

        try {
            s.beginTransaction();
            String sql = "SELECT COUNT(*) FROM User where User.username= :username";
            NativeQuery sqlQuery = s.createSQLQuery(sql);
            sqlQuery.setParameter("username",username);
            int numb = ((Number)sqlQuery.getSingleResult()).intValue();
            if(numb == 1) {
                User user = s.get(User.class, username);
                user.setPassword(password);
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

    public SignUpService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        userRepository = new UserRepositoryImpl(this.sessionFactory);
    }
}
