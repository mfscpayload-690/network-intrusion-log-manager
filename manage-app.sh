#!/bin/bash

# Intrusion Logs Application Management Script
# This script provides easy commands to manage the containerized Java application

set -e

COMPOSE_FILE="docker-compose.yml"
APP_NAME="intrusion-logs-app"
DB_NAME="intrusion-logs-db"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if Docker is installed and running
check_docker() {
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed. Please install Docker first."
        exit 1
    fi
    
    if ! docker info &> /dev/null; then
        print_error "Docker daemon is not running. Please start Docker."
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose is not installed. Please install Docker Compose first."
        exit 1
    fi
}

# Function to set up X11 forwarding for GUI
setup_x11() {
    print_info "Setting up X11 forwarding for GUI applications..."
    xhost +local:root
    print_success "X11 forwarding configured"
}

# Function to build the application
build_app() {
    print_info "Building the intrusion logs application..."
    sudo docker build -t $APP_NAME .
    print_success "Application built successfully"
}

# Function to start the services
start_services() {
    print_info "Starting services..."
    setup_x11
    sudo docker-compose up -d
    
    # Wait for services to be ready
    print_info "Waiting for services to start..."
    sleep 5
    
    # Check service status
    if sudo docker-compose ps | grep -q "Up"; then
        print_success "Services started successfully!"
        print_info "Application GUI should now be visible"
        print_info "MySQL database is available on localhost:3306"
        print_info "Use 'docker-compose logs -f app' to see application logs"
    else
        print_error "Services failed to start. Check logs with: docker-compose logs"
        exit 1
    fi
}

# Function to stop the services
stop_services() {
    print_info "Stopping services..."
    sudo docker-compose down
    print_success "Services stopped"
}

# Function to restart the services
restart_services() {
    print_info "Restarting services..."
    stop_services
    start_services
}

# Function to view logs
view_logs() {
    local service=${1:-"app"}
    print_info "Viewing logs for service: $service"
    sudo docker-compose logs -f $service
}

# Function to get service status
status() {
    print_info "Service status:"
    sudo docker-compose ps
    
    print_info "\nContainer resource usage:"
    sudo docker stats --no-stream $(sudo docker-compose ps -q) 2>/dev/null || print_warning "No running containers found"
}

# Function to access MySQL database
mysql_shell() {
    print_info "Connecting to MySQL database..."
    sudo docker-compose exec mysql mysql -u appuser -p -D network_intrusion_logs
}

# Function to backup database
backup_db() {
    local backup_file="backup_$(date +%Y%m%d_%H%M%S).sql"
    print_info "Creating database backup: $backup_file"
    
    sudo docker-compose exec mysql mysqldump -u appuser -p --no-tablespaces network_intrusion_logs > $backup_file
    print_success "Database backup created: $backup_file"
}

# Function to restore database
restore_db() {
    local backup_file=$1
    if [[ -z "$backup_file" ]]; then
        print_error "Please provide backup file path: $0 restore-db <backup_file>"
        exit 1
    fi
    
    if [[ ! -f "$backup_file" ]]; then
        print_error "Backup file not found: $backup_file"
        exit 1
    fi
    
    print_info "Restoring database from: $backup_file"
    sudo docker-compose exec -T mysql mysql -u appuser -p network_intrusion_logs < $backup_file
    print_success "Database restored successfully"
}

# Function to clean up everything
cleanup() {
    print_warning "This will remove all containers, images, and volumes. Are you sure? (y/N)"
    read -r response
    if [[ "$response" =~ ^[Yy]$ ]]; then
        print_info "Stopping and removing containers..."
        sudo docker-compose down -v
        
        print_info "Removing application image..."
        sudo docker rmi $APP_NAME 2>/dev/null || print_warning "Application image not found"
        
        print_info "Removing unused Docker resources..."
        sudo docker system prune -f
        
        print_success "Cleanup completed"
    else
        print_info "Cleanup cancelled"
    fi
}

# Function to update application (rebuild and restart)
update_app() {
    print_info "Updating application..."
    build_app
    restart_services
    print_success "Application updated successfully"
}

# Function to show help
show_help() {
    echo "Intrusion Logs Application Management Script"
    echo "Usage: $0 [command]"
    echo ""
    echo "Commands:"
    echo "  start        - Start all services (build if needed)"
    echo "  stop         - Stop all services"
    echo "  restart      - Restart all services"
    echo "  build        - Build the application image"
    echo "  status       - Show service status and resource usage"
    echo "  logs [svc]   - View logs (default: app, options: app, mysql)"
    echo "  mysql        - Connect to MySQL database shell"
    echo "  backup-db    - Create database backup"
    echo "  restore-db   - Restore database from backup file"
    echo "  update       - Update application (rebuild and restart)"
    echo "  cleanup      - Remove all containers, images, and volumes"
    echo "  help         - Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 start              # Start the application"
    echo "  $0 logs app           # View application logs"
    echo "  $0 backup-db          # Backup database"
    echo "  $0 restore-db file.sql # Restore from backup"
}

# Main script logic
case "${1:-help}" in
    start)
        check_docker
        build_app
        start_services
        ;;
    stop)
        check_docker
        stop_services
        ;;
    restart)
        check_docker
        restart_services
        ;;
    build)
        check_docker
        build_app
        ;;
    status)
        check_docker
        status
        ;;
    logs)
        check_docker
        view_logs $2
        ;;
    mysql)
        check_docker
        mysql_shell
        ;;
    backup-db)
        check_docker
        backup_db
        ;;
    restore-db)
        check_docker
        restore_db $2
        ;;
    update)
        check_docker
        update_app
        ;;
    cleanup)
        check_docker
        cleanup
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        print_error "Unknown command: $1"
        show_help
        exit 1
        ;;
esac
