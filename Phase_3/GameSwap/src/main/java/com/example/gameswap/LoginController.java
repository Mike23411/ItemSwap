package com.example.gameswap;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class LoginController implements Initializable{

    @FXML private Button loginButton;
    @FXML private Label loginMessageLabel;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    DatabaseController dbConn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dbConn = new DatabaseController();
    }

    @FXML
    protected void loginButtonOnAction(ActionEvent event) throws SQLException, IOException {
        String email = dbConn.login(emailField.getText(), passwordField.getText());
        if(!email.isEmpty()){
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.close();

            Stage registrationStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("home.fxml"));
            Parent root = fxmlLoader.load();
            HomeController controller = fxmlLoader.<HomeController>getController();
            controller.setUser_email(email);
            controller.autoPopulateMyList();
            registrationStage.setTitle("Home");
            registrationStage.setScene(new Scene(root));
            registrationStage.show();
        }
    }

    public void validateLogin() {

    }

    @FXML
    public void registerButtonOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.close();

        Stage registrationStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("registration.fxml"));
        registrationStage.setTitle("Registration");
        registrationStage.setScene(new Scene(root));
        registrationStage.show();
    }
}