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

public class ProposeSwapConfirmationMessageController implements Initializable {

    @FXML
    private Button backToMainMenuButton;


    DatabaseController dbConn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dbConn = new DatabaseController();
    }

    @FXML
    protected void backToMainMenuButtonOnAction(ActionEvent event) throws SQLException, IOException {

        Stage stage = (Stage) backToMainMenuButton.getScene().getWindow();
        stage.close();

        Stage registrationStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("home.fxml"));
        Parent root = fxmlLoader.load();
        HomeController controller = fxmlLoader.<HomeController>getController();
        registrationStage.setTitle("Home");
        registrationStage.setScene(new Scene(root));
        registrationStage.show();

    }
}