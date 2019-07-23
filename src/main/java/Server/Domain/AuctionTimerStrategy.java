package Server.Domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class AuctionTimerStrategy extends TimerTask implements Serializable,StrategyTimer {
    private int id;
    private Long closeMillis;

    private ConcurrentHashMap<Integer,Auction> openAuction;
    private HashMap<Integer,Auction> closedAuction;
    private HashMap<AuctionTimerStrategy, Long> timerTasks;

    void passArgument(ConcurrentHashMap<Integer,Auction> openAuction, HashMap<Integer,Auction> closedAuction,HashMap<AuctionTimerStrategy, Long> timerTasks){
        this.openAuction = openAuction;
        this.closedAuction = closedAuction;
        this.timerTasks = timerTasks;
    }

    /**
     * Used to return the millis to the end of the auction
    */
    public long getTimeLeft() {
        return closeMillis - System.currentTimeMillis();
    }

    public int getId() {
        return id;
    }

    @Override
    public void run() {
        // If the auction is open - close and create a new task for removing it from closed.
        // If the auction is already closed - remove it. QUESTA FUNZIONALITA' E' COMMENTATA, SE SI VUOLE USARLA E' DA SCOMMENTARE
        Auction expiredAuction;
        synchronized(this) {
            if ((expiredAuction = openAuction.remove(id)) != null) {
                // Move auction to closed
                closedAuction.put(id, expiredAuction);
                // Schedule cleanup task
                //LifecycleAuctionTask t = new LifecycleAuctionTask(id);
                //timer.schedule(t, CLOSED_ITEM_CLEANUP_PERIOD);
                //timerTasks.put(t, CLOSED_ITEM_CLEANUP_PERIOD);
                if(expiredAuction.getLastBid() != null) {
                    //protected variations (copiare e modificare il branch)
                    String winner = expiredAuction.getLastActor(); //Auction chiede a Bid di restituire la stringa dell'attore dell'ultima offerta
                    expiredAuction.setWinner(winner);// Auction dice a Lot di settare il vincitore
                }
                else
                    expiredAuction.setWinner("No winner!");
            }
            //else {
                // Remove the closed auction permanently after cleanup period
                //closedAuction.remove(id);
            //}
            // Remove this task from map
            timerTasks.remove(this);
        }
    }

    AuctionTimerStrategy(int auctionId, long millis) {
        this.id = auctionId;
        this.closeMillis = millis;
    }
}