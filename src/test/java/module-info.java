module hotelmanagementsystemfxml {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql;

    opens HotelApp to javafx.fxml;
    exports HotelApp;
    exports HotelApp.db;
}
