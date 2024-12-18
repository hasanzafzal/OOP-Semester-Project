import java.util.Arrays;
import java.util.Scanner;

class Vehicle
{
    private String vehicleNum;
    private String ownerName;
    private long contactNum;
    public Vehicle(String vehicleNum, String ownerName, long contactNum)
    {
        this.vehicleNum = vehicleNum;
        this.ownerName = ownerName;
        this.contactNum = contactNum;
    }
    public String getVehicleNum()
    {
        return vehicleNum;
    }
    public String getOwnerName()
    {
        return ownerName;
    }
    public long getContactNum()
    {
        return contactNum;
    }
    public double calculateFee()
    {
        return 0;
    }
    public void displayDetails()
    {
        System.out.println("Vehicle Number: " + vehicleNum);
        System.out.println("Owner Name: " + ownerName);
        System.out.println("Contact Number: " + contactNum);
    }
}

class Car extends Vehicle
{
    private int numDoors;
    public Car(String vehicleNum, String ownerName, long contactNum, int numDoors)
    {
        super(vehicleNum, ownerName, contactNum);
        this.numDoors = numDoors;
    }

    public int getNumDoors()
    {
        return numDoors;
    }

    @Override
    public void displayDetails()
    {
        super.displayDetails();
        System.out.println("Vehicle Type: Car");
        System.out.println("Number of Doors: " + numDoors);
    }
}

class Bike extends Vehicle
{
    private boolean hasCarrier;
    public Bike(String vehicleNum, String ownerName, long contactNum, boolean hasCarrier)
    {
        super(vehicleNum, ownerName, contactNum);
        this.hasCarrier = hasCarrier;
    }
    public boolean hasCarrier()
    {
        return hasCarrier;
    }
    @Override
    public void displayDetails()
    {
        super.displayDetails();
        System.out.println("Vehicle Type: Bike");
        System.out.println("Has Carrier: " + (hasCarrier ? "Yes" : "No"));
    }
}

class ParkingSlot
{
    private String slotType;
    public ParkingSlot(String slotType)
    {
        this.slotType = slotType;
    }
    public void displaySlotDetails()
    {
        System.out.println("Parking Slot: " + slotType);
    }
}

class Ticket extends ParkingSlot
{
    private int ticketID;
    private String entryTime;
    private String exitTime;
    public Ticket(int ticketID, String entryTime, String exitTime)
    {
        super("Generic Slot");
        this.ticketID = ticketID;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
    }
    public void displayTicketDetails()
    {
        System.out.println("Ticket ID: " + ticketID);
        System.out.println("Entry Time: " + entryTime);
        System.out.println("Exit Time: " + exitTime);
    }
}

public class Main
{
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the vehicle number: ");
        String vehicleNum = scanner.nextLine();
        System.out.println("Enter the owner name: ");
        String ownerName = scanner.nextLine();
        System.out.println("Enter the contact number: ");
        long contactNum = scanner.nextLong();
        scanner.nextLine();
        System.out.println("Enter the vehicle type (Car/Bike): ");
        String vehicleType = scanner.nextLine();
        Vehicle vehicle;
        if (vehicleType.equalsIgnoreCase("Car"))
        {
            System.out.println("Enter the number of doors: ");
            int numDoors = scanner.nextInt();
            vehicle = new Car(vehicleNum, ownerName, contactNum, numDoors);
        }
        else if (vehicleType.equalsIgnoreCase("Bike"))
        {
            System.out.println("Does the bike have a carrier? (true/false): ");
            boolean hasCarrier = scanner.nextBoolean();
            vehicle = new Bike(vehicleNum, ownerName, contactNum, hasCarrier);
        }
        else
        {
            System.out.println("Invalid vehicle type. Exiting...");
            scanner.close();
            return;
        }
        System.out.println("Enter ticket ID: ");
        int ticketID = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter the entry time (HH:mm): ");
        String entryTime = scanner.nextLine();
        System.out.println("Enter the exit time (HH:mm): ");
        String exitTime = scanner.nextLine();
        Ticket ticket = new Ticket(ticketID, entryTime, exitTime);
        System.out.println("Enter the parking slot (e.g., Basement Parking): ");
        String parkingSlot = scanner.nextLine();
        ParkingSlot slot = new ParkingSlot(parkingSlot);
        System.out.println("\nVehicle Details:");
        vehicle.displayDetails();
        System.out.println("\nParking Slot Details:");
        slot.displaySlotDetails();
        System.out.println("\nTicket Details:");
        ticket.displayTicketDetails();
        scanner.close();
    }
}