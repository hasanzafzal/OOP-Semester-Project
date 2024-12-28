#include <iostream>
#include <string>
using namespace std;

class Vehicle {
private:
    string vehicleNum;
    string ownerName;
    long contactNum;

public:
    Vehicle(string vehicleNum, string ownerName, long contactNum) {
        this->vehicleNum = vehicleNum;
        this->ownerName = ownerName;
        this->contactNum = contactNum;
    }

    string getVehicleNum() {
        return vehicleNum;
    }

    string getOwnerName() {
        return ownerName;
    }

    long getContactNum() {
        return contactNum;
    }

    virtual double calculateFee(long duration) {
        return duration * 10;
    }

    virtual void displayDetails() {
        cout << "Vehicle Number: " << vehicleNum << endl;
        cout << "Owner Name: " << ownerName << endl;
        cout << "Contact Number: " << contactNum << endl;
    }
};

class FourWheeler : public Vehicle {
private:
    string vehicleType;

public:
    FourWheeler(string vehicleNum, string ownerName, long contactNum, string vehicleType)
        : Vehicle(vehicleNum, ownerName, contactNum) {
        this->vehicleType = vehicleType;
    }

    void displayDetails() override {
        Vehicle::displayDetails();
        cout << "Vehicle Type: 4 Wheeler (" << vehicleType << ")" << endl;
    }
};

class TwoWheeler : public Vehicle {
private:
    bool hasCarrier;

public:
    TwoWheeler(string vehicleNum, string ownerName, long contactNum, bool hasCarrier)
        : Vehicle(vehicleNum, ownerName, contactNum) {
        this->hasCarrier = hasCarrier;
    }

    void displayDetails() override {
        Vehicle::displayDetails();
        cout << "Vehicle Type: 2 Wheeler" << endl;
        cout << "Has Carrier: " << (hasCarrier ? "Yes" : "No") << endl;
    }
};

class ParkingSlot {
private:
    string slotType;
    bool isOccupied;

public:
    ParkingSlot(string slotType) {
        this->slotType = slotType;
        isOccupied = false;
    }

    bool isOccupiedSlot() {  // Renamed to public method for access
        return isOccupied;
    }

    void occupy() {
        isOccupied = true;
    }

    void vacate() {
        isOccupied = false;
    }

    string getSlotType() {
        return slotType;
    }

    void displaySlotDetails() {
        cout << "Parking Slot: " << slotType << " | Occupied: " << (isOccupied ? "Yes" : "No") << endl;
    }
};

class Ticket {
private:
    int ticketID;
    string entryTime;
    string exitTime;
    Vehicle* vehicle;

public:
    Ticket(int ticketID, string entryTime, Vehicle* vehicle) {
        this->ticketID = ticketID;
        this->entryTime = entryTime;
        this->vehicle = vehicle;
    }

    void setExitTime(string exitTime) {
        this->exitTime = exitTime;
    }

    void displayTicketDetails() {
        cout << "Ticket ID: " << ticketID << endl;
        cout << "Entry Time: " << entryTime << endl;
        cout << "Exit Time: " << exitTime << endl;
        vehicle->displayDetails();
    }
};

class ParkingManagementSystem {
private:
    ParkingSlot* slots[10];
    Ticket* tickets[100];
    static int totalVehicles;
    static double totalRevenue;

public:
    ParkingManagementSystem() {
        for (int i = 0; i < 10; i++) {
            slots[i] = new ParkingSlot("Slot " + to_string(i + 1));
        }
    }

    void registerVehicle(Vehicle* vehicle) {
        for (int i = 0; i < 10; i++) {
            if (!slots[i]->isOccupiedSlot()) {  // Use the renamed function
                slots[i]->occupy();
                cout << "Vehicle registered in " << slots[i]->getSlotType() << endl;
                long duration;
                cout << "Enter the duration of parking in hours: ";
                cin >> duration;
                double fee = vehicle->calculateFee(duration);
                totalRevenue += fee;
                Ticket* ticket = new Ticket(++totalVehicles, getCurrentTime(), vehicle);
                tickets[totalVehicles - 1] = ticket;
                cout << "Parking Fee: $" << fee << endl;
                return;
            }
        }
        cout << "No available slots for the vehicle." << endl;
    }

    void displaySlots() {
        for (int i = 0; i < 10; i++) {
            slots[i]->displaySlotDetails();
        }
    }

    void generateReport() {
        cout << "Total Vehicles: " << totalVehicles << endl;
        cout << "Total Revenue: $" << totalRevenue << endl;
    }

    void notifyTimeAlerts() {
        cout << "Time alerts: Check if any vehicle is about to exceed parking time." << endl;
    }

    string getCurrentTime() {
        return "12:00";
    }

    ~ParkingManagementSystem() {
        for (int i = 0; i < 10; i++) {
            delete slots[i];
        }
        for (int i = 0; i < totalVehicles; i++) {
            delete tickets[i];
        }
    }
};

int ParkingManagementSystem::totalVehicles = 0;
double ParkingManagementSystem::totalRevenue = 0;

int main() {
    ParkingManagementSystem pms;
    while (true) {
        cout << "1. Register Vehicle" << endl;
        cout << "2. Display Slots" << endl;
        cout << "3. Generate Report" << endl;
        cout << "4. Notify Time Alerts" << endl;
        cout << "5. Exit" << endl;
        cout << "Choose an option: ";
        int choice;
        cin >> choice;
        cin.ignore();  // to ignore the newline character

        switch (choice) {
        case 1: {
            string vehicleNum, ownerName, vehicleType, carrierInput;
            long contactNum;
            cout << "Enter the vehicle number: ";
            getline(cin, vehicleNum);
            cout << "Enter the owner name: ";
            getline(cin, ownerName);
            cout << "Enter the contact number: ";
            cin >> contactNum;
            cin.ignore();
            cout << "Enter the vehicle type (4 wheeler/2 wheeler): ";
            getline(cin, vehicleType);

            Vehicle* vehicle;
            if (vehicleType == "4 wheeler" || vehicleType == "4") {
                cout << "Enter the vehicle type (LTV/HTV): ";
                string type;
                getline(cin, type);
                vehicle = new FourWheeler(vehicleNum, ownerName, contactNum, type);
            }
            else if (vehicleType == "2 wheeler" || vehicleType == "2") {
                cout << "Does the 2 wheeler have a carrier? (true/false): ";
                getline(cin, carrierInput);
                bool hasCarrier = (carrierInput == "true" || carrierInput == "t");
                vehicle = new TwoWheeler(vehicleNum, ownerName, contactNum, hasCarrier);
            }
            else {
                cout << "Invalid vehicle type. Exiting..." << endl;
                continue;
            }

            pms.registerVehicle(vehicle);
            delete vehicle;
            break;
        }
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
            cout << "Exiting..." << endl;
            return 0;
        default:
            cout << "Invalid choice. Please try again." << endl;
        }
    }
}
