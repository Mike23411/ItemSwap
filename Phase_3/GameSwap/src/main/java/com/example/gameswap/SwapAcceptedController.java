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
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SwapAcceptedController implements Initializable{

    @FXML
    private Button backToMainButton;
    @FXML private Label emailLabel;
    @FXML private Label nameLabel;
    @FXML private Label phoneLabel;


    DatabaseController dbConn;

    private String user_email;

    private ResultSet userInfo;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dbConn = new DatabaseController();
    }

//    public void retrieveUserInfo() throws IOException {
//        try {
//            this.userInfo =  dbConn.getUserInfo(this.user_email);
//            emailLabel.setText("email" + userInfo.getString("email"));
//            nameLabel.setText(userInfo.getString("first_name"));
//            if(userInfo.getString("phone_number") != null){
//                phoneLabel.setText("phone number: " + userInfo.getString("phone_number"));
//            }
//
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            HelperFunctions.showAlert("Error","Unable to retrieve user information");
//            HelperFunctions.logout((Stage) cancelButton.getScene().getWindow());
//        }
//    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

}
