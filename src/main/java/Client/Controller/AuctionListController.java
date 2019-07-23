package Client.Controller;


import Server.Domain.Auction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class AuctionListController extends TemplateController {
    private ArrayList<Auction> Alist;

    @FXML
    private ListView<Auction> auctionList;

    private final ObservableList<Auction> auction = FXCollections.observableArrayList();

    private AuctionListController auctionListController;

    private CreateAuctionFormController auctionFormController;

    private TitleController titleController;


    private void clearLists(){
        Alist = null;
        auction.clear();

    }

    private void initializeList() {
        if(Alist != null) {
            auction.addAll(Alist);
            auctionList.setCellFactory(handleListView());
            auctionList.setItems(auction);
        }
    }
    private Callback<ListView<Auction>, ListCell<Auction>> handleListView(){
        return new Callback<ListView<Auction>, ListCell<Auction>>() {
            @Override
            public ListCell<Auction> call(ListView<Auction> param) {
                return new ListCell<Auction>() {
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
                                    File file;
                                    String absolutePath;
                                    try {
                                        URL res = getClass().getClassLoader().getResource("Images/Destrauction.png");
                                        assert res != null;
                                        file = Paths.get(res.toURI()).toFile();
                                        absolutePath = file.getAbsolutePath();
                                        img = new Image(new FileInputStream(absolutePath), 100, 100, false, false);

                                    }catch (NullPointerException|URISyntaxException e) {
                                        e.printStackTrace();
                                    }
                                    //In alternativa a tutto quello sopra a partire dal try si puo' usare questo path: target/classes/Images/Destrauction.png
                                    imgview = new ImageView();
                                    imgview.setImage(img);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }

                            setGraphic(imgview);
                            setText(String.format("Id: %d\t\t\tName: %s\t\t\t\t Value: %s\n\t\t\t\tClose Date: %s", au.getId(), au.getLot().getDescription(), Integer.toString(au.getHigherOffer()), parseDate(au.getClosingDate())));
                            setStyle("-fx-background-color: #81c784"); //Da togliere se si vuole lo stacco
                        }
                    }
                };

            }
        };
    }

    private String parseDate(LocalDateTime closingTime) {
        return closingTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }



    void refreshList() {
        clearLists();
        //Per un bug visuale se non ricarico la Lista andando ad aggiornare solo l'observable list si buggano le immagini, probabilmente visto che uso una custom list cell
        try {
            Alist = client.requestListAuction();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        initializeList();
    }


    void searchList(String toSearch) {
        clearLists();
        //Usato per cercare
        try {
            Alist = client.searchAuction(toSearch);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        initializeList();
    }

    void loadFavorite() {
        clearLists();
        //Per un bug visuale se non ricarico la Lista andando ad aggiornare solo l'observable list si buggano le immagini, probabilmente visto che uso una custom list cell
        try {
            Alist = client.requestFavoriteAuction();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        initializeList();
    }

    void loadMyAuction() {
        clearLists();
        //Per un bug visuale se non ricarico la Lista andando ad aggiornare solo l'observable list si buggano le immagini, probabilmente visto che uso una custom list cell
        try {
            Alist = client.myAuction();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        initializeList();
    }

    @FXML
    public void chooseAuction() throws IOException {
        try {

            int idChoose = auctionList.getSelectionModel().getSelectedItem().getId();

            if(client.isClosed(idChoose) && client.getAuction(idChoose).getLastBid()!= null) {
                popOutWinnerCard(idChoose);
            }
            else if ((!client.isClosed(idChoose) || titleController.getMyAuction().isDisable() || titleController.getFavoriteButton().isDisable()) || (client.isClosed(idChoose) && !(client.getAuction(idChoose).getLastBid().getActorDBUsername().equals(client.getLoggedUser())))) {
                popOutAuctionCard(idChoose);
            }
            else {
                refreshList();
            }
        }catch (NullPointerException e) {
            refreshList();
        }

    }

    void popOutAuctionCard(int idChoose)throws IOException{
        Stage popUpStageAuctionCard = loadScenePopUp("/View/AuctionCard.fxml");

        ((AuctionCardController)loader.getController()).setPopUpStage(popUpStageAuctionCard);
        ((AuctionCardController) loader.getController()).setAuction(client.getAuction(idChoose));
        ((AuctionCardController) loader.getController()).setClient(client);
        ((AuctionCardController) loader.getController()).initializeNow();
        ((AuctionCardController) loader.getController()).setAuctionFormController(auctionFormController);
        ((AuctionCardController) loader.getController()).initializeWindow();
        popUpStageAuctionCard.show();
    }

    void popOutWinnerCard(int idChoose) throws IOException{
        if (client.getAuction(idChoose).getLastBid().getActorDBUsername().equals(client.getLoggedUser()) || client.getLoggedUser().equals(client.getAuction(idChoose).getUsernameVendorDB())) { // Apro la winning se l'utente e' il vincitore oppure se il venditore ha venduto!
            Stage popUpStageWinnerCard = loadScenePopUp("/View/AuctionWinnerCard.fxml");

            ((AuctionWinnerCardController)loader.getController()).setPopUpStage(popUpStageWinnerCard);
            ((AuctionWinnerCardController) loader.getController()).setAuction(client.getAuction(idChoose));
            ((AuctionWinnerCardController) loader.getController()).setClient(client);
            ((AuctionWinnerCardController) loader.getController()).initializeNow();
            popUpStageWinnerCard.show();
        }
    }




    public AuctionListController getAuctionListController() {
        return auctionListController;
    }

    void setAuctionListController(AuctionListController auctionListController) {
        this.auctionListController = auctionListController;
    }

    public CreateAuctionFormController getAuctionFormController() {
        return auctionFormController;
    }

    void setAuctionFormController(CreateAuctionFormController auctionFormController) {
        this.auctionFormController = auctionFormController;
    }

    public TitleController getTitleController() {
        return titleController;
    }

    void setTitleController(TitleController titleController) {
        this.titleController = titleController;
    }
}
