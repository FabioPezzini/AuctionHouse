import Server.Domain.Auction;
import Server.Domain.Bid;
import Server.Domain.Lot;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class AuctionTest {

    /**
     * METODO ADD_BID TEST CHE VERIFICA SE UN'OFFERTA PIU BASSA VIENE SCARTATA
     */
    @Test
    public void lowerbidTest(){
        Lot lot = new Lot("description", 20);
        Auction test1 = new Auction(1, lot, LocalDateTime.MAX);

        Bid c = new Bid("actor", 15);
        test1.addBid(c);

        assertEquals(lot.getBasePrice(), test1.getHigherOffer());
    }

    /**
     * METODO ADD_BID: TEST CHE VERIFICA SE UN OFFERTA PIU ALTA VIENE PRESA
     */
    @Test
    public void higherbidTest(){
        Lot lot = new Lot("description", 1);
        Auction test1 = new Auction(1, lot, LocalDateTime.MAX);

        Bid c = new Bid("actor", 15);
        test1.addBid(c);

        assertEquals(c.getAmount(), test1.getHigherOffer());
    }

    /**
     * METODO GET_LAST_BID: TEST CHE VERIFICA SE L'ULTIMA OFFERTA
     */
    @Test
    public void lastbidTest(){
        Lot lot = new Lot("description", 10);
        Auction test1 = new Auction(1, lot, LocalDateTime.MAX);

        Bid a = new Bid("actor", 15);
        test1.addBid(a);

        Bid b = new Bid("actor", 20);
        test1.addBid(b);

        Bid c = new Bid("actor", 21);
        test1.addBid(c);

        assertEquals(c, test1.getLastBid());
    }
}