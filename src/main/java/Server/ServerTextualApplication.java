package Server;

import Server.Domain.FacadeServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class ServerTextualApplication {
    public static void main(String[] args) throws RemoteException {
        FacadeServer sys = new FacadeServer();

        Registry reg = LocateRegistry.createRegistry(999);
        //System.setProperty("java.rmi.server.hostname","LOCAL_IP");
        reg.rebind("hii", sys);
        System.out.println("Server Ready");

        Scanner scn = new Scanner(System.in);

        while(true) {
            sys.setAuctionIdCounter(sys.getDb().idOfAuction());
            System.out.println("Immagini ricaricate");

            sys.refreshTimerStats();
            System.out.println("Timer ricaricati");

            System.out.println("1)Carica da File   2)Salva su file   3)Spegni Server");
            int decision = scn.nextInt();
            switch (decision) {
                case 1:
                    sys.loadState();
                    break;
                case 2:
                    sys.saveState();
                    break;
                case 3:
                    try {
                        sys.saveTimerStats();
                        reg.unbind("hii");
                        UnicastRemoteObject.unexportObject(sys,true);

                        System.out.println("Sto spegnendo il server...");
                        System.exit(0);
                    } catch (NotBoundException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }

    }
}
