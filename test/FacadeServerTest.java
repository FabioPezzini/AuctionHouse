import Server.Domain.FacadeServer;
import Server.People.User;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;

public class FacadeServerTest {
    @Test
    public void addBid() throws RemoteException{
        LocalDateTime time= LocalDateTime.now();
        time= time.plusMinutes(5);
        FacadeServer server= new FacadeServer();
        int id= server.getAuctionIdCounter();
        server.addAuction("title",10,"vendor",time);
        server.makeBid("bidder",12,id);
        //Offerta valida
        assertThat(server.getAuctionList().get(id).getLastActor(), CoreMatchers.is("bidder"));
        assertThat(server.getAuctionList().get(id).getLastBidAmount(), CoreMatchers.is(12));
        assertThat(server.getAuctionList().get(id).getHigherOffer(), CoreMatchers.is(12));
        server.makeBid("bidder-2",11,id);
        //Offerta troppo bassa
        assertThat(server.getAuctionList().get(id).getLastActor(), CoreMatchers.is("bidder"));
        assertThat(server.getAuctionList().get(id).getLastBidAmount(), CoreMatchers.is(12));
        assertThat(server.getAuctionList().get(id).getHigherOffer(), CoreMatchers.is(12));
        server.makeBid("bidder",14,id);
        //Parametri offerta validi ma dello stesso attore, quindi viola le regole
        assertThat(server.getAuctionList().get(id).getLastActor(), CoreMatchers.is("bidder"));
        assertThat(server.getAuctionList().get(id).getLastBidAmount(), CoreMatchers.is(12));
        assertThat(server.getAuctionList().get(id).getHigherOffer(), CoreMatchers.is(12));


    }


    @Test
    public void createUser() throws RemoteException {
        FacadeServer server = new FacadeServer();
        server.createUser("Aldo","Aldo97");
        ConcurrentHashMap<String, User> list =server.getUsersList();
        assertThat(list.get("Aldo").getUsername(),CoreMatchers.is("Aldo"));
        assertThat(list.get("Aldo").getPassword(),CoreMatchers.is("Aldo97"));
    }

    @Test
    public void checkLogin() throws RemoteException {
        FacadeServer server = new FacadeServer();
        server.createUser("Aldo","Aldo97");
        //Username inesistente
        assertThat(server.checkLogin("Ao","Aldo97"),CoreMatchers.is(false));
        //Password errata
        assertThat(server.checkLogin("Aldo","Al97"),CoreMatchers.is(false));
        //Login eseguito
        assertThat(server.checkLogin("Aldo","Aldo97"),CoreMatchers.is(true));
        //Login già eseguito
        assertThat(server.checkLogin("Aldo","Aldo97"),CoreMatchers.is(false));
        //Logout
        server.logoutS("Aldo");
        //Login riuscito
        assertThat(server.checkLogin("Aldo","Aldo97"),CoreMatchers.is(true));

    }


    @Test
    public void addAuction() throws RemoteException{
        FacadeServer server = new FacadeServer();
        LocalDateTime time=LocalDateTime.now().plusMinutes(20);
        int id= server.getAuctionIdCounter();
        server.addAuction("title",10,"vendor",time);
        assertThat(server.getAuctionList().size(),CoreMatchers.is(1));
        assertThat(server.getAuctionList().get(id).getDescriptionLot(),CoreMatchers.is("title"));
        assertThat(server.getAuctionList().get(id).getVendor(),CoreMatchers.is("vendor"));
        assertThat(server.getAuctionList().get(id).getLot().getBasePrice(),CoreMatchers.is(10));
        assertThat(server.getAuctionList().get(id).getClosingDate(),CoreMatchers.is(time));
    }

    @Test
    public void closeAuction() throws RemoteException{
        FacadeServer server = new FacadeServer();
        LocalDateTime time=LocalDateTime.now().plusMinutes(20);
        int id= server.getAuctionIdCounter();
        server.addAuction("title",10,"vendor",time);
        server.makeBid("bidder",12,id);
        server.closeActiveAuction(id);
        assertThat(server.getAuctionList().get(id).getWinner(),CoreMatchers.is("bidder"));
        int id2= server.getAuctionIdCounter();
        server.addAuction("title-2",10,"vendor-2",time);
        server.closeActiveAuction(id2);
        assertThat(server.getAuctionList().get(id2).getWinner(),CoreMatchers.is("No winner"));
    }








}
