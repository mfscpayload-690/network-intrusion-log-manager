#!/bin/bash

# Network Intrusion Log Manager - Docker Runner for Linux
# This script allows GUI applications to run from Docker containers

echo "🛡️ Network Intrusion Log Manager - Docker Edition"
echo "🐧 Running on Linux with GUI support"

# Allow X11 forwarding for GUI
echo "🔓 Setting up X11 permissions for GUI..."
xhost +local:docker

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker first:"
    echo "   sudo apt update && sudo apt install docker.io docker-compose"
    echo "   sudo systemctl enable docker && sudo systemctl start docker"
    echo "   sudo usermod -aG docker $USER"
    echo "   (Then logout and login again)"
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose is not installed. Installing..."
    sudo apt update && sudo apt install docker-compose
fi

# Build and run the application
echo "🔨 Building Docker images..."
docker-compose build

echo "🚀 Starting Network Intrusion Log Manager..."
docker-compose up

# Cleanup X11 permissions
echo "🔒 Cleaning up X11 permissions..."
xhost -local:docker

echo "👋 Application stopped. Thanks for using Network Intrusion Log Manager!"
