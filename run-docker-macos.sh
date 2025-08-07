#!/bin/bash

# Network Intrusion Log Manager - Docker Runner for macOS
# This script sets up GUI support for macOS Docker containers

echo "🛡️ Network Intrusion Log Manager - Docker Edition"
echo "🍎 Running on macOS with GUI support"

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker Desktop is not installed. Please install Docker Desktop first:"
    echo "   Download from: https://www.docker.com/products/docker-desktop/"
    echo "   Or install with Homebrew: brew install --cask docker"
    exit 1
fi

# Check if Docker Desktop is running
if ! docker info &> /dev/null; then
    echo "❌ Docker Desktop is not running. Please start Docker Desktop first."
    exit 1
fi

# Check if XQuartz is installed for GUI support
if ! command -v xquartz &> /dev/null && ! ls /Applications/Utilities/XQuartz.app &> /dev/null; then
    echo "⚠️  XQuartz not found. For GUI support, install XQuartz:"
    echo "   Download from: https://www.xquartz.org/"
    echo "   Or install with Homebrew: brew install --cask xquartz"
    echo "   After installing, restart your Mac and run this script again."
    echo ""
    read -p "Continue without GUI support? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
else
    # Set up XQuartz for GUI support
    echo "🔓 Setting up XQuartz for GUI support..."
    
    # Get the IP address for X11 forwarding
    IP=$(ifconfig en0 | grep inet | awk '$1=="inet" {print $2}')
    if [[ -z "$IP" ]]; then
        IP=$(ifconfig en1 | grep inet | awk '$1=="inet" {print $2}')
    fi
    
    # Allow connections from Docker
    /opt/X11/bin/xhost + $IP
    
    # Set DISPLAY variable
    export DISPLAY=$IP:0
    echo "📺 Display set to: $DISPLAY"
fi

# Check if Docker Compose is available
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose not found. Installing..."
    brew install docker-compose
fi

# Build and run the application
echo "🔨 Building Docker images..."
docker-compose build

echo "🚀 Starting Network Intrusion Log Manager..."
echo "📝 Note: If you installed XQuartz, the GUI should appear shortly"
docker-compose up

# Cleanup X11 permissions (if XQuartz is available)
if command -v xquartz &> /dev/null || ls /Applications/Utilities/XQuartz.app &> /dev/null 2>&1; then
    echo "🔒 Cleaning up X11 permissions..."
    /opt/X11/bin/xhost - $IP
fi

echo "👋 Application stopped. Thanks for using Network Intrusion Log Manager!"
