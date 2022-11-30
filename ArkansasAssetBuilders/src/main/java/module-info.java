module com.aab.arkansasassetbuilders {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires java.sql.rowset;
    requires java.desktop;
    requires javafx.media;
    requires javafx.base;
    requires javafx.web;
    requires javafx.swing;
    requires org.xerial.sqlitejdbc;


    opens com.aab.arkansasassetbuilders to javafx.fxml;
    exports com.aab.arkansasassetbuilders;
}