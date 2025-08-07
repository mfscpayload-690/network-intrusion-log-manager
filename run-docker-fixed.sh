#!/bin/bash

# Network Intrusion Log Manager - Docker Runner with Fixed X11 Support

echo "🛡️ Network Intrusion Log Manager - Docker Edition (X11 Fixed)"
echo "============================================================="

# Clean up any previous containers
echo "🧹 Cleaning up previous containers..."
sudo docker-compose down 2>/dev/null

# Prepare X11 authentication
echo "🔑 Setting up X11 authentication..."
XAUTH=/tmp/.docker.xauth
if [ ! -f $XAUTH ]; then
    xauth_list=$(xauth nlist $DISPLAY | sed -e 's/^..../ffff/')
    if [ ! -z "$xauth_list" ]; then
        echo "$xauth_list" | xauth -f $XAUTH nmerge -
    else
        touch $XAUTH
    fi
    chmod a+r $XAUTH
fi

# Allow X11 connections
echo "🔓 Setting up X11 permissions..."
xhost +local:docker

# Set up environment
export XAUTHORITY=$XAUTH

# Start MySQL container first
echo "🐬 Starting MySQL database..."
sudo docker-compose up -d mysql

# Wait for MySQL to be healthy
echo "⏳ Waiting for MySQL to be ready..."
while [ "$(sudo docker inspect --format='{{.State.Health.Status}}' intrusion-logs-db 2>/dev/null)" != "healthy" ]; do
    echo "Waiting for MySQL to become healthy..."
    sleep 2
done
echo "✅ MySQL is ready!"

# Run the application container with proper X11 setup
echo "🚀 Starting Java application with GUI support..."
sudo docker run --rm -it \
    --name intrusion-logs-app \
    --network grouptwo_intrusion-network \
    -e DISPLAY=$DISPLAY \
    -e XAUTHORITY=$XAUTH \
    -e DB_HOST=mysql \
    -e DB_PORT=3306 \
    -e DB_NAME=network_intrusion_logs \
    -e DB_USER=appuser \
    -e DB_PASSWORD=apppassword \
    -v /tmp/.X11-unix:/tmp/.X11-unix:rw \
    -v $XAUTH:$XAUTH:rw \
    --user $(id -u):$(id -g) \
    grouptwo-app

# Cleanup
echo "🔒 Cleaning up X11 permissions..."
xhost -local:docker

# Stop MySQL
echo "🛑 Stopping containers..."
sudo docker-compose down

echo "👋 Application stopped. Thanks for using Network Intrusion Log Manager!"
