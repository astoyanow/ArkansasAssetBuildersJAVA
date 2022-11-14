package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import SQLite.DB;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ReturnData Database Access Object class. <br/>
 * Methods that facilitate the interactions between the database
 * and objects of the ReturnData class.
 */
public class ReturnDataDAO {
    /**
     * Searches for a ReturnData by ID.
     * @param ID String, ID of the ReturnData.
     * @return ReturnData with corresponding ID.
     * @throws SQLException Unable to retrieve data, loss of connection, or other errors.
     * @throws ClassNotFoundException ReturnData class unable to be found.
     */
    public static ReturnData searchReturnData(String ID) throws SQLException, ClassNotFoundException{
        String selectStmt = "SELECT * From ReturnData WHERE ID = " + ID;

        try{
            ResultSet rs = DB.executeQuery(selectStmt);
            ReturnData rd = getReturnDataFromResultSet(rs);

            return rd;
        }catch(Exception e){
            System.out.println("Error while searching for " + ID + " : " + e);
            throw e;
        }
    }

    /**
     * Get ReturnData from result set after search query.
     * @param rs ResultSet, contains results from a search query.
     * @return ReturnData object.
     * @throws SQLException Unable to retrieve data, loss of connection, or other errors.
     */
    private static ReturnData getReturnDataFromResultSet(ResultSet rs) throws SQLException{
        ReturnData rd = null;
        if(rs.next()){
            rd = new ReturnData();
            rd.setClient_ID(rs.getString("Client_ID"));
            rd.setTaxYear(rs.getInt("TaxYear"));
            rd.setFederalReturn(rs.getInt("FederalReturn"));
            rd.setTotalRefund(rs.getInt("TotalRefund"));
            rd.setEITC(rs.getInt("EITC"));
            rd.setCTC(rs.getInt("CTC"));
            rd.setDependents(rs.getInt("Dependents"));
            rd.setSurveyScore(rs.getInt("SurveyScore"));
        }
        return rd;
    }

    /**
     * Gets the list of ReturnData from a ResultSet.
     * @param rs ResultSet containing ReturnData objects from a search query.
     * @return ObservableList of ReturnData objects.
     * @throws SQLException Unable to retrieve data, loss of connection, or other errors.
     */
    private static ObservableList<ReturnData> getReturnDataList(ResultSet rs) throws SQLException{
        ObservableList<ReturnData> returnDataList = FXCollections.observableArrayList();

        while(rs.next()){
            ReturnData rd = new ReturnData();
            rd.setClient_ID(rs.getString("Client_ID"));
            rd.setTaxYear(rs.getInt("TaxYear"));
            rd.setFederalReturn(rs.getInt("FederalReturn"));
            rd.setTotalRefund(rs.getInt("TotalRefund"));
            rd.setEITC(rs.getInt("EITC"));
            rd.setCTC(rs.getInt("CTC"));
            rd.setDependents(rs.getInt("Dependents"));
            rd.setSurveyScore(rs.getInt("SurveyScore"));
            returnDataList.add(rd);
        }
        return returnDataList;
    }

    /**
     * Update Dependents of a ReturnData object.
     * @param returnDataID String, the ID of the ReturnData object.
     * @param taxYear String, tax year that the old tax year will be changed to.
     * @throws SQLException Unable to retrieve data, loss of connection, or other errors.
     */
    public static void updateTaxYear(String returnDataID, String taxYear) throws SQLException{
        String updateStmt =
                        "UPDATE ReturnData\n" +
                        "SET TaxYear = " + taxYear + "\n" +
                        "WHERE Client_ID = '" + returnDataID + "';";
        try{
            DB.update(updateStmt);
        }catch(Exception e){
            System.out.print("Error occurred while UPDATE Operation: " + e);
            throw e;
        }
    }

    public static void updateFederalReturn(String returnDataID, String federalReturn) throws SQLException{
        String updateStmt =
                        "UPDATE ReturnData\n" +
                        "SET FederalReturn = " + federalReturn + "\n" +
                        "WHERE Client_ID = '" + returnDataID + "';";
        try{
            DB.update(updateStmt);
        }catch(Exception e){
            System.out.print("Error occurred while UPDATE Operation: " + e);
            throw e;
        }
    }

    public static void updateTotalRefund(String returnDataID, String totalRefund) throws SQLException{
        String updateStmt =
                        "UPDATE ReturnData\n" +
                        "SET TotalRefund = " + totalRefund + "\n" +
                        "WHERE Client_ID = '" + returnDataID + "';";
        try{
            DB.update(updateStmt);
        }catch(Exception e){
            System.out.print("Error occurred while UPDATE Operation: " + e);
            throw e;
        }
    }

    public static void updateEITC(String returnDataID, String EITC) throws SQLException{
        String updateStmt =
                        "UPDATE ReturnData\n" +
                        "SET EITC = " + EITC + "\n" +
                        "WHERE Client_ID = '" + returnDataID + "';";
        try{
            DB.update(updateStmt);
        }catch(Exception e){
            System.out.print("Error occurred while UPDATE Operation: " + e);
            throw e;
        }
    }

    public static void updateCTC(String returnDataID, String CTC) throws SQLException{
        String updateStmt =
                        "UPDATE ReturnData\n" +
                        "SET CTC = " + CTC + "\n" +
                        "WHERE Client_ID = '" + returnDataID + "';";
        try{
            DB.update(updateStmt);
        }catch(Exception e){
            System.out.print("Error occurred while UPDATE Operation: " + e);
            throw e;
        }
    }

    public static void updateDependents(String returnDataID, String dependents) throws SQLException{
        String updateStmt =
                        "UPDATE ReturnData\n" +
                        "SET Dependents = " + dependents + "\n" +
                        "WHERE Client_ID = '" + returnDataID + "';";
        try{
            DB.update(updateStmt);
        }catch(Exception e){
            System.out.print("Error occurred while UPDATE Operation: " + e);
            throw e;
        }
    }

    public static void updateSurveyScore(String returnDataID, String surveyScore) throws SQLException{
        String updateStmt =
                        "UPDATE ReturnData\n" +
                        "SET SurveyScore = " + surveyScore + "\n" +
                        "WHERE Client_ID = '" + returnDataID + "';";
        try{
            DB.update(updateStmt);
        }catch(Exception e){
            System.out.print("Error occurred while UPDATE Operation: " + e);
            throw e;
        }
    }
}
