package com.example.parkingmanagement;

import java.util.*;

class Vehicle {
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

    public double calculateFee(long duration) {
        return duration * 10;
    }

    public void displayDetails() {
        System.out.println("Vehicle Number: " + vehicleNum);
        System.out.println("Owner Name: " + ownerName);
        System.out.println("Contact Number: " + contactNum);
    }
}

class FourWheeler extends Vehicle {
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

class TwoWheeler extends Vehicle {
    private boolean hasCarrier;

    public TwoWheeler(String vehicleNum, String ownerName, long contactNum, boolean hasCarrier) {
        super(vehicleNum, ownerName, contactNum);
        this.hasCarrier = hasCarrier;
    }

    @Override
    public void displayDetails() {
        super.displayDetails();
        System.out.println("Vehicle Type: 2 Wheeler");
        System.out.println("Has Carrier: " + (hasCarrier ? "Yes" : "No"));
    }
}

class ParkingSlot {
    private String slotType;
    private boolean isOccupied;

    public ParkingSlot(String slotType) {
        this.slotType = slotType;
        this.isOccupied = false;
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

    public String getSlotType() {
        return slotType;
    }

    public void displaySlotDetails() {
        System.out.println("Parking Slot: " + slotType + " | Occupied: " + (isOccupied ? "Yes" : "No"));
    }
}

class Ticket {
    private int ticketID;
    private String entryTime;
    private String exitTime;
    private Vehicle vehicle;

    public Ticket(int ticketID, String entryTime, Vehicle vehicle) {
        this.ticketID = ticketID;
        this.entryTime = entryTime;
        this.vehicle = vehicle;
    }

    public void setExitTime(String exitTime) {
        this.exitTime = exitTime;
    }

    public void displayTicketDetails() {
        System.out.println("Ticket ID: " + ticketID);
        System.out.println("Entry Time: " + entryTime);
        System.out.println("Exit Time: " + exitTime);
        vehicle.displayDetails();
    }
}

class ParkingManagementSystem {
    private List<ParkingSlot> slots;
    private List<Ticket> tickets;
    private static int totalVehicles = 0;
    private static double totalRevenue = 0;

    public ParkingManagementSystem() {
        slots = new ArrayList<>();
        tickets = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            slots.add(new ParkingSlot("Slot " + (i + 1)));
        }
    }

    public void registerVehicle(Vehicle vehicle) {
        for (ParkingSlot slot : slots) {
            if (!slot.isOccupied()) {
                slot.occupy();
                System.out.println("Vehicle registered in " + slot.getSlotType());
                Scanner scanner = new Scanner(System.in);
                System.out.print("Enter the duration of parking in hours: ");
                long duration = scanner.nextLong();
                double fee = vehicle.calculateFee(duration);
                totalRevenue += fee;
                Ticket ticket = new Ticket(++totalVehicles, getCurrentTime(), vehicle);
                tickets.add(ticket);
                System.out.println("Parking Fee: $" + fee);
                return;
            }
        }
        System.out.println("No available slots for the vehicle.");
    }

    public void displaySlots() {
        for (ParkingSlot slot : slots) {
            slot.displaySlotDetails();
        }
    }

    public void generateReport() {
        System.out.println("Total Vehicles: " + totalVehicles);
        System.out.println("Total Revenue: $" + totalRevenue);
    }

    public void notifyTimeAlerts() {
        System.out.println("Time alerts: Check if any vehicle is about to exceed parking time.");
    }

    private String getCurrentTime() {
        return "12:00";
    }
}

