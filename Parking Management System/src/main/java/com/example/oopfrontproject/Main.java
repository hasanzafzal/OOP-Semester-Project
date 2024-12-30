package com.example.oopfrontproject;

import java.sql.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Parking Management System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    static class DatabaseHandler {
        private static final String URL = "jdbc:sqlite:parking_system.db";

        static {
            try (Connection connection = DriverManager.getConnection(URL)) {
                Statement statement = connection.createStatement();
                statement.execute("CREATE TABLE IF NOT EXISTS Vehicles (" +
                        "vehicleNum TEXT PRIMARY KEY, ownerName TEXT, contactNum INTEGER, vehicleType TEXT, hasCarrier BOOLEAN)");
                statement.execute("CREATE TABLE IF NOT EXISTS ParkingSlots (" +
                        "slotID INTEGER PRIMARY KEY AUTOINCREMENT, slotType TEXT, isOccupied BOOLEAN)");
                statement.execute("CREATE TABLE IF NOT EXISTS Tickets (" +
                        "ticketID INTEGER PRIMARY KEY AUTOINCREMENT, entryTime TEXT, exitTime TEXT, vehicleNum TEXT, duration INTEGER, FOREIGN KEY(vehicleNum) REFERENCES Vehicles(vehicleNum))");

                ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM ParkingSlots");
                rs.next();
                if (rs.getInt(1) == 0) {
                    statement.execute("INSERT INTO ParkingSlots (slotType, isOccupied) VALUES ('Car', 0), ('Bike', 0), ('Car',0), ('Bike',0), ('Car',0)");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(URL);
        }
    }
    abstract static class Vehicle {
        private String vehicleNum;
        private String ownerName;
        private long contactNum;

        public Vehicle(String vehicleNum, String ownerName, long contactNum) {
            this.vehicleNum = vehicleNum;
            this.ownerName = ownerName;
            this.contactNum = contactNum;
        }

        public String getVehicleNum() {
            return vehicleNum;
        }

        public String getOwnerName() {
            return ownerName;
        }

        public long getContactNum() {
            return contactNum;
        }

        public void displayDetails() {
            System.out.println("Vehicle Number: " + vehicleNum);
            System.out.println("Owner Name: " + ownerName);
            System.out.println("Contact Number: " + contactNum);
        }
    }

    static class FourWheeler extends Vehicle {
        private String vehicleType;

        public FourWheeler(String vehicleNum, String ownerName, long contactNum, String vehicleType) {
            super(vehicleNum, ownerName, contactNum);
            this.vehicleType = vehicleType;
        }

        @Override
        public void displayDetails() {
            super.displayDetails();
            System.out.println("Vehicle Type: 4 Wheeler (" + vehicleType + ")");
        }
    }

    static class TwoWheeler extends Vehicle {
        private boolean hasCarrier;

        public TwoWheeler(String vehicleNum, String ownerName, long contactNum, boolean hasCarrier) {
            super(vehicleNum, ownerName, contactNum);
            this.hasCarrier = hasCarrier;
        }

        public boolean hasCarrier() {
            return hasCarrier;
        }

        @Override
        public void displayDetails() {
            super.displayDetails();
            System.out.println("Vehicle Type: 2 Wheeler");
            System.out.println("Has Carrier: " + (hasCarrier ? "Yes" : "No"));
        }
    }

    static class ParkingSlot {
        String slotType;
        boolean isOccupied;

        public ParkingSlot(String slotType) {
            this.slotType = slotType;
            this.isOccupied = false; // Initialize to false
        }

        public String getSlotType() {
            return slotType;
        }

        public boolean isOccupied() {
            return isOccupied;
        }

        public void occupy() {
            isOccupied = true;
        }

        public void vacate() {
            isOccupied = false;
        }
    }

    static class Ticket {
        private int ticketID;
        private String entryTime;
        private String exitTime;
        private Vehicle vehicle;
        private int duration;

        public Ticket(int ticketID, String entryTime, Vehicle vehicle) {
            this.ticketID = ticketID;
            this.entryTime = entryTime;
            this.vehicle = vehicle;
        }

        public void setExitTime(String exitTime) {
            this.exitTime = exitTime;
        }
        public void setDuration(int duration){this.duration = duration;}
        public int getDuration(){return duration;}

        public void displayTicketDetails() {
            System.out.println("Ticket ID: " + ticketID);
            System.out.println("Entry Time: " + entryTime);
            System.out.println("Exit Time: " + exitTime);
            vehicle.displayDetails();
        }
    }

    static class ParkingManagementSystem {

        public List<ParkingSlot> getAvailableSlots() {
            List<ParkingSlot> slots = new ArrayList<>();
            try (Connection connection = DatabaseHandler.getConnection();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT slotID, slotType, isOccupied FROM ParkingSlots")) {
                while (resultSet.next()) {
                    ParkingSlot slot = new ParkingSlot(resultSet.getString("slotType"));
                    slot.isOccupied = resultSet.getBoolean("isOccupied");
                    slots.add(slot);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return slots;
        }

        public void registerVehicle(Vehicle vehicle) {
            try (Connection connection = DatabaseHandler.getConnection()) {
                String findSlotQuery = "SELECT slotID FROM ParkingSlots WHERE isOccupied = 0 LIMIT 1";
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(findSlotQuery)) {

                    if (!rs.next()) {
                        System.out.println("No available slots for the vehicle.");
                        return;
                    }

                    int slotID = rs.getInt("slotID");

                    String vehicleQuery = "INSERT INTO Vehicles (vehicleNum, ownerName, contactNum, vehicleType, hasCarrier) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement vehicleStmt = connection.prepareStatement(vehicleQuery)) {
                        vehicleStmt.setString(1, vehicle.getVehicleNum());
                        vehicleStmt.setString(2, vehicle.getOwnerName());
                        vehicleStmt.setLong(3, vehicle.getContactNum());
                        vehicleStmt.setString(4, vehicle instanceof FourWheeler ? "FourWheeler" : "TwoWheeler");
                        vehicleStmt.setBoolean(5, (vehicle instanceof TwoWheeler) ? ((TwoWheeler) vehicle).hasCarrier() : false);
                        vehicleStmt.executeUpdate();
                    }

                    String occupySlotQuery = "UPDATE ParkingSlots SET isOccupied = 1 WHERE slotID = ?";
                    try (PreparedStatement slotStmt = connection.prepareStatement(occupySlotQuery)) {
                        slotStmt.setInt(1, slotID);
                        slotStmt.executeUpdate();
                    }

                    String ticketQuery = "INSERT INTO Tickets (entryTime, vehicleNum) VALUES (?, ?)";
                    try (PreparedStatement ticketStmt = connection.prepareStatement(ticketQuery)) {
                        ticketStmt.setString(1, getCurrentTime());
                        ticketStmt.setString(2, vehicle.getVehicleNum());
                        ticketStmt.executeUpdate();
                    }

                    System.out.println("Vehicle registered successfully. Slot: " + slotID);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        public void generateReport() {
            try (Connection connection = DatabaseHandler.getConnection()) {
                String query = "SELECT COUNT(*) AS vehicleCount, SUM(duration * 10) AS revenue FROM Tickets";
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(query)) {
                    if (rs.next()) {
                        System.out.println("Total Vehicles: " + rs.getInt("vehicleCount"));
                        System.out.println("Total Revenue: $" + rs.getDouble("revenue"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private String getCurrentTime() {
            return java.time.LocalDateTime.now().toString();
        }
    }
}
