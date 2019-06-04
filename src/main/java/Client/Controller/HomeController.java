package Client.Controller;

import Client.Domain.ClientManager;

import animatefx.animation.FadeIn;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.AnchorPane;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.event.KeyEvent;
import java.io.IOException;


public class HomeController {
    private ClientManager client;
    private Stage primaryStage;
    private FXMLLoader fxml = null;

    @FXML
    private AnchorPane header;

    @FXML
    private AnchorPane auctionList;

    @FXML
    private AnchorPane windowsPane;

    @FXML
    private JFXButton createAu;

    @FXML
    private JFXButton search;

    @FXML
    private JFXTextField searchText;


    @FXML
    public void createAuctionAction(ActionEvent event) throws IOException {
        BoxBlur blur = new BoxBlur(3,3,3);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/java/Client/Controller/FormCreaAsta.fxml"));
        Parent root = (Parent) loader.load();

        Stage popUpStage = new Stage(StageStyle.TRANSPARENT);
        popUpStage.initOwner(primaryStage);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.setScene(new Scene(root));
        popUpStage.show();

        windowsPane.setEffect(blur);



        //Animation
        new FadeIn(root).play();


        ((FormCreaAstaController)loader.getController()).setClient(client);
        ((FormCreaAstaController)loader.getController()).setPopUpStage(popUpStage);
        ((FormCreaAstaController)loader.getController()).setPrimaryStage(primaryStage);
    }

    @FXML
    public void reloadLatestAuction() {
        ((AuctionListController)fxml.getController()).refreshList();
    }

    @FXML
    public void searchAuction() {
        if(searchText == null) {
            reloadLatestAuction();
        }
        else {
            String text = searchText.getText();
            ((AuctionListController)fxml.getController()).searchList(text);
        }
    }



    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage; }

    public ClientManager getClient() { return client; }

    public void setClient(ClientManager client) {
        this.client = client;
        initializeHeader();
        initializeAuctionList();
    }

    public Stage getPrimaryStage() { return primaryStage; }

    private void initializeHeader() {
        FXMLLoader fxml = null;
        Parent root = null;

        try {
            fxml = new FXMLLoader();
            fxml.setLocation(getClass().getResource("/main/java/Client/Controller/Title.fxml"));
            root = (Parent)fxml.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        header.getChildren().setAll(root);

        ((TitleController)fxml.getController()).setPrimaryStage(primaryStage);
        ((TitleController)fxml.getController()).setClient(client);
    }

    private void initializeAuctionList() {
        fxml = null;
        Parent root = null;

        try {
            fxml = new FXMLLoader();
            fxml.setLocation(getClass().getResource("/main/java/Client/Controller/AuctionList.fxml"));
            root = (Parent)fxml.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        auctionList.getChildren().setAll(root);

        ((AuctionListController)fxml.getController()).setPrimaryStage(primaryStage);
        ((AuctionListController)fxml.getController()).setClient(client);
    }






}