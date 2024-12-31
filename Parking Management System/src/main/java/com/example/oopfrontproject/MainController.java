package com.example.oopfrontproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import com.example.oopfrontproject.Main.ParkingManagementSystem;
import com.example.oopfrontproject.Main.Vehicle;
import com.example.oopfrontproject.Main.FourWheeler;
import com.example.oopfrontproject.Main.TwoWheeler;
import com.example.oopfrontproject.Main.ParkingSlot;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MainController {

    @FXML
    private TextField vehicleNumberField;

    @FXML
    private TextField ownerNameField;

    @FXML
    private TextField contactNumberField;

    @FXML
    private ComboBox<String> vehicleTypeComboBox;

    @FXML
    private CheckBox hasCarrierCheckBox;

    @FXML
    private TextArea outputArea;

    @FXML
    private TableView<ParkingSlot> slotsTable;

    @FXML
    private TableColumn<ParkingSlot, String> slotColumn;

    @FXML
    private TableColumn<ParkingSlot, String> statusColumn;

    private ParkingManagementSystem pms;

    public MainController() {
        pms = new ParkingManagementSystem();
    }

    @FXML
    public void initialize() {
        // Populate the vehicle type combo box
        vehicleTypeComboBox.getItems().addAll("FourWheeler", "TwoWheeler");

        // Set up table columns
        slotColumn.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().getSlotType()));
        statusColumn.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().isOccupied() ? "Occupied" : "Available"));
    }

    @FXML
    private void handleRegisterVehicle(ActionEvent event) {
        try {
            String vehicleNum = vehicleNumberField.getText();
            String ownerName = ownerNameField.getText();
            long contactNum = Long.parseLong(contactNumberField.getText());

            if (vehicleNum.isEmpty() || ownerName.isEmpty()) {
                outputArea.setText("Please fill all the fields correctly.");
                return;
            }

            Vehicle vehicle;
            String vehicleType = vehicleTypeComboBox.getValue();
            boolean hasCarrier = hasCarrierCheckBox.isSelected();

            if ("FourWheeler".equalsIgnoreCase(vehicleType)) {
                vehicle = new FourWheeler(vehicleNum, ownerName, contactNum, vehicleType);
            } else if ("TwoWheeler".equalsIgnoreCase(vehicleType)) {
                vehicle = new TwoWheeler(vehicleNum, ownerName, contactNum, hasCarrier);
            } else {
                outputArea.setText("Please select a valid vehicle type.");
                return;
            }

            pms.registerVehicle(vehicle);
            outputArea.appendText("Vehicle Registered Successfully.\n");
        } catch (NumberFormatException e) {
            outputArea.setText("Please enter a valid contact number.");
        }
    }

    @FXML
    private void handleGenerateReport(ActionEvent event) {
        outputArea.appendText("Generating Report...\n");
        pms.generateReport();
    }

    @FXML
    private void handleDisplaySlots(ActionEvent event) {
        outputArea.appendText("Fetching Available Parking Slots...\n");

        // Get available slots from Parking Management System
        ObservableList<ParkingSlot> availableSlots = FXCollections.observableArrayList(pms.getAvailableSlots());

        // Clear previous data in the table
        slotsTable.getItems().clear();

        // Populate the slots table with available slots
        slotsTable.setItems(availableSlots);

        outputArea.appendText("Displayed available parking slots.\n");
    }

    @FXML
    private void handleNotifyAlerts(ActionEvent event) {
        outputArea.appendText("Sending Time Alerts...\n");

        // Placeholder for time alert functionality
        // Assuming this is a placeholder, you can implement logic here to send alerts to users based on time
        outputArea.appendText("Time alerts have been sent.\n");
    }
}
