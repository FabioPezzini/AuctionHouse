package Server;

import Server.Domain.SystemManager;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class ServerTextualApplication {
    public static void main(String[] args) throws RemoteException {
        SystemManager sys = new SystemManager();

        Registry reg = LocateRegistry.createRegistry(1099);
        //System.setProperty("java.rmi.server.hostname","LOCAL_IP");
        reg.rebind("hii", sys);
        System.out.println("Server Ready");

        Scanner scn = new Scanner(System.in);

        while(true) {
            System.out.println("1)Carica da File   2)Salva su file   3)Ricarica Immagini   4)Spegni Server");
            int decision = scn.nextInt();
            switch (decision) {
                case 1:
                    sys.loadState();
                    break;
                case 2:
                    sys.saveState();
                    break;
                case 3:
                    sys.setAuctionIdCounter(sys.getDb().idOfAuction());
                    System.out.println("Immagini ricaricate");
                    break;
                case 4:
                    try {
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
