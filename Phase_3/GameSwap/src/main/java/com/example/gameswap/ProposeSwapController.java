

package com.example.gameswap;

        import javafx.collections.FXCollections;
        import javafx.collections.ObservableList;
        import javafx.fxml.FXML;
        import javafx.fxml.FXMLLoader;
        import javafx.fxml.Initializable;
        import javafx.scene.Parent;
        import javafx.scene.Scene;
        import javafx.scene.control.*;
        import javafx.scene.control.cell.PropertyValueFactory;
        import javafx.stage.Stage;
        import javafx.event.ActionEvent;
        import javafx.scene.control.Label;

        import java.io.IOException;
        import java.net.URL;
        import java.sql.ResultSet;
        import java.sql.SQLException;
        import java.util.ResourceBundle;

public class ProposeSwapController implements Initializable {

    @FXML private Button confirmationButton;
    @FXML private Button backToMainMenuButton;

    @FXML private Label distanceLabel;
    @FXML private Label itemNameLabel;

    @FXML private TableView<Item> proposedItemTable;
    @FXML private TableColumn<Item, String> itemNumberColumn;
    @FXML private TableColumn<Item, String> nameColumn;
    @FXML private TableColumn<Item, String> gameTypeColumn;
    @FXML private TableColumn<Item, String> conditionColumn;

    private ObservableList<Item> itemList = FXCollections.observableArrayList();;
    private String user_email;
    private String distance;
    private String itemName;
    private String itemNumber;

    DatabaseController dbConn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dbConn = new DatabaseController();

        itemNumberColumn.setCellValueFactory(new PropertyValueFactory<>("itemNumber"));
        conditionColumn.setCellValueFactory(new PropertyValueFactory<>("condition"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        gameTypeColumn.setCellValueFactory(new PropertyValueFactory<>("gameType"));
        conditionColumn.setCellValueFactory(new PropertyValueFactory<>("condition"));
        proposedItemTable.setItems(itemList);
    }

    public void autoPopulateProposeSwap() throws SQLException {

        if(Double.parseDouble(this.distance) > 100){
            distanceLabel.setText("The other user is " + this.distance + " miles away!");
        }else{
            distanceLabel.setVisible(false);
        }

        itemNameLabel.setText(this.itemName);

        ResultSet resultSet =  dbConn.myAvailableItems(this.user_email);
        populateItemList(resultSet);
        //Query is having issues need to check
        //ResultSet proposedItemsCountsRS = dbConn.proposedItemsCounts(this.user_email);
        //populateTableViewFromDB(proposedItemsCountsRS, proposedItemTable);
    }

    private void populateItemList(ResultSet resultSet) throws SQLException {
        itemList.clear();
        int i = 0;
        while (resultSet.next()){
            i ++;
            Item item = new Item(resultSet);
            itemList.add(item);
        }
        if (i == 0) HelperFunctions.showAlert("Propose Items", "No games found");

    }

    @FXML
    protected void confirmationButtonOnAction(ActionEvent event) throws SQLException, IOException {
        Item item = proposedItemTable.getSelectionModel().getSelectedItem();
        if(item == null){
            HelperFunctions.showAlert("Error Proposing Swap", "No Game Selected");
            return;
        }

        if (dbConn.proposeSwap(item.getItemNumber(), this.itemNumber)){
            HelperFunctions.showAlert("Swap","Swap proposed. Returning to main menu");

            Stage stage = (Stage) backToMainMenuButton.getScene().getWindow();
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


    }

    @FXML
    protected void backToMainMenuButtonOnAction(ActionEvent event) throws SQLException, IOException {

        Stage stage = (Stage) backToMainMenuButton.getScene().getWindow();
        stage.close();

        Stage registrationStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("home.fxml"));
        Parent root = fxmlLoader.load();
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

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setItemName(String item_Name) {
        this.itemName = item_Name;
    }

    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }
}