package Server.Domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class LifeCycleAuctionTask extends TimerTask implements Serializable {
    private int id;
    private long closeMillis;
    private final long CLOSED_ITEM_CLEANUP_PERIOD = 60 * (60 * 1000); // DA USARE SOLO SE SI VUOLE PULIRE LA LISTA DI INSERZIONI CONCLUSE, espresso in millisecondi, attuale: 60 minuti

    private ConcurrentHashMap<Integer,Auction> openAuction;
    private HashMap<Integer,Auction> closedAuction;
    private HashMap<LifeCycleAuctionTask, Long> timerTasks;

    public void passArgument(ConcurrentHashMap<Integer,Auction> openAuction, HashMap<Integer,Auction> closedAuction,HashMap<LifeCycleAuctionTask, Long> timerTasks){
        this.openAuction = openAuction;
        this.closedAuction = closedAuction;
        this.timerTasks = timerTasks;
    }

    /**
     * Il metodo ritorna il numero di millisecondi che mancano alla fine dell'asta, e' usato nello schedulare i timer
     *
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
                    String winner = expiredAuction.getLastBid().getActor();
                    expiredAuction.getLot().setWinner(winner);
                }
                else
                    expiredAuction.getLot().setWinner("No winner!");
            }
            //else {
                // Remove the closed auction permanently after cleanup period
                //closedAuction.remove(id);
            //}
            // Remove this task from map
            timerTasks.remove(this);
        }
    }

    public LifeCycleAuctionTask(int auctionId, long millis) {
        this.id = auctionId;
        this.closeMillis = millis;
    }
}