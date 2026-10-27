# Space Saver DB — Smart Parking Management System

Students Name: Hasan Zahid & Waleed Ahmed Razzaqi
Enrollment #: 01-131232-028 & 093 
Teachers: Engr. Aleem Ahmad & Engr. Raheela Ambrin  
Dept of SE, BUIC

Space Saver is a robust, Object-Oriented Parking Management System written in Java with a MySQL database backend. Designed to streamline parking operations, track vehicle entry and exit times, manage real-time slot availability, and handle billing calculations efficiently.

📌 Features

Slot Allocation & Tracking: Real-time management of available, occupied, and reserved parking spaces.

Vehicle Entry & Exit Logs: Automatically record timestamps, vehicle details, and license plate numbers.

Automated Fare Calculation: Calculates total parking fees based on duration and vehicle type.

Database Persistence: Integrated with MySQL using JDBC for real-time data storage, retrieval, and updates.

Clean Architecture: Designed following strict Object-Oriented Design Principles (SOLID) for high maintainability and extensibility.

🛠️ Tech Stack

Language: Java (JDK 17+)

Database: MySQL

Database Driver: MySQL Connector/J (mysql-connector-j-9.1.0.jar)

IDE: NetBeans / IntelliJ IDEA / Eclipse / VS Code

📁 Repository Structure

Space-Saver/
├── src/                          # Java Source Files
├── lib/                          # External Dependencies (MySQL Connector JDBC Jar)
├── sql/                          # Database Schema & Initialization Scripts
│   └── schema.sql
├── docs/                         # Project Documentation & Reports
├── README.md                     # Project Documentation
└── LICENSE                       # License Information


🚀 Getting Started

Follow these steps to set up and run Space Saver on your local machine.

Prerequisites

Java Development Kit (JDK 17 or higher)

MySQL Server & MySQL Workbench (optional)

1. Database Configuration

Start your local MySQL server.

Open your MySQL client/terminal and execute the schema script to create the necessary database and tables:

CREATE DATABASE IF NOT EXISTS parking_management;
USE parking_management;

-- Run the SQL scripts provided in sql/schema.sql


Update your database connection credentials in your Java project (typically inside DBConnection.java or Database.java):

String url = "jdbc:mysql://localhost:3306/parking_management";
String user = "your_mysql_username";
String password = "your_mysql_password";


2. Add MySQL Connector JAR

Ensure mysql-connector-j-9.1.0.jar (located in the lib/ directory) is added to your project's class path:

NetBeans: Right-click Libraries → Add JAR/Folder → select the driver JAR.

IntelliJ IDEA: File → Project Structure → Libraries → + → select the driver JAR.

3. Run the Application

Compile and launch the project through your preferred IDE or command line:

# Compile
javac -cp "lib/mysql-connector-j-9.1.0.jar" -d bin src/*.java

# Run
java -cp "bin:lib/mysql-connector-j-9.1.0.jar" Main


🎯 System Architecture & Design

Space Saver was designed with scalable OOP practices in mind:

Encapsulation: Domain models like Vehicle, ParkingSlot, and Ticket hide internal state and expose operations safely.

Inheritance & Polymorphism: Specialized handling for different vehicle classes (e.g., Cars, Motorcycles).

Separation of Concerns: Decoupled database interaction (DAO layer) from core business logic and user interface.

🤝 Contributing

Contributions, issues, and feature requests are welcome!

Feel free to check the issues page or submit a pull request.

📝 License

Distributed under the MIT License. See LICENSE for more information.
