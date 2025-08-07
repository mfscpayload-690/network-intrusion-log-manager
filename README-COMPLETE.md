# 🛡️ Network Intrusion Log Manager - Complete Edition

**A cross-platform Java Swing application for managing network intrusion logs with enhanced UI and containerized deployment.**

![Platform Support](https://img.shields.io/badge/Platform-Linux%20%7C%20Windows%20%7C%20macOS-brightgreen)
![Docker](https://img.shields.io/badge/Docker-Supported-blue)
![Java](https://img.shields.io/badge/Java-21-orange)
![Database](https://img.shields.io/badge/Database-MySQL-blue)

---

## 🚀 **Quick Start - Choose Your Platform**

### 🐧 **Linux Users**
```bash
# Option 1: Universal launcher (Recommended)
./start.sh

# Option 2: Docker only
./run-docker-linux.sh

# Option 3: Native execution
./run.sh
```

### 🪟 **Windows Users**
```batch
# Option 1: Batch file (Double-click)
start.bat

# Option 2: PowerShell
.\run-docker-windows.ps1
```

### 🍎 **macOS Users**
```bash
./run-docker-macos.sh
```

---

## 🌟 **Features**

### ✨ **Enhanced User Interface**
- **🎨 Smooth Hover Effects** - Buttery smooth 15ms animations on all buttons
- **🎯 Icon-Enhanced Buttons** - 🗑️ Delete, 🔄 Refresh, ✅ Add Log with visual feedback
- **🌙 Dark Theme** - Professional dark theme with JetBrains Mono font
- **⚡ Responsive Animations** - Press, hover, and release state animations

### 🔧 **Fixed Functionality**
- **✅ Working Delete Button** - Proper log ID parsing and database deletion
- **🔄 Real-time Refresh** - Instant table updates after operations
- **📊 Pre-fed Sample Data** - 50+ sample intrusion logs on first launch
- **🛡️ Error Handling** - Comprehensive error messages and validation

### 🏗️ **Database Operations**
- **📈 Add Logs** - IP address, threat type, severity, timestamp
- **📋 View Logs** - Sortable table with all log details
- **🔍 Filter Logs** - By severity and threat type
- **🗑️ Delete Logs** - Safe deletion with confirmation
- **🔄 Refresh Data** - Manual and automatic refresh capabilities

### 🌐 **Cross-Platform Support**
- **🐳 Docker Containers** - Isolated, consistent environment
- **📦 All Dependencies Included** - MySQL, Java, GUI libraries
- **🎮 GUI on All Platforms** - X11 forwarding, native display support
- **⚙️ Auto-Configuration** - Database setup, network configuration

---

## 📋 **System Requirements**

### 🐳 **Docker Version (Recommended)**
- **Docker Desktop** (Windows/macOS) or **Docker + Docker Compose** (Linux)
- **4GB RAM** minimum, 8GB recommended
- **2GB free disk space** for containers and data

### 🖥️ **Native Version**
- **Java 21** or higher (OpenJDK recommended)
- **MySQL 8.0** or higher
- **Linux desktop environment** with X11
- **2GB RAM** minimum

---

## 🛠️ **Installation Guide**

### 🐧 **Linux Installation**
```bash
# Install Docker (if not already installed)
sudo apt update && sudo apt install docker.io docker-compose
sudo systemctl enable docker && sudo systemctl start docker
sudo usermod -aG docker $USER

# Clone and run
cd GroupTWO
chmod +x start.sh
./start.sh
```

### 🪟 **Windows Installation**
1. **Install Docker Desktop** from [docker.com](https://www.docker.com/products/docker-desktop/)
2. **Enable WSL2 backend** (recommended)
3. **Download project** and double-click `start.bat`

### 🍎 **macOS Installation**
```bash
# Install Docker Desktop
brew install --cask docker

# Install XQuartz for GUI (optional)
brew install --cask xquartz

# Run the application
./run-docker-macos.sh
```

---

## 🏗️ **Architecture Overview**

### 🐳 **Docker Architecture**
```
┌─────────────────┐    ┌──────────────────┐
│  Java App       │───▶│  MySQL Database  │
│  (GUI + Logic)  │    │  (Persistent)    │
│  Port: GUI      │    │  Port: 3306      │
└─────────────────┘    └──────────────────┘
         │                        │
         └──────────┬──────────────┘
                    │
               Docker Network
              (intrusion-network)
```

### 💾 **Database Schema**
```sql
CREATE TABLE intrusion_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ip_address VARCHAR(45),
    threat_type VARCHAR(100),
    severity VARCHAR(50),
    log_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    log_message VARCHAR(255),
    log_level VARCHAR(50)
);
```

---

## 🎮 **Usage Guide**

### 📊 **Dashboard**
- **Overview** of system status and recent logs
- **Quick stats** on threat types and severity levels
- **Export capabilities** for reports

### ➕ **Adding Logs**
1. Navigate to **"Add Log"** page
2. Enter **IP Address** (e.g., 192.168.1.100)
3. Select **Threat Type** (DDoS, Malware, Phishing, etc.)
4. Choose **Severity** (Low, Medium, High, Critical)
5. Click **✅ Add Log** button

### 👀 **Viewing Logs**
1. Go to **"View Logs"** page
2. Browse the **sortable table**
3. **Select a row** to highlight
4. Use **🔄 Refresh** to update data
5. Use **🗑️ Delete Selected Log** to remove entries

### 🔍 **Filtering Logs**
1. Access **"Filter"** page
2. Choose **Severity** filter
3. Select **Threat Type** filter
4. Click **Apply Filter**
5. Results show in **View Logs** page

---

## 🔧 **Configuration**

### 🐳 **Docker Configuration**
```yaml
# docker-compose.yml
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: network_intrusion_logs
      MYSQL_USER: appuser
      MYSQL_PASSWORD: apppassword
  
  app:
    build: .
    depends_on:
      - mysql
    environment:
      - DISPLAY=${DISPLAY}
```

### 🔗 **Database Connection**
```properties
# db.properties
db.url=jdbc:mysql://mysql:3306/network_intrusion_logs
db.user=appuser
db.password=apppassword
```

---

## 🐛 **Troubleshooting**

### ❌ **Common Issues**

#### **GUI Not Appearing**
```bash
# Linux - Allow X11 forwarding
xhost +local:docker

# Check display
echo $DISPLAY

# Windows - Ensure Docker Desktop is running with WSL2
```

#### **Database Connection Failed**
```bash
# Check container status
docker-compose ps

# View database logs
docker-compose logs mysql

# Test connection
docker-compose exec mysql mysql -u appuser -papppassword
```

#### **Delete Button Not Working**
- **Fixed in this version** ✅
- Proper log ID parsing implemented
- Enhanced error handling added
- Confirmation dialogs improved

### 🔍 **Debugging Commands**
```bash
# View application logs
docker-compose logs app

# Check running services
docker-compose ps

# Restart everything
docker-compose down && docker-compose up

# Rebuild containers
docker-compose build --no-cache
```

---

## 📁 **Project Structure**
```
GroupTWO/
├── 🐳 Docker Files
│   ├── Dockerfile
│   ├── docker-compose.yml
│   └── init.sql
├── 🚀 Launch Scripts
│   ├── start.sh (Universal)
│   ├── start.bat (Windows)
│   ├── run-docker-linux.sh
│   ├── run-docker-windows.ps1
│   └── run-docker-macos.sh
├── ☕ Java Application
│   └── javagp2/
│       ├── src/ (Source code)
│       ├── bin/ (Compiled classes)
│       ├── lib/ (Dependencies)
│       └── resources/ (Config files)
└── 📚 Documentation
    ├── README.md
    ├── README-DOCKER.md
    └── README-COMPLETE.md
```

---

## 🎯 **Supported Threat Types**
- **🔓 Unauthorized Access** - Illegal system access attempts
- **⚡ DDoS** - Distributed Denial of Service attacks
- **🦠 Malware** - Malicious software detection
- **🎣 Phishing** - Social engineering attempts
- **🔨 Bruteforce** - Password cracking attempts
- **💉 SQL Injection** - Database attack vectors
- **👥 MITM** - Man-in-the-Middle attacks
- **🌐 DNS Spoofing** - Domain name system attacks
- **❓ Other** - Miscellaneous security events

---

## 🔄 **Version History**

### v2.0 - Docker Edition (Current)
- ✅ **Cross-platform Docker support**
- ✅ **Fixed delete functionality**
- ✅ **Enhanced UI with smooth animations**
- ✅ **Automated deployment scripts**
- ✅ **Comprehensive documentation**

### v1.0 - Native Edition
- ✅ Basic Java Swing GUI
- ✅ MySQL database integration
- ✅ CRUD operations for logs
- ⚠️ Platform-specific deployment

---

## 🤝 **Support & Contribution**

### 🆘 **Getting Help**
1. **Check logs**: `docker-compose logs app`
2. **Review documentation**: `README-DOCKER.md`
3. **Try clean rebuild**: `docker-compose build --no-cache`
4. **Verify prerequisites** are installed

### 🔧 **Development**
```bash
# Local development
./start.sh  # Choose option 2 for native mode

# Docker development
docker-compose up -d  # Detached mode
docker-compose logs -f app  # Follow logs
```

---

## 📜 **License**
This project is for educational and research purposes. Use responsibly and in accordance with your organization's security policies.

---

## 🎉 **Success!**

**You now have a fully functional, cross-platform Network Intrusion Log Manager!**

### ✨ **What you can do:**
- 🛡️ **Monitor network intrusions** across multiple platforms
- 📊 **Analyze threat patterns** with filtering and sorting
- 🔄 **Manage log data** with full CRUD operations
- 🎨 **Enjoy smooth UI interactions** with enhanced animations
- 🐳 **Deploy anywhere** with Docker containers

**Happy threat hunting! 🕵️‍♂️🔍**
