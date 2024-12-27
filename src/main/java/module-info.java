module com.example.parkingmanagement {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.parkingmanagement to javafx.fxml;
    exports com.example.parkingmanagement;
}