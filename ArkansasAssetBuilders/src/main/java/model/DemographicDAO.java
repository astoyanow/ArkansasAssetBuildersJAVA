package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import SQLite.DB;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Demographic Database Access Object class. <br/>
 * Methods that facilitate the interactions between the database
 * and objects of the Demographic class.
 */
public class DemographicDAO {
    /**
     * Searches for a Demographic by ID.
     * @param Client_ID String, ID of the Demographic.
     * @return Demographic with corresponding ID.
     * @throws SQLException Unable to retrieve data, loss of connection, or other errors.
     * @throws ClassNotFoundException Demographic class unable to be found.
     */
    public static Demographic searchDemographic(String Client_ID, String TaxYear) throws SQLException, ClassNotFoundException{
        String selectStmt = "SELECT * From Demographic WHERE Client_ID = " + Client_ID + " AND WERE TaxYear = " ;

        try{
            ResultSet rs = DB.executeQuery(selectStmt);
            Demographic dem = getDemographicFromResultSet(rs);

            return dem;
        }catch(Exception e){
            System.out.println("Error while searching for " + Client_ID + " : " + e);
            throw e;
        }
    }

    public static ObservableList<Demographic> searchDemographics(String condition) throws SQLException, ClassNotFoundException{
        String selectStmt = "SELECT * FROM Demographic " + condition;
        System.out.println(selectStmt);
        try{
            ResultSet rsDemographics = DB.executeQuery(selectStmt);
            ObservableList<Demographic> demographicList = getDemographicList(rsDemographics, false, false);
            return demographicList;
        }catch(SQLException e){
            System.out.println("SQL select operation has failed:" + e);
            throw e;
        }
    }

    public static ObservableList<Demographic> searchDemographicsAndReturnData(String condition) throws SQLException, ClassNotFoundException{
        String selectStmt = "SELECT * FROM Demographic INNER JOIN ReturnData ON Demographic.TaxYear = ReturnData.TaxYear AND Demographic.Client_ID = ReturnData.Client_ID" + condition;
        System.out.println(selectStmt);
        try{
            ResultSet rsDemographics = DB.executeQuery(selectStmt);
            ObservableList<Demographic> demographicList = getDemographicList(rsDemographics, true, false);
            return demographicList;
        }catch(SQLException e){
            System.out.println("SQL select operation has failed:" + e);
            throw e;
        }
    }

    public static ObservableList<Demographic> searchDemographicsAndClients(String condition) throws SQLException, ClassNotFoundException{
        String selectStmt = "SELECT * FROM Demographic INNER JOIN Client ON Demographic.Client_ID = Client.ID" + condition;
        System.out.println(selectStmt);
        try{
            ResultSet rsDemographics = DB.executeQuery(selectStmt);
            ObservableList<Demographic> demographicList = getDemographicList(rsDemographics, false, true);
            return demographicList;
        }catch(SQLException e){
            System.out.println("SQL select operation has failed:" + e);
            throw e;
        }
    }

    public static ObservableList<Demographic> searchDemographicsAndReturnDataAndClients(String condition) throws SQLException, ClassNotFoundException{
        String selectStmt = "SELECT * FROM Demographic INNER JOIN ReturnData ON Demographic.TaxYear = ReturnData.TaxYear AND Demographic.Client_ID = ReturnData.Client_ID INNER JOIN Client ON Demographic.Client_ID = Client.ID" + condition;
        System.out.println(selectStmt);
        try{
            ResultSet rsDemographics = DB.executeQuery(selectStmt);
            ObservableList<Demographic> demographicList = getDemographicList(rsDemographics, true, true);
            return demographicList;
        }catch(SQLException e){
            System.out.println("SQL select operation has failed:" + e);
            throw e;
        }
    }

    /**
     * Get Demographic from result set after search query.
     * @param rs ResultSet, contains results from a search query.
     * @return Demographic object
     * @throws SQLException Unable to retrieve data, loss of connection, or other errors.
     */
    private static Demographic getDemographicFromResultSet(ResultSet rs) throws SQLException{
        Demographic dem = null;
        if(rs.next()){
            dem = new Demographic();
            dem.setClient_ID(rs.getString("Client_ID"));
            dem.setTaxYear(rs.getInt("TaxYear"));
            dem.setAddress(rs.getString("Address"));
            dem.setCounty(rs.getString("County"));
            dem.setZip(rs.getInt("Zip"));
            dem.setState(rs.getString("State"));
        }
        return dem;
    }

    /**
     * Gets the list of demographics from a ResultSet.
     * @param rs ResultSet containing Demographics from a search query.
     * @return ObservableList of Demographics.
     * @throws SQLException Unable to retrieve data, loss of connection, or other errors.
     */
    private static ObservableList<Demographic> getDemographicList(ResultSet rs, boolean returnData, boolean client) throws SQLException{
        ObservableList<Demographic> demographicList = FXCollections.observableArrayList();

        while(rs.next()){
            Demographic dem = new Demographic();
            dem.setClient_ID(rs.getString("Client_ID"));
            dem.setTaxYear(rs.getInt("TaxYear"));
            dem.setAddress(rs.getString("Address"));
            dem.setCounty(rs.getString("County"));
            dem.setZip(rs.getInt("Zip"));
            dem.setState(rs.getString("State"));
            if(returnData){
                dem.setFederalReturn(rs.getInt("FederalReturn"));
                dem.setTotalRefund(rs.getInt("TotalRefund"));
                dem.setEITC(rs.getInt("EITC"));
                dem.setCTC(rs.getInt("CTC"));
                dem.setDependents(rs.getInt("Dependents"));
                dem.setSurveyScore(rs.getInt("SurveyScore"));
            }
            if(client){
                dem.setFirstName(rs.getString("FirstName"));
                dem.setLastName(rs.getString("LastName"));
                dem.setDoB(rs.getString("DoB"));
                dem.setLast4SS(rs.getInt("Last4SS"));
            }
            demographicList.add(dem);
        }
        return demographicList;
    }

    /**
     * Update Address of a Demographic.
     * @param demographicID String, the ID of the Demographic.
     * @param Address String, address that the old address will be changed to.
     * @throws SQLException Unable to retrieve data, loss of connection, or other errors.
     */
    public static void updateAddress(String demographicID, String Address) throws SQLException{
        String updateStmt =
                        "UPDATE Demographic\n" +
                        "SET Address = '" + Address + "'\n" +
                        "WHERE Client_ID = '" + demographicID + "';";
        try{
            DB.update(updateStmt);
        }catch(Exception e){
            System.out.print("Error occurred while UPDATE Operation: " + e);
            throw e;
        }
    }

    public static void updateZip(String demographicID, String zip) throws SQLException{
        String updateStmt =
                        "UPDATE Demographic\n" +
                        "SET Zip = " + zip + "\n" +
                        "WHERE Client_ID = '" + demographicID + "';";
        try{
            DB.update(updateStmt);
        }catch(Exception e){
            System.out.print("Error occurred while UPDATE Operation: " + e);
            throw e;
        }
    }

    public static void updateCounty(String demographicID, String county) throws SQLException{
        String updateStmt =
                        "UPDATE Demographic\n" +
                        "SET County = '" + county + "'\n" +
                        "WHERE Client_ID = '" + demographicID + "';";
        try{
            DB.update(updateStmt);
        }catch(Exception e){
            System.out.print("Error occurred while UPDATE Operation: " + e);
            throw e;
        }
    }

    public static void updateState(String demographicID, String state) throws SQLException{
        String updateStmt =
                        "UPDATE Demographic\n" +
                        "SET State = '" + state + "'\n" +
                        "WHERE Client_ID = '" + demographicID + "';";
        try{
            DB.update(updateStmt);
        }catch(Exception e){
            System.out.print("Error occurred while UPDATE Operation: " + e);
            throw e;
        }
    }

    public static void updateTaxYear(String demographicID, String taxYear) throws SQLException{
        String updateStmt =
                        "UPDATE Demographic\n" +
                        "SET TaxYear = " + taxYear + "\n" +
                        "WHERE Client_ID = '" + demographicID + "';";
        try{
            DB.update(updateStmt);
        }catch(Exception e){
            System.out.print("Error occurred while UPDATE Operation: " + e);
            throw e;
        }
    }
}
