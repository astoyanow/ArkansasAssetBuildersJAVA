package parsing;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class FileParserTest {

    File siteProdTestFile = new File("./ArkansasAssetBuilders/src/test/testDataExamples/exampleData1.csv").getCanonicalFile();
    File questionsTestFile = new File("./ArkansasAssetBuilders/src/test/testDataExamples/exampleData2.csv").getCanonicalFile();
    FileParser siteProdParser = new FileParser(siteProdTestFile);
    FileParser questionsParser = new FileParser(questionsTestFile);
    FileParserTest() throws IOException {
    }

    @Test
    void capitalizeNames() {
        assertEquals("JOHN", siteProdParser.getClientProperty(siteProdParser.getClient("A1234"), "FIRST NAME"));
        assertEquals("JOHN", questionsParser.getClientProperty(questionsParser.getClient("DOE1234"), "FIRST NAME"));
    }

    @Test
    void createKey() {
        List<String> siteProdLine = siteProdParser.removeCommas(siteProdParser.fileLines.get(1));
        List<String> questionsLine = questionsParser.removeCommas(questionsParser.fileLines.get(1));
        assertEquals("A1234", siteProdParser.createKey(siteProdLine));
        assertEquals("Doe1234", questionsParser.createKey(questionsLine));
    }

    @Test
    void getColumn() {
        assertEquals(0, siteProdParser.getColumn("EFIN"));
        assertEquals(1, questionsParser.getColumn("L4SSN"));
    }

    @Test
    void getColumnNames() {
        List<String> siteProdColumnNames = Arrays.asList("EFIN", "Last 4", "Preparer Name", "Agency ID", "Return Type",
                                                 "Residency", "FilingStatus", "First Name", "Last Name", "Ack Code",
                                                 "Refund", "Balance Due", "State Withholding", "State EIC",
                                                 "State Tax Liability", "CreatedDateTime", "Zip", "State",
                                                 "ChildTaxCredit", "AddCTC", "EIC", "TotalIRSExemptions", "PaperState",
                                                 "PaperFederal", "RequestingDirectDeposit", "FederalRejected",
                                                 "StateAccepted", "StateRejected", "PrimaryOrSecondary60+", "AGI",
                                                 "Created Date", "#SavingsBonds", "SavingsBonds", "EducationTaxCredit",
                                                 "ElderlyCredit", "TotalRespPymnt", "TotalADVPTCRepayment",
                                                 "AverageADVPTCPayment", "TotalPTC", "BalDueReturns", "ITIN",
                                                 "Exemption 7", "FullYearCoverage", "Form8888", "ScheduleA",
                                                 "ScheduleB", "ScheduleC", "ScheduleCEZ", "ScheduleD", "ScheduleE",
                                                 "ScheduleF", "ScheduleH", "ScheduleR", "ScheduleSETP", "ScheduleSESP");
        List<String> questionsColumnNames = Arrays.asList("EFIN", "L4SSN", "First Name", "Last Name", "DOB", "State",
                                                 "Zip", "AGI", "Consent to Disclose Tax Return",
                                                 "Consent to Disclose Taxpayer D",	"Consent to Use Taxpayer Data",
                                                 "Consent to Disclose Tax Return", "Consent to Disclose Tax Return",
                                                 "Consent to Disclose Tax Return", "Consent to Disclose Tax Return",
                                                 "1. Would you say you can carry", "2. Would you say you can read",
                                                 "3. Do you or any member of you", "4. Are you or your spouse a Ve",
                                                 "5. Your Race?", "6. Your Spouse's race?", "7. Your ethnicity?",
                                                 "8. Your spouse's ethnicity?", "9. Was the taxpayer physically",
                                                 "A.Are you planning to save an", "B.If you set aside money from",
                                                 "C.Which of the following best", "D.In the past 12 months, have",
                                                 "E.In a typical month, which o", "F.Suppose that you have an em",
                                                 "G.Because of my money situati", "H.I am just getting by financ",
                                                 "I.I am concerned that the mon", "J.I have money left over at t",
                                                 "K.My finances control my life",
                                                 "Persons on tax return age 5 an",	"Persons on tax return age 6-18",
                                                 "Persons on tax return age 65+");
        siteProdColumnNames.replaceAll(String::toUpperCase);
        questionsColumnNames.replaceAll(String::toUpperCase);
        assertEquals(siteProdColumnNames, siteProdParser.getColumnNames());
        assertEquals(questionsColumnNames, questionsParser.getColumnNames());
    }

    @Test
    void parseCSV() {
    }

    @Test
    void removeCommas() {
        List<String> line = siteProdParser.removeCommas(siteProdParser.fileLines.get(siteProdParser.fileLines.size()-1));
        String balanceDue = line.get(11);
        assertEquals("3419.00", balanceDue);
    }

    @Test
    void reformatDOB() {
        int dobColumn = questionsParser.getColumn("DOB");
        List<String> line = questionsParser.removeCommas(questionsParser.fileLines.get(1));
        questionsParser.reformatDOB(line, dobColumn);
        assertEquals("06/10/1990", line.get(dobColumn));
    }

    @Test
    void reformatSS() {
        int ssColumn = siteProdParser.getColumn("LAST 4");
        List<String> line1 = siteProdParser.removeCommas(siteProdParser.fileLines.get(2));
        siteProdParser.reformatSS(line1, ssColumn);
        List<String> line2 = siteProdParser.removeCommas(siteProdParser.fileLines.get(3));
        siteProdParser.reformatSS(line2, ssColumn);
        List<String> line3 = siteProdParser.removeCommas(siteProdParser.fileLines.get(4));
        siteProdParser.reformatSS(line3, ssColumn);
        assertEquals("5678", line1.get(ssColumn));
        assertEquals("9012", line2.get(ssColumn));
        assertEquals("0123", line3.get(ssColumn));
    }

    @Test
    void removeXChars() {
        assertEquals("5678", siteProdParser.removeXChars("xxxx5678"));
    }
}