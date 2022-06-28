package com.example.gameswap;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.beans.property.SimpleStringProperty;


public class SwapDetailsController implements Initializable{

    @FXML
    private Button SwapDetailsReturnHomeButton, SwapDetailReturnSwapHistoryButton, swapDetailsRateSwapButton;

    @FXML
    private Label SwapProposedDate, SwapAccepRejectDate, SwapStatus, SwapMyRole, SwapRating,
            SwapProposedItemNumber, SwapProposedTitle, SwapProposedGameType, SwapProposedCondition, SwapProposedDescription,
            SwapOtherUserNickname, SwapOtherDistance, SwapOtherUserName, SwapOtherEmail, SwapOtherPhone,
            SwapDesiredItemNumber, SwapOtherTitle, SwapOtherGameType, SwapOtherCondition;

    @FXML
    private TextField swapDetailsRateInput;

    private String my_email;
    private String other_user_email;
    private String my_role;
    private String proposed_item_number;
    private String desired_item_number;
    private DatabaseController dbConn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        dbConn = new DatabaseController();
    }

    public void autoPopulateSwapDetails(){
        autoPopulateSwapPart();
        autoPopulateOtherUserPart();
        autoPopulateMyPart();
    }

    public void autoPopulateSwapPart(){
        try{
            ResultSet swapDetailsSwapPart = dbConn.dbQuerySwapPart(this.proposed_item_number, this.desired_item_number);
            SwapProposedDate.setText(swapDetailsSwapPart.getString("proposed_date"));
            SwapAccepRejectDate.setText(swapDetailsSwapPart.getString("accept_reject_date"));
            SwapStatus.setText(swapDetailsSwapPart.getString("swap_status"));
            SwapMyRole.setText(this.my_role);
            if (this.my_role == "Proposer"){
                SwapRating.setText(swapDetailsSwapPart.getString("counterparty_rating"));
            }
            else{
                SwapRating.setText(swapDetailsSwapPart.getString("proposer_rating"));
            }
            if ((SwapRating.getText() == null && !SwapStatus.getText().isEmpty()) && SwapStatus.getText().equals("Accepted")){
                ///// To complete here. When there is no rating from me, enable a field to rate it.
                swapDetailsRateSwapButton.setVisible(true);
                swapDetailsRateInput.setVisible(true);
            }
            else{
                swapDetailsRateSwapButton.setVisible(false);
                swapDetailsRateInput.setVisible(false);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            e.getCause();
        }
    }
    @FXML
    private void swapDetailsRateSwapOnAction(){
        try{
            String my_input_rate = swapDetailsRateInput.getText();
            if(Integer.parseInt(my_input_rate) <= 5 & Integer.parseInt(my_input_rate) >= 0 ){
                if (this.my_role == "Proposer"){
                    dbConn.runUpdate("UPDATE `SWAP` SET counterparty_rating = " + my_input_rate + " WHERE  proposed_item_number = "
                            + this.proposed_item_number);
                }
                else{
                    dbConn.runUpdate("UPDATE `SWAP` SET proposer_rating = " + my_input_rate + " WHERE  desired_item_number = "
                            + this.desired_item_number);
                }
                SwapRating.setText(my_input_rate);
                swapDetailsRateSwapButton.setVisible(false);
                swapDetailsRateInput.setVisible(false);
            }else{
                HelperFunctions.showAlert("Rate Swap Error", "Bad input, try again (rating should be 0-5)");
            }

        }
        catch(Exception e)
        {
            HelperFunctions.showAlert("Rate Swap", "Bad input, try again (rating should be 0-5)" );
            e.printStackTrace();
            e.getCause();
        }
    }

    public void autoPopulateOtherUserPart(){
        try{
            if (this.my_role == "Proposer"){
                ResultSet rs = dbConn.getItemDetails(this.my_email, (String) this.desired_item_number);
                Item item_other_user = new Item(rs);
                SwapOtherUserNickname.setText(item_other_user.getUserNickName());
                SwapOtherDistance.setText(item_other_user.getDistance());
                //SwapOtherUserName.setText(item_other_user.getName());
                SwapOtherEmail.setText(item_other_user.getUserEmail());
                //SwapOtherPhone.setText(item_other_user.get);
                SwapDesiredItemNumber.setText(item_other_user.getItemNumber());
                SwapOtherTitle.setText(item_other_user.getName());
                SwapOtherGameType.setText(item_other_user.getGameType());
                SwapOtherCondition.setText(item_other_user.getCondition());
            }
            else{
                ResultSet rs = dbConn.getItemDetails(this.my_email, (String) this.proposed_item_number);
                Item item_other_user = new Item(rs);
                SwapOtherUserNickname.setText(item_other_user.getUserNickName());
                SwapOtherDistance.setText(item_other_user.getDistance());
                //SwapOtherUserName.setText(item_other_user.getName());
                SwapOtherEmail.setText(item_other_user.getUserEmail());
                SwapProposedItemNumber.setText(item_other_user.getItemNumber());
                SwapProposedTitle.setText(item_other_user.getName());
                SwapProposedGameType.setText(item_other_user.getGameType());
                SwapProposedCondition.setText(item_other_user.getCondition());
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            e.getCause();
        }
    }

    public void autoPopulateMyPart(){
        try{
            if (this.my_role == "Proposer"){
                ResultSet rs = dbConn.getItemDetails(this.my_email, (String) this.proposed_item_number);
                Item item_other_user = new Item(rs);
                SwapProposedItemNumber.setText(item_other_user.getItemNumber());
                SwapProposedTitle.setText(item_other_user.getName());
                SwapProposedGameType.setText(item_other_user.getGameType());
                SwapProposedCondition.setText(item_other_user.getCondition());
                SwapProposedDescription.setText(item_other_user.getDescription());
            }
            else{
                ResultSet rs = dbConn.getItemDetails(this.my_email, (String) this.desired_item_number);
                Item item_other_user = new Item(rs);
                SwapDesiredItemNumber.setText(item_other_user.getItemNumber());
                SwapOtherTitle.setText(item_other_user.getName());
                SwapOtherGameType.setText(item_other_user.getGameType());
                SwapOtherCondition.setText(item_other_user.getCondition());
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            e.getCause();
        }
    }

    @FXML
    public void SwapDetailReturnHomeOnAction() throws IOException {
        // Should we add this type of page switch function into helper functions?
        Stage stage = (Stage) SwapDetailsReturnHomeButton.getScene().getWindow();
        stage.close();

        Stage registrationStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("home.fxml"));
        Parent root = fxmlLoader.load();
        HomeController controller = fxmlLoader.<HomeController>getController();
        controller.setUser_email(this.my_email);
        controller.autoPopulateMyList();
        registrationStage.setTitle("Home");
        registrationStage.setScene(new Scene(root));
        registrationStage.show();
    }

    @FXML
    public void SwapDetailReturnSwapHistoryOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) SwapDetailReturnSwapHistoryButton.getScene().getWindow();
        stage.close();

        Stage swapHistoryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("swaphistory.fxml"));
        Parent root = (Parent)fxmlLoader.load();
        SwapHistoryController controller = fxmlLoader.<SwapHistoryController>getController();
        controller.setUser_email(this.my_email);
        controller.autoPopulateMyList();
        swapHistoryStage.setTitle("Swap History");
        swapHistoryStage.setScene(new Scene(root));
        swapHistoryStage.show();
    }

    public void setMy_email(String my_email) {
        this.my_email = my_email;
    }
    public void setOther_user_email(String other_user_email) {
        this.other_user_email = other_user_email;
    }
    public void setMy_role(String my_role) {
        this.my_role = my_role;
    }
    public void setProposed_item_number(String proposed_item_number) {
        this.proposed_item_number = proposed_item_number;
    }
    public void setDesired_item_number(String desired_item_number) {
        this.desired_item_number = desired_item_number;
    }
}
