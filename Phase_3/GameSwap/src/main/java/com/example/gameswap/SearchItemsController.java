package com.example.gameswap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SearchItemsController implements Initializable {

    @FXML private RadioButton searchItemByKeywordRadio;
    @FXML private RadioButton searchItemMyPostalCodeRadio;
    @FXML private RadioButton searchItemWithinRadio;
    @FXML private RadioButton searchItemPostalRadio;
    @FXML private TextField searchItemKeyword;
    @FXML private TextField searchItemMiles;
    @FXML private TextField searchItemPostalCode;
    @FXML private ToggleGroup searchItemToggleGroup;
    @FXML private Button cancelButton;
    @FXML private Button searchButton;

    @FXML private TableView<Item> itemsTable;
    @FXML private TableColumn<Item, String> itemNumberColumn;
    @FXML private TableColumn<Item, String> conditionColumn;
    @FXML private TableColumn<Item, String> nameColumn;
    @FXML private TableColumn<Item, String> gameTypeColumn;
    @FXML private TableColumn<Item, String> descriptionColumn;
    @FXML private TableColumn<Item, String> distanceColumn;

    private ObservableList<Item> itemList = FXCollections.observableArrayList();;

    private String user_email;
    private DatabaseController dbConn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dbConn = new DatabaseController();
        //searchItemByKeywordRadio.setSelected(true);

        itemNumberColumn.setCellValueFactory(new PropertyValueFactory<>("itemNumber"));
        conditionColumn.setCellValueFactory(new PropertyValueFactory<>("condition"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        gameTypeColumn.setCellValueFactory(new PropertyValueFactory<>("gameType"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        distanceColumn.setCellValueFactory(new PropertyValueFactory<>("distance"));
        searchItemKeyword.setVisible(false);
        searchItemMiles.setVisible(false);
        searchItemPostalCode.setVisible(false);
        addButtonToTable();
    }

    private void clearAllInputOnSelectRatio(){
        searchItemKeyword.clear();
        searchItemMiles.clear();
        searchItemPostalCode.clear();
    }
    @FXML
    private void searchItemByKeywordRadioOnAction(){
        searchItemKeyword.setVisible(true);
        searchItemMiles.setVisible(false);
        searchItemPostalCode.setVisible(false);
        clearAllInputOnSelectRatio();
    }

    @FXML
    private void searchItemMyPostalCodeRadioOnAction(){
        searchItemKeyword.setVisible(false);
        searchItemMiles.setVisible(false);
        searchItemPostalCode.setVisible(false);
        clearAllInputOnSelectRatio();
    }
    @FXML
    private void searchItemWithinRadioOnAction(){
        searchItemKeyword.setVisible(false);
        searchItemMiles.setVisible(true);
        searchItemPostalCode.setVisible(false);
        clearAllInputOnSelectRatio();
    }
    @FXML
    private void searchItemPostalRadioOnAction(){
        searchItemKeyword.setVisible(false);
        searchItemMiles.setVisible(false);
        searchItemPostalCode.setVisible(true);
    }

    private void addButtonToTable() {
        TableColumn<Item, Void> colBtn = new TableColumn("");

        Callback<TableColumn<Item, Void>, TableCell<Item, Void>> cellFactory = new Callback<TableColumn<Item, Void>, TableCell<Item, Void>>() {
            @Override
            public TableCell<Item, Void> call(final TableColumn<Item, Void> param) {
                final TableCell<Item, Void> cell = new TableCell<Item, Void>() {

                    private final Button btn = new Button("Details");{
                        btn.setOnAction((ActionEvent event) -> {
                            Item item = getTableView().getItems().get(getIndex());
                            Stage stage = (Stage) btn.getScene().getWindow();
                            //stage.close();

                            FXMLLoader itemLoader = new FXMLLoader(getClass().getResource("viewItem.fxml"));
                            Parent root = null;
                            try {
                                root = (Parent) itemLoader.load();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            ViewItemController controller = itemLoader.<ViewItemController>getController();
                            controller.setUser_email(user_email);
                            try {
                                controller.retrieveItemInfo(item.getItemNumber());
                            } catch (SQLException e) {
                                e.printStackTrace();
                                HelperFunctions.showAlert("Error", "Unable to retrieve item information");
                                return;
                            }

                            Scene scene = new Scene(root);
                            stage.setScene(scene);
                            stage.show();

                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };
        colBtn.setCellFactory(cellFactory);
        itemsTable.getColumns().add(colBtn);
        itemsTable.setItems(itemList);
    }

    @FXML
    protected void searchButtonOnAction() throws SQLException {
        ResultSet resultSet;
        RadioButton selectedRadioButton = (RadioButton) searchItemToggleGroup.getSelectedToggle();
        if (selectedRadioButton == null){
            HelperFunctions.showAlert("Search Error", "Please select one search method");
            return;
        }
        if (selectedRadioButton.getText().equals("By keyword:")){
            if(searchItemKeyword.getText().isEmpty()){
                HelperFunctions.showAlert("Search Error", "Empty search text");
                return;
            }
            resultSet = dbConn.searchItemsByKeyword(this.user_email, searchItemKeyword.getText());
            populateItemList(resultSet);
        }

        else if (selectedRadioButton.getText().equals("In my postal code")){
            resultSet = dbConn.searchItemsInMyPostalCode(this.user_email);
            populateItemList(resultSet);
        }
        else if (selectedRadioButton.getText().equals("Within")){
            if(searchItemMiles.getText().isEmpty()){
                HelperFunctions.showAlert("Search Error", "Empty miles text");
                return;
            }

            try{
                int input_within_miles = Integer.parseInt(searchItemMiles.getText());
                resultSet = dbConn.searchItemsWithinMiles(this.user_email, input_within_miles);
                populateItemList(resultSet);
            }
            catch (Exception e) {
                HelperFunctions.showAlert("Search Error", "Bad miles input");
                e.printStackTrace();
                e.getCause();
                return;
            }
        }

        else if (selectedRadioButton.getText().equals("In postal code:")){
            if(searchItemPostalCode.getText().isEmpty()){
                HelperFunctions.showAlert("Search Error", "Empty target postal code text");
                return;
            }
            try{

                int input_target_code = Integer.parseInt(searchItemPostalCode.getText());
                resultSet = dbConn.searchItemsInTargetPostalCode(this.user_email, input_target_code);
                populateItemList(resultSet);

            }
            catch (Exception e) {
                HelperFunctions.showAlert("Search Error", "Bad postal code input");
                e.printStackTrace();
                e.getCause();
                return;
            }
        }
    }

    private void populateItemList(ResultSet resultSet) throws SQLException {
        itemList.clear();
        int i = 0;
        while (resultSet.next()){
            i ++;
            Item item = new Item(resultSet);
            itemList.add(item);
        }
        if (i == 0) HelperFunctions.showAlert("Search Items", "No games found");

    }

    @FXML
    protected void cancelButtonOnAction() throws IOException {
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
