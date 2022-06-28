package com.example.gameswap;

import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class DatabaseController {

    public Connection databaseLink;


    public DatabaseController() {
        this.databaseLink = getConnection();

    }

    public Connection getConnection() {
        String databaseName = "Gameswap";
        String databaseUser = "root";
        String databasePassword = "secret";

        String url = "jdbc:mysql://localhost:3306/GameSwap?serverTimezone=UTC";


        try {
            Class.forName("com.mysql.jdbc.Driver");
            databaseLink = DriverManager.getConnection(url, databaseUser, databasePassword);
        }
        catch(Exception e) {
            e.printStackTrace();
            e.getCause();
        }

        return databaseLink;
    }


    public ResultSet runQuery(String query) throws SQLException {
        Statement statement = this.databaseLink.createStatement();
        ResultSet queryResult = statement.executeQuery(query);
        return queryResult;
    }

    public int runUpdate(String query) throws SQLException {
        Statement statement = this.databaseLink.createStatement();
        int queryResult = statement.executeUpdate(query);
        return queryResult;
    }


    // USER RELATED QUERIES

    public String login(String user, String password) throws SQLException {

        String userQuery = "SELECT email FROM `User` u LEFT JOIN `Phone` p ON u.email = p.user_email " +
                "WHERE email = '"+user+"' OR phone_number = '"+user+"'";
        if(runQuery(userQuery).next()){
            String passQuery = "SELECT email FROM `User` u LEFT JOIN `Phone` p ON u.email = p.user_email " +
                    "WHERE (email = '"+user+"' OR phone_number = '"+user+"') AND " +
                    "password = '" + password +"'";
            ResultSet resultSet = runQuery(passQuery);
            if(resultSet.next()){
                return resultSet.getString("email");
            }else{
                HelperFunctions.showAlert("Login Error","Wrong password");
            }
        }else{
            HelperFunctions.showAlert("Login Error","Email or phone number is not registered");
        }

        return "";
    }

    public boolean userExists(String email) throws SQLException {

        String query = "SELECT email FROM `User` WHERE email = '" + email + "'";
        ResultSet resultSet = runQuery(query);
        return resultSet.next() ? true: false;
    }

    public boolean postalCodeExists(String postalCode) throws SQLException {
        String query = "SELECT postal_code FROM `Address` WHERE postal_code = '" + postalCode + "'";
        ResultSet resultSet = runQuery(query);
        return resultSet.next() ? true: false;
    }

    public boolean phoneExists(String phoneNumber) throws SQLException {
        String query = "SELECT phone_number FROM `Phone` WHERE phone_number = '" + phoneNumber + "'";
        ResultSet resultSet = runQuery(query);
        return resultSet.next() ? true: false;
    }

    public boolean phoneExistsOtherUser(String phoneNumber, String email) throws SQLException {
        String query = "SELECT phone_number FROM `Phone` " +
                "WHERE phone_number = '" + phoneNumber + "' AND user_email <> '" + email + "'";
        ResultSet resultSet = runQuery(query);
        return resultSet.next() ? true: false;
    }

    public ResultSet getAddress(String postalCode) throws SQLException {
        String query = "SELECT city, state, postal_code, latitude, longitude " +
                "FROM `Address` WHERE postal_code = '" + postalCode + "'";
        ResultSet resultSet = runQuery(query);
        return resultSet;
    }

    public boolean registerUser(String email, String nickname, String password,
                                String postalCode, String firstName, String lastName,
                                String phoneNumber, String phoneType, boolean sharePhone){


        try {
            runUpdate("INSERT INTO `User` (email, nickname, password, postal_code, first_name, last_name) " +
                    "VALUES('"+email+"','"+nickname+"','"+password+"','"+postalCode+"','"+firstName+"'," + "'"+lastName+"');");
            if(!phoneNumber.isEmpty()){
                runUpdate("INSERT INTO `Phone` (phone_number, user_email, phone_type, share) VALUES('" +
                        phoneNumber +"','"+ email +"','"+ phoneType + "'," +(sharePhone ? "1":"0") +");");
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            HelperFunctions.showAlert("Registration Error",e.getMessage());
        }

        return false;

    }

    public boolean updateUser(String email, String nickname, String password,
                                String postalCode, String firstName, String lastName,
                                String phoneNumber, String phoneType, boolean sharePhone){


        try {
            runUpdate("UPDATE `User` SET password='"+password+"', nickname='"+nickname+"', " +
                    "postal_code = '"+postalCode+"', first_name='" + firstName +"'," +
                    "last_name='"+lastName+"' WHERE email = '"+email+"'");

            if(phoneNumber.isEmpty()){
                runUpdate("DELETE FROM `Phone` WHERE user_email = '"+email+"'");
            }else{
                runUpdate("DELETE FROM `Phone` WHERE user_email = '"+email+"'");
                runUpdate("INSERT INTO `Phone` (phone_number, user_email, phone_type, share) VALUES('" +
                        phoneNumber +"','"+ email +"','"+ phoneType + "'," +(sharePhone ? "1":"0") +");");
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            HelperFunctions.showAlert("Update Info Error",e.getMessage());
        }

        return false;

    }


    public ResultSet getUserInfo(String email) throws SQLException {
        String query = "SELECT  \n" +
                "\tu.email, \n" +
                "    nickname, \n" +
                "    postal_code, \n" +
                "    first_name, \n" +
                "    last_name,\n" +
                "\tphone_number, \n" +
                "    phone_type, \n" +
                "    share,\n" +
                "    ROUND(AVG(rating),2) as rating\n" +
                "FROM `User` u LEFT JOIN `Phone` p ON p.user_email = u.email\n" +
                "LEFT JOIN (\n" +
                "\tSELECT email, counterparty_rating as rating\n" +
                "\tFROM  `User` u\n" +
                "\tLEFT JOIN `Item` i ON i.user_email = u.email\n" +
                "\tLEFT JOIN `Swap` s ON s.desired_item_number = i.item_number\n" +
                "\tWHERE counterparty_rating IS NOT NULL\n" +
                "\tUNION ALL\n" +
                "\tSELECT email, proposer_rating as rating\n" +
                "\tFROM  `User` u\n" +
                "\tLEFT JOIN `Item` i ON i.user_email = u.email\n" +
                "\tLEFT JOIN `Swap` s ON s.proposed_item_number = i.item_number\n" +
                "\tWHERE proposer_rating IS NOT NULL\n" +
                ") ur ON  ur.email = u.email\n" +
                "WHERE u.email = '"+email+"'\n" +
                "GROUP BY u.email, nickname, postal_code, first_name, last_name, phone_number, phone_type, share";
        ResultSet resultSet = runQuery(query);
        if(resultSet.next()){
            return resultSet;
        }else{
            throw new SQLException("Unable to retrieve user information");
        }
    }

    public ResultSet getUserInfoWithItem(String itemNumber) throws SQLException {
        String query = "SELECT \n" +
                "\temail,\n" +
                "    phone_number,\n" +
                "    phone_type,\n" +
                "    share\n" +
                "FROM `User`\n" +
                "LEFT JOIN `Phone` p ON email = p.user_email\n" +
                "INNER JOIN `Item`  i ON email = i.user_email\n" +
                "WHERE item_number = " + itemNumber;
        ResultSet resultSet = runQuery(query);
        if(resultSet.next()){
            return resultSet;
        }else{
            throw new SQLException("Unable to retrieve user information");
        }
    }

    // ITEM RELATED QUERIES

    public ResultSet searchItemsByKeyword(String user_email, String keyword) throws SQLException {
        String query = "SELECT\n" +
                "\ti.item_number,\n" +
                "\ti.condition,\n" +
                "\ti.name,\n" +
                "\tit.game_type,\n" +
                "\tLEFT(description, 100) AS description,\n" +
                "\tROUND(3958.75 * (2 * ATAN2( \n" +
                "\t\tSQRT(POWER( SIN ((RADIANS(CounterpartyAddress.latitude)-RADIANS(ProposerAddress.latitude))/2) ,2) * POWER( COS(RADIANS(CounterpartyAddress.latitude)) ,2) * POWER( SIN ((RADIANS(CounterpartyAddress.longitude)-RADIANS(ProposerAddress.longitude))/2) ,2)),\n" +
                "\t\tSQRT( 1 -(POWER( SIN ((RADIANS(CounterpartyAddress.latitude)-RADIANS(ProposerAddress.latitude))/2) ,2) * POWER( COS(RADIANS(CounterpartyAddress.latitude)) ,2) * POWER( SIN ((RADIANS(CounterpartyAddress.longitude)-RADIANS(ProposerAddress.longitude))/2) ,2))) \n" +
                "\t\t)\n" +
                "\t),1) AS distance\n" +
                "FROM `Item` i\n" +
                "INNER JOIN(\n" +
                "\tSELECT item_number, 'Board game' AS game_type FROM `BoardItem` UNION\n" +
                "\tSELECT item_number, 'Card game' AS game_type FROM `CardItem` UNION\n" +
                "\tSELECT item_number, 'Video game' AS game_type FROM `VideoItem` UNION\n" +
                "\tSELECT item_number, 'Computer game' AS game_type FROM `ComputerItem` UNION\n" +
                "\tSELECT item_number, 'Jigsaw puzzle' AS game_type FROM `JigsawItem`\n" +
                ") it ON it.item_number = i.item_number\n" +
                "INNER JOIN `User` u ON u.email = i.user_email\n" +
                "INNER JOIN `Address` AS ProposerAddress ON u.postal_code = ProposerAddress.postal_code\n" +
                "LEFT JOIN `Swap` s ON s.proposed_item_number = i.item_number OR s.desired_item_number = i.item_number\n" +
                "CROSS JOIN (\n" +
                "\tSELECT latitude, longitude FROM `Address`\n" +
                "\tINNER JOIN `User` ON `User`.postal_code = `Address`.postal_code AND email = '"+user_email+"'\n" +
                ") AS CounterpartyAddress\n" +
                "WHERE i.user_email != '"+user_email+"' AND (i.name LIKE '%"+keyword+"%' OR i.description LIKE '%"+keyword+"%')\n" +
                "GROUP BY \n" +
                "\ti.item_number, i.condition, i.name, it.game_type, i.description, CounterpartyAddress.latitude,\n" +
                "\tCounterpartyAddress.longitude, ProposerAddress.latitude, ProposerAddress.longitude\n" +
                "HAVING (SUM(s.status) = 0 OR COUNT(s.status) = 0)\n" +
                "\n";
        ResultSet resultSet = runQuery(query);
        return resultSet;
    }

    public ResultSet searchItemsInMyPostalCode(String user_email) throws SQLException {
        String query = "SELECT tb.item_number, tb.`condition`, tb.`name`, tb.game_type, LEFT(tb.description, 100) AS `description`," +
        "ROUND(3958.75 * ("+
                "2 * ATAN2( SQRT(" +
                                "POWER( SIN ((tb.lat2-tb.lat1)/2) ,2) * POWER( COS(tb.lat2) ,2) * POWER( SIN ((tb.lon2-tb.lon1)/2) ,2)" +
                        ")," +
                        "SQRT( 1 -" +
                                "(POWER( SIN ((tb.lat2-tb.lat1)/2) ,2) * POWER( COS(tb.lat2) ,2) * POWER( SIN ((tb.lon2-tb.lon1)/2) ,2))" +
                        ") )" +
        "),1) AS distance " +
        "FROM ( SELECT " +
                "RADIANS(ProposerAddress.latitude) AS lat1, RADIANS(ProposerAddress.longitude) AS lon1," +
                "RADIANS(CounterpartyAddress.latitude) AS lat2, RADIANS(CounterpartyAddress.longitude) AS lon2," +
                "i.item_number, i.`condition`, i.`name`, it.game_type, LEFT(`description`, 100) AS `description` " +
                "FROM `Item` i " +
                "INNER JOIN(" +
                        "SELECT item_number, 'Board game' AS game_type FROM `BoardItem` UNION ALL " +
                        "SELECT item_number, 'Card game' AS game_type FROM `CardItem` UNION ALL " +
                        "SELECT item_number, 'Video game' AS game_type FROM `VideoItem` UNION ALL " +
                        "SELECT item_number, 'Computer game' AS game_type FROM `ComputerItem` UNION ALL " +
                        "SELECT item_number, 'Jigsaw puzzle' AS game_type FROM `JigsawItem`" +
                ") it ON it.item_number = i.item_number " +
                "INNER JOIN `User` u ON i.user_email = u.email " +
                "INNER JOIN `Address` AS ProposerAddress ON u.postal_code = ProposerAddress.postal_code " +
                "CROSS JOIN ("+
                        "SELECT latitude, longitude FROM `Address` " +
                        "INNER JOIN `User` ON `User`.postal_code = `Address`.postal_code AND email = '" + user_email + "' "+
        ") AS CounterpartyAddress " +
        "WHERE ProposerAddress.postal_code = (SELECT postal_code FROM `User` " +
        "WHERE email = '" + user_email +"') and i.user_email != '" + user_email + "') AS tb " +
                "WHERE item_number NOT IN (\n" +
                "\tSELECT proposed_item_number FROM `Swap` WHERE status = 1 OR status IS NULL UNION\n" +
                "    SELECT desired_item_number FROM `Swap` WHERE status = 1 OR status IS NULL\n" +
                ")";
        ResultSet resultSet = runQuery(query);
        return resultSet;
    }

    public ResultSet searchItemsInTargetPostalCode(String user_email, int target_postal_code) throws SQLException {
        String query = "SELECT tb.item_number, tb.`condition`, tb.`name`, tb.game_type, LEFT(tb.description, 100) AS `description`," +
                "ROUND(3958.75 * ("+
                "2 * ATAN2( SQRT(" +
                "POWER( SIN ((tb.lat2-tb.lat1)/2) ,2) * POWER( COS(tb.lat2) ,2) * POWER( SIN ((tb.lon2-tb.lon1)/2) ,2)" +
                ")," +
                "SQRT( 1 -" +
                "(POWER( SIN ((tb.lat2-tb.lat1)/2) ,2) * POWER( COS(tb.lat2) ,2) * POWER( SIN ((tb.lon2-tb.lon1)/2) ,2))" +
                ") )" +
                "),1) AS distance " +
                "FROM ( SELECT " +
                "RADIANS(ProposerAddress.latitude) AS lat1, RADIANS(ProposerAddress.longitude) AS lon1," +
                "RADIANS(CounterpartyAddress.latitude) AS lat2, RADIANS(CounterpartyAddress.longitude) AS lon2," +
                "i.item_number, i.`condition`, i.`name`, it.game_type, LEFT(`description`, 100) AS `description` " +
                "FROM `Item` i " +
                "INNER JOIN(" +
                "SELECT item_number, 'Board game' AS game_type FROM `BoardItem` UNION ALL " +
                "SELECT item_number, 'Card game' AS game_type FROM `CardItem` UNION ALL " +
                "SELECT item_number, 'Video game' AS game_type FROM `VideoItem` UNION ALL " +
                "SELECT item_number, 'Computer game' AS game_type FROM `ComputerItem` UNION ALL " +
                "SELECT item_number, 'Jigsaw puzzle' AS game_type FROM `JigsawItem`" +
                ") it ON it.item_number = i.item_number " +
                "INNER JOIN `User` u ON i.user_email = u.email " +
                "INNER JOIN `Address` AS ProposerAddress ON u.postal_code = ProposerAddress.postal_code " +
                "CROSS JOIN ("+
                "SELECT latitude, longitude FROM `Address` " +
                "INNER JOIN `User` ON `User`.postal_code = `Address`.postal_code AND email = '" + user_email + "' "+
                ") AS CounterpartyAddress " +
                "WHERE ProposerAddress.postal_code = " + target_postal_code +" and i.user_email != '"+user_email+"') AS tb " +
                "WHERE item_number NOT IN (\n" +
                "\tSELECT proposed_item_number FROM `Swap` WHERE status = 1 OR status IS NULL UNION\n" +
                "    SELECT desired_item_number FROM `Swap` WHERE status = 1 OR status IS NULL\n" +
                ")";
        ResultSet resultSet = runQuery(query);
        return resultSet;
    }

    public ResultSet searchItemsWithinMiles(String user_email, int miles) throws SQLException {
        String query = "SELECT tb.item_number, tb.`condition`, tb.`name`, tb.game_type, LEFT(tb.description, 100) AS `description`," +
        "ROUND(3958.75 * (" +
                "2 * ATAN2( SQRT("+
                                "POWER( SIN ((tb.lat2-tb.lat1)/2) ,2) * POWER( COS(tb.lat2) ,2) * POWER( SIN ((tb.lon2-tb.lon1)/2) ,2)" +
                        ")," +
                        "SQRT( 1 - " +
                                "(POWER( SIN ((tb.lat2-tb.lat1)/2) ,2) * POWER( COS(tb.lat2) ,2) * POWER( SIN ((tb.lon2-tb.lon1)/2) ,2))" +
                       " ) )" +
        "),1) AS distance " +
        "FROM ( SELECT " +
                "RADIANS(ProposerAddress.latitude) AS lat1, RADIANS(ProposerAddress.longitude) AS lon1," +
                "RADIANS(CounterpartyAddress.latitude) AS lat2, RADIANS(CounterpartyAddress.longitude) AS lon2," +
                "i.item_number, i.`condition`, i.`name`, it.game_type, LEFT(`description`, 100) AS `description` " +
                "FROM `Item` i " +
                "INNER JOIN(" +
                        "SELECT item_number, 'Board game' AS game_type FROM `BoardItem` UNION ALL " +
                        "SELECT item_number, 'Card game' AS game_type FROM `CardItem` UNION ALL " +
                        "SELECT item_number, 'Video game' AS game_type FROM `VideoItem` UNION ALL " +
                        "SELECT item_number, 'Computer game' AS game_type FROM `ComputerItem` UNION ALL " +
                        "SELECT item_number, 'Jigsaw puzzle' AS game_type FROM `JigsawItem` " +
                ") it ON it.item_number = i.item_number " +
                "INNER JOIN `User` u ON i.user_email = u.email " +
                "INNER JOIN `Address` AS ProposerAddress ON u.postal_code = ProposerAddress.postal_code "+
                "CROSS JOIN ( "+
                        "SELECT latitude, longitude FROM `Address` " +
                        "INNER JOIN `User` ON `User`.postal_code = `Address`.postal_code AND email = '" + user_email + "'" +
        ") AS CounterpartyAddress WHERE u.email != '" + user_email + "') AS tb " +
        "WHERE item_number NOT IN (\n" +
        "\tSELECT proposed_item_number FROM `Swap` WHERE status = 1 OR status IS NULL UNION\n" +
        "    SELECT desired_item_number FROM `Swap` WHERE status = 1 OR status IS NULL\n" +
        ") " +
        "HAVING distance < " + miles + " " +
        "ORDER BY distance ASC;";
        ResultSet resultSet = runQuery(query);
        return resultSet;
    }

    public ResultSet myItemListCounts(String user_email) throws SQLException{
        String query = "SELECT IFNULL(game_type, 'Total') as game_type, COUNT(*) as type_counts FROM " +
                "(" +
                "SELECT item_number, 'Board game' AS game_type FROM `BoardItem` " +
                "UNION ALL " +
                "SELECT item_number, 'Card game' AS game_type FROM `CardItem` " +
                "UNION ALL " +
                "SELECT item_number, 'Video game' AS game_type FROM `VideoItem` " +
                "UNION ALL " +
                "SELECT item_number, 'Computer game' AS game_type FROM `ComputerItem` " +
                "UNION ALL " +
                "SELECT item_number, 'Jigsaw puzzle' AS game_type FROM `JigsawItem` " +
                ") it " +
                "INNER JOIN `Item` i ON it.item_number = i.item_number " +
                "WHERE user_email = '" + user_email + "' AND i.item_number NOT IN (\n" +
                "\tSELECT proposed_item_number FROM `Swap` WHERE status = 1 OR status IS NULL UNION\n" +
                "    SELECT desired_item_number FROM `Swap` WHERE status = 1 OR status IS NULL\n" +
                ")\n" +
                "GROUP BY game_type " +
                "WITH ROLLUP;";
        ResultSet resultSet = runQuery(query);
        return resultSet;
    }

    public ResultSet myItemListWithDetails(String user_email) throws SQLException{
        String query = "SELECT i.item_number AS `Item #`, game_type AS `Game Type`, `name` AS `Title`, `condition` AS `Condition`, " +
                "LEFT(`description`, 100) as `Description` FROM " +
                "(" +
                        "SELECT item_number, 'Board game' AS game_type FROM `BoardItem` UNION ALL " +
                        "SELECT item_number, 'Card game' AS game_type FROM `CardItem` UNION ALL " +
                        "SELECT item_number, 'Video game' AS game_type FROM `VideoItem` UNION ALL " +
                        "SELECT item_number, 'Computer game' AS game_type FROM `ComputerItem` UNION ALL " +
                        "SELECT item_number, 'Jigsaw puzzle' AS game_type FROM `JigsawItem` " +
                ") it " +
        "INNER JOIN `Item` i ON it.item_number = i.item_number Where user_email = '" + user_email + "' " +
        " AND i.item_number NOT IN (\n" +
        "\tSELECT proposed_item_number FROM `Swap` WHERE status = 1 OR status IS NULL UNION\n" +
        "    SELECT desired_item_number FROM `Swap` WHERE status = 1 OR status IS NULL\n" +
        ")\n" +
        "ORDER BY i.item_number ASC;";
        ResultSet resultSet = runQuery(query);
        return resultSet;
    }

    public ResultSet myAvailableItems(String user_email) throws SQLException{
        String query = "SELECT \n" +
                "\ti.item_number,\n" +
                "    game_type, \n" +
                "    `name`, \n" +
                "    `condition`, \n" +
                "LEFT(`description`, 100) as `description` FROM\n" +
                "( SELECT item_number, 'Board game' AS game_type FROM `BoardItem` UNION ALL\n" +
                "SELECT item_number, 'Card game' AS game_type FROM `CardItem` UNION ALL\n" +
                "SELECT item_number, 'Video game' AS game_type FROM `VideoItem` UNION ALL\n" +
                "SELECT item_number, 'Computer game' AS game_type FROM `ComputerItem` UNION ALL\n" +
                "SELECT item_number, 'Jigsaw puzzle' AS game_type FROM `JigsawItem`\n" +
                ") it \n" +
                "INNER JOIN `Item` i ON it.item_number = i.item_number \n" +
                "WHERE user_email = '" + user_email + "' AND i.item_number NOT IN (\n" +
                "\tSELECT proposed_item_number FROM `Swap` WHERE status = 1 OR status IS NULL UNION\n" +
                "    SELECT desired_item_number FROM `Swap` WHERE status = 1 OR status IS NULL\n" +
                ")\n" +
                "ORDER BY i.item_number ASC;";
        ResultSet resultSet = runQuery(query);
        return resultSet;
    }

    public ResultSet proposedItemsCounts(String user_email) throws SQLException{
        String query ="SELECT item_number, game_type, name, condition, LEFT(description, 100)\n" +
                "FROM\n" +
                "(\n" +
                "SELECT item_number, 'Board game' AS game_type FROM `BoardItem`\n" +
                "UNION ALL\n" +
                "SELECT item_number, 'Card game' AS game_type FROM `CardItem`\n" +
                "UNION ALL\n" +
                "SELECT item_number, 'Video game' AS game_type FROM `VideoItem`\n" +
                "UNION ALL\n" +
                "SELECT item_number, 'Computer game' AS game_type FROM `ComputerItem`\n" +
                "UNION ALL\n" +
                "SELECT item_number, 'Jigsaw puzzle' AS game_type FROM `JigsawItem`\n" +
                ")\n" +
                "INNER JOIN `Item` ON item_number = `Item`.item_number\n" +
                "Where user_email = '" + user_email + "' " +
                "ORDER BY item_number ASC";
        ResultSet resultSet = runQuery(query);
        return resultSet;
    }

    public ResultSet getItemDetails(String user_email, String itemNumber) throws SQLException {

        String query = "SELECT \n" +
                "\ti.item_number,\n" +
                "    user_email,\n" +
                "    name,\n" +
                "    game_type,\n" +
                "    platform,\n" +
                "    media,\n" +
                "    piece_count,\n" +
                "    `condition`,\n" +
                "    description,    \n" +
                "\tnickname,\n" +
                "    CONCAT(city, \", \", state, \" \", a.postal_code) AS location,\n" +
                "    ROUND(AVG(rating),2) as rating,\n" +
                "    ROUND(3958.75 * (2 * ATAN2( \n" +
                "\t\tSQRT(POWER( SIN ((RADIANS(ca.latitude)-RADIANS(a.latitude))/2) ,2) * POWER( COS(RADIANS(ca.latitude)) ,2) * POWER( SIN ((RADIANS(ca.longitude)-RADIANS(a.longitude))/2) ,2)),\n" +
                "\t\tSQRT( 1 -(POWER( SIN ((RADIANS(ca.latitude)-RADIANS(a.latitude))/2) ,2) * POWER( COS(RADIANS(ca.latitude)) ,2) * POWER( SIN ((RADIANS(ca.longitude)-RADIANS(a.longitude))/2) ,2))) \n" +
                "\t\t)\n" +
                "\t),1) AS distance\n" +
                "FROM `Item` i\n" +
                "INNER JOIN(\n" +
                "\tSELECT item_number, 'Board Game' AS game_type, NULL AS platform, NULL AS media, NULL AS piece_count FROM BoardItem UNION\n" +
                "\tSELECT item_number, 'Card Game' AS game_type, NULL AS platform, NULL AS media, NULL AS piece_count FROM CardItem UNION\n" +
                "\tSELECT item_number, 'Video Game' AS game_type, platform,  media, NULL AS piece_count FROM VideoItem UNION\n" +
                "\tSELECT item_number, 'Computer Game' AS game_type,  platform, NULL AS media, NULL AS piece_count FROM ComputerItem UNION\n" +
                "\tSELECT item_number, 'Jigsaw Puzzle' AS game_type, NULL AS platform, NULL AS media, piece_count FROM JigsawItem\n" +
                " ) AS it ON it.item_number = i.item_number\n" +
                "INNER JOIN (\n" +
                "\tSELECT email, nickname, postal_code, counterparty_rating as rating\n" +
                "\tFROM  `User` u\n" +
                "\tLEFT JOIN `Item` i ON i.user_email = u.email\n" +
                "\tLEFT JOIN `Swap` s ON s.desired_item_number = i.item_number\n" +
                "\tUNION ALL\n" +
                "\tSELECT email, nickname, postal_code, proposer_rating as rating\n" +
                "\tFROM  `User` u\n" +
                "\tLEFT JOIN `Item` i ON i.user_email = u.email\n" +
                "\tLEFT JOIN `Swap` s ON s.proposed_item_number = i.item_number\n" +
                ") u ON  u.email = i.user_email\n" +
                "INNER JOIN `Address` a ON a.postal_code =  u.postal_code\n" +
                "CROSS JOIN (\n" +
                "\tSELECT latitude, longitude FROM `Address`\n" +
                "\tINNER JOIN `User` ON `User`.postal_code = `Address`.postal_code AND email = '"+user_email+"'\n" +
                ") AS ca\n" +
                "WHERE i.item_number = '"+itemNumber+"' \n" +
                "GROUP BY i.item_number,name,game_type,platform,media,`condition`,description,nickname,a.postal_code,city,state,\n" +
                "\t\ta.latitude, a.longitude, ca.latitude, ca.longitude, piece_count, i.user_email";

        ResultSet resultSet = runQuery(query);
        if(resultSet.next()){
            return resultSet;
        }else{
            throw new SQLException("Unable to retrieve Item info");
        }

    }

    // SWAP RELATED QUERIES

    public boolean proposeSwap(String proposedItemNumber, String desiredItemNumber) throws SQLException {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();

        try{
            runUpdate("INSERT INTO `Swap` (proposed_item_number, desired_item_number, proposed_date) " +
                    "VALUES("+proposedItemNumber+","+desiredItemNumber+",'"+formatter.format(date)+"')");
        }catch (SQLException e){
            if (e.getMessage().contains("Duplicate")){
                HelperFunctions.showAlert("Error Proposing Swap", "This item was already rejected for this swap");
                return false;
            }
        }


        return true;
    }

    public ResultSet unAcceptedSwaps(String user_email) throws SQLException {
        String query = "SELECT  \n" +
                "COUNT(*) AS pending_swaps,\n" +
                "MIN(proposed_date) < DATE_SUB(NOW(), INTERVAL 5 DAY) OR COUNT(*) > 5 AS red_count\n" +
                "FROM  `Swap`  \n" +
                "INNER JOIN `Item` ON item_number = desired_item_number  \n" +
                "WHERE user_email = '"+user_email+"' AND status IS NULL\n" +
                "GROUP BY user_email";
        ResultSet resultSet = runQuery(query);
        return resultSet;
    }

    public ResultSet unRatedSwapsResult(String user_email) throws SQLException {
        String query = "\n" +
                "SELECT accept_reject_date AS `Acceptance Date`,\n" +
                "'Proposer' AS `My Role`,\n" +
                "ProposedItem.item_number AS `Proposed Item #`, " +
                "ProposedItem.name AS `Proposed Item Title`, " +
                "DesiredItem.item_number AS `Desired Item #`, " +
                "DesiredItem.name AS `Desired Item Title`, " +
                "OtherUser.nickname AS `Other User`\n" +
                "FROM `Swap`\n" +
                "INNER JOIN `Item` AS ProposedItem ON ProposedItem.item_number = proposed_item_number\n" +
                "INNER JOIN `Item` AS DesiredItem ON DesiredItem.item_number = desired_item_number\n" +
                "INNER JOIN `User` AS OtherUser ON DesiredItem.user_email = OtherUser.email WHERE ProposedItem.user_email = '"+user_email+"'" +
                "AND counterparty_rating IS NULL AND status = 1 \n" +
                "UNION ALL\n" +
                "SELECT\n" +
                "accept_reject_date AS `Acceptance Date`,\n" +
                "'Counterparty' AS `My Role`,\n" +
                "ProposedItem.item_number AS `Proposed Item #`, " +
                "ProposedItem.name AS `Proposed Item`, " +
                "DesiredItem.item_number AS `Desired Item #`, " +
                "DesiredItem.name AS `Desired Item Title`, " +
                "OtherUser.nickname AS `Other User`\n" +
                "FROM `Swap`\n" +
                "INNER JOIN `Item` AS ProposedItem ON ProposedItem.item_number = proposed_item_number\n" +
                "INNER JOIN `Item` AS DesiredItem ON DesiredItem.item_number = desired_item_number\n" +
                "INNER JOIN `User` AS OtherUser ON ProposedItem.user_email = OtherUser.email WHERE DesiredItem.user_email = '"+user_email+"'" +
                "AND proposer_rating IS NULL AND status = 1 \n" +
                "ORDER BY `Acceptance Date` DESC";

        ResultSet resultSet = runQuery(query);
        return resultSet;
    }

    public ResultSet unAcceptedSwapsResult(String user_email) throws SQLException {
        String query = "SELECT \n" +
                "\tproposed_date AS `Date`, \n" +
                "    DesiredItem.item_number AS `Desired Item #`, \n" +
                "    DesiredItem.name AS `Desired Item Title`, \n" +
                "    Proposer.nickname AS `Proposer`, \n" +
                "    ROUND(AVG(rating),2) AS Rating,\n" +
                "    ROUND(3958.75 * (2 * ATAN2( \n" +
                "\t\tSQRT(POWER( SIN ((RADIANS(ca.latitude)-RADIANS(a.latitude))/2) ,2) * POWER( COS(RADIANS(ca.latitude)) ,2) * POWER( SIN ((RADIANS(ca.longitude)-RADIANS(a.longitude))/2) ,2)),\n" +
                "\t\tSQRT( 1 -(POWER( SIN ((RADIANS(ca.latitude)-RADIANS(a.latitude))/2) ,2) * POWER( COS(RADIANS(ca.latitude)) ,2) * POWER( SIN ((RADIANS(ca.longitude)-RADIANS(a.longitude))/2) ,2))) \n" +
                "\t\t)\n" +
                "\t),1) AS Distance,\n" +
                "    ProposedItem.item_number AS `Proposed Item #`,\n" +
                "    ProposedItem.name AS `Proposed Item Title`\n" +
                "FROM `Swap` \n" +
                "INNER JOIN `Item` AS DesiredItem ON DesiredItem.item_number = desired_item_number \n" +
                "INNER JOIN `Item` AS ProposedItem ON ProposedItem.item_number = proposed_item_number \n" +
                "INNER JOIN `User` AS Proposer ON Proposer.email = ProposedItem.user_email \n" +
                "INNER JOIN (\n" +
                "\tSELECT email, nickname, postal_code, counterparty_rating as rating\n" +
                "\tFROM  `User` u\n" +
                "\tLEFT JOIN `Item` i ON i.user_email = u.email\n" +
                "\tLEFT JOIN `Swap` s ON s.desired_item_number = i.item_number\n" +
                "\tUNION ALL\n" +
                "\tSELECT email, nickname, postal_code, proposer_rating as rating\n" +
                "\tFROM  `User` u\n" +
                "\tLEFT JOIN `Item` i ON i.user_email = u.email\n" +
                "\tLEFT JOIN `Swap` s ON s.proposed_item_number = i.item_number\n" +
                ") r ON  r.email = Proposer.email\n" +
                "INNER JOIN `User` AS CounterParty ON CounterParty.email = DesiredItem.user_email \n" +
                "INNER JOIN `Address` a ON a.postal_code =  Proposer.postal_code\n" +
                "CROSS JOIN (\n" +
                "\tSELECT latitude, longitude FROM `Address`\n" +
                "\tINNER JOIN `User` ON `User`.postal_code = `Address`.postal_code AND email = '"+user_email+"'\n" +
                ") AS ca\n" +
                "WHERE DesiredItem.user_email = '"+user_email+"' AND `status` IS NULL\n" +
                "GROUP BY proposed_date,  DesiredItem.name, ProposedItem.name, Proposer.nickname, a.latitude, a.longitude, ca.latitude, ca.longitude," +
                "ProposedItem.item_number, DesiredItem.item_number";
        ResultSet resultSet = runQuery(query);
        return resultSet;
    }

    public ResultSet unRatedSwaps(String user_email) throws SQLException {
        String query = "SELECT COUNT(swapped_item) AS unrated_swaps\n" +
                "FROM\n" +
                "(SELECT\n" +
                "proposed_item_number AS swapped_item\n" +
                "FROM Swap\n" +
                "INNER JOIN `Item` AS ProposedItem ON ProposedItem.item_number =\n" +
                "proposed_item_number\n" +
                "WHERE ProposedItem.user_email = '" + user_email + "' "+
                "AND counterparty_rating IS NULL AND status = 1\n" +
                "UNION ALL\n" +
                "SELECT\n" +
                "desired_item_number AS swapped_item\n" +
                "FROM `Swap`\n" +
                "INNER JOIN `Item` AS DesiredItem ON DesiredItem.item_number =\n" +
                "desired_item_number\n" +
                "WHERE DesiredItem.user_email = '" + user_email + "' "+
                "AND proposer_rating IS NULL AND status = 1) AS\n" +
                "USwaps ";
        ResultSet resultSet = runQuery(query);
        return resultSet;
    }

    public ResultSet swapHistorySummaryCounts(String user_email) throws SQLException{
        String query = "SELECT my_role,\n" +
                "COUNT(*)AS total,\n" +
                "SUM(status) AS accepted,\n" +
                "COUNT(*) - SUM(status) AS rejected,\n" +
                "(1 - SUM(status) / COUNT(*) ) * 100 rejected_percent\n" +
                "FROM ( SELECT\n" +
                "'Proposer' AS my_role,\n" +
                "status FROM `Swap`\n" +
                "INNER JOIN `Item` AS ProposedItem ON ProposedItem.item_number = proposed_item_number\n" +
                "WHERE ProposedItem.user_email = '" + user_email + "' " +
                "AND status IS NOT NULL UNION ALL\n" +
                "SELECT\n" +
                "'Counterparty' AS role,\n" +
                "status FROM `Swap`\n" +
                "INNER JOIN `Item` AS DesiredItem ON DesiredItem.item_number = desired_item_number\n" +
                "WHERE DesiredItem.user_email = '" + user_email + "' " +
                " AND status IS NOT NULL ) AS UserSwaps\n" +
                "GROUP BY my_role";
        ResultSet resultSet = runQuery(query);
        return resultSet;
    }

    public ResultSet swapHistoryCounts(String user_email) throws SQLException{
        String query = "SELECT " +
                "proposed_item_number, desired_item_number, OtherUser.email AS desirerEmail, '"+ user_email +"' AS proposerEmail, " +
                "proposed_date AS `Proposed Date`,\n" +
                "accept_reject_date AS `Accepted/Rejected Date`,\n" +
                "IF(status IS NULL,'', IF(status = 1, 'Accepted', 'Rejected')) AS `Swap Status`, " +
                "'Proposer' AS `My role`,\n" +
                "ProposedItem.name AS `Proposed Item`,\n" +
                "DesiredItem.name AS `Desired Item`,\n" +
                "OtherUser.nickname AS `Other User`,\n" +
                "counterparty_rating AS `Rating`\n" +
                "FROM `Swap`\n" +
                "INNER JOIN `Item` AS ProposedItem ON ProposedItem.item_number = proposed_item_number\n" +
                "INNER JOIN `Item` AS DesiredItem ON DesiredItem.item_number = desired_item_number\n" +
                "INNER JOIN `User` AS OtherUser ON DesiredItem.user_email = OtherUser.email WHERE ProposedItem.user_email = '" + user_email + "' " +
                "UNION ALL\n" +
                "SELECT proposed_item_number, desired_item_number, '" + user_email + "' AS desirerEmail, OtherUser.email AS proposerEmail, " +
                "proposed_date,\n" +
                "accept_reject_date,\n" +
                "IF(status IS NULL,'', IF(status = 1, 'Accepted', 'Rejected')) AS swap_status, 'Counterparty' AS my_role,\n" +
                "ProposedItem.name AS proposed_item_name,\n" +
                "DesiredItem.name AS desired_item_name,\n" +
                "OtherUser.nickname AS other_user,\n" +
                "proposer_rating AS rating\n" +
                "FROM `Swap`\n" +
                "INNER JOIN `Item` AS ProposedItem ON ProposedItem.item_number = proposed_item_number\n" +
                "INNER JOIN `Item` AS DesiredItem ON DesiredItem.item_number = desired_item_number\n" +
                "INNER JOIN `User` AS OtherUser ON ProposedItem.user_email = OtherUser.email WHERE DesiredItem.user_email = '" + user_email + "' " +
                "ORDER BY `Accepted/Rejected Date` DESC, `Proposed Date` ASC";
        ResultSet resultSet = runQuery(query);
        return resultSet;
    }

    public ResultSet dbQuerySwapPart(String proposed_item_number, String desired_item_number) throws SQLException{
        String query = "SELECT proposed_date, accept_reject_date, proposer_rating, counterparty_rating, " +
                "IF(`status` IS NULL,'', IF(status = 1, 'Accepted', 'Rejected')) AS swap_status " +
        "FROM `Swap` " +
        "WHERE proposed_item_number = " + proposed_item_number +" AND desired_item_number = " + desired_item_number +";";
        ResultSet resultSet = runQuery(query);
        if(resultSet.next()){
            return resultSet;
        }else{
            throw new SQLException("Unable to retrieve Item info");
        }
    }

    public boolean acceptSwap(String proposedItemNumber, String desiredItemNumber) throws SQLException{
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();

        runUpdate("UPDATE `Swap` SET status = 1, accept_reject_date = '"+formatter.format(date)+"' WHERE proposed_item_number = " + proposedItemNumber + "" +
                " AND desired_item_number = " + desiredItemNumber);

        return true;
    }

    public boolean rejectSwap(String proposedItemNumber, String desiredItemNumber) throws SQLException{
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();

        runUpdate("UPDATE `Swap` SET status = 0, accept_reject_date = '"+formatter.format(date)+"' WHERE proposed_item_number = " + proposedItemNumber + "" +
                " AND desired_item_number = " + desiredItemNumber);

        return true;
    }

    public boolean rateProposedSwaps(String proposer_rating, String ProposedItemNumber, String DesiredItemNumber ){


        try {
            runUpdate("UPDATE `SWAP` SET proposer_rating='"+proposer_rating+"'" +
                    "WHERE  desired_item_number = '"+DesiredItemNumber+"' AND proposed_item_number = '" + ProposedItemNumber + "'");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            HelperFunctions.showAlert("counterparty_rating  Error",e.getMessage());
        }

        return false;

    }

    public boolean rateCounterpartySwaps(String counterparty_rating, String ProposedItemNumber, String DesiredItemNumber ){

        try {
            runUpdate("UPDATE `SWAP` SET counterparty_rating='"+counterparty_rating+"'" +
                    "WHERE  desired_item_number = '"+DesiredItemNumber+"' AND proposed_item_number = '" + ProposedItemNumber + "'");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            HelperFunctions.showAlert("counterparty_rating  Error",e.getMessage());
        }

        return false;

    }


}
