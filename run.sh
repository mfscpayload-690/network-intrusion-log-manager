#!/bin/bash

# Network Intrusion Log Manager - Launch Script
# Enhanced version with smooth hover effects and working delete functionality

echo "🛡️ Starting Network Intrusion Log Manager..."
echo "🔧 Enhanced with smooth hover effects and improved UI"

# Check if MySQL is running
if ! systemctl is-active --quiet mysql; then
    echo "🔄 Starting MySQL service..."
    sudo systemctl start mysql
fi

# Launch the application
echo "🚀 Launching application..."
java -cp javagp2/bin:javagp2/lib/mysql-connector-j-9.3.0.jar:javagp2/lib/jackson-core-2.15.2.jar:javagp2/lib/jackson-databind-2.15.2.jar:javagp2/lib/jackson-annotations-2.15.2.jar:javagp2/lib/jackson-datatype-jsr310-2.15.2.jar:javagp2/resources App

echo "👋 Application closed. Thanks for using Network Intrusion Log Manager!"
