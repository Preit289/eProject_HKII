module hotelmanagementsystemfxml {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    // add icon pack modules
    requires org.kordamp.ikonli.fontawesome;
    requires org.kordamp.ikonli.boxicons;
    requires org.kordamp.ikonli.coreui;

    opens HotelApp to javafx.fxml;
    opens HotelApp.model to javafx.base;
    exports HotelApp.db;
    exports HotelApp;

}
