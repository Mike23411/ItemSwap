package com.example.gameswap;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class HomeController implements Initializable{

    @FXML private Button logoutButton;
    @FXML private Button updateInfoButton;
    @FXML private Button listItemButton;
    @FXML private Button myItemsButton;
    @FXML private Button searchItemButton;
    @FXML private Button swapHistoryButton;
    @FXML private Hyperlink rateSwapsLink;
    @FXML private Hyperlink acceptRejectSwapsLink;

    @FXML private Label ratingLabel;
    @FXML private Label welcomeLabel;


    private String user_email;

    DatabaseController dbConn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dbConn = new DatabaseController();
    }

    @FXML
    public void logoutButtonOnAction(ActionEvent actionEvent) throws IOException {
        HelperFunctions.logout((Stage) logoutButton.getScene().getWindow());
    }

    public void autoPopulateMyList(){
        try{
            ResultSet unratedSwapsRS = dbConn.unRatedSwaps(this.user_email);
            ResultSet pendingSwapsRS = dbConn.unAcceptedSwaps(this.user_email);
            ResultSet userInfo = dbConn.getUserInfo(this.user_email);

            welcomeLabel.setText("Welcome, " + userInfo.getString("first_name") +
                    " " + userInfo.getString("last_name") + "!");

            if(userInfo.getString("rating") != null) ratingLabel.setText(userInfo.getString("rating"));

            if(pendingSwapsRS.next()){
                if(pendingSwapsRS.getBoolean("red_count")){
                    acceptRejectSwapsLink.setStyle("-fx-text-fill: red; -fx-font-weight: bold");
                    listItemButton.setDisable(true);
                }
                acceptRejectSwapsLink.setText(pendingSwapsRS.getString("pending_swaps"));
            }

            if(unratedSwapsRS.next()){
                if(Integer.parseInt(unratedSwapsRS.getString("unrated_swaps"))  == 0){
                    rateSwapsLink.setVisible(false);
                }else{
                    if(Integer.parseInt(unratedSwapsRS.getString("unrated_swaps")) > 2){
                        rateSwapsLink.setStyle("-fx-text-fill: red; -fx-font-weight: bold");
                        listItemButton.setDisable(true);
                    }
                    rateSwapsLink.setText(unratedSwapsRS.getString("unrated_swaps"));
                }
            }


        }
        catch(Exception e)
        {
            e.printStackTrace();
            e.getCause();
        }
    }

    @FXML
    public void updateButtonOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) updateInfoButton.getScene().getWindow();
        stage.close();

        Stage registrationStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("updateInfo.fxml"));
        Parent root = (Parent)fxmlLoader.load();
        UpdateInfoController controller = fxmlLoader.<UpdateInfoController>getController();
        controller.setUser_email(this.user_email);
        controller.retrieveUserInfo();
        registrationStage.setTitle("Update Info");
        registrationStage.setScene(new Scene(root));
        registrationStage.show();
    }

    @FXML
    public void listItemButtonOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) listItemButton.getScene().getWindow();
        stage.close();

        Stage registrationStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("listNewItem.fxml"));
        Parent root = (Parent)fxmlLoader.load();
        ListItemController controller = fxmlLoader.<ListItemController>getController();
        controller.setUser_email(this.user_email);
        registrationStage.setTitle("List Item");
        registrationStage.setScene(new Scene(root));
        registrationStage.show();
    }

    @FXML
    public void myItemsButtonOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) listItemButton.getScene().getWindow();
        stage.close();

        Stage registrationStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("myItems.fxml"));
        Parent root = (Parent)fxmlLoader.load();
        MyItemsController controller = fxmlLoader.<MyItemsController>getController();
        controller.setUser_email(this.user_email);
        controller.autoPopulateMyList();
        registrationStage.setTitle("My Items");
        registrationStage.setScene(new Scene(root));
        registrationStage.show();
    }

    @FXML
    public void searchItemsButtonOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) listItemButton.getScene().getWindow();
        stage.close();

        Stage registrationStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("searchItems.fxml"));
        Parent root = (Parent)fxmlLoader.load();
        SearchItemsController controller = fxmlLoader.<SearchItemsController>getController();
        controller.setUser_email(this.user_email);
        registrationStage.setTitle("Search Items");
        registrationStage.setScene(new Scene(root));
        registrationStage.show();
    }

    @FXML
    public void swapHistoryButtonOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) swapHistoryButton.getScene().getWindow();
        stage.close();

        Stage swapHistoryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("swaphistory.fxml"));
        Parent root = (Parent)fxmlLoader.load();
        SwapHistoryController controller = fxmlLoader.<SwapHistoryController>getController();
        controller.setUser_email(this.user_email);
        controller.autoPopulateMyList();
        swapHistoryStage.setTitle("Swap History");
        swapHistoryStage.setScene(new Scene(root));
        swapHistoryStage.show();
    }

    @FXML
    public void rateSwapsLinkOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) rateSwapsLink.getScene().getWindow();
        stage.close();

        Stage rateSwapsStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("rateSwaps.fxml"));
        Parent root = (Parent)fxmlLoader.load();
        RateSwapsController controller = fxmlLoader.<RateSwapsController>getController();
        controller.setUser_email(this.user_email);
        controller.autoPopulateMyList();
        rateSwapsStage.setTitle("Rate Swaps");
        rateSwapsStage.setScene(new Scene(root));
        rateSwapsStage.show();
    }

    @FXML
    public void acceptRejectSwapsOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) acceptRejectSwapsLink.getScene().getWindow();
        stage.close();

        Stage acceptRejectSwapsStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("acceptrejectswaps.fxml"));
        Parent root = (Parent)fxmlLoader.load();
        AcceptRejectSwapsController controller = fxmlLoader.<AcceptRejectSwapsController>getController();
        controller.setUser_email(this.user_email);
        controller.autoPopulateMyList();
        acceptRejectSwapsStage.setTitle("Accept/Reject Swaps");
        acceptRejectSwapsStage.setScene(new Scene(root));
        acceptRejectSwapsStage.show();
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }
}