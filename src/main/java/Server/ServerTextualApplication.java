package Server;

import Server.Domain.FacadeServer;

import java.rmi.RemoteException;
import java.util.Scanner;

public class ServerTextualApplication {
    public static void main(String[] args) throws RemoteException {
        FacadeServer sys = new FacadeServer();

        sys.init();

        Scanner scn = new Scanner(System.in);

        while(true) {
            sys.reloadImages();
            System.out.println("!!!Immagini ricaricate!!!");

            sys.refreshTimerStats();
            System.out.println("!!!Timer ricaricati!!!");

            System.out.println("Server Ready");

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
                    sys.closeServer();
                    System.out.println("Sto spegnendo il server...");
                    System.exit(0);
                    break;
                default:
                    break;
            }
        }
    }
}
