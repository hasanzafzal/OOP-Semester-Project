package com.example.oopfrontproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;
import java.util.List;

public class MainController {

    @FXML private TextField   vehicleNumberField;
    @FXML private TextField   ownerNameField;
    @FXML private TextField   contactNumberField;
    @FXML private ComboBox<String> vehicleTypeComboBox;
    @FXML private CheckBox    hasCarrierCheckBox;

    // ── Remove field ──────────────────────────────────────
    @FXML private TextField   removeVehicleField;

    // ── Output ────────────────────────────────────────────
    @FXML private TextArea    outputArea;

    @FXML private TableView<Main.ParkingSlot>          slotsTable;
    @FXML private TableColumn<Main.ParkingSlot, String> slotColumn;
    @FXML private TableColumn<Main.ParkingSlot, String> statusColumn;

    private final Main.ParkingManagementSystem pms = new Main.ParkingManagementSystem();

    @FXML
    public void initialize() {
        vehicleTypeComboBox.setItems(
                FXCollections.observableArrayList("Two Wheeler", "Four Wheeler"));

        slotColumn.setCellValueFactory(data -> new SimpleStringProperty(
                "Slot #" + data.getValue().getSlotID() +
                        " (" + data.getValue().getSlotType() + ")"));

        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().isOccupied() ? "🔴 Occupied" : "🟢 Available"));

        slotsTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Main.ParkingSlot slot, boolean empty) {
                super.updateItem(slot, empty);
                if (empty || slot == null) {
                    setStyle("");
                } else if (slot.isOccupied()) {
                    setStyle("-fx-background-color: #ffe0e0;");   // light red
                } else {
                    setStyle("-fx-background-color: #e0ffe0;");   // light green
                }
            }
        });

        refreshSlotTable();
    }

    @FXML
    private void handleRegisterVehicle() {
        String vehicleNum  = vehicleNumberField.getText().trim();
        String ownerName   = ownerNameField.getText().trim();
        String contactStr  = contactNumberField.getText().trim();
        String vehicleType = vehicleTypeComboBox.getValue();
        boolean hasCarrier = hasCarrierCheckBox.isSelected();

        if (vehicleNum.isEmpty() || ownerName.isEmpty() ||
                contactStr.isEmpty()  || vehicleType == null) {
            appendOutput("⚠ Please fill in all fields before registering.\n");
            return;
        }

        long contactNum;
        try {
            contactNum = Long.parseLong(contactStr);
        } catch (NumberFormatException e) {
            appendOutput("⚠ Contact number must be numeric.\n");
            return;
        }

        Main.Vehicle vehicle;
        if ("Two Wheeler".equals(vehicleType)) {
            vehicle = new Main.TwoWheeler(vehicleNum, ownerName, contactNum, hasCarrier);
        } else {
            vehicle = new Main.FourWheeler(vehicleNum, ownerName, contactNum, "Sedan");
        }

        String result = pms.registerVehicle(vehicle);
        appendOutput(result + "\n");

        if (result.startsWith("OK")) {
            clearRegistrationFields();
            refreshSlotTable();
        }
    }

    @FXML
    private void removeVehicle() {
        String vehicleNum = removeVehicleField.getText().trim();
        if (vehicleNum.isEmpty()) {
            appendOutput("⚠ Enter a vehicle number to remove.\n");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Removal");
        confirm.setHeaderText("Remove vehicle: " + vehicleNum);
        confirm.setContentText("This will calculate the fee and free the slot. Continue?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;

        String result = pms.removeVehicle(vehicleNum);
        appendOutput(result + "\n");

        if (result.startsWith("OK")) {
            removeVehicleField.clear();
            refreshSlotTable();
        }
    }

    @FXML
    private void handleDisplaySlots() {
        appendOutput("Refreshing parking slot view...\n");
        refreshSlotTable();
    }

    @FXML
    private void handleGenerateReport() {
        String report = pms.generateReport();
        appendOutput(report);
    }

    @FXML
    private void handleNotifyAlerts() {
        List<String> alerts = pms.getTimeAlerts(2); // threshold: 2 hours
        if (alerts.isEmpty()) {
            appendOutput("✔ No vehicles have exceeded the parking time limit.\n");
        } else {
            alerts.forEach(a -> {
                appendOutput(a + "\n");
                Alert dialog = new Alert(Alert.AlertType.WARNING);
                dialog.setTitle("Parking Time Alert");
                dialog.setHeaderText("Parking Time Exceeded");
                dialog.setContentText(a);
                dialog.showAndWait();
            });
        }
    }

    @FXML
    private void handleResetSystem() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm System Reset");
        confirm.setHeaderText("⚠ Reset Entire System");
        confirm.setContentText(
                "This will permanently delete ALL vehicles, tickets, and slot data,\n" +
                        "and restore the default parking slots.\n\nAre you sure?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;

        String result = pms.resetSystem();
        appendOutput(result + "\n");
        clearRegistrationFields();
        removeVehicleField.clear();
        refreshSlotTable();
    }

    @FXML
    private void handleClearOutput() {
        outputArea.clear();
    }

    private void refreshSlotTable() {
        ObservableList<Main.ParkingSlot> slots =
                FXCollections.observableArrayList(pms.getAvailableSlots());
        slotsTable.setItems(slots);
        slotsTable.refresh();
    }

    private void appendOutput(String text) {
        outputArea.appendText(text);
        outputArea.setScrollTop(Double.MAX_VALUE);
    }

    private void clearRegistrationFields() {
        vehicleNumberField.clear();
        ownerNameField.clear();
        contactNumberField.clear();
        vehicleTypeComboBox.setValue(null);
        hasCarrierCheckBox.setSelected(false);
    }
}