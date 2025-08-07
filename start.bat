@echo off
REM Network Intrusion Log Manager - Windows Launcher

title Network Intrusion Log Manager

echo.
echo 🛡️ Network Intrusion Log Manager - Windows Edition
echo ===================================================
echo.

REM Check if Docker Desktop is available
docker --version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Docker Desktop not found!
    echo.
    echo Please install Docker Desktop from:
    echo https://www.docker.com/products/docker-desktop/
    echo.
    pause
    exit /b 1
)

echo ✅ Docker Desktop found
echo.

REM Check if Docker is running
docker info >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Docker Desktop is not running
    echo Please start Docker Desktop and try again
    echo.
    pause
    exit /b 1
)

echo ✅ Docker Desktop is running
echo.

REM Build and start the application
echo 🔨 Building Docker containers...
docker-compose build

if %ERRORLEVEL% NEQ 0 (
    echo ❌ Failed to build containers
    pause
    exit /b 1
)

echo.
echo 🚀 Starting Network Intrusion Log Manager...
echo 📝 The GUI will appear shortly
echo 🛑 Press Ctrl+C to stop the application
echo.

docker-compose up

echo.
echo 👋 Application stopped. Thanks for using Network Intrusion Log Manager!
pause
