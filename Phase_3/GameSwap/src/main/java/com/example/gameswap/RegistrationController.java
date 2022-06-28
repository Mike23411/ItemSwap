package com.example.gameswap;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.validator.routines.EmailValidator;

public class RegistrationController implements Initializable{

    @FXML private TextField emailField;
    @FXML private TextField nicknameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField postalCodeField;
    @FXML private TextField stateField;
    @FXML private TextField cityField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField phoneField;
    @FXML private ComboBox phoneTypeCombo;
    @FXML private CheckBox sharePhoneCheckBox;

    @FXML private Button cancelButton;
    @FXML private Button registerButton;

    DatabaseController dbConn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dbConn = new DatabaseController();

        phoneTypeCombo.getItems().addAll("Home", "Work", "Mobile");
        phoneTypeCombo.getSelectionModel().selectFirst();

        postalCodeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.isEmpty()){
                try {
                    ResultSet resultSet = dbConn.getAddress(newValue);
                    if (resultSet.next()){
                        cityField.setText(resultSet.getString("city"));
                        stateField.setText(resultSet.getString("state"));
                    }else{
                        cityField.clear();
                        stateField.clear();
                    }
                } catch (SQLException e) {}
            }
        });
    }

    @FXML
    public void registerButtonOnAction(ActionEvent actionEvent) throws SQLException, IOException {

        if(!EmailValidator.getInstance().isValid(emailField.getText())){
            HelperFunctions.showAlert("Registration Error","Invalid Email" );
            return;
        }

        if(dbConn.userExists(emailField.getText())){
            HelperFunctions.showAlert("Registration Error","User with email " + emailField.getText()+ " already exists" );
            return;
        }

        if(nicknameField.getText().isEmpty()){
            HelperFunctions.showAlert("Registration Error","Empty nickname" );
            return;
        }

        if(passwordField.getText().isEmpty()){
            HelperFunctions.showAlert("Registration Error","Empty password" );
            return;
        }

        if(!dbConn.postalCodeExists(postalCodeField.getText())){
            HelperFunctions.showAlert("Registration Error","Invalid postal code" );
            return;
        }

        if(firstNameField.getText().isEmpty()){
            HelperFunctions.showAlert("Registration Error","Empty First Name" );
            return;
        }

        if(lastNameField.getText().isEmpty()){
            HelperFunctions.showAlert("Registration Error","Empty Last Name" );
            return;
        }

        if(!phoneField.getText().isEmpty()){
            Pattern p = Pattern.compile("[0-9]{3}-[0-9]{3}-[0-9]{4}");//. represents single character
            if(!p.matcher(phoneField.getText()).matches()){
                HelperFunctions.showAlert("Registration Error",
                        "Invalid phone number. Please format is as XXX-XXX-XXXX" );
                return;
            }
            if(dbConn.phoneExists(phoneField.getText())){
                HelperFunctions.showAlert("Registration Error", "Phone number already exists" );
                return;
            }
        }

        boolean registered = dbConn.registerUser(emailField.getText(), nicknameField.getText(), passwordField.getText(),
                postalCodeField.getText(), firstNameField.getText(), lastNameField.getText(),
                phoneField.getText(), phoneTypeCombo.getValue().toString(), sharePhoneCheckBox.isSelected());

        if (registered){
            HelperFunctions.showAlert("Registration", "Registration Successful. Returning to login screen" );
            cancelButtonOnAction(null);
            return;
        }

        return;
    }

    @FXML
    public void cancelButtonOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();

        Stage loginStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        loginStage.setTitle("Login");
        loginStage.setScene(new Scene(root));
        loginStage.show();
    }


}