package com.example.gameswap;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class AcceptRejectSwapsController implements Initializable{

    @FXML private Button backToMainButton;
    @FXML private TableView unacceptedTable;

    private DatabaseController dbConn;
    private String user_email;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dbConn = new DatabaseController();
    }

    public void autoPopulateMyList(){
        try{
            ResultSet unRatedTableRS = dbConn.unAcceptedSwapsResult(this.user_email);
            unacceptedTable.getColumns().clear();
            populateTableViewFromDB(unRatedTableRS, unacceptedTable);
            addButtonToTable();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            e.getCause();
        }
    }

    private void addButtonToTable() {
        TableColumn<ObservableList, Void> colBtn = new TableColumn("accept");
        TableColumn<ObservableList, Void> colBtn2 = new TableColumn("reject");
        Callback<TableColumn<ObservableList, Void>, TableCell<ObservableList, Void>> acceptFactory = new Callback<TableColumn<ObservableList, Void>, TableCell<ObservableList, Void>>() {
            @Override
            public TableCell<ObservableList, Void> call(final TableColumn<ObservableList, Void> param) {
                final TableCell<ObservableList, Void> cell = new TableCell<ObservableList, Void>() {
                    private final Button btn = new Button("Accept");{
                        btn.setOnAction((ActionEvent event) -> {
                            ObservableList item =  getTableView().getItems().get(getIndex());
                            String desiredItemNumber = (String) item.get(1);
                            String proposedItemNumber = (String) item.get(6);
                            try {
                                dbConn.acceptSwap(proposedItemNumber, desiredItemNumber);
                                ResultSet userInfo = dbConn.getUserInfoWithItem(proposedItemNumber);
                                if(userInfo.getString("phone_number") != null & userInfo.getBoolean("share")){
                                    HelperFunctions.showAlert("Swap Accepted","Contact the proposer to swap items!" +
                                            "\nEmail: " + userInfo.getString("email") + "" +
                                            "\n" + userInfo.getString("phone_type") + ": " + userInfo.getString("phone_number"));
                                }else{
                                    HelperFunctions.showAlert("Swap Accepted","Contact the proposer to swap items!" +
                                            "\nEmail: " + userInfo.getString("email") + "" +
                                            "\n No phone number available");
                                }
                                autoPopulateMyList();


                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

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

        Callback<TableColumn<ObservableList, Void>, TableCell<ObservableList, Void>> rejectFactory = new Callback<TableColumn<ObservableList, Void>, TableCell<ObservableList, Void>>() {
            @Override
            public TableCell<ObservableList, Void> call(final TableColumn<ObservableList, Void> param) {
                final TableCell<ObservableList, Void> cell = new TableCell<ObservableList, Void>() {
                    private final Button btn = new Button("Reject");{
                        btn.setOnAction((ActionEvent event) -> {
                            ObservableList item =  getTableView().getItems().get(getIndex());
                            String desiredItemNumber = (String) item.get(1);
                            String proposedItemNumber = (String) item.get(6);
                            try {
                                dbConn.rejectSwap(proposedItemNumber, desiredItemNumber);
                                HelperFunctions.showAlert("Swap Rejected", "Swap Rejected");
                                autoPopulateMyList();


                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

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

        colBtn.setCellFactory(acceptFactory);
        unacceptedTable.getColumns().add(colBtn);
        colBtn2.setCellFactory(rejectFactory);
        unacceptedTable.getColumns().add(colBtn2);
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
                Callback<TableColumn<ObservableList, String>, TableCell<ObservableList, String>> cellFactoryDesired = new Callback<TableColumn<ObservableList, String>, TableCell<ObservableList, String>>() {
                    @Override
                    public TableCell<ObservableList, String> call(final TableColumn<ObservableList, String> param) {
                        final TableCell<ObservableList, String> cell = new TableCell<ObservableList, String>() {
                            private final Hyperlink hyperlink = new Hyperlink("");

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                } else {
                                    setGraphic(hyperlink);
                                    hyperlink.setText(item);
                                    hyperlink.setOnAction((ActionEvent event) -> {
                                        ObservableList row =  getTableView().getItems().get(getIndex());
                                        Stage stage = (Stage) hyperlink.getScene().getWindow();
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
                                        controller.cancelButton.setOnAction((ActionEvent cancelEvent) ->{
                                            Stage stageCancel = (Stage) controller.cancelButton.getScene().getWindow();
                                            stageCancel.close();

                                            Stage acceptRejectSwapsStage = new Stage();
                                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("acceptrejectswaps.fxml"));
                                            Parent rootCancel = null;
                                            try {
                                                rootCancel = (Parent)fxmlLoader.load();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            AcceptRejectSwapsController controllerAccept = fxmlLoader.<AcceptRejectSwapsController>getController();
                                            controllerAccept.setUser_email(user_email);
                                            controllerAccept.autoPopulateMyList();
                                            acceptRejectSwapsStage.setTitle("Accept/Reject Swaps");
                                            acceptRejectSwapsStage.setScene(new Scene(rootCancel));
                                            acceptRejectSwapsStage.show();
                                        });

                                        try {
                                            controller.proposeButton.setVisible(false);
                                            controller.retrieveItemInfo((String) row.get(1));
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                            HelperFunctions.showAlert("Error", "Unable to retrieve item information");
                                            return;
                                        }

                                        Scene scene = new Scene(root);
                                        stage.setTitle("View Item");
                                        stage.setScene(scene);
                                        stage.show();
                                    });
                                }
                            }
                        };
                        return cell;
                    }
                };

                Callback<TableColumn<ObservableList, String>, TableCell<ObservableList, String>> cellFactoryProposed = new Callback<TableColumn<ObservableList, String>, TableCell<ObservableList, String>>() {
                    @Override
                    public TableCell<ObservableList, String> call(final TableColumn<ObservableList, String> param) {
                        final TableCell<ObservableList, String> cell = new TableCell<ObservableList, String>() {
                            private final Hyperlink hyperlink = new Hyperlink("");

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                } else {
                                    setGraphic(hyperlink);
                                    hyperlink.setText(item);
                                    hyperlink.setOnAction((ActionEvent event) -> {
                                        ObservableList row =  getTableView().getItems().get(getIndex());
                                        Stage stage = (Stage) hyperlink.getScene().getWindow();
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
                                        controller.cancelButton.setOnAction((ActionEvent cancelEvent) ->{
                                            Stage stageCancel = (Stage) controller.cancelButton.getScene().getWindow();
                                            stageCancel.close();

                                            Stage acceptRejectSwapsStage = new Stage();
                                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("acceptrejectswaps.fxml"));
                                            Parent rootCancel = null;
                                            try {
                                                rootCancel = (Parent)fxmlLoader.load();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            AcceptRejectSwapsController controllerAccept = fxmlLoader.<AcceptRejectSwapsController>getController();
                                            controllerAccept.setUser_email(user_email);
                                            controllerAccept.autoPopulateMyList();
                                            acceptRejectSwapsStage.setTitle("Accept/Reject Swaps");
                                            acceptRejectSwapsStage.setScene(new Scene(rootCancel));
                                            acceptRejectSwapsStage.show();
                                        });

                                        try {
                                            controller.proposeButton.setVisible(false);
                                            controller.retrieveItemInfo((String) row.get(6));
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                            HelperFunctions.showAlert("Error", "Unable to retrieve item information");
                                            return;
                                        }

                                        Scene scene = new Scene(root);
                                        stage.setTitle("View Item");
                                        stage.setScene(scene);
                                        stage.show();
                                    });
                                }
                            }
                        };
                        return cell;
                    }
                };

                if(j == 2){
                    col.setCellFactory(cellFactoryDesired);
                }else if(j == 7){
                    col.setCellFactory(cellFactoryProposed);
                }
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