-- Courier Service System Database Schema
-- Created for JDBC Project

-- Drop database if exists and create new one
DROP DATABASE IF EXISTS courier_service;
CREATE DATABASE courier_service;
USE courier_service;

-- Users table
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role ENUM('admin', 'agent', 'driver') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Locations table
CREATE TABLE locations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    address TEXT NOT NULL,
    city VARCHAR(50) NOT NULL,
    state VARCHAR(50) NOT NULL,
    postal_code VARCHAR(10) NOT NULL,
    contact_number VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Agents table
CREATE TABLE agents (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    location_id INT,
    name VARCHAR(100) NOT NULL,
    contact_details VARCHAR(100),
    status ENUM('active', 'inactive') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (location_id) REFERENCES locations(id) ON DELETE SET NULL
);

-- Drivers table
CREATE TABLE drivers (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    name VARCHAR(100) NOT NULL,
    license_number VARCHAR(50) UNIQUE NOT NULL,
    contact_details VARCHAR(100),
    vehicle_type VARCHAR(50),
    vehicle_number VARCHAR(20),
    status ENUM('active', 'inactive', 'on_delivery') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Shipments table
CREATE TABLE shipments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    origin_id INT NOT NULL,
    destination_id INT NOT NULL,
    status ENUM('pending', 'in_transit', 'arrived', 'returned', 'completed') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (origin_id) REFERENCES locations(id),
    FOREIGN KEY (destination_id) REFERENCES locations(id)
);

-- Driver assignments table
CREATE TABLE driver_assignments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    driver_id INT NOT NULL,
    shipment_id INT NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estimated_delivery TIMESTAMP,
    actual_delivery TIMESTAMP NULL,
    status ENUM('assigned', 'in_progress', 'completed', 'cancelled') DEFAULT 'assigned',
    FOREIGN KEY (driver_id) REFERENCES drivers(id),
    FOREIGN KEY (shipment_id) REFERENCES shipments(id),
    UNIQUE KEY unique_shipment_assignment (shipment_id)
);

-- Status logs table
CREATE TABLE status_logs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    shipment_id INT NOT NULL,
    status ENUM('pending', 'in_transit', 'arrived', 'returned', 'completed') NOT NULL,
    location_id INT,
    agent_id INT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    FOREIGN KEY (shipment_id) REFERENCES shipments(id),
    FOREIGN KEY (location_id) REFERENCES locations(id),
    FOREIGN KEY (agent_id) REFERENCES agents(id)
);

-- Packages table (for future expansion)
CREATE TABLE packages (
    id INT PRIMARY KEY AUTO_INCREMENT,
    shipment_id INT NOT NULL,
    description TEXT,
    weight DECIMAL(8,2),
    dimensions VARCHAR(50),
    declared_value DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (shipment_id) REFERENCES shipments(id)
);

-- Insert sample data
INSERT INTO users (username, password, email, role) VALUES
('admin', 'admin123', 'admin@courier.com', 'admin'),
('agent1', 'agent123', 'agent1@courier.com', 'agent'),
('driver1', 'driver123', 'driver1@courier.com', 'driver');

INSERT INTO locations (name, address, city, state, postal_code, contact_number) VALUES
('Mumbai Central Hub', '123 Main Street', 'Mumbai', 'Maharashtra', '400001', '+91-22-12345678'),
('Delhi Distribution Center', '456 Park Avenue', 'Delhi', 'Delhi', '110001', '+91-11-87654321'),
('Bangalore Warehouse', '789 Tech Park', 'Bangalore', 'Karnataka', '560001', '+91-80-11223344'),
('Chennai Hub', '321 Marina Road', 'Chennai', 'Tamil Nadu', '600001', '+91-44-55667788'),
('Kolkata Center', '654 Howrah Bridge', 'Kolkata', 'West Bengal', '700001', '+91-33-99887766');

INSERT INTO agents (user_id, location_id, name, contact_details) VALUES
(2, 1, 'Rajesh Kumar', '+91-9876543210'),
(2, 2, 'Priya Sharma', '+91-9876543211'),
(2, 3, 'Amit Patel', '+91-9876543212'),
(2, 4, 'Sneha Reddy', '+91-9876543213'),
(2, 5, 'Vikram Singh', '+91-9876543214');

INSERT INTO drivers (user_id, name, license_number, contact_details, vehicle_type, vehicle_number) VALUES
(3, 'Rahul Verma', 'DL123456789', '+91-9876543220', 'Bike', 'MH-01-AB-1234'),
(3, 'Suresh Kumar', 'DL987654321', '+91-9876543221', 'Van', 'DL-02-CD-5678'),
(3, 'Mohan Singh', 'KA456789123', '+91-9876543222', 'Truck', 'KA-03-EF-9012'),
(3, 'Lakshmi Devi', 'TN789123456', '+91-9876543223', 'Bike', 'TN-04-GH-3456'),
(3, 'Arjun Reddy', 'WB321654987', '+91-9876543224', 'Van', 'WB-05-IJ-7890');

-- Insert sample shipments
INSERT INTO shipments (origin_id, destination_id, status) VALUES
(1, 2, 'completed'),
(2, 3, 'in_transit'),
(3, 4, 'pending'),
(4, 5, 'arrived'),
(5, 1, 'completed');

-- Insert sample driver assignments
INSERT INTO driver_assignments (driver_id, shipment_id, estimated_delivery) VALUES
(1, 1, DATE_ADD(NOW(), INTERVAL 24 HOUR)),
(2, 2, DATE_ADD(NOW(), INTERVAL 48 HOUR)),
(3, 3, DATE_ADD(NOW(), INTERVAL 72 HOUR)),
(4, 4, DATE_ADD(NOW(), INTERVAL 12 HOUR)),
(5, 5, DATE_ADD(NOW(), INTERVAL 36 HOUR));

-- Insert sample status logs
INSERT INTO status_logs (shipment_id, status, location_id, agent_id) VALUES
(1, 'pending', 1, 1),
(1, 'in_transit', 2, 2),
(1, 'completed', 2, 2),
(2, 'pending', 2, 2),
(2, 'in_transit', 3, 3),
(3, 'pending', 3, 3),
(4, 'pending', 4, 4),
(4, 'arrived', 5, 5),
(5, 'pending', 5, 5),
(5, 'completed', 1, 1); 