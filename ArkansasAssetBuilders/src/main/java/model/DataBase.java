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

    /*
     *Initialization Functions
     */

    public static void initializeDB() throws SQLException {
        if(!DB.dbExists()){
            String Table1 = "CREATE TABLE CLIENT(ID text NOT NULL , SIDN text, L4SSN text, FIRSTNAME text, LASTNAME text, DOB text, EFIN text, PRIMARY KEY(ID));";
            String Table2 = "CREATE TABLE DEMOGRAPHIC(CLIENTID text NOT NULL , TAXYEAR num NOT NULL , CITY text, STATE text, ZIP text, PRIMARYSECONDARY60PLUS num, RESIDENCY text, PRIMARY KEY(CLIENTID, TAXYEAR), FOREIGN KEY(CLIENTID) REFERENCES CLIENT(ID), FOREIGN KEY(TAXYEAR) REFERENCES TAXYEAR(TAXYEAR));";
            String Table3 = "CREATE TABLE RETURNDATA(CLIENTID text NOT NULL , TAXYEAR num NOT NULL , FEDERAL bool NOT NULL , ACCEPTEDDATA text, RETURNTYPE text, FILINGSTATUS text, TOTALIRSEXEMPTIONS num, REFUND num, PAPERSTATE bool, PAPERFED bool, REQUESTINGDD bool, AGI num, CREATEDDATE text, ADDCTC num, POUNDSAVINGSBONDS text, SAVINGSBONDS text, EIC num, CHILDTAXCREDIT num, EDUCATIONTAXCREDIT num, ELDERLYCREDIT num, TOTALRESPPAYMENT num, TOTALADVPTCREPAYMENT num, AVERAGEADVPTCPAYMENT num, TOTALPTC num, BALANCEDUE num, ITIN bool, EXEMPTION7 bool, FULLYEARCOVERAGE bool, FORM8888 bool, SCHEDULEA bool, SCHEDULEB bool, SCHEDULEC bool, SCHEDULECEZ bool, SCHEDULED bool, SCHEDULEE bool, SCHEDULEF bool, SCHEDULEH bool, SCHEDULER bool, SCHEDULESETP bool, SCHEDULESESP bool, AGENCYID text, STATEWITHHOLDING num, STATETAXLIABILITY num, AAMOUNTTAXPAYERISPLANNINGTOSAVE num, PRIMARY KEY(CLIENTID, TAXYEAR, FEDERAL), FOREIGN KEY(CLIENTID) REFERENCES CLIENT(ID), FOREIGN KEY(TAXYEAR) REFERENCES TAXYEAR(TAXYEAR));";
            String Table4 = "CREATE TABLE QUESTION(CLIENTID text NOT NULL , TAXYEAR num NOT NULL , CONSENTTODISCLOSETAXRETURN bool, CONSENTTODISCLOSETAXPAYERD bool, CONSENTTOUSETAXPAYERDATA bool, CONSENTTODISCLOSETAXRETURN1 bool, CONSENTTODISCLOSETAXRETURN2 bool, CONSENTTODISCLOSETAXRETURN3 bool, CONSENTTODISCLOSETAXRETURN4 bool, QUESTIONS1 text, QUESTIONS2 text, QUESTIONS3 text, QUESTIONS4 text, QUESTIONS5 text, QUESTIONS6 text, QUESTIONS7 text, QUESTIONS8 text, QUESTIONS9 text, QUESTIONA text, QUESTIONB text, QUESTIONC text, QUESTIOND text, QUESTIONE text, QUESTIONF text, QUESTIONG text, QUESTIONH text, QUESTIONI text, QUESTIONJ text, QUESTIONK text, PERSONS5ANDUNDER num, PERSONSAGE6TO18 num, PERSONSAGE65PLUS num, PRIMARY KEY(CLIENTID, TAXYEAR), FOREIGN KEY(CLIENTID) REFERENCES CLIENT(ID), FOREIGN KEY(TAXYEAR) REFERENCES TAXYEAR(TAXYEAR));";
            String Table5 = "CREATE TABLE TAXYEAR(TAXYEAR num NOT NULL , PRIMARY KEY(TAXYEAR));";
            DB.executeCreateQuery(Table1);
            DB.executeCreateQuery(Table2);
            DB.executeCreateQuery(Table3);
            DB.executeCreateQuery(Table4);
            DB.executeCreateQuery(Table5);
        }
    }


    /*
     * CLIENT FUNCTIONS
     */

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
                updateFirstName(clientID, firstName);
                updateLastName(clientID, lastName);
                updateLast4SS(clientID, String.valueOf(last4SS));
                updateDOB(clientID, dob);
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
     * Update first name of a Client.
     * @param clientID String, the ID of the Client.
     * @param firstName String, name that the first name will be changed to.
     * @throws SQLException Unable to retrieve data, loss of connection, or other errors.
     */
    private static void updateFirstName(String clientID, String firstName) throws SQLException{
        String updateStmt =
                "UPDATE Client\n" +
                        "SET FirstName = '" + firstName + "'\n" +
                        "WHERE ID = '" + clientID + "';";
        System.out.println(updateStmt);
        try{
            DB.update(updateStmt);
        }catch(Exception e){
            System.out.print("Error occurred while UPDATE Operation: " + e);
            throw e;
        }
    }

    /**
     * Update last name of a Client.
     * @param clientID String, the ID of the Client.
     * @param lastName String, name that the last name will be changed to.
     * @throws SQLException Unable to retrieve data, loss of connection, or other errors.
     */
    private static void updateLastName(String clientID, String lastName) throws SQLException{
        String updateStmt =
                "UPDATE Client\n" +
                        "SET LastName = '" + lastName + "'\n" +
                        "WHERE ID = '" + clientID + "';";
        try{
            DB.update(updateStmt);
        }catch(Exception e){
            System.out.print("Error occurred while UPDATE Operation: " + e);
            throw e;
        }
    }

    /**
     * Update Date of Birth of a Client.
     * @param clientID String, the ID of the Client.
     * @param dob String, date that the date of birth will be changed to.
     * @throws SQLException Unable to retrieve data, loss of connection, or other errors.
     */
    private static void updateDOB(String clientID, String dob) throws SQLException{
        String updateStmt =
                "UPDATE Client\n" +
                        "SET DoB = '" + dob + "'\n" +
                        "WHERE ID = '" + clientID + "';";
        try{
            DB.update(updateStmt);
        }catch(Exception e){
            System.out.print("Error occurred while UPDATE Operation: " + e);
            throw e;
        }
    }

    /**
     * Update Last4SS of a Client.
     * @param clientID String, the ID of the Client.
     * @param last4SS String, number that the last 4 SS numbers will be changed to.
     * @throws SQLException Unable to retrieve data, loss of connection, or other errors.
     */
    private static void updateLast4SS(String clientID, String last4SS) throws SQLException{
        String updateStmt =
                "UPDATE Client\n" +
                        "SET Last4SS = " + last4SS + "\n" +
                        "WHERE ID = '" + clientID + "';";
        try{
            DB.update(updateStmt);
        }catch(Exception e){
            System.out.print("Error occurred while UPDATE Operation: " + e);
            throw e;
        }
    }


    /*
    DEMOGRAPHIC FUNCTIONS
     */
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
                    updateDemoTaxYear(clientID, taxYear);
                }
                if (!address.equals("")){
                    updateAddress(clientID, address);
                }
                if (!zip.equals("")){
                    updateZip(clientID, zip);
                }
                if (!county.equals("")){
                    updateCounty(clientID, county);
                }
                if (!state.equals("")){
                    updateState(clientID, state);
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
     * Update Address of a Demographic.
     * @param demographicID String, the ID of the Demographic.
     * @param address String, address that the old address will be changed to.
     * @throws SQLException Unable to retrieve data, loss of connection, or other errors.
     */
    public static void updateAddress(String demographicID, String address) throws SQLException{
        String updateStmt =
                "UPDATE Demographic\n" +
                        "SET Address = '" + address + "'\n" +
                        "WHERE Client_ID = '" + demographicID + "';";
        try{
            DB.update(updateStmt);
        }catch(Exception e){
            System.out.print("Error occurred while UPDATE Operation: " + e);
            throw e;
        }
    }

    /**
     * Update Zip code of a Demographic.
     * @param demographicID String, the ID of the Demographic.
     * @param zip String, zip that the old zip will be changed to.
     * @throws SQLException Unable to retrieve data, loss of connection, or other errors.
     */
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

    /**
     * Update County of a Demographic.
     * @param demographicID String, the ID of the Demographic.
     * @param county String, county that the old county will be changed to.
     * @throws SQLException Unable to retrieve data, loss of connection, or other errors.
     */
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

    /**
     * Update State of a Demographic.
     * @param demographicID String, the ID of the Demographic.
     * @param state String, state that the old state will be changed to.
     * @throws SQLException Unable to retrieve data, loss of connection, or other errors.
     */
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

    /**
     * Update Tax Year of a Demographic.
     * @param demographicID String, the ID of the Demographic.
     * @param taxYear String, year that the old tax year will be changed to.
     * @throws SQLException Unable to retrieve data, loss of connection, or other errors.
     */
    public static void updateDemoTaxYear(String demographicID, String taxYear) throws SQLException{
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



    /*
    RETURN DATA FUNCTIONS
     */

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
                    updateReturnTaxYear(clientID, taxYear);
                }
                if (!federalReturn.equals("")){
                    updateFederalReturn(clientID, federalReturn);
                }
                if (!totalRefund.equals("")){
                    updateTotalRefund(clientID, federalReturn);
                }
                if (!EITC.equals("")){
                    updateEITC(clientID, EITC);
                }
                if (!CTC.equals("")){
                    updateCTC(clientID, CTC);
                }
                if (!dependents.equals("")){
                    updateDependents(clientID, dependents);
                }
                if (!surveyScore.equals("")){
                    updateSurveyScore(clientID, surveyScore);
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

    /**
     * Update Tax Year of a ReturnData object.
     * @param returnDataID String, the ID of the ReturnData object.
     * @param taxYear String, tax year that the old tax year will be changed to.
     * @throws SQLException Unable to retrieve data, loss of connection, or other errors.
     */
    public static void updateReturnTaxYear(String returnDataID, String taxYear) throws SQLException{
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

    /**
     * Update Federal Return of a ReturnData object.
     * @param returnDataID String, the ID of the ReturnData object.
     * @param federalReturn String, federal return that the old federal return will be changed to.
     * @throws SQLException Unable to retrieve data, loss of connection, or other errors.
     */
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

    /**
     * Update Total Refund of a ReturnData object.
     * @param returnDataID String, the ID of the ReturnData object.
     * @param totalRefund String, refund that the old total refund will be changed to.
     * @throws SQLException Unable to retrieve data, loss of connection, or other errors.
     */
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

    /**
     * Update EITC of a ReturnData object.
     * @param returnDataID String, the ID of the ReturnData object.
     * @param EITC String, EITC that the old EITC will be changed to.
     * @throws SQLException Unable to retrieve data, loss of connection, or other errors.
     */
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

    /**
     * Update CTC of a ReturnData object.
     * @param returnDataID String, the ID of the ReturnData object.
     * @param CTC String, CTC that the old CTC will be changed to.
     * @throws SQLException Unable to retrieve data, loss of connection, or other errors.
     */
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

    /**
     * Update Dependents of a ReturnData object.
     * @param returnDataID String, the ID of the ReturnData object.
     * @param dependents String, number of dependents that the old dependents will be changed to.
     * @throws SQLException Unable to retrieve data, loss of connection, or other errors.
     */
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

    /**
     * Update Survey Score of a ReturnData object.
     * @param returnDataID String, the ID of the ReturnData object.
     * @param surveyScore String, score that the old survey score will be changed to.
     * @throws SQLException Unable to retrieve data, loss of connection, or other errors.
     */
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


    /*
    TAX YEAR FUNCTIONS
     */

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
                dataObject.setId(rs.getString("Client_ID"));
                dataObject.setTaxYear(rs.getInt("TaxYear"));
                dataObject.setZip(rs.getString("Zip"));
                dataObject.setState(rs.getString("State"));
            }
            if(returnData){
                dataObject.setFederal(rs.getBoolean("FederalReturn"));
                dataObject.setRefund(rs.getInt("TotalRefund"));
                dataObject.setEic(rs.getInt("EITC"));
                dataObject.setChildTaxCredit(rs.getInt("CTC"));
            }
            if(client){
                dataObject.setFirstName(rs.getString("FirstName"));
                dataObject.setLastName(rs.getString("LastName"));
                dataObject.setDoB(rs.getString("DoB"));
                dataObject.setL4SSN(rs.getString("Last4SS"));
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
     * Searches for a Client by ID.
     * @param ID String, ID of the Client.
     * @return Client with corresponding ID.
     * @throws SQLException Unable to retrieve data, loss of connection, or other errors.
     * @throws ClassNotFoundException Client class unable to be found.
     */
    public static Client searchClient(String ID) throws SQLException, ClassNotFoundException{
        String selectStmt = "SELECT * From Client WHERE ID = " + ID;

        try{
            ResultSet rs = DB.executeQuery(selectStmt);
            return getClientFromResultSet(rs);
        }catch(Exception e){
            System.out.println("Error while searching for " + ID + " : " + e);
            throw e;
        }
    }

    private static Client getClientFromResultSet(ResultSet rs) throws SQLException{
        Client client = null;
        if(rs.next()){
            client = new Client();
            client.setId(rs.getString("ID"));
            client.setFirstName(rs.getString("FirstName"));
            client.setLastName(rs.getString("LastName"));
            client.setDoB(rs.getString("DoB"));
            client.setL4SSN(rs.getString("Last4SS"));
        }
        return client;
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
        String selectStmt = "SELECT * FROM Demographic INNER JOIN ReturnData ON Demographic.TaxYear = ReturnData.TaxYear AND Demographic.Client_ID = ReturnData.Client_ID" + condition;
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
        String selectStmt = "SELECT * FROM Demographic INNER JOIN ReturnData ON Demographic.TaxYear = ReturnData.TaxYear AND Demographic.Client_ID = ReturnData.Client_ID INNER JOIN Client ON Demographic.Client_ID = Client.ID" + condition;
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
