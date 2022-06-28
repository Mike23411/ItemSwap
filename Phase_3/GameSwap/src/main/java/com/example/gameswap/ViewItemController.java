package com.example.gameswap;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ViewItemController implements Initializable{

    @FXML private TextField itemNumberField;
    @FXML private TextField itemNameField;
    @FXML private TextField typeField;
    @FXML private TextField platformField;
    @FXML private TextField mediaField;
    @FXML private TextField conditionField;
    @FXML private TextField offeredByField;
    @FXML private TextField locationField;
    @FXML private TextField ratingField;
    @FXML private TextField distanceField;
    @FXML private TextField pieceCountField;
    @FXML private Label mediaLabel;
    @FXML private Label platformLabel;
    @FXML private Label pieceCountLabel;
    @FXML private Label distanceLabel;

    @FXML private TextArea descriptionField;

    @FXML public Button cancelButton;
    @FXML public Button proposeButton;


    private String user_email;
    private DatabaseController dbConn;
    private Item item;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dbConn = new DatabaseController();
    }

    public void retrieveItemInfo(String itemNumber) throws SQLException {

        ResultSet resultSet = dbConn.getItemDetails(user_email,itemNumber);
        this.item = new Item(resultSet);

        itemNumberField.setText(item.getItemNumber());
        itemNameField.setText(item.getName());
        typeField.setText(item.getGameType());
        conditionField.setText(item.getCondition());
        offeredByField.setText(item.getUserNickName());
        locationField.setText(item.getUserLocation());
        ratingField.setText(item.getUserRating());


        distanceField.setText(item.getDistance());
        if(Double.parseDouble(item.getDistance()) <= 25){
            distanceField.setStyle("-fx-control-inner-background: green");
        }else if (Double.parseDouble(item.getDistance()) <= 50){
            distanceField.setStyle("-fx-control-inner-background: yellow");
        }else if (Double.parseDouble(item.getDistance()) <= 100){
            distanceField.setStyle("-fx-control-inner-background: orange");
        }else{
            distanceField.setStyle("-fx-control-inner-background: red");
        }

        if(item.getDescription() == null)descriptionField.setVisible(false);
        else descriptionField.setText(item.getDescription());

        if(item.getMedia() == null){
            mediaLabel.setVisible(false);
            mediaField.setVisible(false);
        }else{
            mediaField.setText(item.getMedia());
        }

        if(item.getPlatform() == null){
            platformLabel.setVisible(false);
            platformField.setVisible(false);
        }else{
            platformField.setText(item.getPlatform());
        }

        if(item.getPieceCount() == null){
            pieceCountLabel.setVisible(false);
            pieceCountField.setVisible(false);
        }else{
            pieceCountField.setText(item.getPieceCount());
        }

        ResultSet unratedSwapsRS = dbConn.unRatedSwaps(this.user_email);
        ResultSet pendingSwapsRS = dbConn.unAcceptedSwaps(this.user_email);
        ResultSet userInfo = dbConn.getUserInfo(this.user_email);

        if(pendingSwapsRS.next()){
            if(pendingSwapsRS.getBoolean("red_count")){
                proposeButton.setDisable(true);
            }
        }

        if(unratedSwapsRS.next()){
            if(Integer.parseInt(unratedSwapsRS.getString("unrated_swaps")) > 2){
                proposeButton.setDisable(true);
            }
        }

    }

    @FXML
    public void cancelButtonOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();

        Stage registrationStage = new Stage();
        FXMLLoader fxmlLoader;
        Parent root;
        if (proposeButton.isVisible()){
            fxmlLoader = new FXMLLoader(getClass().getResource("searchItems.fxml"));
            root = (Parent)fxmlLoader.load();
            SearchItemsController controller = fxmlLoader.<SearchItemsController>getController();
            controller.setUser_email(this.user_email);
            registrationStage.setTitle("Search Items");
        }else{
            fxmlLoader = new FXMLLoader(getClass().getResource("myItems.fxml"));
            root = (Parent)fxmlLoader.load();
            MyItemsController controller = fxmlLoader.<MyItemsController>getController();
            controller.setUser_email(this.user_email);
            controller.autoPopulateMyList();
            registrationStage.setTitle("My Items");
        }

        registrationStage.setScene(new Scene(root));
        registrationStage.show();
    }


    @FXML
    public void proposeSwapOnAction(ActionEvent actionEvent) throws IOException, SQLException {



        Stage stage = (Stage) proposeButton.getScene().getWindow();
        stage.close();

        Stage proposeSwapStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("proposeSwap.fxml"));
        Parent root = (Parent)fxmlLoader.load();
        ProposeSwapController controller = fxmlLoader.<ProposeSwapController>getController();
        controller.setUser_email(this.user_email);
        controller.setDistance(item.getDistance());
        controller.setItemName(item.getName());
        controller.setItemNumber(item.getItemNumber());
        controller.autoPopulateProposeSwap();
        proposeSwapStage.setTitle("Propose Swap");
        proposeSwapStage.setScene(new Scene(root));
        proposeSwapStage.show();
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

}