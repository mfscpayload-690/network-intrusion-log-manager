# Network Intrusion Log Manager

## Overview
Network Intrusion Log Manager is a Java Swing desktop application designed to help users manage and monitor network intrusion logs. The application provides a user-friendly interface to add, view, and filter intrusion logs, backed by a MySQL database for persistent storage.

## Features
- **Add Log:** Easily add new intrusion logs with details such as IP address, threat type, severity, and timestamp.
- **View Logs:** View a table of intrusion logs with columns for Log ID, IP Address, Threat Type, Severity, and Timestamp.
- **Filter Logs:** Filter logs by severity and threat type (UI-only filtering).
- **Pre-Fed Sample Data:** On first launch, the application populates the database with 50 sample intrusion logs featuring diverse threat types.
- **Database Integration:** Uses MySQL as the backend database with JDBC for connectivity.
- **User Interface:** Built with Java Swing, featuring a dark theme and monospace fonts for readability.

## Threat Types Supported
- Unauthorized Access
- DDoS
- Malware
- Phishing
- Other
- Bruteforce
- SQL Injection
- MITM (Man-in-the-Middle)
- DNS Spoofing

## Prerequisites
- Java Development Kit (JDK) 8 or higher
- MySQL Server running locally or accessible remotely
- MySQL Connector/J (JDBC driver) included in the project (`mysql-connector-j-9.3.0.jar`)

## Setup Instructions

1. **Database Setup:**
   - Ensure MySQL server is running.
   - Create a database named `network_intrusion_logs`.
   - Create a user `appuser` with password `apppassword` and grant all privileges on the database.
   - Example SQL commands:
     ```sql
     CREATE DATABASE IF NOT EXISTS network_intrusion_logs;
     CREATE USER IF NOT EXISTS 'appuser'@'localhost' IDENTIFIED BY 'apppassword';
     GRANT ALL PRIVILEGES ON network_intrusion_logs.* TO 'appuser'@'localhost';
     FLUSH PRIVILEGES;
     ```

2. **Configure Database Connection:**
   - Verify the `javagp2/resources/db.properties` file contains the correct database URL, username, and password.

3. **Build and Run the Application:**
   - Compile the Java source files:
     ```
     javac -cp "javagp2/mysql-connector-j-9.3.0.jar" -d javagp2/bin javagp2/src/**/*.java
     ```
   - Run the application:
     ```
     java -cp "javagp2/bin:javagp2/mysql-connector-j-9.3.0.jar" ui.MainFrame
     ```

## Project Structure
- `src/dao`: Data Access Objects for database operations.
- `src/model`: Data models representing intrusion logs.
- `src/ui`: Java Swing UI components including forms and tables.
- `resources`: Configuration files such as database properties.
- `lib`: External libraries including MySQL Connector/J.

## Usage
- Launch the application.
- Use the navigation panel to switch between Add Log, View Logs, and Filter pages.
- Add new logs via the Add Log page.
- View and delete logs in the View Logs page.
- Apply filters in the Filter page (UI-only filtering).

## Notes
- The application populates 50 sample logs on first launch if the database is empty.
- Filtering is currently implemented as UI-only and does not affect database queries.
- Ensure the MySQL server is accessible and credentials are correct to avoid connection errors.

## License
This project is for academic use and is provided as-is without warranty.

## Contact
For questions or support, please contact the project maintainer.
<<<<<<< HEAD
Aravind Lal
=======
>>>>>>> 1c23cb7 (Initial commit of Network Intrusion Log Manager project)
