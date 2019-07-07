package Server.Domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.TimerTask;

@Entity
@Table(name = "TIMER")
public class AuctionDBTimerStrategy extends TimerTask implements Serializable,StrategyTimer {
    @Id
    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "id")
    private Auction auction;

    @Column(name = "auction", updatable = false, nullable = false)
    private int id;

    @Column(name = "millis", updatable = false, nullable = false)
    private Long closeMillis;

    @Transient
    private ArrayList<AuctionDBTimerStrategy> timerTasks;

    @Transient
    private InterpreterRDB dbManager;

    public void passArgument(ArrayList<AuctionDBTimerStrategy> timerTasks, InterpreterRDB db){
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

    public AuctionDBTimerStrategy() {
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

    public void setDbManager(InterpreterRDB dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Auction)
            return ((Auction)obj).getId()==this.id;
        else
            return false;
    }


    public AuctionDBTimerStrategy(int id, long closeMillis) {
        this.id = id;
        this.closeMillis = closeMillis;
    }

    @Override
    public int hashCode() {
        return 101;
    }

    public AuctionDBTimerStrategy(Auction auction, long millis) {
        this.auction = auction;
        this.id = auction.getId();
        this.closeMillis = millis;
    }
}

