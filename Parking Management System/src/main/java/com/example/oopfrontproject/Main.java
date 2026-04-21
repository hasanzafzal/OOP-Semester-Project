package com.example.oopfrontproject;

import java.sql.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 820, 680);
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
            try (Connection connection = DriverManager.getConnection(URL);
                 Statement statement = connection.createStatement()) {

                statement.execute(
                        "CREATE TABLE IF NOT EXISTS Vehicles (" +
                                "vehicleNum TEXT PRIMARY KEY, " +
                                "ownerName TEXT, " +
                                "contactNum INTEGER, " +
                                "vehicleType TEXT, " +
                                "hasCarrier BOOLEAN)"
                );
                statement.execute(
                        "CREATE TABLE IF NOT EXISTS ParkingSlots (" +
                                "slotID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "slotType TEXT, " +
                                "isOccupied BOOLEAN DEFAULT 0)"
                );
                statement.execute(
                        "CREATE TABLE IF NOT EXISTS Tickets (" +
                                "ticketID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "entryTime TEXT, " +
                                "exitTime TEXT, " +
                                "vehicleNum TEXT, " +
                                "duration INTEGER, " +
                                "FOREIGN KEY(vehicleNum) REFERENCES Vehicles(vehicleNum))"
                );

                // Seed slots only if table is empty
                try (ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM ParkingSlots")) {
                    rs.next();
                    if (rs.getInt(1) == 0) {
                        statement.execute(
                                "INSERT INTO ParkingSlots (slotType, isOccupied) VALUES " +
                                        "('Car',0),('Bike',0),('Car',0),('Bike',0),('Car',0)"
                        );
                    }
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
        private final String vehicleNum;
        private final String ownerName;
        private final long contactNum;

        public Vehicle(String vehicleNum, String ownerName, long contactNum) {
            this.vehicleNum = vehicleNum;
            this.ownerName  = ownerName;
            this.contactNum = contactNum;
        }

        public String getVehicleNum()  { return vehicleNum; }
        public String getOwnerName()   { return ownerName; }
        public long   getContactNum()  { return contactNum; }

        /** Returns "Car" or "Bike" — used for slot-type matching. */
        public abstract String getSlotType();

        public String summary() {
            return String.format("Num: %s | Owner: %s | Contact: %d",
                    vehicleNum, ownerName, contactNum);
        }
    }

    static class FourWheeler extends Vehicle {
        private final String carType;   // e.g. Sedan, SUV

        public FourWheeler(String vehicleNum, String ownerName, long contactNum, String carType) {
            super(vehicleNum, ownerName, contactNum);
            this.carType = carType;
        }

        @Override public String getSlotType() { return "Car"; }
        public String getCarType()             { return carType; }

        @Override
        public String summary() {
            return super.summary() + " | Type: 4-Wheeler (" + carType + ")";
        }
    }

    static class TwoWheeler extends Vehicle {
        private final boolean hasCarrier;

        public TwoWheeler(String vehicleNum, String ownerName, long contactNum, boolean hasCarrier) {
            super(vehicleNum, ownerName, contactNum);
            this.hasCarrier = hasCarrier;
        }

        @Override public String getSlotType() { return "Bike"; }
        public boolean hasCarrier()            { return hasCarrier; }

        @Override
        public String summary() {
            return super.summary() + " | Type: 2-Wheeler | Carrier: " + (hasCarrier ? "Yes" : "No");
        }
    }

    static class ParkingSlot {
        private final int    slotID;
        private final String slotType;
        private boolean      isOccupied;

        public ParkingSlot(int slotID, String slotType, boolean isOccupied) {
            this.slotID     = slotID;
            this.slotType   = slotType;
            this.isOccupied = isOccupied;
        }

        public int     getSlotID()    { return slotID; }
        public String  getSlotType()  { return slotType; }
        public boolean isOccupied()   { return isOccupied; }
        public void    occupy()       { isOccupied = true; }
        public void    vacate()       { isOccupied = false; }
    }

    static class ParkingManagementSystem {

        private static final DateTimeFormatter FMT =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        /** Returns ALL slots with their current status. */
        public List<ParkingSlot> getAvailableSlots() {
            List<ParkingSlot> slots = new ArrayList<>();
            String sql = "SELECT slotID, slotType, isOccupied FROM ParkingSlots ORDER BY slotID";
            try (Connection con = DatabaseHandler.getConnection();
                 Statement  st  = con.createStatement();
                 ResultSet  rs  = st.executeQuery(sql)) {

                while (rs.next()) {
                    slots.add(new ParkingSlot(
                            rs.getInt("slotID"),
                            rs.getString("slotType"),
                            rs.getBoolean("isOccupied")
                    ));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return slots;
        }

        /**
         * Registers a vehicle: finds a matching free slot, inserts vehicle + ticket.
         * @return status message
         */
        public String registerVehicle(Vehicle vehicle) {
            String slotType = vehicle.getSlotType(); // "Car" or "Bike"

            try (Connection con = DatabaseHandler.getConnection()) {

                // Check duplicate
                try (PreparedStatement chk = con.prepareStatement(
                        "SELECT vehicleNum FROM Vehicles WHERE vehicleNum = ?")) {
                    chk.setString(1, vehicle.getVehicleNum());
                    try (ResultSet rs = chk.executeQuery()) {
                        if (rs.next()) {
                            return "ERROR: Vehicle " + vehicle.getVehicleNum() + " is already registered.";
                        }
                    }
                }

                // Find a free slot that matches the vehicle type
                int slotID = -1;
                try (PreparedStatement findSlot = con.prepareStatement(
                        "SELECT slotID FROM ParkingSlots WHERE isOccupied = 0 AND slotType = ? LIMIT 1")) {
                    findSlot.setString(1, slotType);
                    try (ResultSet rs = findSlot.executeQuery()) {
                        if (rs.next()) slotID = rs.getInt("slotID");
                    }
                }

                if (slotID == -1) {
                    return "ERROR: No available " + slotType + " slots.";
                }

                try (PreparedStatement ins = con.prepareStatement(
                        "INSERT INTO Vehicles (vehicleNum, ownerName, contactNum, vehicleType, hasCarrier) " +
                                "VALUES (?, ?, ?, ?, ?)")) {
                    ins.setString(1, vehicle.getVehicleNum());
                    ins.setString(2, vehicle.getOwnerName());
                    ins.setLong(3, vehicle.getContactNum());
                    ins.setString(4, vehicle instanceof FourWheeler ? "FourWheeler" : "TwoWheeler");
                    ins.setBoolean(5, vehicle instanceof TwoWheeler && ((TwoWheeler) vehicle).hasCarrier());
                    ins.executeUpdate();
                }

                try (PreparedStatement upd = con.prepareStatement(
                        "UPDATE ParkingSlots SET isOccupied = 1 WHERE slotID = ?")) {
                    upd.setInt(1, slotID);
                    upd.executeUpdate();
                }

                try (PreparedStatement tkt = con.prepareStatement(
                        "INSERT INTO Tickets (entryTime, vehicleNum) VALUES (?, ?)")) {
                    tkt.setString(1, LocalDateTime.now().format(FMT));
                    tkt.setString(2, vehicle.getVehicleNum());
                    tkt.executeUpdate();
                }

                return "OK: Vehicle " + vehicle.getVehicleNum() +
                        " registered in slot #" + slotID + " (" + slotType + ").";

            } catch (SQLException e) {
                e.printStackTrace();
                return "ERROR: " + e.getMessage();
            }
        }

        /**
         * Removes a vehicle: closes its ticket and frees the correct slot.
         * @return status message
         */
        public String removeVehicle(String vehicleNum) {
            try (Connection con = DatabaseHandler.getConnection()) {

                int    ticketID;
                String entryTimeStr;
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT ticketID, entryTime FROM Tickets " +
                                "WHERE vehicleNum = ? AND exitTime IS NULL")) {
                    ps.setString(1, vehicleNum);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            return "ERROR: No active ticket found for " + vehicleNum + ".";
                        }
                        ticketID     = rs.getInt("ticketID");
                        entryTimeStr = rs.getString("entryTime");
                    }
                }

                LocalDateTime entryTime = LocalDateTime.parse(entryTimeStr, FMT);
                LocalDateTime exitTime  = LocalDateTime.now();
                long hours = java.time.Duration.between(entryTime, exitTime).toHours();
                int billableHours = (int) Math.max(1, hours);

                try (PreparedStatement upd = con.prepareStatement(
                        "UPDATE Tickets SET exitTime = ?, duration = ? WHERE ticketID = ?")) {
                    upd.setString(1, exitTime.format(FMT));
                    upd.setInt(2, billableHours);
                    upd.setInt(3, ticketID);
                    upd.executeUpdate();
                }

                String vType;
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT vehicleType FROM Vehicles WHERE vehicleNum = ?")) {
                    ps.setString(1, vehicleNum);
                    try (ResultSet rs = ps.executeQuery()) {
                        vType = rs.next() ? rs.getString("vehicleType") : "Car";
                    }
                }
                String slotType = vType.equals("TwoWheeler") ? "Bike" : "Car";

                try (PreparedStatement free = con.prepareStatement(
                        "UPDATE ParkingSlots SET isOccupied = 0 " +
                                "WHERE slotID = (" +
                                "  SELECT slotID FROM ParkingSlots " +
                                "  WHERE isOccupied = 1 AND slotType = ? LIMIT 1)")) {
                    free.setString(1, slotType);
                    free.executeUpdate();
                }

                double fee = billableHours * 10.0;
                return String.format(
                        "OK: Vehicle %s removed. Duration: %d hr(s). Fee: $%.2f",
                        vehicleNum, billableHours, fee);

            } catch (SQLException e) {
                e.printStackTrace();
                return "ERROR: " + e.getMessage();
            }
        }

        /**
         * Generates a revenue/usage report.
         * @return multi-line report string
         */
        public String generateReport() {
            StringBuilder sb = new StringBuilder();
            sb.append("========== PARKING REPORT ==========\n");

            try (Connection con = DatabaseHandler.getConnection();
                 Statement  st  = con.createStatement()) {

                try (ResultSet rs = st.executeQuery(
                        "SELECT COUNT(*) AS total, " +
                                "SUM(CASE WHEN exitTime IS NOT NULL THEN 1 ELSE 0 END) AS completed, " +
                                "SUM(CASE WHEN exitTime IS NULL     THEN 1 ELSE 0 END) AS active " +
                                "FROM Tickets")) {
                    if (rs.next()) {
                        sb.append(String.format("Total Tickets    : %d%n", rs.getInt("total")));
                        sb.append(String.format("Active (parked)  : %d%n", rs.getInt("active")));
                        sb.append(String.format("Completed        : %d%n", rs.getInt("completed")));
                    }
                }

                try (ResultSet rs = st.executeQuery(
                        "SELECT SUM(duration * 10) AS revenue FROM Tickets WHERE exitTime IS NOT NULL")) {
                    if (rs.next()) {
                        sb.append(String.format("Total Revenue    : $%.2f%n", rs.getDouble("revenue")));
                    }
                }

                // Slot occupancy summary
                try (ResultSet rs = st.executeQuery(
                        "SELECT slotType, " +
                                "COUNT(*) AS total, " +
                                "SUM(isOccupied) AS occupied " +
                                "FROM ParkingSlots GROUP BY slotType")) {
                    sb.append("-------- Slot Summary --------\n");
                    while (rs.next()) {
                        int total    = rs.getInt("total");
                        int occupied = rs.getInt("occupied");
                        sb.append(String.format("  %-6s : %d/%d occupied%n",
                                rs.getString("slotType"), occupied, total));
                    }
                }

                sb.append("====================================\n");

            } catch (SQLException e) {
                sb.append("ERROR generating report: ").append(e.getMessage()).append("\n");
            }
            return sb.toString();
        }

        /**
         * Checks all active tickets and returns alert messages for those
         * parked longer than the threshold.
         * @param thresholdHours alert if parked longer than this
         * @return list of alert strings
         */
        public List<String> getTimeAlerts(int thresholdHours) {
            List<String> alerts = new ArrayList<>();
            String sql = "SELECT vehicleNum, entryTime FROM Tickets WHERE exitTime IS NULL";

            try (Connection con = DatabaseHandler.getConnection();
                 Statement  st  = con.createStatement();
                 ResultSet  rs  = st.executeQuery(sql)) {

                while (rs.next()) {
                    String        vNum      = rs.getString("vehicleNum");
                    LocalDateTime entryTime = LocalDateTime.parse(rs.getString("entryTime"), FMT);
                    long hours = java.time.Duration.between(entryTime, LocalDateTime.now()).toHours();
                    if (hours >= thresholdHours) {
                        alerts.add(String.format(
                                "ALERT: Vehicle %s has been parked for %d hour(s)!", vNum, hours));
                    }
                }
            } catch (SQLException e) {
                alerts.add("ERROR checking alerts: " + e.getMessage());
            }
            return alerts;
        }

        /**
         * Resets the entire system: clears all data and re-seeds the default slots.
         * @return status message
         */
        public String resetSystem() {
            try (Connection con = DatabaseHandler.getConnection();
                 Statement  st  = con.createStatement()) {

                st.execute("DELETE FROM Tickets");
                st.execute("DELETE FROM Vehicles");
                st.execute("DELETE FROM ParkingSlots");
                // Reset auto-increment counter
                st.execute("DELETE FROM sqlite_sequence WHERE name='ParkingSlots'");
                st.execute("DELETE FROM sqlite_sequence WHERE name='Tickets'");
                // Re-seed default slots
                st.execute(
                        "INSERT INTO ParkingSlots (slotType, isOccupied) VALUES " +
                                "('Car',0),('Bike',0),('Car',0),('Bike',0),('Car',0)"
                );
                return "OK: System reset. All data cleared and default slots restored.";

            } catch (SQLException e) {
                e.printStackTrace();
                return "ERROR during reset: " + e.getMessage();
            }
        }
    }
}