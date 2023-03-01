# ArkansasAssetBuildersJAVA
A desktop application for Arkansas Asset Builders, a non-profit organization helping the community of Arkansas with tax form preparation.

The user is able to upload Excel files containing client tax return information into a database where the information
can be filtered by different criteria, making data viewing and collecting efficient and streamlined.

## Prerequisites
Users will need to have Java 8 or Java Development Kit (JDK) 19 installed in their computer to run the application. 
To check what version of Java is currently installed, in a Command Prompt (Windows) or Terminal (Mac) window, enter:

<code> java --version </code>

If you do not have Java installed in your computer, you can go to https://www.java.com/en/download/manual.jsp, which lists
the different download options depending on what operating system your computer runs on.
Follow the instructions of the desired option to download Java to your computer.


## Usage
The program contains a Java Archive (JAR) file that contains all 
the necessary libraries to run the application without needing to open the project in 
an Integrated Development Environment (IDE). The 
*ArkansasAssetBuilders/out/artifacts/ArkansasAssetBuilders_jar* folder contains the .jar to launch the program. 
The user can launch the application by double-clicking on the .jar file in a File Explorer
(Finder on Mac).
### Upload Screen
Once the application is opened, the user will be directed to an upload screen where users can upload the Excel files containing user tax return information.
The screen includes:
* a button to open a file explorer that allows the user to select an Excel file to upload.
* a text field containing the file path of the Excel file that will be uploaded.
* a text field where the user inputs the tax year attributed to the Excel file.
* a choice box where the user selects if the Excel file contents are Federal return information, State return information, or if neither are applicable.

Once all the fields are filled out, the user can then select the upload button to upload the Excel file contents to the application's database.
The user can then select next to move to the screen containing the filter results.

### Filter Result Screen
The filter screen displays the results of the filters applied to the data in the database. At first, there will not be any 
data displayed as no filters will be applied yet to the data. To apply filters to the data, the user can select the 
'Filter' button, which directs the user to the filter selection screen, where filters can be selected and applied to the data.

To return to the upload screen, the user can select the 'Back to Home' button.
### Filter Selection Screen
The filter selection screen allows the user to select different filters to apply to the data in order to view different queries of data 
from the database. As of right now, aggregate searches (e.g. sum of AGI for Arkansas residents, sorting clients based on 
location or tax return type, etc.) and custom searches have not yet been implemented for the filter, though it is planned for future versions.