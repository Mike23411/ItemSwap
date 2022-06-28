package com.example.gameswap;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.beans.property.SimpleStringProperty;

public class MyItemsController implements Initializable {
    @FXML
    private TableView MyItemsCounts;

    @FXML
    private TableView MyItemsDetails;

    @FXML
    private Button newListReturnButton;

    private String user_email;

    private DatabaseController dbConn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        dbConn = new DatabaseController();
    }

    public void autoPopulateMyList(){
        try{
            ResultSet myItemsCountsRS = dbConn.myItemListCounts(this.user_email);
            ResultSet myItemsDetailsRS = dbConn.myItemListWithDetails(this.user_email);
            populateTableViewFromDB(myItemsCountsRS, MyItemsCounts);
            populateTableViewFromDB(myItemsDetailsRS, MyItemsDetails);
            addButtonToTable();
            //populateItemList(myItemsDetailsRS);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            e.getCause();
        }
    }

    private void addButtonToTable() {
        TableColumn<ObservableList, Void> colBtn = new TableColumn("");
        Callback<TableColumn<ObservableList, Void>, TableCell<ObservableList, Void>> cellFactory = new Callback<TableColumn<ObservableList, Void>, TableCell<ObservableList, Void>>() {
            @Override
            public TableCell<ObservableList, Void> call(final TableColumn<ObservableList, Void> param) {
                final TableCell<ObservableList, Void> cell = new TableCell<ObservableList, Void>() {
                    private final Button btn = new Button("Details");{
                        btn.setOnAction((ActionEvent event) -> {

                            ObservableList item =  getTableView().getItems().get(getIndex());
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
                                controller.proposeButton.setVisible(false);
                                controller.retrieveItemInfo((String) item.get(0));
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
        MyItemsDetails.getColumns().add(colBtn);
    }

    private void populateTableViewFromDB(ResultSet rs, TableView tableObj){
        ObservableList data = FXCollections.observableArrayList();
        /**********************************
         * TABLE COLUMN ADDED DYNAMICALLY *
         **********************************/
        try{
            for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnLabel(i+1));
                col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                        if (param.getValue().get(j) == null){
                            return new SimpleStringProperty("None");
                        }
                        else{
                            return new SimpleStringProperty(param.getValue().get(j).toString());
                        }
                    }
                });
                tableObj.getColumns().addAll(col);
            }
            while(rs.next()){
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    String cellValue = rs.getString(i);
                    if(cellValue != null){
                        if (cellValue.length() >= 100){
                            cellValue = cellValue + "...";
                        }
                    }
                    row.add(cellValue);
                }
                data.add(row);
            }
        }
        catch(Exception e){
            e.printStackTrace();
            e.getCause();
        }
        //FINALLY ADDED TO TableView
        tableObj.setItems(data);
    }

    @FXML
    public void MyItemsReturnHomeOnAction() throws IOException {
        // Should we add this type of page switch function into helper functions?
        Stage stage = (Stage) newListReturnButton.getScene().getWindow();
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
}