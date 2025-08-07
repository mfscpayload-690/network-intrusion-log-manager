# 🐳 Network Intrusion Log Manager - Docker Edition

**Run your Network Intrusion Log Manager on any platform with Docker!**

This containerized version includes:
- ✅ **Cross-platform support** (Linux, Windows, macOS)
- ✅ **Isolated MySQL database** 
- ✅ **GUI support** for all platforms
- ✅ **Smooth hover effects** and enhanced UI
- ✅ **Working delete functionality**
- ✅ **Pre-configured database** with sample data
- ✅ **One-command deployment**

---

## 🚀 Quick Start

### Linux Users
```bash
chmod +x run-docker-linux.sh
./run-docker-linux.sh
```

### Windows Users
```powershell
# Run in PowerShell as Administrator
.\run-docker-windows.ps1
```

### macOS Users
```bash
chmod +x run-docker-macos.sh
./run-docker-macos.sh
```

---

## 📋 Prerequisites

### All Platforms
- **Docker Desktop** installed and running
- **Docker Compose** (usually included with Docker Desktop)

### Platform-Specific Requirements

#### 🐧 Linux
- **X11 server** (usually pre-installed)
- Docker and Docker Compose:
  ```bash
  sudo apt update && sudo apt install docker.io docker-compose
  sudo systemctl enable docker && sudo systemctl start docker
  sudo usermod -aG docker $USER
  # Logout and login again after this
  ```

#### 🪟 Windows
- **Docker Desktop for Windows** with WSL2 backend (recommended)
- **Windows PowerShell 5.0+** or **PowerShell Core**
- Download: [Docker Desktop](https://www.docker.com/products/docker-desktop/)

#### 🍎 macOS
- **Docker Desktop for Mac**
- **XQuartz** for GUI support (optional but recommended):
  ```bash
  brew install --cask xquartz
  # OR download from: https://www.xquartz.org/
  ```

---

## 🛠️ Manual Docker Commands

If you prefer to run Docker commands manually:

```bash
# Build the application
docker-compose build

# Start the application (with logs)
docker-compose up

# Start in background (detached mode)
docker-compose up -d

# Stop the application
docker-compose down

# View logs
docker-compose logs app

# Rebuild and restart
docker-compose down && docker-compose build && docker-compose up
```

---

## 🏗️ Architecture

The Docker setup includes:

### Services
1. **MySQL Database** (`mysql` service)
   - MySQL 8.0 with persistent storage
   - Pre-configured database and user
   - Sample intrusion log data
   - Health checks for reliability

2. **Java Application** (`app` service)
   - OpenJDK 21 with GUI libraries
   - All dependencies included
   - Waits for database to be ready
   - X11 forwarding for GUI

### Networking
- **Isolated network** (`intrusion-network`)
- **Database port** 3306 exposed for external access
- **Application** connects to database via internal network

### Storage
- **Persistent MySQL data** in Docker volume
- **Configuration files** mounted from host

---

## 🔧 Configuration

### Database Connection
The application automatically configures itself to use the containerized MySQL:
- **Host:** `mysql` (Docker service name)
- **Port:** `3306`
- **Database:** `network_intrusion_logs`
- **User:** `appuser`
- **Password:** `apppassword`

### GUI Display
- **Linux:** Direct X11 forwarding
- **Windows:** Docker Desktop's built-in display
- **macOS:** XQuartz X11 forwarding

---

## 🐛 Troubleshooting

### GUI Not Appearing

#### Linux
```bash
# Allow X11 connections
xhost +local:docker

# Check DISPLAY variable
echo $DISPLAY

# Ensure X11 socket is accessible
ls -la /tmp/.X11-unix/
```

#### Windows
- Ensure Docker Desktop is using **WSL2 backend**
- Check Docker Desktop settings for X11 support
- Try running: `docker-compose up -d` and check Docker Desktop

#### macOS
```bash
# Install XQuartz if not done
brew install --cask xquartz

# Restart after installing XQuartz
sudo reboot

# Check XQuartz is running
ps aux | grep X11
```

### Database Connection Issues
```bash
# Check if MySQL container is healthy
docker-compose ps

# View MySQL logs
docker-compose logs mysql

# Test database connection
docker-compose exec mysql mysql -u appuser -papppassword -e "SHOW DATABASES;"
```

### Application Not Starting
```bash
# View application logs
docker-compose logs app

# Check if all services are up
docker-compose ps

# Restart everything
docker-compose down && docker-compose up
```

---

## 🔄 Updates and Maintenance

### Updating the Application
```bash
# Pull latest code and rebuild
git pull
docker-compose build --no-cache
docker-compose up
```

### Database Backup
```bash
# Backup database
docker-compose exec mysql mysqldump -u appuser -papppassword network_intrusion_logs > backup.sql

# Restore database
cat backup.sql | docker-compose exec -T mysql mysql -u appuser -papppassword network_intrusion_logs
```

### Clean Up
```bash
# Remove containers and networks
docker-compose down

# Remove volumes (⚠️ This deletes database data!)
docker-compose down -v

# Remove images
docker rmi grouptwo_app mysql:8.0
```

---

## 🌟 Features

### Enhanced UI
- **Smooth hover effects** on all buttons
- **Icon-enhanced buttons** (🗑️ Delete, 🔄 Refresh, ✅ Add)
- **Responsive animations** with 15ms timer precision
- **Better visual feedback** for user interactions

### Robust Database Operations
- **Fixed delete functionality** with proper ID handling
- **Transaction safety** and error handling
- **Pre-populated sample data** for immediate testing
- **Persistent storage** across container restarts

### Cross-Platform Compatibility
- **Native GUI support** on all platforms
- **Automatic dependency management**
- **Consistent behavior** regardless of host OS
- **Easy deployment** with platform-specific scripts

---

## 🆘 Support

If you encounter issues:

1. **Check the logs:** `docker-compose logs app`
2. **Verify prerequisites** are installed
3. **Try rebuilding:** `docker-compose build --no-cache`
4. **Platform-specific troubleshooting** (see above)

---

## 🎉 Success!

Once running, you'll see:
- 🛡️ Network Intrusion Log Manager GUI
- 📊 Pre-loaded sample intrusion data
- 🎨 Smooth hover effects on buttons
- ✅ Fully functional delete operations
- 🔄 Real-time data refresh capabilities

**Your containerized Network Intrusion Log Manager is ready to use!**
