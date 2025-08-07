#!/bin/bash

# Simple Docker Test - Network Intrusion Log Manager

echo "🔧 Testing Docker setup..."

# Build the image
echo "🔨 Building image..."
sudo docker build -t test-app .

# Test if we can start MySQL
echo "🐬 Starting MySQL..."
sudo docker run -d --name test-mysql \
    -e MYSQL_ROOT_PASSWORD=rootpassword \
    -e MYSQL_DATABASE=network_intrusion_logs \
    -e MYSQL_USER=appuser \
    -e MYSQL_PASSWORD=apppassword \
    -p 3307:3306 \
    mysql:8.0

# Wait for MySQL
echo "⏳ Waiting for MySQL..."
sleep 30

# Allow X11
xhost +local:docker

# Test the app
echo "🚀 Testing application..."
sudo docker run --rm -it \
    --name test-app \
    --link test-mysql:mysql \
    -e DISPLAY=$DISPLAY \
    -e DB_HOST=mysql \
    -e DB_PORT=3306 \
    -e DB_NAME=network_intrusion_logs \
    -e DB_USER=appuser \
    -e DB_PASSWORD=apppassword \
    -v /tmp/.X11-unix:/tmp/.X11-unix:rw \
    test-app

# Cleanup
echo "🧹 Cleaning up..."
xhost -local:docker
sudo docker stop test-mysql
sudo docker rm test-mysql

echo "✅ Test completed!"
