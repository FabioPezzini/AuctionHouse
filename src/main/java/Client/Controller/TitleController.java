package Client.Controller;

import Client.Domain.ClientManager;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.RemoteException;

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
    private void handleLogout(){
        try {

            if (client.logoutGUI()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/java/Client/Controller/Login.fxml"));
                AnchorPane root = loader.load();

                Scene scene = new Scene(root);
                // Show the scene containing the root layout.
                primaryStage.setScene(scene);

                ((LoginDataController) loader.getController()).setPrimaryStage(primaryStage);
                ((LoginDataController) loader.getController()).setClient(client);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Logout");
                alert.setHeaderText("Problemi nel logout, contatta l'amministratore");

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
            favoriteButton.setVisible(false);
            auctionListController.loadFavorite();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void myAuction () {
        try {
            setVisibleButtons();
            myAuction.setVisible(false);
            auctionListController.loadMyAuction();

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setVisibleButtons() {
        favoriteButton.setVisible(true);
        userSection.setVisible(true);
        myAuction.setVisible(true);
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
}