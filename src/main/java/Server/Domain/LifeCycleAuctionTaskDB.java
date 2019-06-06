package Server.Domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimerTask;

@Entity
@Table(name = "TIMER")
public class LifeCycleAuctionTaskDB extends TimerTask implements Serializable {

    @Id
    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "id")
    private Auction auction;

    @Column(name = "auction", updatable = false, nullable = false)
    private int id;

    @Column(name = "millis", updatable = false, nullable = false)
    private long closeMillis;

    @Transient
    private final long CLOSED_ITEM_CLEANUP_PERIOD = 60 * (60 * 1000); // DA USARE SOLO SE SI VUOLE PULIRE LA LISTA DI INSERZIONI CONCLUSE, espresso in millisecondi, attuale: 60 minuti

    @Transient
    private ArrayList<LifeCycleAuctionTaskDB> timerTasks;

    @Transient
    private DBManager dbManager;

    public void passArgument(ArrayList<LifeCycleAuctionTaskDB> timerTasks, DBManager db){
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
                System.out.println(id);
                // Move auction to closed
                dbManager.winner(id);
                dbManager.closeAuction(id);
            }
            timerTasks.remove(this);
        }
    }

    public LifeCycleAuctionTaskDB() {
    }

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    public long getCloseMillis() {
        return closeMillis;
    }

    public void setCloseMillis(long closeMillis) {
        this.closeMillis = closeMillis;
    }

    public LifeCycleAuctionTaskDB(Auction auction, long millis) {
        this.auction = auction;
        this.id = auction.getId();
        this.closeMillis = millis;
    }
}

