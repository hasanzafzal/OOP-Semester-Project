package com.example.parkingmanagement;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ParkingManagementSystem pms = new ParkingManagementSystem();

        while (true) {
            System.out.println("1. Register Vehicle");
            System.out.println("2. Display Slots");
            System.out.println("3. Generate Report");
            System.out.println("4. Notify Time Alerts");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("Enter the vehicle number: ");
                    String vehicleNum = scanner.nextLine();
                    System.out.println("Enter the owner name: ");
                    String ownerName = scanner.nextLine();
                    System.out.println("Enter the contact number: ");
                    long contactNum = scanner.nextLong();
                    scanner.nextLine();
                    System.out.println("Enter the vehicle type (4 wheeler/2 wheeler): ");
                    String vehicleType = scanner.nextLine();
                    Vehicle vehicle;

                    if (vehicleType.equalsIgnoreCase("4 wheeler") || vehicleType.equals("4")) {
                        System.out.println("Enter the vehicle type (LTV/HTV): ");
                        String type = scanner.nextLine();
                        vehicle = new FourWheeler(vehicleNum, ownerName, contactNum, type);
                    } else if (vehicleType.equalsIgnoreCase("2 wheeler") || vehicleType.equals("2")) {
                        System.out.println("Does the 2 wheeler have a carrier? (true/false): ");
                        String carrierInput = scanner.nextLine().trim().toLowerCase();
                        boolean hasCarrier;

                        if (carrierInput.equals("true") || carrierInput.equals("t")) {
                            hasCarrier = true;
                        } else if (carrierInput.equals("false") || carrierInput.equals("f")) {
                            hasCarrier = false;
                        } else {
                            System.out.println("Invalid input for carrier. Please enter 'true', 'false', 't', or 'f'.");
                            continue;
                        }

                        vehicle = new TwoWheeler(vehicleNum, ownerName, contactNum, hasCarrier);
                    } else {
                        System.out.println("Invalid vehicle type. Exiting...");
                        continue;
                    }

                    pms.registerVehicle(vehicle);
                    break;
                case 2:
                    pms.displaySlots();
                    break;
                case 3:
                    pms.generateReport();
                    break;
                case 4:
                    pms.notifyTimeAlerts();
                    break;
                case 5:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
