package com.example.oopfrontproject;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;

import java.util.Scanner;

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
    private TableView<ParkingSlot> slotsTable;
    @FXML
    private TableColumn<ParkingSlot, String> slotColumn, statusColumn;

    private ParkingManagementSystem parkingSystem;

    public void initialize() {
        parkingSystem = new ParkingManagementSystem();

        // Initialize ComboBox with vehicle types
        vehicleTypeComboBox.setItems(FXCollections.observableArrayList("Two Wheeler", "Four Wheeler"));

        // Set up the TableView for slots
        slotColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSlotType()));
        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().isOccupied() ? "Occupied" : "Available"));

        // Load slots into TableView
        refreshSlotTable();
    }

    @FXML
    private void registerVehicle() {
        String vehicleNum = vehicleNumberField.getText();
        String ownerName = ownerNameField.getText();
        String contactNum = contactNumberField.getText();
        String vehicleType = vehicleTypeComboBox.getValue();
        boolean hasCarrier = carrierCheckBox.isSelected();

        // Validation checks
        if (vehicleNum.isEmpty() || ownerName.isEmpty() || contactNum.isEmpty() || vehicleType == null) {
            outputTextArea.appendText("Please fill in all fields.\n");
            return;
        }

        try {
            long contactNumber = Long.parseLong(contactNum);
            Vehicle vehicle;

            if (vehicleType.equals("Two Wheeler")) {
                vehicle = new TwoWheeler(vehicleNum, ownerName, contactNumber, hasCarrier);
            } else {
                vehicle = new FourWheeler(vehicleNum, ownerName, contactNumber, "Sedan");
            }

            // Register vehicle in the parking system
            parkingSystem.registerVehicle(vehicle);
            outputTextArea.appendText("Vehicle registered: " + vehicle.getVehicleNum() + "\n");

            // Refresh the parking slots view
            refreshSlotTable();
        } catch (NumberFormatException e) {
            outputTextArea.appendText("Invalid contact number. Please enter a valid number.\n");
        }
    }

    @FXML
    private void viewSlots() {
        outputTextArea.appendText("Displaying all parking slots...\n");
        for (ParkingSlot slot : parkingSystem.getSlots()) {
            outputTextArea.appendText(slot.getSlotType() + " - " + (slot.isOccupied() ? "Occupied" : "Available") + "\n");
        }
    }

    @FXML
    private void generateReport() {
        outputTextArea.appendText("Generating report...\n");
        parkingSystem.generateReport();
    }

    @FXML
    private void sendTimeAlerts() {
        outputTextArea.appendText("Sending time alerts...\n");
        parkingSystem.notifyTimeAlerts();
    }

    private void refreshSlotTable() {
        ObservableList<ParkingSlot> slotList = FXCollections.observableArrayList(parkingSystem.getSlots());
        slotsTable.setItems(slotList);
    }
}
