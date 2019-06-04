package Client.Domain;

import Server.Domain.Auction;
import Server.Domain.Proxy;
import Server.People.Credentials.CharAnalizer;
import Server.People.User;

import java.io.File;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientManager {
    private String loggedUser;
    private Proxy ad;
    private ConnectionLayer connection;

    /**
     * Consente il logout solo se vi e' un utente loggato nel client, in caso di logout setta la stringa utente a null e non permette nessun'azione.
     * PER UNA MAGGIORE SICUREZZA SI POTREBBE INSERIRE UN ID GENERATO CASUALMENTE ED ASSEGNATO AD OGNI UTENTE OLTRE ALL'username, IN QUESTO MODO E' PIU DIFFICILE SPACCIARSI PER QUALCUN ALTRO
     * UN ULTERIORE LIVELLO DI SICUREZZA SI PUO' OTTENERE ASSEGNANDO UN NUOVO ID CASUALE AD OGNI NUOVO LOGIN.
     *
     *
     */
    private boolean logout() throws RemoteException {
        if (!(loggedUser == null)) {
            System.out.println("Effettuo la disconnessione per l'account: " + loggedUser);
            if (ad.logoutSDB(loggedUser)) {
                loggedUser = null;
                System.out.println("Disconnessione avvenuta con successo!");
                return true;
            } else {
                System.out.println("Ci sono stati problemi nella disconnessione, contatta l'amministratore");
            }
        } else {
            System.out.println("Nessun utente e' connesso!");
            return true;
        }
        return false;
    }

    public boolean logoutGUI() throws RemoteException {
        if (!(loggedUser == null)) {
            if (ad.logoutSDB(loggedUser)) {
                loggedUser = null;
                return true;
            }
        }
        return false;
    }

    /**
     * Crea un account solo se la password rispetta le specifiche del Char Analizer e se l'username non e' in uso.
     */
    private void createAccount() {
        try {
            Scanner scan = new Scanner(System.in);
            System.out.println("//-------------------------//");
            System.out.println("Enter your Login ID: ");
            String uid = scan.nextLine();
            System.out.println("Enter your Password:");
            String pw = scan.nextLine();
            if (validatePassword(pw)) {
                if (!ad.alredyTakenUsernameDB(uid)) {
                    ad.createUserDB(uid, pw);
                    System.out.println("Utente creato con successo: " + uid + "\t" + pw);
                } else
                    System.out.println("Username gia' in uso, riprovare");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public int signUpGUI(String username, String password) throws RemoteException {
        if (validatePassword(password)) {
            if (!ad.alredyTakenUsernameDB(username)) {
                ad.createUserDB(username, password);
                return 1; //Utente inserito con successo
            } else
                return 0; //Username gia' in uso
        }
        else
            return -1; //Password non valida

    }

    /**
     * Effettua il login, solo se l'utente e' registrato e se non vi e' gia' qualcuno connesso con lo stesso account
     *
     *
     */
    private boolean login() throws RemoteException {
        if (loggedUser == null) {
            Scanner scan = new Scanner(System.in);
            System.out.println("//-------------------------//");
            System.out.println("Enter your Login ID: ");
            String uid = scan.nextLine();
            System.out.println("Enter your Password:");
            String pw = scan.nextLine();
            if (ad.checkLoginDB(uid, pw)) {
                loggedUser = uid;
                System.out.println("Login successful. Welcome " + uid + " !");
                return true;
            } else {
                System.out.println("Invalid Login ID or password. Please try again.");
                return false;
            }
        } else {
            System.out.println("Sei gia' loggato,effettua prima il logout");
            return true;
        }
    }

    public int loginGUI(String username, String password) throws RemoteException {
        if (loggedUser == null) {
            if (ad.checkLoginDB(username, password)) {
                loggedUser = username;
                return 1; //Login effettuato con successo
            } else {
                return 0; //Dati inseriti errati
            }
        } else {
            return -1; //Qualcuno e' gia' loggato
        }
    }


    /**
     * Il metodo effettua il controllo sulla correttezza della password.
     * Viene analizzata attraverso il Char Analizer che restituisce l'esito ed eventualmente il motivo per cui una pass non e' accettata.
     *
     *
     *
     */
    private boolean validatePassword(String password) {
        CharAnalizer analizer = new CharAnalizer();
        if(!analizer.validatePassword(password)) {
            return false;
        }
        return true;
    }

    /**
     * Crea l'asta, per facilitare il controllo sul funzionamento dei metodi se si lascia vuoto termina dopo 1 minuto
     *
     */
    private void createAuction() throws RemoteException {
        try {
            Scanner scn = new Scanner(System.in);

            System.out.print("Inserisci il titolo dell'inserzione: ");
            String name = scn.nextLine();

            System.out.print("Inserisci il prezzo di partenza: ");
            int price = Integer.parseInt(scn.nextLine()); //usato in quanto nel buffer rimane uno \n

            System.out.print("Inserisci ora e data di fine dell'asta (formato dd/mm/yyyy hs:min) --> se lasci vuoto termina dopo 1 minuto:");
            String line = scn.nextLine();
            LocalDateTime d = formatDate(line);
            if (!d.isBefore(ad.currentiTime())) { //Faccio il controllo che la data attuale non sia inferiore a quella attuale
                String vendor = loggedUser;
                ad.addAuctionDB(name, price, vendor, d);
                System.out.println("Aggiunta con successo!");
            } else
                System.out.println("La data inserita e' precedente all'orario attuale, non e' stato possibile creare l'asta!");
        } catch (NumberFormatException e) {
            System.out.println("E' stato inserito un carattere inatteso, l'operazione va ripetuta");
        }
    }

    public int createAuctionGUI(String name,String description,int basePrice,LocalDateTime close) throws RemoteException {
        if(!close.isBefore(ad.currentiTime())) {
            ad.addAuctionDB(name,basePrice,loggedUser,close);
            return 1;
        }
        else
            return 0;
    }



    /**
     * Permette l'offerta SOLO su aste esistenti
     * Fa si che il creatore non possa offrire sulla sua stessa asta
     * Permette l'offerta solo se l'importo e' superiore all'offerta piu' alta precedente
     *
     *
     */
    private void makeOffer() throws RemoteException {
        Scanner scn = new Scanner(System.in);
        System.out.println("Inserisci l'id dell'asta su cui vuoi offrire:");
        int id = Integer.parseInt(scn.nextLine());

        if (ad.checkExistingAuctionDB(id)) {
            if (!(ad.vendorOfAuctionDB(id, loggedUser))) {
                int higher = ad.higherOfferDB(id);
                System.out.println("Offerta massima attuale:" + higher);
                System.out.println("Inserisci la tua offerta:");
                int amount = Integer.parseInt(scn.nextLine());
                if (amount > ad.higherOfferDB(id)) { //Richiamo ad affinche nel caso di concorrenza non permetta l'inserimento se contemporanemte due fanno l'offerta
                    if(ad.makeBidDB(loggedUser, amount, id))
                        System.out.println("Offerta accettata! Sei il nuovo offerente migliore");
                } else {
                    System.out.println("Offerta Rifiutata, importo troppo basso");
                }
            } else
                System.out.println("Non puoi effettuare offerte su un oggetto da te venduto!");
        } else
            System.out.println("L'id inserito non e' abbinato a nessun'asta esistente");
    }

    public boolean makeBid(String user,int amout, int id) throws RemoteException{
        return ad.makeBidDB(user,amout,id);
    }

    /**
     * Metodo usato per la formattazione della data inserita.
     *
     * @param line (linea da formattare)
     * @return line in LocalDateTime
     */
    private LocalDateTime formatDate(String line) {
        LocalDateTime d;
        if (line.equalsIgnoreCase("")) {
            d = LocalDateTime.now().plusMinutes(1); // dura un minuto
        } else {
            String[] line1 = line.split(" ");
            String[] date = line1[0].split("/");
            String[] hour = line1[1].split(":");

            d = LocalDateTime.of(Integer.parseInt(date[2]), Integer.parseInt(date[1]), Integer.parseInt(date[0]), Integer.parseInt(hour[0]), Integer.parseInt(hour[1]));

        }
        return d;
    }

    /**
     * Metodo usato per mostrare tutte le aste aperte
     * @return stringa contenete le info
     *
     */
    private String activeAuctions() throws RemoteException {
        return ad.showAllActiveAuctionsDB();
    }

    /**
     * Metodo usato per mostrare tutte le aste chiuse
     * @return stringa contenente le info
     *
     */
    private String closedAuctions() throws RemoteException {
        return ad.showClosedAuctionsDB();
    }

    public void sendFile(File image) {
        try {
            ad.saveAuctionImage(image);
        }catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Auction> requestListAuction() throws RemoteException {
        return ad.takeAuctionList();
    }

    public ArrayList<Auction> requestFavoriteAuction() throws RemoteException {
        return ad.favoriteAuction(loggedUser);
    }

    public void saveUserStateFavorites(User user,Auction au, int choose) throws RemoteException {
        ad.saveUserStateDB(user,au,choose);
    }

    public void saveAuctionState(Auction auction) throws RemoteException {
        ad.saveAuctionStateDB(auction);
    }

    public Auction getAuction(int id) throws RemoteException { return ad.getAuction(id);}

    /**
     * Menu base
     *
     *
     */
    public void menu() throws RemoteException {
        int decision = 0;
        try {
            while (decision != 99) {
                System.out.println("Benvenuto nel sistema gestione d'aste , scegli cosa fare:   1)CREATE USER   2)LOGIN   100)CLOSE");
                Scanner tastiera = new Scanner(System.in);
                decision = Integer.parseInt(tastiera.nextLine());
                switch (decision) {
                    case 1:
                        createAccount();
                        break;
                    case 2:
                        if (login()) {
                            loggedMenu();
                        }
                        break;
                    case 100:
                        decision = 99;
                        break;
                    default:
                        System.out.println("Operazione richiesta non esistente");
                        break;
                }
            }
            connection.setUserWantDisconnect(true);
        } catch (NumberFormatException e) {
            System.out.println("E' stato inserito un carattere inatteso, inserire solo numeri per la scelta");
            menu();
        }
    }

    /**
     * Menu avanzato attivo solo una volta che nel client e' loggato un utente
     *
     *
     */
    private void loggedMenu() throws RemoteException {
        int decision = 0;
        try {
            while (decision != 99) {
                System.out.println("Sei nella tua area privata " + loggedUser + " scegli cosa fare:   1)CREATE AN AUCTION   2)VIEW ACTIVE AUCTIONS   3)BID FOR AN ITEM 4)VIEW CLOSED AUCTIONS   5)LOGOUT");
                Scanner tastiera = new Scanner(System.in);
                decision = Integer.parseInt(tastiera.nextLine());
                switch (decision) {
                    case 1:
                        createAuction();
                        break;
                    case 2:
                        System.out.println(activeAuctions());
                        break;
                    case 3:
                        makeOffer();
                        break;
                    case 4:
                        System.out.println(closedAuctions());
                        break;
                    case 5:
                        if (logout()) {
                            decision = 99;
                        }
                        break;
                    default:
                        System.out.println("Operazione richiesta non esistente");
                        break;
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("E' stato inserito un carattere inatteso, inserire solo numeri per la scelta");
            loggedMenu();
        }
    }

    public String getLoggedUser() {
        return loggedUser;
    }

    public User getUser() throws RemoteException {
        return ad.getUser(loggedUser);
    }

    public boolean userLikeAuction(int id) throws RemoteException {
        return ad.userLikeAuction(loggedUser,id);
    }

    public ClientManager(ConnectionLayer c, Proxy bind) {
        connection = c;
        ad = bind;
    }
}
