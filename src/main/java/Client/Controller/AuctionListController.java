package Client.Controller;


import Server.Domain.Auction;
import Client.Domain.ClientManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.ArrayList;


public class AuctionListController {
    private ClientManager client;
    private Stage primaryStage;
    private ArrayList<Auction> Alist;

    @FXML
    private ListView<Auction> auctionList;

    private final ObservableList<Auction> auction = FXCollections.observableArrayList();



    public void initializeList() {
        Alist = null;
        auction.clear();
        //Richiedo al server una lista di 10 aste al massimo, successivamente posso chiedere di prenderne altre 10
        try {
            Alist = client.requestListAuction();
        }catch (RemoteException e) {
            e.printStackTrace();
        }
        if(Alist != null) {
            for (int i = 0; i < Alist.size(); i++) {
                auction.add(Alist.get(i));
            }

            auctionList.setCellFactory(new Callback<ListView<Auction>, ListCell<Auction>>() {
                @Override
                public ListCell<Auction> call(ListView<Auction> param) {
                    ListCell<Auction> cell = new ListCell<Auction>() {
                        Image img;
                        ImageView imgview = null;

                        protected void updateItem(Auction au, boolean bt1) {
                            super.updateItem(au,bt1);
                            if(bt1)
                                setStyle("-fx-background-color: #81c784"); // Da togliere se si vuole lo stacco
                            if(au != null) {
                                if(au.getImage().exists()) {
                                    try {
                                        img = new Image(new FileInputStream(au.getImage()),100,100,false,false);

                                        imgview = new ImageView();
                                        imgview.setImage(img);
                                    }catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                else {
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
                                        img = new Image(new FileInputStream(absolutePath), 100, 100, false, false);

                                        imgview = new ImageView();
                                        imgview.setImage(img);
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }

                                setGraphic(imgview);
                                setText("Id:" + Integer.toString(au.getId()) + "\t\t" + "Name:" + au.getLot().getDescription() + "\t\t " + "Value:" + Integer.toString(au.getHigherOffer()) + "\n\t\t" + "Close Date:" + au.getClosingDate().toString());

                                setStyle("-fx-background-color: #81c784"); //Da togliere se si vuole lo stacco
                            }
                        }
                    };
                    return cell;
                }
            });
            auctionList.setItems(auction);
        }
    }


    public void refreshList() {
        //Per un bug visuale se non ricarico la Lista andando ad aggiornare solo l'observable list si buggano le immagini, probabilmente visto che uso una custom list cell
        initializeList();
    }

    @FXML
    public void chooseAuction() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/java/Client/Controller/AuctionCard.fxml"));
        Parent root = (Parent) loader.load();

        Stage popUpStage = new Stage(StageStyle.TRANSPARENT);
        popUpStage.initOwner(primaryStage);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.setScene(new Scene(root));
        popUpStage.show();

        ((AuctionCardController)loader.getController()).setPopUpStage(popUpStage);
        int idChoose = auctionList.getSelectionModel().getSelectedItem().getId();
        ((AuctionCardController)loader.getController()).setAuction(client.getAuction(idChoose));

        ((AuctionCardController)loader.getController()).setClient(client);
    }




    public ClientManager getClient() { return client; }

    public void setClient(ClientManager client) {
        this.client = client;
        initializeList();
    }

    public Stage getPrimaryStage() { return primaryStage; }

    public void setPrimaryStage(Stage primaryStage) { this.primaryStage = primaryStage; }
}
