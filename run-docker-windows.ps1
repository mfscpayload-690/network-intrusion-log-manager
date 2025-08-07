# Network Intrusion Log Manager - Docker Runner for Windows
# PowerShell script for Windows users

Write-Host "🛡️ Network Intrusion Log Manager - Docker Edition" -ForegroundColor Green
Write-Host "🪟 Running on Windows" -ForegroundColor Cyan

# Check if Docker Desktop is installed and running
try {
    $dockerVersion = docker --version
    Write-Host "✅ Docker found: $dockerVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker Desktop is not installed or not running." -ForegroundColor Red
    Write-Host "   Please install Docker Desktop from: https://www.docker.com/products/docker-desktop/" -ForegroundColor Yellow
    Write-Host "   Make sure Docker Desktop is running before executing this script." -ForegroundColor Yellow
    pause
    exit 1
}

# Check if Docker Compose is available
try {
    $composeVersion = docker-compose --version
    Write-Host "✅ Docker Compose found: $composeVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker Compose not found. It should be included with Docker Desktop." -ForegroundColor Red
    Write-Host "   Please ensure Docker Desktop is properly installed." -ForegroundColor Yellow
    pause
    exit 1
}

# Check if WSL2 backend is being used (recommended for GUI apps)
$dockerInfo = docker info 2>$null
if ($dockerInfo -match "WSL") {
    Write-Host "✅ WSL2 backend detected - good for performance!" -ForegroundColor Green
} else {
    Write-Host "⚠️ Consider switching to WSL2 backend for better performance" -ForegroundColor Yellow
}

try {
    # Build and run the application
    Write-Host "🔨 Building Docker images..." -ForegroundColor Cyan
    docker-compose build

    Write-Host "🚀 Starting Network Intrusion Log Manager..." -ForegroundColor Green
    Write-Host "📝 Note: GUI will work through Docker Desktop's X11 forwarding" -ForegroundColor Yellow
    
    # Run in detached mode on Windows to avoid blocking
    docker-compose up -d
    
    Write-Host "✅ Application started successfully!" -ForegroundColor Green
    Write-Host "📊 Check Docker Desktop to see running containers" -ForegroundColor Cyan
    Write-Host "🛑 To stop the application, run: docker-compose down" -ForegroundColor Yellow
    
    # Optional: Open Docker Desktop
    $openDocker = Read-Host "Would you like to open Docker Desktop to monitor containers? (y/n)"
    if ($openDocker -eq "y" -or $openDocker -eq "Y") {
        Start-Process "docker-desktop:"
    }
    
} catch {
    Write-Host "❌ Error occurred: $_" -ForegroundColor Red
    pause
}

Write-Host "🎉 Setup complete! Your Network Intrusion Log Manager is running in Docker." -ForegroundColor Green
