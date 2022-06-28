package com.example.gameswap;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class HelperFunctions {

    public static Optional<ButtonType> showAlert(String title, String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        return alert.showAndWait();
    }

    public static void logout(Stage stage) throws IOException {
        stage.close();
        Stage loginStage = new Stage();
        Parent root = FXMLLoader.load(Main.class.getResource("login.fxml"));
        loginStage.setTitle("Login");
        loginStage.setScene(new Scene(root));
        loginStage.show();
    }
}
