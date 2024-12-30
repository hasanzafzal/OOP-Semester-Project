package com.example.oopfrontproject;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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

    public void initialize() {
        // Initialize ComboBox
        vehicleTypeComboBox.setItems(FXCollections.observableArrayList("Two Wheeler", "Four Wheeler"));

        // Add Parking Slots to Table
        slotColumn.setCellValueFactory(data -> data.getValue().getSlotTypeProperty());
        statusColumn.setCellValueFactory(data -> data.getValue().getIsOccupiedProperty());
    }

    @FXML
    private void registerVehicle() {
        String vehicleNum = vehicleNumberField.getText();
        String ownerName = ownerNameField.getText();
        String contactNum = contactNumberField.getText();
        String vehicleType = vehicleTypeComboBox.getValue();
        boolean hasCarrier = carrierCheckBox.isSelected();

        if (vehicleNum.isEmpty() || ownerName.isEmpty() || contactNum.isEmpty() || vehicleType == null) {
            outputTextArea.appendText("Please fill all required fields.\n");
            return;
        }

        Vehicle vehicle;
        if (vehicleType.equals("Two Wheeler")) {
            vehicle = new TwoWheeler(vehicleNum, ownerName, Long.parseLong(contactNum), hasCarrier);
        } else {
            vehicle = new FourWheeler(vehicleNum, ownerName, Long.parseLong(contactNum), "Sedan");
        }

        // Simulate parking registration
        outputTextArea.appendText("Vehicle Registered: " + vehicle.getVehicleNum() + "\n");
    }

    @FXML
    private void viewSlots() {
        // Simulate viewing slots
        outputTextArea.appendText("Displaying all parking slots...\n");
    }

    @FXML
    private void generateReport() {
        // Simulate generating report
        outputTextArea.appendText("Generating report...\n");
    }

    @FXML
    private void sendTimeAlerts() {
        // Simulate sending time alerts
        outputTextArea.appendText("Time alerts sent to all vehicles.\n");
    }
}
