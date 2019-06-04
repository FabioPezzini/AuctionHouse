package Server.Domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.TimerTask;

public class LifeCycleAuctionTaskDB extends TimerTask implements Serializable {
    private int id;
    private long closeMillis;
    private final long CLOSED_ITEM_CLEANUP_PERIOD = 60 * (60 * 1000); // DA USARE SOLO SE SI VUOLE PULIRE LA LISTA DI INSERZIONI CONCLUSE, espresso in millisecondi, attuale: 60 minuti

    private HashMap<LifeCycleAuctionTaskDB, Long> timerTasks;
    private DBManager dbManager;

    public void passArgument(HashMap<LifeCycleAuctionTaskDB, Long> timerTasks,DBManager db){
        this.timerTasks = timerTasks;
        this.dbManager = db;
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
        synchronized(this) {
            if (!dbManager.isClosed(id)) {
                // Move auction to closed
                dbManager.winner(id);
                dbManager.closeAuction(id);
            }
            timerTasks.remove(this);
        }
    }

    public LifeCycleAuctionTaskDB(int auctionId, long millis) {
        this.id = auctionId;
        this.closeMillis = millis;
    }
}

