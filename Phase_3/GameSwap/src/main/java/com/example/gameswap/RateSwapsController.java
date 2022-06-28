package com.example.gameswap;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
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

public class RateSwapsController implements Initializable {

    @FXML
    private Button backToMainButton;
    @FXML private Label loginMessageLabel;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    DatabaseController dbConn;

    private String user_email;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dbConn = new DatabaseController();
    }

    @FXML
    private TableView unRatedTable;


    public void autoPopulateMyList(){
        try{
            ResultSet unRatedTableRS = dbConn.unRatedSwapsResult(this.user_email);

            populateTableViewFromDB(unRatedTableRS, unRatedTable);
            addRatingsToTable();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            e.getCause();
        }
    }

    private void addRatingsToTable() {
        TableColumn<ObservableList, Void> colBtn = new TableColumn("");
        Callback<TableColumn<ObservableList, Void>, TableCell<ObservableList, Void>> cellFactory = new Callback<TableColumn<ObservableList, Void>, TableCell<ObservableList, Void>>() {
            @Override
            public TableCell<ObservableList, Void> call(final TableColumn<ObservableList, Void> param) {
                final TableCell<ObservableList, Void> cell = new TableCell<ObservableList, Void>() {
                    String ratings[] =
                            { "1", "2", "3",
                                    "4", "5" };
                    ComboBox combo_box =
                            new ComboBox(FXCollections
                                    .observableArrayList(ratings));{
                        combo_box.setOnAction(e -> {
                            combo_box.setVisibleRowCount(combo_box.getVisibleRowCount());
                            ObservableList item =  getTableView().getItems().get(getIndex());
                            if (item.get(1).equals("Counterparty"))
                            {
                                dbConn.rateProposedSwaps(combo_box.getValue().toString(), item.get(2).toString(), item.get(4).toString());
                                Stage stage = (Stage) unRatedTable.getScene().getWindow();
                                stage.close();

                                Stage rateSwapsStage = new Stage();
                                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("rateSwaps.fxml"));
                                Parent root = null;
                                try {
                                    root = (Parent)fxmlLoader.load();
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                                RateSwapsController controller = fxmlLoader.<RateSwapsController>getController();
                                controller.setUser_email(user_email);
                                controller.autoPopulateMyList();
                                rateSwapsStage.setTitle("Rate Swaps");
                                rateSwapsStage.setScene(new Scene(root));
                                rateSwapsStage.show();
                            }
                            else if (item.get(1).equals("Proposer") )
                            {
                                dbConn.rateCounterpartySwaps(combo_box.getValue().toString(), item.get(2).toString(), item.get(4).toString());
                                Stage stage = (Stage) unRatedTable.getScene().getWindow();
                                stage.close();

                                Stage rateSwapsStage = new Stage();
                                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("rateSwaps.fxml"));
                                Parent root = null;
                                try {
                                    root = (Parent)fxmlLoader.load();
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                                RateSwapsController controller = fxmlLoader.<RateSwapsController>getController();
                                controller.setUser_email(user_email);
                                controller.autoPopulateMyList();
                                rateSwapsStage.setTitle("Rate Swaps");
                                rateSwapsStage.setScene(new Scene(root));
                                rateSwapsStage.show();
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(combo_box);
                        }
                    }
                };
                return cell;
            }
        };
        colBtn.setCellFactory(cellFactory);
        unRatedTable.getColumns().add(colBtn);
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
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>, ObservableValue<String>>(){
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
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
                    row.add(rs.getString(i));
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
    public void backToMainButtonOnAction() throws IOException {
        // Should we add this type of page switch function into helper functions?
        Stage stage = (Stage) backToMainButton.getScene().getWindow();
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

