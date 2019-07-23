package Client.Controller;

import animatefx.animation.*;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class TitleController extends TemplateController {

    @FXML
    private JFXButton logout;

    @FXML
    private JFXButton myAuction;

    @FXML
    private JFXButton favoriteButton;

    @FXML
    private JFXButton userButton;

    private AuctionListController auctionListController;

    private UserPageController userPageController;

    private HomeController homeController;

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
                String title="Error Logout";
                String header="Error";
                String message="Some error occured, contact your administrator";
                ControllerServices.getInstance().showAlert(title,header,message,primaryStage, Alert.AlertType.ERROR);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public  void reloadHomeAuction() {
        homeController.reloadLatestAuction();
    }

    @FXML
    public void viewFavorites() {
        try {
            setVisibleButtons();

            new BounceOut(favoriteButton).play();
            auctionListController.loadFavorite();
            favoriteButton.setDisable(true);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void viewMyAuction () {
        try {
            setVisibleButtons();

            new BounceOut(myAuction).play();
            auctionListController.loadMyAuction();
            myAuction.setDisable(true);

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void viewUser() throws IOException {
        try {
            setVisibleButtons();

            new BounceOut(userButton).play();
            userButton.setDisable(true);


        }catch (Exception e) {
            e.printStackTrace();
        }

        BoxBlur blur = new BoxBlur(5,5,5);

        Stage popUpStageUserPage = loadScenePopUp("/View/UserPage.fxml");

        primaryStage.getScene().lookup("#windowsPane").setEffect(blur);


        userPageController =loader.getController();
        userPageController.setPrimaryStage(primaryStage);
        userPageController.setClient(client);
        userPageController.setPopUpStage(popUpStageUserPage);
        userPageController.initializeWindow();
        userPageController.initializeNow();
        userPageController.setTitleController(this);

    }

    public void setVisibleButtons() {
        new BounceIn(favoriteButton).play();
        favoriteButton.setDisable(false);

        new BounceIn(userButton).play();
        userButton.setDisable(false);

        new BounceIn(myAuction).play();
        myAuction.setDisable(false);
    }


    public AuctionListController getAuctionListController() {
        return auctionListController;
    }

    public void setAuctionListController(AuctionListController auctionListController) { this.auctionListController = auctionListController; }
    

    public JFXButton getMyAuction() { return myAuction; }

    public JFXButton getFavoriteButton() { return favoriteButton; }

    public HomeController getHomeController() { return homeController; }

    public void setHomeController(HomeController homeController) { this.homeController = homeController; }
}
