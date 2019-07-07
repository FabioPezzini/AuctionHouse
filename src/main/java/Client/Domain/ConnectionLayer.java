package Client.Domain;

import Server.Domain.Proxy;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ConnectionLayer {
    private FailureDetector failureDetector;
    private String connectionStr;

    private boolean connected = false;

    private boolean userWantDisconnect = false;

    private Proxy server;

    /**
     * Basic constructor that only requires a connection string
     *
     */
    public ConnectionLayer(String connectionServer) {
        this.connectionStr = connectionServer;
        connect();
        failureDetector = new FailureDetector(this);
    }

    /**
     * The period parameter is passed to the FailureDetector, determines how often to retry in case connection breaks
     *
     */
    public ConnectionLayer(String connectionStr, long period) {
        this.connectionStr = connectionStr;
        connect();
        failureDetector = new FailureDetector(this, period);
    }

    /**
     * Metodo che apre la connessione al Server, cambiare indirizzo host per connettersi fuori dalla LAN
     *
     */
    private void connect() {
            try {
                Registry reg = LocateRegistry.getRegistry("localhost",999);
                server = (Proxy) reg.lookup(connectionStr);

                setConnected(true);

            } catch (NotBoundException e) {
                System.err.println("Unable to bind the server - " + e);
            } catch (RemoteException e) {
                System.err.println("Unable to contact the server - " + e);
            }
    }


    public void reconnect() {
        connect();
        if (isConnected()) {
            System.out.println("Reconnected!");
        }
    }

    public synchronized boolean isConnected() {
        return connected;
    }

    public synchronized void setConnected(boolean connected) {
        this.connected = connected;
    }

    public FailureDetector getFailureDetector() {
        return failureDetector;
    }

    public Proxy getServer() throws RemoteException {
        if (isConnected()) {
            return server;
        } else {
            throw new RemoteException("Server is dead.");
        }
    }

    public synchronized boolean UserWantDisconnect() {
        return userWantDisconnect;
    }

    public synchronized void setUserWantDisconnect(boolean userWantDisconnect) {
        this.userWantDisconnect = userWantDisconnect;
    }

    public void setServer(Proxy server) {
        this.server = server;
    }
}

