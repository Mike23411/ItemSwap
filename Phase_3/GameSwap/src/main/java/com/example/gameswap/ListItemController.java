package com.example.gameswap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class ListItemController implements Initializable {
    @FXML
    private ChoiceBox newListGameType;

    @FXML
    private TextField newListTitle;

    @FXML
    private ChoiceBox newListCondition;

    @FXML
    private ChoiceBox newListPlatform;

    @FXML
    private ChoiceBox newListMedia;

    @FXML
    private TextField newListPiece;

    @FXML
    private TextField newListDescription;

    @FXML
    private Button cancelButton;

    @FXML
    private Button newListListButton;

    private String user_email;

    ObservableList<String> gameTypeList = FXCollections.observableArrayList("Board game",
            "Card game", "Video game", "Computer game", "Jigsaw puzzle");
    ObservableList<String> conditionList = FXCollections.observableArrayList("Mint",
            "Like New", "Lightly Used", "Moderately Used", "Heavily Used", "Damaged/Missing parts");
    ObservableList<String> platformList = FXCollections.observableArrayList("Nintendo",
            "PlayStation", "Xbox");
    ObservableList<String> mediaList = FXCollections.observableArrayList("disc",
            "game card", "cartridge");
    ObservableList<String> platformListComputer = FXCollections.observableArrayList("Linux",
            "macOS", "Windows");


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        newListGameType.setItems(gameTypeList);
        newListCondition.setItems(conditionList);
        newListPlatform.setItems(platformList);
        newListMedia.setItems(mediaList);
        newListPiece.setVisible(false);
        newListPlatform.setVisible(false);
        newListMedia.setVisible(false);
    }

    @FXML
    protected void newListSelectGameTypeOnAction(ActionEvent event){
        String gameType = (String) newListGameType.getValue();
        if (gameType == "Jigsaw puzzle"){
            newListPiece.setVisible(true);
        }
        else{
            newListPiece.setVisible(false);
        }

        if (gameType == "Computer game"){
            newListPlatform.setVisible(true);
            newListPlatform.setItems(platformListComputer);
            newListMedia.setVisible(false);
        }
        else if (gameType == "Video game"){
            newListPlatform.setVisible(true);
            newListPlatform.setItems(platformList);
            newListMedia.setVisible(true);
        }
        else{
            newListPlatform.setVisible(false);
            newListMedia.setVisible(false);
        }
    }

    @FXML
    protected void newListListButtonOnAction(ActionEvent event) {
        listNewItemInsertIntoDB();
    }

    private boolean checkInputIsGood(){
        if (newListGameType.getValue() == null){
            return false;
        }
        else if (newListTitle.getText().isEmpty()){
            return false;
        }
        else if (newListCondition.getValue() == null){
            return false;
        }
        String gameType = (String) newListGameType.getValue();
        if (gameType == "Video game"){
            if (newListPlatform.getValue() == null || newListMedia.getValue() == null){
                return false;
            }
        }
        else if (gameType == "Computer game"){
            if (newListPlatform.getValue() == null) {
                return false;
            }
        }
        else if (gameType == "Jigsaw puzzle"){
            try {
                int intValue = Integer.parseInt(newListPiece.getText());
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    private void listNewItemInsertIntoDB(){
        if (!checkInputIsGood()){
            HelperFunctions.showAlert("ListItem", "Bad input" );
            return;
        };
        String gameType = (String) newListGameType.getValue();
        String title = newListTitle.getText();
        String condition = (String) newListCondition.getValue();
        String description = newListDescription.getText();
        if(description.isEmpty()) description = "NULL";
        else description = "'" + description + "'";

        DatabaseController connectNow = new DatabaseController();

        String insertNewItem = "INSERT INTO Item (user_email, `name`, `condition`, `description`) VALUES ('" +
                this.user_email + "','" + title + "','" + condition + "'," + description + ");";
        String insertToSubItem = "";
        if (gameType == "Board game") {
            insertToSubItem = "INSERT INTO `BoardItem` (item_number)\n" +
                    "VALUES ((SELECT MAX(item_number) FROM `Item`));";
        }
        else if (gameType == "Card game"){
            insertToSubItem = "INSERT INTO `CardItem` (item_number)\n" +
                    "VALUES ((SELECT MAX(item_number) FROM `Item`));";
        }
        else if (gameType == "Video game"){
            String platform = (String) newListPlatform.getValue();
            String media = (String) newListMedia.getValue();
            insertToSubItem = "INSERT INTO `VideoItem` (item_number, platform, media)\n" +
                    "VALUES ((SELECT MAX(item_number) FROM `Item`),'" + platform + "','" + media + "');";
        }
        else if (gameType == "Computer game"){
            String platform = (String) newListPlatform.getValue();
            insertToSubItem = "INSERT INTO `ComputerItem` (item_number, platform)\n" +
                    "VALUES ((SELECT MAX(item_number) FROM `Item`),'" + platform + "');";
        }
        else if (gameType == "Jigsaw puzzle"){
            int pieceCounts = Integer.parseInt(newListPiece.getText());
            insertToSubItem = "INSERT INTO `JigsawItem` (item_number, piece_count)\n" +
                    "VALUES ((SELECT MAX(item_number) FROM Item),"+  pieceCounts + ");";
        }
        try{
            connectNow.runUpdate(insertNewItem);
            connectNow.runUpdate(insertToSubItem);
            ResultSet resultSet = connectNow.runQuery("SELECT MAX(item_number) AS item_number FROM `Item` WHERE user_email = '" + user_email +"'");
            resultSet.next();
            HelperFunctions.showAlert("ListItem", "List item Successful." +
                    "\nYour item number is " + resultSet.getString("item_number") +
                    "\nReturning to home screen" );
            cancelButtonOnAction(null);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            e.getCause();
            HelperFunctions.showAlert("Error Listing Item", e.getMessage());
        }
        cleanAllField();
    }

    @FXML
    private void cleanAllField(){
        newListGameType.setValue(null);
        newListCondition.setValue(null);
        newListTitle.clear();
        newListDescription.clear();
        newListPlatform.setValue(null);
        newListMedia.setValue(null);
        newListPiece.clear();

        newListPiece.setVisible(false);
        newListPlatform.setVisible(false);
        newListMedia.setVisible(false);
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
