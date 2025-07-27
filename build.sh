#!/bin/bash

# Courier Service System - Build Script
echo "Building Courier Service System..."

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven is not installed. Please install Maven first."
    echo "Visit: https://maven.apache.org/install.html"
    exit 1
fi

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed. Please install Java 11 or higher."
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 11 ]; then
    echo "Error: Java 11 or higher is required. Current version: $JAVA_VERSION"
    exit 1
fi

echo "Java version: $(java -version 2>&1 | head -n 1)"
echo "Maven version: $(mvn -version 2>&1 | head -n 1)"

# Clean previous builds
echo "Cleaning previous builds..."
mvn clean

# Compile the project
echo "Compiling project..."
mvn compile

# Create fat JAR
echo "Creating fat JAR..."
mvn package

# Check if build was successful
if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    echo "📦 Fat JAR created: target/courier-service-system-1.0.0.jar"
    echo ""
    echo "To run the application:"
    echo "  java -jar target/courier-service-system-1.0.0.jar"
    echo ""
    echo "Make sure to:"
    echo "  1. Set up the database using sql/schema.sql"
    echo "  2. Update database credentials in src/util/DBConnection.java"
else
    echo "❌ Build failed!"
    exit 1
fi 