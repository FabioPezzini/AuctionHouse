package Client;

import Client.Domain.ClientManager;
import Client.Domain.ConnectionLayer;

import java.rmi.RemoteException;

public class ClientTextualApplication {
    public static void main(String[] args) throws RemoteException {

        ConnectionLayer connection = new ConnectionLayer("hii");

        while (!connection.isConnected()) { //FINCHE NON E' CONNESSO ASPETTA A CREARE IL CLIENT
        }

        ClientManager c = new ClientManager(connection, connection.getServer());
        System.out.println("Connesso al server!");
        c.menu();
    }
}
