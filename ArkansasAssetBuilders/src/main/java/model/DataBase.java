package model;

import SQLite.DB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DataBase {

    /**
     * Insert a client into the Client table of the database.
     * Inserts a client if the client associated with the clientID
     * does not already exist, otherwise the client with the associated
     * clientID is updated.
     * @param clientData Properties associated with the client.
     * @param clientID ID of the client.
     */
    public static void insertClient(HashMap<String, String> clientData, String clientID) {
        // Grab client data for new or existing client in the table.
        String firstName = clientData.get("FIRST NAME");
        String lastName = clientData.get("LAST NAME");
        int last4SS;
        if (clientData.containsKey("LAST 4")){
            last4SS = Integer.parseInt(clientData.get("LAST 4"));
        }else if (clientData.containsKey("LAST4SS")){
            last4SS = Integer.parseInt(clientData.get("LAST4SS"));
        } else{
            last4SS = Integer.parseInt(clientData.get("L4SSN"));
        }
        // Not all CSVs contain the DoB of a client.
        String dob = "";
        if (clientData.containsKey("DOB") || clientData.containsKey("DATE OF BIRTH")) {
            dob = clientData.containsKey("DOB")
                    ? clientData.get("DOB")
                    : clientData.get("DATE OF BIRTH");
        }
        // Get the row where the clientID exists in the Client table (if it does exist).
        try (ResultSet query = DB.executeQuery(String.format("SELECT * FROM Client WHERE ID = '%s';", clientID))) {
            // If the client already exists, update the necessary fields.
            if (query.next()){
                // Reset pointer of the ResultSet (somewhat redundant since there should only be one or no elements
                // but a good habit nonetheless).
                query.beforeFirst();

                // Update the fields.
                ClientDAO.updateFirstName(clientID, firstName);
                ClientDAO.updateLastName(clientID, lastName);
                ClientDAO.updateLast4SS(clientID, String.valueOf(last4SS));
                ClientDAO.updateDOB(clientID, dob);
            }else {
                // Create an update SQL command to insert a new row into the Client table.
                String sqlStmt = String.format("INSERT INTO Client (ID, FirstName, LastName, DoB, Last4SS) \n" +
                        "VALUES ('%1$s', '%2$s', '%3$s', '%4$s', %5$d);", clientID, firstName, lastName, dob, last4SS);
                try {
                    // Execute the SQL statement.
                    DB.update(sqlStmt);
                } catch (Exception e) {
                    System.out.print("Error occurred while UPDATE Operation: " + e);
                    throw e;
                }
            }
        }catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    /**
     * Insert a demographic into the Demographic table of the database.
     * Inserts a demographic if the demographic associated with the clientID
     * does not already exist, otherwise the demographic with the associated
     * clientID is updated.
     * @param clientData Properties associated with the demographic.
     * @param clientID ID of the demographic's client.
     */
    public static void insertDemographic(HashMap<String, String> clientData, String clientID){
        // Grab demographic data for new or existing demographic in the demographic table.
        // Not guaranteed that each field is in clientData, so must instantiate variables.
        String taxYear = "";
        String address = "";
        String zip = "";
        String county = "";
        String state = "";
        if (clientData.containsKey("CREATEDDATETIME")){
            // Only want the year created.
            taxYear = clientData.get("CREATEDDATETIME").substring(0,4);
        }
        if (clientData.containsKey("ADDRESS")){
            address = clientData.get("ADDRESS");
        }
        if (clientData.containsKey("ZIP")){
            zip = clientData.get("ZIP");
        }
        if (clientData.containsKey("COUNTY")){
            county = clientData.get("COUNTY");
        }
        if (clientData.containsKey("STATE")){
            state = clientData.get("STATE");
        }
        try (ResultSet query = DB.executeQuery(String.format("SELECT * FROM Demographic WHERE Client_ID = '%s';", clientID))) {
            // If the demographic already exists, update the necessary fields.
            if (query.next()){
                // Reset pointer of the ResultSet (somewhat redundant since there should only be one or no elements
                // but a good habit nonetheless).
                query.beforeFirst();

                // Update the fields. Since field in table could have existing data, do not want to
                // overwrite with a null value.
                if (!taxYear.equals("")){
                    DemographicDAO.updateTaxYear(clientID, taxYear);
                }
                if (!address.equals("")){
                    DemographicDAO.updateAddress(clientID, address);
                }
                if (!zip.equals("")){
                    DemographicDAO.updateZip(clientID, zip);
                }
                if (!county.equals("")){
                    DemographicDAO.updateCounty(clientID, county);
                }
                if (!state.equals("")){
                    DemographicDAO.updateState(clientID, state);
                }
            }else {
                // Create an update SQL command to insert a new row into the Client table.
                List<String> fields = Arrays.asList("TaxYear", "Address", "Zip", "County", "State");
                List<String> values = Arrays.asList(taxYear, address, zip, county, state);
                StringBuilder insertStmt = new StringBuilder("INSERT INTO Demographic (Client_ID");
                StringBuilder intoStmt = new StringBuilder("VALUES ('" + clientID + "'");
                for (int index = 0; index < fields.size(); index++){
                    if (!values.get(index).equals("")){
                        insertStmt.append(", ").append(fields.get(index));
                        if (fields.get(index).equals("TaxYear") || fields.get(index).equals("Zip")){
                            intoStmt.append(", ").append(values.get(index));
                        }else{
                            intoStmt.append(", ").append("'").append(values.get(index)).append("'");
                        }
                    }
                }
                insertStmt.append(") \n");
                intoStmt.append((");"));
                System.out.println(insertStmt + intoStmt.toString());
                try {
                    // Execute the SQL statement.
                    DB.update(insertStmt + intoStmt.toString());
                } catch (Exception e) {
                    System.out.print("Error occurred while UPDATE Operation: " + e);
                    throw e;
                }
            }
        }catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    /**
     * Insert a return into the Return table of the database.
     * Inserts a return if the return associated with the clientID
     * does not already exist, otherwise the return with the associated
     * clientID is updated.
     * @param clientData Properties associated with the return.
     * @param clientID ID of the return's client.
     */
    public static void insertReturnData(HashMap<String, String> clientData, String clientID){
        // Grab demographic data for new or existing demographic in the demographic table.
        // Not guaranteed that each field is in clientData, so must instantiate variables.
        String taxYear = "";
        String federalReturn = "";
        String totalRefund = "";
        String EITC = "";
        String CTC = "";
        String dependents = "";
        String surveyScore = "";
        if (clientData.containsKey("CREATEDDATETIME")){
            // Only want the year created.
            taxYear = clientData.get("CREATEDDATETIME").substring(0,4);
        }
        if (clientData.containsKey("RETURN")){
            federalReturn = clientData.get("RETURN");
        }
        if (clientData.containsKey("TOTALREFUND")){
            totalRefund = clientData.get("TOTALREFUND");
        }
        if (clientData.containsKey("EITC")){
            EITC = clientData.get("EITC");
        }
        if (clientData.containsKey("CTC")){
            CTC = clientData.get("CTC");
        }
        if (clientData.containsKey("DEPENDENTS")){
            dependents = clientData.get("DEPENDENTS");
        }
        if (clientData.containsKey("SURVEYSCORE")){
            surveyScore = clientData.get("SURVEYSCORE");
        }
        try (ResultSet query = DB.executeQuery(String.format("SELECT * FROM ReturnData WHERE Client_ID = '%s';", clientID))) {
            // If the demographic already exists, update the necessary fields.
            if (query.next()){
                // Reset pointer of the ResultSet (somewhat redundant since there should only be one or no elements
                // but a good habit nonetheless).
                query.beforeFirst();

                // Update the fields. Since field in table could have existing data, do not want to
                // overwrite with a null value.
                if (!taxYear.equals("")){
                    ReturnDataDAO.updateTaxYear(clientID, taxYear);
                }
                if (!federalReturn.equals("")){
                    ReturnDataDAO.updateFederalReturn(clientID, federalReturn);
                }
                if (!totalRefund.equals("")){
                    ReturnDataDAO.updateTotalRefund(clientID, federalReturn);
                }
                if (!EITC.equals("")){
                    ReturnDataDAO.updateEITC(clientID, EITC);
                }
                if (!CTC.equals("")){
                    ReturnDataDAO.updateCTC(clientID, CTC);
                }
                if (!dependents.equals("")){
                    ReturnDataDAO.updateDependents(clientID, dependents);
                }
                if (!surveyScore.equals("")){
                    ReturnDataDAO.updateSurveyScore(clientID, surveyScore);
                }
            }else {
                // Create an update SQL command to insert a new row into the Client table.
                List<String> fields = Arrays.asList("TaxYear", "FederalReturn", "TotalRefund",
                        "EITC", "CTC", "Dependents", "SurveyScore");
                List<String> values = Arrays.asList(taxYear, federalReturn, totalRefund,
                        EITC, CTC, dependents, surveyScore);
                StringBuilder insertStmt = new StringBuilder("INSERT INTO ReturnData (Client_ID");
                StringBuilder intoStmt = new StringBuilder("VALUES ('" + clientID + "'");
                for (int index = 0; index < fields.size(); index++){
                    if (!values.get(index).equals("")){
                        insertStmt.append(", ").append(fields.get(index));
                        intoStmt.append(", ").append(values.get(index));
                    }
                }
                insertStmt.append(") \n");
                intoStmt.append((");"));
                System.out.println(insertStmt + intoStmt.toString());
                try {
                    // Execute the SQL statement.
                    DB.update(insertStmt + intoStmt.toString());
                } catch (Exception e) {
                    System.out.print("Error occurred while UPDATE Operation: " + e);
                    throw e;
                }
            }
        }catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }


    public static void insertTaxYear(HashMap<String, String> clientData){
        String taxYear = "";
        if (clientData.containsKey("CREATEDDATETIME")){
            taxYear = clientData.get("CREATEDDATETIME").substring(0, 4);
        }
        if (!taxYear.equals("")){
            try (ResultSet query = DB.executeQuery(String.format("SELECT * FROM TaxYear WHERE TaxYear = %s;", taxYear))) {
                // If the demographic already exists, there is no need to do anything else.
                if (query.next()){
                    // Reset pointer of the ResultSet (somewhat redundant since there should only be one or no elements
                    // but a good habit nonetheless).
                    query.beforeFirst();
                }else {
                    // Create an update SQL command to insert a new row into the Client table.
                    String sqlStmt = "INSERT INTO TaxYear (TaxYear)\n" +
                                     "VALUES (" + taxYear + ");";
                    try {
                        // Execute the SQL statement.
                        DB.update(sqlStmt);
                    } catch (Exception e) {
                        System.out.print("Error occurred while UPDATE Operation: " + e);
                        throw e;
                    }
                }
            }catch (Exception e){
                System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                System.exit(0);
            }
        }
    }


    /**
     * Translates a result set into an ObservableList of Data Objects
     * @param rs
     * @param demographic
     * @param returnData
     * @param client
     * @return
     * @throws SQLException
     */
    private static ObservableList<DataObject> getDataObjectList(ResultSet rs, boolean demographic, boolean returnData, boolean client) throws SQLException{
        ObservableList<DataObject> dataObjectList = FXCollections.observableArrayList();
        while(rs.next()){
            DataObject dataObject = new DataObject();
            if(demographic) {
                dataObject.setClient_ID(rs.getString("Client_ID"));
                dataObject.setTaxYear(rs.getInt("TaxYear"));
                dataObject.setAddress(rs.getString("Address"));
                dataObject.setCounty(rs.getString("County"));
                dataObject.setZip(rs.getInt("Zip"));
                dataObject.setState(rs.getString("State"));
            }
            if(returnData){
                dataObject.setFederalReturn(rs.getInt("FederalReturn"));
                dataObject.setTotalRefund(rs.getInt("TotalRefund"));
                dataObject.setEITC(rs.getInt("EITC"));
                dataObject.setCTC(rs.getInt("CTC"));
                dataObject.setDependents(rs.getInt("Dependents"));
                dataObject.setSurveyScore(rs.getInt("SurveyScore"));
            }
            if(client){
                dataObject.setFirstName(rs.getString("FirstName"));
                dataObject.setLastName(rs.getString("LastName"));
                dataObject.setDoB(rs.getString("DoB"));
                dataObject.setLast4SS(rs.getInt("Last4SS"));
            }
            //This may not be a good way to set TaxYear
            //
            if(demographic || returnData || !client){
                dataObject.setTaxYear(rs.getInt("TaxYear"));
            }
            dataObjectList.add(dataObject);
        }
        return dataObjectList;

    }

    /**
     * runs an SQL query selecting data from the Client table with a given condition
     * @param condition the condition of the WHERE clause of the SQL statement
     * @return the results of the SQL query as an ObservableList
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static ObservableList<DataObject> searchClients(String condition) throws SQLException, ClassNotFoundException {
        String selectStmt = "SELECT * FROM Client" + condition;
        System.out.println(selectStmt);
        try {
            ResultSet rsClients = DB.executeQuery(selectStmt);
            ObservableList<DataObject> clientList = getDataObjectList(rsClients, false, false, true);
            return clientList;
        } catch (SQLException e) {
            System.out.println("SQL select operation has failed:" + e);
            throw e;
        }
    }

    public static ObservableList<DataObject> searchDemographics(String condition) throws SQLException, ClassNotFoundException{
        String selectStmt = "SELECT * FROM Demographic " + condition;
        System.out.println(selectStmt);
        try{
            ResultSet rsDemographics = DB.executeQuery(selectStmt);
            ObservableList<DataObject> demographicList = getDataObjectList(rsDemographics, true, false, false);
            return demographicList;
        }catch(SQLException e){
            System.out.println("SQL select operation has failed:" + e);
            throw e;
        }
    }

    public static ObservableList<DataObject> searchReturnData(String condition) throws SQLException, ClassNotFoundException{
        String selectStmt = "SELECT * FROM ReturnData " + condition;
        System.out.println(selectStmt);
        try{
            ResultSet rsDemographics = DB.executeQuery(selectStmt);
            ObservableList<DataObject> demographicList = getDataObjectList(rsDemographics, false, true, false);
            return demographicList;
        }catch(SQLException e){
            System.out.println("SQL select operation has failed:" + e);
            throw e;
        }
    }

    public static ObservableList<DataObject> searchDemographicsAndReturnData(String condition) throws SQLException, ClassNotFoundException{
        String selectStmt = "SELECT * FROM Demographic INNER JOIN ReturnData ON Demographic.Client_ID = ReturnData.Client_ID" + condition;
        System.out.println(selectStmt);
        try{
            ResultSet rsDemographics = DB.executeQuery(selectStmt);
            ObservableList<DataObject> demographicList = getDataObjectList(rsDemographics, true, true, false);
            return demographicList;
        }catch(SQLException e){
            System.out.println("SQL select operation has failed:" + e);
            throw e;
        }
    }

    public static ObservableList<DataObject> searchDemographicsAndClients(String condition) throws SQLException, ClassNotFoundException{
        String selectStmt = "SELECT * FROM Demographic INNER JOIN Client ON Demographic.Client_ID = Client.ID" + condition;
        System.out.println(selectStmt);
        try{
            ResultSet rsDemographics = DB.executeQuery(selectStmt);
            ObservableList<DataObject> demographicList = getDataObjectList(rsDemographics, true, false, true);
            return demographicList;
        }catch(SQLException e){
            System.out.println("SQL select operation has failed:" + e);
            throw e;
        }
    }

    public static ObservableList<DataObject> searchReturnDataAndClients(String condition) throws SQLException, ClassNotFoundException{
        String selectStmt = "SELECT * FROM ReturnData INNER JOIN Client ON ReturnData.Client_ID = Client.ID" + condition;
        System.out.println(selectStmt);
        try{
            ResultSet rsDemographics = DB.executeQuery(selectStmt);
            ObservableList<DataObject> returnDataClientList = getDataObjectList(rsDemographics, false, true, true);
            return returnDataClientList;
        }catch(SQLException e){
            System.out.println("SQL select operation has failed:" + e);
            throw e;
        }
    }

    public static ObservableList<DataObject> searchDemographicsAndReturnDataAndClients(String condition) throws SQLException, ClassNotFoundException{
        String selectStmt = "SELECT * FROM Demographic INNER JOIN ReturnData ON Demographic.Client_ID = ReturnData.Client_ID INNER JOIN Client ON Demographic.Client_ID = Client.ID" + condition;
        System.out.println(selectStmt);
        try{
            ResultSet rsDemographics = DB.executeQuery(selectStmt);
            ObservableList<DataObject> demographicList = getDataObjectList(rsDemographics, true, true, true);
            return demographicList;
        }catch(SQLException e){
            System.out.println("SQL select operation has failed:" + e);
            throw e;
        }
    }

    public static ObservableList<DataObject> searchTaxYears(String condition) throws SQLException, ClassNotFoundException{
        String selectStmt = "SELECT * FROM TaxYear" + condition;
        System.out.println(selectStmt);
        try {
            ResultSet rsClients = DB.executeQuery(selectStmt);
            ObservableList<DataObject> taxYearList = getDataObjectList(rsClients, false, false, false);
            return taxYearList;
        } catch (SQLException e) {
            System.out.println("SQL select operation has failed:" + e);
            throw e;
        }
    }


}
