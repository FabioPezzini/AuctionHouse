package Server.Domain;

public interface StrategyTimer {
    long CLOSED_ITEM_CLEANUP_PERIOD = 60 * (60 * 1000);

    long getTimeLeft();

    int getId();

    void run();
}
