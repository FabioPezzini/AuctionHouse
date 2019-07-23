package Client.Controller;
import animatefx.animation.Flip;
import animatefx.animation.Pulse;
import animatefx.animation.RotateIn;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.AnchorPane;

import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.io.IOException;


public class HomeController extends TemplateController {


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
    private FontAwesomeIconView reloadButton;

    private TitleController titleController;

    private AuctionListController auctionListController;

    void init() {
        initializeAuctionList();
        initializeHeader();
    }


    @FXML
    public void createAuctionAction() throws IOException {
        BoxBlur blur = new BoxBlur(3,3,3);

        Stage popUpStageCreate = loadScenePopUp("/View/CreateAuctionForm.fxml");

        windowsPane.setEffect(blur);

        CreateAuctionFormController auctionFormController = loader.getController();

        auctionFormController.setClient(client);
        auctionFormController.setPopUpStage(popUpStageCreate);
        auctionFormController.setPrimaryStage(primaryStage);
        auctionFormController.initializeWindow();
        auctionFormController.disableModifyDeleteAuction();

    }

    @FXML
    public void reloadCurrentTab() {
        if(titleController.getFavoriteButton().isDisable())
            titleController.viewFavorites();
        if(titleController.getMyAuction().isDisable())
            titleController.viewMyAuction();
        if(!titleController.getMyAuction().isDisable() && !titleController.getFavoriteButton().isDisable())
            reloadLatestAuction();

        new RotateIn(reloadButton).play();


    }

    @FXML
    public void reloadLatestAuction() {

        auctionListController.refreshList();
        titleController.setVisibleButtons();
    }

    @FXML
    public void searchAuction() {
        if(searchText == null) {
            reloadLatestAuction();
        }
        else {
            String text = searchText.getText();

            auctionListController.searchList(text);

            titleController.setVisibleButtons();
        }
        new Pulse(search).setSpeed(2).play();
    }

    @FXML
    public void handleCursorHand() {
        primaryStage.getScene().setCursor(Cursor.HAND);
    }

    @FXML
    public void handleCursor() {
        primaryStage.getScene().setCursor(Cursor.DEFAULT);
    }


    private void initializeHeader() {
        FXMLLoader fxml = null;
        Parent root = null;

        try {
            fxml = new FXMLLoader();
            fxml.setLocation(getClass().getResource("/View/Title.fxml"));
            root = fxml.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        header.getChildren().setAll(root);

        titleController = fxml.getController();

        titleController.setPrimaryStage(primaryStage);
        titleController.setClient(client);
        titleController.setAuctionListController(auctionListController);

        //Inietto dipendenza
        auctionListController.setTitleController(titleController);

        titleController.setHomeController(this);

    }

    private void initializeAuctionList() {
        FXMLLoader fxml = new FXMLLoader();
        Parent root = null;

        try {
            fxml.setLocation(getClass().getResource("/View/AuctionList.fxml"));
            root = fxml.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        auctionList.getChildren().setAll(root);
        auctionListController = fxml.getController();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/CreateAuctionForm.fxml"));

        auctionListController.setPrimaryStage(primaryStage);
        auctionListController.setClient(client);
        auctionListController.refreshList();
        auctionListController.setAuctionListController(auctionListController);
        auctionListController.setAuctionFormController( loader.getController());
    }

    public TitleController getTitleController() {
        return titleController;
    }

    public void setTitleController(TitleController titleController) {
        this.titleController = titleController;
    }

    public AuctionListController getAuctionListController() {
        return auctionListController;
    }

    public void setAuctionListController(AuctionListController auctionListController) {
        this.auctionListController = auctionListController;
    }

    public AnchorPane getAuctionList() {
        return auctionList;
    }

    public void setAuctionList(AnchorPane auctionList) {
        this.auctionList = auctionList;
    }
}