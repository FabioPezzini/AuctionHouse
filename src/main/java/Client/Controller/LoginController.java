package Client.Controller;

import Client.Domain.ClientManager;
import Client.Exceptions.AlreadyLoggedInException;
import Client.Exceptions.ErrorLoginException;
import animatefx.animation.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;


public class LoginController extends TemplateController {

    private HomeController homeController;
    private SignUpController signupController;

    @FXML
    private JFXTextField username;

    @FXML
    private JFXPasswordField password;

    @FXML
    private JFXButton signIn;

    @FXML
    private JFXButton signUp;

    @FXML
    private AnchorPane windowsPane;



    @FXML
    private void handleSignIn() throws IOException {
        try{
            String us = username.getText();
            String pass = password.getText();
            client.loginGUI(us,pass);
            String title="Success";
            String header="Welcome back "+us+"!";
            String message ="Logged in successfully";
            ControllerServices.getInstance().showAlert(title,header,message,primaryStage,Alert.AlertType.INFORMATION);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Home.fxml"));
            Parent home = (Parent) loader.load();
            homeController = (HomeController)loader.getController();
            homeController.setPrimaryStage(primaryStage);
            homeController.setClient(client);
            homeController.init();
            primaryStage.setScene(new Scene(home));
        }
        catch(ErrorLoginException e) {
            e.show(popUpStage);
        }
        catch(AlreadyLoggedInException e){
            e.show(popUpStage);
        }
    }

    @FXML
    private void changeSceneSignUp() throws IOException {
        BoxBlur blur = new BoxBlur(5,5,5);
        Stage popUpStageSignUp = loadScenePopUp("/View/SignUp.fxml");
        windowsPane.setEffect(blur);
        signupController = loader.getController();
        signupController.setPopUpStage(popUpStageSignUp);
        signupController.setPrimaryStage(primaryStage);
        signupController.setClient(client);
        signupController.initializeWindow();

    }

    @FXML
    public void handleCursorHand() {
        primaryStage.getScene().setCursor(Cursor.HAND);
    }

    @FXML
    public void handleCursor() {
        primaryStage.getScene().setCursor(Cursor.DEFAULT);
    }


    public HomeController getHomeController() { return homeController; }

    public void setHomeController(HomeController homeController) { this.homeController = homeController; }

    public SignUpController getSignupController() { return signupController; }

    public void setSignupController(SignUpController signupController) { this.signupController = signupController; }

}
