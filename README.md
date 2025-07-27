# Courier Service Management System

A comprehensive Java-based courier service management system built with JDBC and MySQL. This system provides complete CRUD operations for managing shipments, drivers, agents, and locations with real-time status tracking and analytics.

## Features

### Core Functionalities
- ✅ **Shipment Management**: Create, view, and track shipments
- ✅ **Driver Assignment**: Assign drivers to shipments with workload management
- ✅ **Status Tracking**: Real-time status updates with location tracking
- ✅ **Agent Management**: Manage agents at different locations
- ✅ **Location Management**: Handle multiple distribution centers
- ✅ **Analytics**: Delivery time analysis, delayed shipment tracking, volume reports

### Advanced Features
- 📊 **Analytics Dashboard**: Average delivery times, delayed shipments, daily volume
- 🔄 **Status Logging**: Complete audit trail of shipment movements
- ⏰ **ETA Management**: Estimated and actual delivery time tracking
- 🚫 **Workload Control**: Maximum 5 active shipments per driver
- 📍 **Location Updates**: Real-time location tracking with agent assignment

## Prerequisites

- Java 11 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

## Database Setup

1. **Install MySQL** if not already installed
2. **Run the schema script**:
   ```bash
   mysql -u your_username -p < sql/schema.sql
   ```
   Or execute the SQL commands in your MySQL client.

3. **Update Database Configuration**:
   Edit `src/util/DBConnection.java` and update the connection details:
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/courier_service";
   private static final String USERNAME = "your_username";
   private static final String PASSWORD = "your_password";
   ```

## Building the Fat JAR

### Option 1: Using Maven (Recommended)
```bash
# Clean and compile
mvn clean compile

# Create fat JAR with all dependencies
mvn package

# The fat JAR will be created in target/courier-service-system-1.0.0.jar
```

### Option 2: Manual JAR Creation
```bash
# Compile all Java files
javac -cp "lib/*" -d bin src/*.java src/*/*.java src/*/*/*.java

# Create JAR with manifest
jar cfm courier-service.jar manifest.txt -C bin . -C lib .

# Create manifest.txt with:
# Main-Class: Main
# Class-Path: mysql-connector-j-9.4.0.jar
```

## Running the Application

### Option 1: Using the Fat JAR
```bash
java -jar target/courier-service-system-1.0.0.jar
```

### Option 2: Using Maven
```bash
mvn exec:java -Dexec.mainClass="Main"
```

### Option 3: Direct Java Execution
```bash
java -cp "bin:lib/*" Main
```

## Usage Guide

Once the application starts, you'll see a menu with the following options:

1. **Create New Shipment**: Add new shipments with origin/destination
2. **View Shipment Status**: Track shipment progress with detailed logs
3. **List Pending Shipments**: View driver's pending assignments
4. **Find Average Delivery Time**: Analytics by route
5. **Show Delayed Shipments**: Identify and analyze delays
6. **Generate Daily Volume**: Shipment volume by location
7. **Assign Driver**: Assign drivers with workload validation
8. **Update Shipment Location**: Real-time location updates

## Database Schema

The system uses the following main tables:
- `users`: User authentication and roles
- `locations`: Distribution centers and hubs
- `agents`: Staff at each location
- `drivers`: Delivery personnel with vehicle details
- `shipments`: Core shipment data
- `driver_assignments`: Driver-shipment relationships
- `status_logs`: Complete audit trail
- `packages`: Package details (for future expansion)

## Project Structure

```
CourierServiceSystem/
├── src/                    # Source code
│   ├── dao/               # Data Access Objects
│   ├── model/             # Entity classes
│   ├── service/           # Business logic
│   ├── util/              # Utilities (DB connection)
│   └── Main.java          # Main application
├── sql/                   # Database scripts
│   └── schema.sql         # Complete database setup
├── lib/                   # Dependencies
├── bin/                   # Compiled classes
├── pom.xml               # Maven configuration
└── README.md             # This file
```

## Features Implemented

### ✅ CRUD Operations
- **Create**: New shipments, driver assignments, status logs
- **Read**: Shipment status, driver info, analytics reports
- **Update**: Location updates, status changes, driver assignments
- **Delete**: (Soft delete through status management)

### ✅ Advanced Analytics
- Average delivery time per route
- Delayed shipment identification
- Daily shipment volume by location
- Driver workload analysis

### ✅ Real-time Tracking
- Status logging with timestamps
- Location-based updates
- Agent assignment tracking
- ETA vs actual delivery time

### ✅ Data Validation
- Driver workload limits (max 5 active shipments)
- Status progression validation
- Location-agent assignment validation
- Completed shipment protection

## Technical Implementation

- **JDBC**: Direct database connectivity
- **DAO Pattern**: Clean data access layer
- **Service Layer**: Business logic separation
- **Connection Pooling**: Efficient database connections
- **Error Handling**: Comprehensive exception management
- **Logging**: Complete audit trail

## Testing the System

1. **Start the application**
2. **Create a test shipment** (Option 1)
3. **Assign a driver** (Option 7)
4. **Update location** (Option 8)
5. **View status logs** (Option 2)
6. **Check analytics** (Options 4, 5, 6)

## Troubleshooting

### Common Issues:
1. **Database Connection**: Verify MySQL is running and credentials are correct
2. **JAR Execution**: Ensure Java 11+ is installed
3. **Dependencies**: Maven will automatically download MySQL connector
4. **Permissions**: Ensure database user has proper permissions

### Error Messages:
- "Driver not found": Check driver ID exists in database
- "No agents at location": Verify agents are assigned to locations
- "Maximum shipments reached": Driver has 5 active shipments

## Contributing

This is a JDBC project for educational purposes. The system demonstrates:
- Database design and normalization
- JDBC programming best practices
- Object-oriented design patterns
- Real-world application development

## License

This project is created for educational purposes as part of a DBMS course assignment.

---

**Note**: This system is designed to be a comprehensive courier service management solution with all the features mentioned in the instructor's requirements. The fat JAR includes all dependencies and can be run directly with `java -jar`.
