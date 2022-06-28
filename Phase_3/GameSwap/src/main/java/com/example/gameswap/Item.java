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
import java.util.ResourceBundle;

public class Item{

    private String itemNumber;
    private String name;
    private String gameType;
    private String platform;
    private String media;
    private String pieceCount;
    private String condition;
    private String description;

    private String userNickName;
    private String userLocation;
    private String userRating;
    private String userEmail;
    private String distance;

    public Item() {

    }

    public Item(ResultSet resultSet) throws SQLException {
        this.itemNumber = resultSet.getString("item_number");
        this.condition = resultSet.getString("condition");
        this.name = resultSet.getString("name");
        this.gameType = resultSet.getString("game_type");
        this.description = resultSet.getString("description");

        // I feel distance should not be an attribute of Item. R: Just for convenience, it might be null in some cases
        if(isThere(resultSet,"user_email"))  this.userEmail = resultSet.getString("user_email");
        if(isThere(resultSet,"distance"))  this.distance = resultSet.getString("distance");
        if(isThere(resultSet,"nickname")) this.userNickName = resultSet.getString("nickname");
        if(isThere(resultSet,"location")) this.userLocation = resultSet.getString("location");
        if(isThere(resultSet,"rating")) this.userRating = resultSet.getString("rating");

        if(isThere(resultSet,"piece_count")) this.pieceCount = resultSet.getString("piece_count");
        if(isThere(resultSet,"platform")) this.platform = resultSet.getString("platform");
        if(isThere(resultSet,"media")) this.media = resultSet.getString("media");

    }

    private boolean isThere(ResultSet rs, String column){
        // shame on this func
        try{
            rs.findColumn(column);
            return true;
        } catch (SQLException e){
        }
        return false;
    }

    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    public String getUserRating() {
        return userRating;
    }

    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    public String getPieceCount() {
        return pieceCount;
    }

    public void setPieceCount(String pieceCount) {
        this.pieceCount = pieceCount;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
