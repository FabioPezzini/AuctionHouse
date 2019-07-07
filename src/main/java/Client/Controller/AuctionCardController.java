package Client.Controller;

import Client.Domain.ClientManager;
import Server.Domain.Auction;
import Server.People.User;
import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


public class AuctionCardController {
    private ClientManager client;
    private Stage popUpStage;
    private Auction auction;

    private long day;
    private long hour;
    private long minute;
    private long second;

    @FXML
    private Label auctionName;

    @FXML
    private Label higherOffer;

    @FXML
    private Label bidderHigher;

    @FXML
    private Label vendor;

    @FXML
    private ImageView auctionImage;

    @FXML
    private Label closeDate;

    @FXML
    private JFXButton offerButton;

    @FXML
    private FontAwesomeIconView star;


    @FXML
    private Label timer;
    private Timeline timeline;


    public void initializeNow() {
        try {
            if(!client.userLikeAuction(auction.getId())) { //Se l'asta non e' tra le preferite
                star.setIcon(FontAwesomeIcon.STAR_ALT);

            }
            else {
                star.setIcon(FontAwesomeIcon.STAR);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        auctionName.setText(auction.getLot().getDescription());
        if(auction.getLastBid() != null) {
            higherOffer.setText("$" + auction.getLastBid().getAmount());
            bidderHigher.setText(auction.getLastBid().getActorDB().getUsername());
        }
        else {
            higherOffer.setText("$"+auction.getHigherOffer());
            bidderHigher.setText("null");
        }

        vendor.setText(auction.getLot().getVendorDB().getUsername());
        closeDate.setText(parseDate(auction.getClosingDate()));


        Image img;


        if(auction.getImage() == null) {

                //img = new Image(new FileInputStream(auction.getImage()),100,100,false,false);
                //img = new Image((auction.getImage().toURI().toString()));
                //auctionImage.setImage(img);
            try {
                File file = null;
                try {
                    URL res = getClass().getClassLoader().getResource("Images/i_have_no_idea.png");
                    file = Paths.get(res.toURI()).toFile();
                }catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                //In alternativa a tutto quello sopra a partire dal try si puo' usare questo path: target/classes/Images/i_have_no_idea.png
                String absolutePath = file.getAbsolutePath();
                img = new Image(new FileInputStream(absolutePath),268,226,false,false);

                auctionImage.setImage(img);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else {
                try {
                    img = new Image(new FileInputStream(auction.getImage()),268,226,false,false);

                    auctionImage.setImage(img);
                }catch (IOException e) {
                    e.printStackTrace();
                }
        }


        ZonedDateTime zdt = auction.getClosingDate().atZone(ZoneId.of("Europe/Rome"));
        long millis = zdt.toInstant().toEpochMilli() - System.currentTimeMillis();
        countMilliToDay(millis);

        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().addAll(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(second > 0) {
                    second--;
                }
                else if(second <= 0 ) {
                    second = 59;
                    if(minute > 0) {
                        minute--;
                    }
                    else if (minute <= 0) {
                        minute = 59;
                        if(hour > 0) {
                            hour--;
                        }
                        else if(hour <= 0) {
                            hour = 23;
                            if(day > 0) {
                                day--;
                            }
                        }
                    }
                }

                timer.setText("D " + day  + "  H " + hour + "  M " + minute + "  S " + second);

                if(second <= 0 && minute <= 0 && hour <= 0 && day <= 0) {
                    timeline.stop();
                }

            }
        }));
        timeline.play();

    }

    public String parseDate(LocalDateTime closingTime) {

        return closingTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    @FXML
    private void backToLoginScreen() {
        popUpStage.close();
    }

    @FXML
    private void addRemoveToFavourite() throws RemoteException {
        User user = client.getUser();
        Auction au = client.getAuction(auction.getId());
        try {
            if(client.userLikeAuction(au.getId())) { //Se contiene l'asta
                client.saveUserStateFavorites(user,au,0); //Togli l'asta
                star.setIcon(FontAwesomeIcon.STAR_ALT);
            }
            else {
                client.saveUserStateFavorites(user,au,1);
                star.setIcon(FontAwesomeIcon.STAR);
            }

        }catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void makeAnOffer() {
        if(client.getLoggedUser().equals(auction.getLot().getVendorDB().getUsername())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Offer");
            alert.setHeaderText("Error ");
            alert.setContentText("Il creatore dell'asta non puo' fare offerte sulla stessa");
            alert.initOwner(popUpStage);

            alert.showAndWait();
        }
        else {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Offer Dialog");
            dialog.setHeaderText("Higher Offer:" + auction.getHigherOffer());
            dialog.setContentText("Your Offer:");

            Optional<String> input = dialog.showAndWait();
            input.ifPresent(offer -> {
                int offerInt;
                try {
                    offerInt = Integer.parseInt(offer);
                    if(client.makeBid(client.getLoggedUser(),offerInt,auction.getId())) {
                        auction = client.getAuction(auction.getId());
                        initializeNow();
                    }
                    else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error Offer");
                        alert.setHeaderText("Error ");
                        alert.setContentText("L'offeta e' stata superata, ricarica");

                        alert.showAndWait();
                    }
                }catch (NumberFormatException e) {
                }catch (RemoteException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void countMilliToDay(Long ms) {
        final int SECOND = 1000;
        final int MINUTE = 60 * SECOND;
        final int HOUR = 60 * MINUTE;
        final int DAY = 24 * HOUR;



        if (ms > DAY) {
            day = ms / DAY;
            ms %= DAY;
        }
        if (ms > HOUR) {
            hour = ms / HOUR;
            ms %= HOUR;
        }
        if (ms > MINUTE) {
            minute = ms / MINUTE;
            ms %= MINUTE;
        }
        if (ms > SECOND) {
           second = ms / SECOND;
           ms %= SECOND;
        }
    }

    public ClientManager getClient() {
        return client;
    }

    public void setClient(ClientManager client) {
        this.client = client;
        initializeNow();
    }

    public Stage getPopUpStage() {
        return popUpStage;
    }

    public void setPopUpStage(Stage popUpStage) {
        this.popUpStage = popUpStage;
    }

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }
}
