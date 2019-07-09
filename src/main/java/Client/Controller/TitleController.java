package Client.Controller;

import Client.Domain.ClientManager;
import animatefx.animation.*;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TitleController {
    private ClientManager client;
    private Stage primaryStage;

    @FXML
    private JFXButton logout;

    @FXML
    private JFXButton userSection;

    @FXML
    private JFXButton myAuction;

    @FXML
    private JFXButton favoriteButton;

    private AuctionListController auctionListController;

    @FXML
    public void handleCursorHand(MouseEvent me) {
        primaryStage.getScene().setCursor(Cursor.HAND);
    }

    @FXML
    public void handleCursor(MouseEvent me) {
        primaryStage.getScene().setCursor(Cursor.DEFAULT);
    }

    @FXML
    private void handleLogout(){
        try {
            if (client.logoutGUI()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Login.fxml"));
                AnchorPane root = loader.load();

                Scene scene = new Scene(root);
                // Show the scene containing the root layout.
                primaryStage.setScene(scene);

                ((LoginController) loader.getController()).setPrimaryStage(primaryStage);
                ((LoginController) loader.getController()).setClient(client);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Logout");
                alert.setHeaderText("Some error occured, contact your administrator");

                alert.showAndWait();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void viewFavorites() {
        try {
            setVisibleButtons();

            auctionListController.loadFavorite();
            //auctionListController.setTitleController(this);
            new BounceOut(favoriteButton).play();
            favoriteButton.setDisable(true);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void myAuction () {
        try {
            setVisibleButtons();

            new BounceOut(myAuction).play();
            myAuction.setDisable(true);
            auctionListController.loadMyAuction();
            //auctionListController.setTitleController(this);

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setVisibleButtons() {
        new BounceIn(favoriteButton).play();
        favoriteButton.setDisable(false);

        new BounceIn(userSection).play();
        userSection.setDisable(false);

        new BounceIn(myAuction).play();
        myAuction.setDisable(false);
    }


    public ClientManager getClient() { return client; }

    public void setClient(ClientManager client) { this.client = client; }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public AuctionListController getAuctionListController() {
        return auctionListController;
    }

    public void setAuctionListController(AuctionListController auctionListController) {
        this.auctionListController = auctionListController;
    }

    public JFXButton getMyAuction() {
        return myAuction;
    }

    public JFXButton getFavoriteButton() {
        return favoriteButton;
    }
}
