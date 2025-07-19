# Network Intrusion Log Manager

A Java Swing desktop application for managing network intrusion logs with MySQL database connectivity.

## Structure
- `src/model/`: Domain classes
- `src/dao/`: Data access and DB connection
- `src/ui/`: Swing GUI
- `resources/`: Config files
- `lib/`: External libraries (e.g., MySQL JDBC driver)

## Features
- Add, view, update, delete, and filter intrusion logs

## Setup
1. Add MySQL JDBC driver to `lib/`
2. Configure `resources/db.properties`
3. Build and run `App.java`
