package Client.Controller;

import Server.Domain.Auction;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.rmi.RemoteException;

public class AuctionWinnerCardController extends TemplateController {
    private Auction auction;

    @FXML
    private AnchorPane windowsPane;

    @FXML
    private ImageView auctionImage;

    @FXML
    private Label vendorWinner;

    @FXML
    private Label line1;

    @FXML
    private Label line2;

    @FXML
    private Label line3;




    @FXML
    private Label auctionName;

    @FXML
    private Label vendor;

    @FXML
    private Label higherOffer;

    @FXML
    private Hyperlink e_mail;

    @FXML
    void backToLoginScreen() {
        popUpStage.close();
    }

    @FXML
    public void handleCursorHand() {
        popUpStage.getScene().setCursor(Cursor.HAND);
    }

    @FXML
    public void handleCursor() { popUpStage.getScene().setCursor(Cursor.DEFAULT); }


    /**
     * Il layout cambia a seconda dell'utente che la visualizza, sia esso il venditore o il vincitore
     * @throws RemoteException
     */
    void initializeNow() throws RemoteException {
        //PROTECTED VARIATIONS
        auctionName.setText(auction.getDescriptionLot());
        higherOffer.setText("$" + auction.getLastBidAmount());
        ControllerServices.getInstance().setImagetoTheAuction(auction,auctionImage);
        if(client.getLoggedUser().equals(auction.getUsernameVendorDB())) {
            writeForVendor();
        }
        else {
            writeForWinner();
        }
    }

    void writeForVendor()throws RemoteException{
        line1.setText("Congratulazioni! Il tuo oggetto e' stato venduto.");
        line2.setText("Verrai contattato dal vincitore sul tuo indirizzo email");
        line3.setText("in alternativa, contattalo entro 48h dalla chiusura.");
        vendorWinner.setText("Winner");
        vendor.setText(auction.getLastBid().getActorDBUsername());
        e_mail.setText(client.getVendorEmail(auction.getLastBid().getActorDBUsername()));
    }

    void writeForWinner() throws RemoteException{
        line1.setText("Congratulazioni! Sei il vincitore dell'asta.");
        line2.setText("Per effettuare il pagamento e ricevere il prodotto, la");
        line3.setText("preghiamo di contattare l'indirizzo e-mail del venditore.");

        vendor.setText(auction.getUsernameVendorDB());
        e_mail.setText(client.getVendorEmail(auction.getUsernameVendorDB()));
    }

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }


}

