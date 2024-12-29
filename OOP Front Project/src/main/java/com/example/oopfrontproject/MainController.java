package com.example.oopfrontproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;

public class MainController {
    @FXML
    private TextField vehicleNumberField;
    @FXML
    private TextField ownerNameField;
    @FXML
    private TextField contactNumberField;
    @FXML
    private TextField vehicleTypeField;
    @FXML
    private CheckBox hasCarrierCheckBox;
    @FXML
    private TextArea outputArea;
    private ParkingManagementSystem pms;
    public MainController() {
        pms = new ParkingManagementSystem();
    }
    @FXML
    public void initialize() {  }
    @FXML
    private void handleRegisterVehicle(ActionEvent event) {
        try {
            String vehicleNum = vehicleNumberField.getText();
            String ownerName = ownerNameField.getText();
            long contactNum = Long.parseLong(contactNumberField.getText());
            if (vehicleNum.isEmpty() || ownerName.isEmpty() || contactNum <= 0) {
                outputArea.setText("Please fill all the fields correctly.");
                return;
            }
            Vehicle vehicle;
            String vehicleType = vehicleTypeField.getText();
            boolean hasCarrier = hasCarrierCheckBox.isSelected();
            if (vehicleTypeField.getText().equalsIgnoreCase("FourWheeler")) {
                vehicle = new FourWheeler(vehicleNum, ownerName, contactNum, vehicleType);
            } else {
                vehicle = new TwoWheeler(vehicleNum, ownerName, contactNum, hasCarrier);
            }
            pms.registerVehicle(vehicle);
            outputArea.setText("Vehicle Registered Successfully.\n");
            pms.displaySlots();
        } catch (NumberFormatException e) {
            outputArea.setText("Please enter a valid contact number.");
        }
    }
    @FXML
    private void handleGenerateReport(ActionEvent event) {
        outputArea.setText("Generating Report...\n");
        pms.generateReport();
    }
    @FXML
    private void handleDisplaySlots(ActionEvent event) {
        outputArea.setText("Displaying Available Parking Slots...\n");
        pms.displaySlots();
    }
    @FXML
    private void handleNotifyAlerts(ActionEvent event) {
        outputArea.setText("Checking for Time Alerts...\n");
        pms.notifyTimeAlerts();
    }
}
