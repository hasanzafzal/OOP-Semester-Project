package com.example.oopfrontproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Controller {

    @FXML
    private TextField vehicleNumberField, ownerNameField, contactNumberField;
    @FXML
    private ComboBox<String> vehicleTypeComboBox;
    @FXML
    private CheckBox carrierCheckBox;
    @FXML
    private TextArea outputTextArea;
    @FXML
    private TableView<Main.ParkingSlot> slotsTable;
    @FXML
    private TableColumn<Main.ParkingSlot, String> slotColumn, statusColumn;

    private Main.ParkingManagementSystem parkingSystem = new Main.ParkingManagementSystem();

    public void initialize() {
        vehicleTypeComboBox.setItems(FXCollections.observableArrayList("Two Wheeler", "Four Wheeler"));

        slotColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSlotType()));
        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().isOccupied() ? "Occupied" : "Available"));

        refreshSlotTable();
    }

    @FXML
    private void registerVehicle() {
        String vehicleNum = vehicleNumberField.getText();
        String ownerName = ownerNameField.getText();
        String contactNum = contactNumberField.getText();
        String vehicleType = vehicleTypeComboBox.getValue();
        boolean hasCarrier = carrierCheckBox.isSelected();

        if (vehicleNum.isEmpty() || ownerName.isEmpty() || contactNum.isEmpty() || vehicleType == null) {
            outputTextArea.appendText("Please fill in all fields.\n");
            return;
        }

        try {
            long contactNumber = Long.parseLong(contactNum);
            Main.Vehicle vehicle;

            if (vehicleType.equals("Two Wheeler")) {
                vehicle = new Main.TwoWheeler(vehicleNum, ownerName, contactNumber, hasCarrier);
            } else {
                vehicle = new Main.FourWheeler(vehicleNum, ownerName, contactNumber, "Sedan"); // or get the type from input
            }

            parkingSystem.registerVehicle(vehicle);
            outputTextArea.appendText("Vehicle registered: " + vehicle.getVehicleNum() + "\n");
            refreshSlotTable();

        } catch (NumberFormatException e) {
            outputTextArea.appendText("Invalid contact number.\n");
        }
    }

    @FXML
    private void viewSlots() {
        outputTextArea.appendText("Displaying parking slots...\n");
        refreshSlotTable();
    }

    @FXML
    private void generateReport() {
        parkingSystem.generateReport();
    }

    @FXML
    private void sendTimeAlerts() {
        // Implement time alert functionality in Main.ParkingManagementSystem
        outputTextArea.appendText("Time alerts functionality is not yet implemented.\n");
        // parkingSystem.notifyTimeAlerts(); // Call the method once implemented
    }

    private void refreshSlotTable() {
        try {
            List<Main.ParkingSlot> slotList = parkingSystem.getAvailableSlots();
            ObservableList<Main.ParkingSlot> observableSlots = FXCollections.observableArrayList(slotList);
            slotsTable.setItems(observableSlots);
        } catch (Exception e) {
            outputTextArea.appendText("Error refreshing parking slots: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    private String getCurrentTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }
}