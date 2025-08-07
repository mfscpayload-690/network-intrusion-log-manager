#!/bin/bash

# Network Intrusion Log Manager - Universal Launcher
# Enhanced version with cross-platform Docker support

clear
echo "🛡️ Network Intrusion Log Manager - Universal Launcher"
echo "====================================================="
echo ""
echo "Choose your preferred way to run the application:"
echo ""
echo "1) 📦 Docker (Recommended - Works on all platforms)"
echo "2) 🐧 Native Linux (Direct execution)"
echo "3) ℹ️  Show system information"
echo "4) 🔧 Setup/Install requirements"
echo "5) 📚 Open Docker documentation"
echo ""

read -p "Enter your choice (1-5): " choice

case $choice in
    1)
        echo ""
        echo "🐳 Starting Docker version..."
        echo "================================"
        
        # Check if Docker is available
        if ! command -v docker &> /dev/null; then
            echo "❌ Docker not found. Would you like to install it?"
            read -p "Install Docker? (y/n): " install_docker
            if [[ $install_docker =~ ^[Yy]$ ]]; then
                echo "Installing Docker..."
                sudo apt update && sudo apt install -y docker.io docker-compose
                sudo systemctl enable docker && sudo systemctl start docker
                sudo usermod -aG docker $USER
                echo "⚠️  Please logout and login again to use Docker without sudo"
                echo "Then run this script again"
            fi
            exit 1
        fi
        
        # Check if Docker daemon is running
        if ! sudo docker info &> /dev/null; then
            echo "🔄 Starting Docker daemon..."
            sudo systemctl start docker
        fi
        
        # Allow X11 for GUI
        echo "🔓 Setting up GUI permissions..."
        xhost +local:docker 2>/dev/null || echo "⚠️  X11 forwarding may not work"
        
        # Fix X11 authentication for Docker
        echo "🔑 Setting up X11 authentication..."
        touch ~/.Xauthority
        chmod 644 ~/.Xauthority
        
        echo "🔨 Building and starting containerized application..."
        sudo -E docker-compose build && sudo -E docker-compose up
        
        # Cleanup
        xhost -local:docker 2>/dev/null
        ;;
    2)
        echo ""
        echo "🐧 Starting native Linux version..."
        echo "=================================="
        
        # Check if MySQL is running
        if ! systemctl is-active --quiet mysql; then
            echo "🔄 Starting MySQL service..."
            sudo systemctl start mysql
        fi
        
        # Check if compiled
        if [ ! -d "javagp2/bin" ] || [ ! "$(ls -A javagp2/bin)" ]; then
            echo "🔨 Compiling application..."
            mkdir -p javagp2/bin
            javac -cp "javagp2/lib/mysql-connector-j-9.3.0.jar:javagp2/lib/jackson-core-2.15.2.jar:javagp2/lib/jackson-databind-2.15.2.jar:javagp2/lib/jackson-annotations-2.15.2.jar:javagp2/lib/jackson-datatype-jsr310-2.15.2.jar" -d javagp2/bin javagp2/src/**/*.java
            
            # Copy properties
            mkdir -p javagp2/bin/dao
            cp javagp2/resources/db.properties javagp2/bin/dao/
        fi
        
        echo "🚀 Launching native application..."
        java -cp javagp2/bin:javagp2/lib/mysql-connector-j-9.3.0.jar:javagp2/lib/jackson-core-2.15.2.jar:javagp2/lib/jackson-databind-2.15.2.jar:javagp2/lib/jackson-annotations-2.15.2.jar:javagp2/lib/jackson-datatype-jsr310-2.15.2.jar:javagp2/resources App
        ;;
    3)
        echo ""
        echo "📊 System Information"
        echo "===================="
        echo "🖥️  OS: $(uname -s) $(uname -r)"
        echo "🏗️  Architecture: $(uname -m)"
        echo "☕ Java: $(java -version 2>&1 | head -1 || echo 'Not installed')"
        echo "🐳 Docker: $(docker --version 2>/dev/null || echo 'Not installed')"
        echo "🐬 MySQL: $(mysql --version 2>/dev/null || echo 'Not installed')"
        echo "🎨 GUI Display: ${DISPLAY:-'Not set'}"
        echo ""
        echo "📁 Project files:"
        ls -la | grep -E "(Dockerfile|docker-compose|\.sh$|\.jar$)"
        echo ""
        read -p "Press Enter to return to main menu..." -r
        exec "$0"
        ;;
    4)
        echo ""
        echo "🔧 Setup & Requirements Installation"
        echo "===================================="
        echo ""
        echo "Choose what to install:"
        echo "1) Install Java (OpenJDK 21)"
        echo "2) Install MySQL Server"
        echo "3) Install Docker & Docker Compose"
        echo "4) Install everything"
        echo "5) Back to main menu"
        echo ""
        read -p "Enter choice (1-5): " setup_choice
        
        case $setup_choice in
            1|4)
                echo "Installing Java..."
                sudo apt update && sudo apt install -y openjdk-21-jdk
                ;;&
            2|4)
                echo "Installing MySQL..."
                sudo apt update && sudo apt install -y mysql-server
                sudo systemctl enable mysql && sudo systemctl start mysql
                ;;&
            3|4)
                echo "Installing Docker..."
                sudo apt update && sudo apt install -y docker.io docker-compose
                sudo systemctl enable docker && sudo systemctl start docker
                sudo usermod -aG docker $USER
                ;;&
            5)
                exec "$0"
                ;;
            *)
                echo "Invalid choice"
                ;;
        esac
        
        echo "✅ Installation completed!"
        echo "⚠️  If you installed Docker, please logout and login again"
        read -p "Press Enter to continue..." -r
        exec "$0"
        ;;
    5)
        echo ""
        echo "📚 Opening Docker documentation..."
        if command -v xdg-open &> /dev/null; then
            xdg-open README-DOCKER.md 2>/dev/null || cat README-DOCKER.md
        else
            cat README-DOCKER.md
        fi
        ;;
    *)
        echo "❌ Invalid choice. Please select 1-5."
        sleep 2
        exec "$0"
        ;;
esac

echo ""
echo "👋 Thanks for using Network Intrusion Log Manager!"
