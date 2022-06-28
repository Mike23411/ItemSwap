package com.example.gameswap;

        import javafx.event.ActionEvent;
        import javafx.fxml.FXML;
        import javafx.fxml.FXMLLoader;
        import javafx.fxml.Initializable;
        import javafx.scene.Parent;
        import javafx.scene.Scene;
        import javafx.scene.control.*;

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

        import java.io.*;
        import java.util.*;

public class SwapHistoryController implements Initializable {
    @FXML
    private TableView SwapHistoryCounts;

    @FXML
    private TableView SwapHistoryDetails;

    @FXML
    private Button SearchHistoryReturnButton;

    private String user_email;

    private DatabaseController dbConn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        dbConn = new DatabaseController();
    }

    public void autoPopulateMyList(){
        try{
            ResultSet myItemsCountsRS = dbConn.swapHistorySummaryCounts(this.user_email);
            ResultSet myItemsDetailsRS = dbConn.swapHistoryCounts(this.user_email);
            populateTableViewFromDB(myItemsCountsRS, SwapHistoryCounts);
            populateTableViewFromDB(myItemsDetailsRS, SwapHistoryDetails);
            addButtonToTable();
            hideFirstFourColumns();
            //populateItemList(myItemsDetailsRS);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            e.getCause();
        }
    }

    private void hideFirstFourColumns(){
        List<String> col_to_hide = new ArrayList<>();
        col_to_hide.add("proposed_item_number");
        col_to_hide.add("desired_item_number");
        col_to_hide.add("desirerEmail");
        col_to_hide.add("proposerEmail");
        for (Object col : SwapHistoryDetails.getColumns()) {
            TableColumn colCasted = ((TableColumn)col);
            if(col_to_hide.contains(colCasted.getText())){
                colCasted.setVisible(false);
            }
        }
    }

    private void addButtonToTable() {
        TableColumn<ObservableList, Void> colBtn = new TableColumn("details");
        Callback<TableColumn<ObservableList, Void>, TableCell<ObservableList, Void>> cellFactory = new Callback<TableColumn<ObservableList, Void>, TableCell<ObservableList, Void>>() {
            @Override
            public TableCell<ObservableList, Void> call(final TableColumn<ObservableList, Void> param) {
                final TableCell<ObservableList, Void> cell = new TableCell<ObservableList, Void>() {
                    private final Button btn = new Button("Details");{
                        btn.setOnAction((ActionEvent event) -> {
                            ObservableList item =  getTableView().getItems().get(getIndex());
                            Stage stage = (Stage) btn.getScene().getWindow();
                            //stage.close();

                            FXMLLoader itemLoader = new FXMLLoader(getClass().getResource("swapdetails.fxml"));
                            Parent root = null;
                            try {
                                root = (Parent) itemLoader.load();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            SwapDetailsController controller = itemLoader.<SwapDetailsController>getController();
                            controller.setMy_email(user_email);
                            controller.setMy_role((String) item.get(7));
                            controller.setProposed_item_number((String) item.get(0));
                            controller.setDesired_item_number((String) item.get(1));
                            if ((String) item.get(7) == "Proposer"){
                                controller.setOther_user_email((String) item.get(2));
                            }
                            else{
                                controller.setOther_user_email((String) item.get(3));
                            }
                            controller.autoPopulateSwapDetails();
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
        SwapHistoryDetails.getColumns().add(colBtn);
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
                            if(j==11){
                                if(!param.getValue().get(6).toString().equals("Accepted")){
                                    return new SimpleStringProperty("-");
                                }
                            }
                            return new SimpleStringProperty("None");
                        }
                        else{
                            return new SimpleStringProperty(param.getValue().get(j).toString());
                        }
                    }
                });

                Callback<TableColumn<ObservableList, String>, TableCell<ObservableList, String>> cellFactory = new Callback<TableColumn<ObservableList, String>, TableCell<ObservableList, String>>() {
                    @Override
                    public TableCell<ObservableList, String> call(final TableColumn<ObservableList, String> param) {
                        final TableCell<ObservableList, String> cell = new TableCell<ObservableList, String>() {
                            String ratings[] =
                                    { "1", "2", "3",
                                            "4", "5" };
                            ComboBox combo_box = new ComboBox(FXCollections.observableArrayList(ratings));
                            {
                                combo_box.setOnAction(e -> {
                                    combo_box.setVisibleRowCount(combo_box.getVisibleRowCount());
                                    ObservableList comboItem =  getTableView().getItems().get(getIndex());
                                    if (comboItem.get(7).equals("Counterparty"))
                                    {
                                        dbConn.rateProposedSwaps(combo_box.getValue().toString(), comboItem.get(0).toString(), comboItem.get(1).toString());
                                        Stage stage = (Stage) SwapHistoryDetails.getScene().getWindow();
                                        stage.close();

                                        Stage rateSwapsStage = new Stage();
                                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("swaphistory.fxml"));
                                        Parent root = null;
                                        try {
                                            root = (Parent)fxmlLoader.load();
                                        } catch (IOException ex) {
                                            ex.printStackTrace();
                                        }
                                        SwapHistoryController controller = fxmlLoader.<SwapHistoryController>getController();
                                        controller.setUser_email(user_email);
                                        controller.autoPopulateMyList();
                                        rateSwapsStage.setTitle("Rate Swaps");
                                        rateSwapsStage.setScene(new Scene(root));
                                        rateSwapsStage.show();
                                    }
                                    else if (comboItem.get(7).equals("Proposer") )
                                    {
                                        dbConn.rateCounterpartySwaps(combo_box.getValue().toString(), comboItem.get(0).toString(), comboItem.get(1).toString());
                                        Stage stage = (Stage) SwapHistoryDetails.getScene().getWindow();
                                        stage.close();

                                        Stage rateSwapsStage = new Stage();
                                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("swaphistory.fxml"));
                                        Parent root = null;
                                        try {
                                            root = (Parent)fxmlLoader.load();
                                        } catch (IOException ex) {
                                            ex.printStackTrace();
                                        }
                                        SwapHistoryController controller = fxmlLoader.<SwapHistoryController>getController();
                                        controller.setUser_email(user_email);
                                        controller.autoPopulateMyList();
                                        rateSwapsStage.setTitle("Rate Swaps");
                                        rateSwapsStage.setScene(new Scene(root));
                                        rateSwapsStage.show();
                                    }
                                });}


                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if(empty){
                                    setGraphic(null);
                                }else{
                                    if(!item.equals("None")){
                                        combo_box.setOnAction(null);
                                        combo_box.setValue(item);
                                        combo_box.setDisable(true);
                                    }
                                    setGraphic(combo_box);
                                }
                            }
                        };
                        return cell;
                    }
                };
                if(i == 11){
                    col.setCellFactory(cellFactory);
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
    public void SearchHistoryHomeOnAction() throws IOException {
        // Should we add this type of page switch function into helper functions?
        Stage stage = (Stage) SearchHistoryReturnButton.getScene().getWindow();
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
