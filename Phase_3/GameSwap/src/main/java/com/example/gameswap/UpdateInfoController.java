package com.example.gameswap;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.commons.validator.routines.EmailValidator;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class UpdateInfoController implements Initializable{

    @FXML private TextField emailField;
    @FXML private TextField nicknameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField postalCodeField;
    @FXML private TextField cityField;
    @FXML private TextField stateField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField phoneField;
    @FXML private ComboBox phoneTypeCombo;
    @FXML private CheckBox sharePhoneCheckBox;

    @FXML private Button cancelButton;
    @FXML private Button updateButton;

    private String user_email;
    private ResultSet userInfo;

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

    public void retrieveUserInfo() throws IOException {
        try {
            this.userInfo =  dbConn.getUserInfo(this.user_email);
            emailField.setText(this.user_email);
            nicknameField.setText(userInfo.getString("nickname"));
            postalCodeField.setText(userInfo.getString("postal_code"));
            firstNameField.setText(userInfo.getString("first_name"));
            lastNameField.setText(userInfo.getString("last_name"));
            if(userInfo.getString("phone_number") != null){
                phoneField.setText(userInfo.getString("phone_number"));
                phoneTypeCombo.setValue(userInfo.getString("phone_type"));
                sharePhoneCheckBox.setSelected(userInfo.getBoolean("share"));
            }


        } catch (SQLException e) {
            e.printStackTrace();
            HelperFunctions.showAlert("Error","Unable to retrieve user information");
            HelperFunctions.logout((Stage) cancelButton.getScene().getWindow());
        }
    }

    @FXML
    public void updateButtonOnAction(ActionEvent actionEvent) throws SQLException, IOException {

        if(nicknameField.getText().isEmpty()){
            HelperFunctions.showAlert("Update Info Error","Empty nickname" );
            return;
        }

        if(passwordField.getText().isEmpty()){
            HelperFunctions.showAlert("Update Info Error","Empty password" );
            return;
        }

        if(!dbConn.postalCodeExists(postalCodeField.getText())){
            HelperFunctions.showAlert("Update Info Error","Invalid postal code" );
            return;
        }

        if(firstNameField.getText().isEmpty()){
            HelperFunctions.showAlert("Update Info Error","Empty First Name" );
            return;
        }

        if(lastNameField.getText().isEmpty()){
            HelperFunctions.showAlert("Update Info Error","Empty Last Name" );
            return;
        }

        if(!phoneField.getText().isEmpty()){
            Pattern p = Pattern.compile("[0-9]{3}-[0-9]{3}-[0-9]{4}");//. represents single character
            if(!p.matcher(phoneField.getText()).matches()){
                HelperFunctions.showAlert("Update Info Error",
                        "Invalid phone number. Please format is as XXX-XXX-XXXX" );
                return;
            }
            if(dbConn.phoneExistsOtherUser(phoneField.getText(), this.user_email)){
                HelperFunctions.showAlert("Update Info Error", "Phone number already exists" );
                return;
            }
        }

        boolean updated = dbConn.updateUser(emailField.getText(), nicknameField.getText(), passwordField.getText(),
                postalCodeField.getText(), firstNameField.getText(), lastNameField.getText(),
                phoneField.getText(), phoneTypeCombo.getValue().toString(), sharePhoneCheckBox.isSelected());

        if (updated){
            HelperFunctions.showAlert("Update Info", "Update Successful. Returning to Home Screen" );
            cancelButtonOnAction(null);
            return;
        }

        return;
    }

    @FXML
    public void cancelButtonOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();

        Stage registrationStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("home.fxml"));
        Parent root = (Parent)fxmlLoader.load();
        HomeController controller = fxmlLoader.<HomeController>getController();
        controller.setUser_email(this.user_email);
        controller.autoPopulateMyList();
        registrationStage.setTitle("Home");
        registrationStage.setScene(new Scene(root));
        registrationStage.show();
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }
}