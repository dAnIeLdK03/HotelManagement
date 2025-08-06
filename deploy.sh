#!/bin/bash

# Hotel Reservation System Deployment Script
# This script automates the deployment process for the application

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
PROJECT_NAME="hotel-reservation-system"
BACKEND_DIR="HotelReservationSystem-main/HotelReservationSystem/HotelReservationSystem/HotelSystemManegment"
FRONTEND_DIR="Frontend"
DOCKER_COMPOSE_FILE="docker-compose.yml"

# Function to print colored output
print_status() {
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

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to check prerequisites
check_prerequisites() {
    print_status "Checking prerequisites..."
    
    # Check Java
    if ! command_exists java; then
        print_error "Java is not installed. Please install Java 17 or higher."
        exit 1
    fi
    
    # Check Node.js
    if ! command_exists node; then
        print_error "Node.js is not installed. Please install Node.js 16 or higher."
        exit 1
    fi
    
    # Check Maven
    if ! command_exists mvn; then
        print_error "Maven is not installed. Please install Maven 3.6 or higher."
        exit 1
    fi
    
    # Check Docker (optional)
    if command_exists docker; then
        print_success "Docker is available"
    else
        print_warning "Docker is not installed. Docker deployment will be skipped."
    fi
    
    print_success "All prerequisites are satisfied"
}

# Function to build backend
build_backend() {
    print_status "Building backend application..."
    
    cd "$BACKEND_DIR"
    
    # Clean and compile
    mvn clean compile
    
    # Run tests
    print_status "Running backend tests..."
    mvn test
    
    # Package application
    print_status "Packaging backend application..."
    mvn package -DskipTests
    
    cd - > /dev/null
    
    print_success "Backend build completed"
}

# Function to build frontend
build_frontend() {
    print_status "Building frontend application..."
    
    cd "$FRONTEND_DIR"
    
    # Install dependencies
    print_status "Installing frontend dependencies..."
    npm install
    
    # Build for production
    print_status "Building frontend for production..."
    npm run build
    
    cd - > /dev/null
    
    print_success "Frontend build completed"
}

# Function to start services
start_services() {
    print_status "Starting services..."
    
    # Start MySQL (if not running)
    if ! pgrep -x "mysqld" > /dev/null; then
        print_status "Starting MySQL..."
        sudo systemctl start mysql
    fi
    
    # Start backend
    print_status "Starting backend service..."
    cd "$BACKEND_DIR"
    nohup mvn spring-boot:run > ../backend.log 2>&1 &
    BACKEND_PID=$!
    echo $BACKEND_PID > ../backend.pid
    cd - > /dev/null
    
    # Wait for backend to start
    print_status "Waiting for backend to start..."
    sleep 30
    
    # Start frontend
    print_status "Starting frontend service..."
    cd "$FRONTEND_DIR"
    nohup npm start > ../frontend.log 2>&1 &
    FRONTEND_PID=$!
    echo $FRONTEND_PID > ../frontend.pid
    cd - > /dev/null
    
    print_success "Services started successfully"
}

# Function to stop services
stop_services() {
    print_status "Stopping services..."
    
    # Stop backend
    if [ -f "backend.pid" ]; then
        BACKEND_PID=$(cat backend.pid)
        if kill -0 $BACKEND_PID 2>/dev/null; then
            kill $BACKEND_PID
            print_status "Backend service stopped"
        fi
        rm -f backend.pid
    fi
    
    # Stop frontend
    if [ -f "frontend.pid" ]; then
        FRONTEND_PID=$(cat frontend.pid)
        if kill -0 $FRONTEND_PID 2>/dev/null; then
            kill $FRONTEND_PID
            print_status "Frontend service stopped"
        fi
        rm -f frontend.pid
    fi
    
    print_success "Services stopped"
}

# Function to deploy with Docker
deploy_docker() {
    if ! command_exists docker; then
        print_error "Docker is not installed. Cannot deploy with Docker."
        return 1
    fi
    
    print_status "Deploying with Docker Compose..."
    
    # Build and start containers
    docker-compose -f "$DOCKER_COMPOSE_FILE" up -d --build
    
    print_success "Docker deployment completed"
}

# Function to check service health
check_health() {
    print_status "Checking service health..."
    
    # Check backend
    if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
        print_success "Backend is healthy"
    else
        print_error "Backend health check failed"
    fi
    
    # Check frontend
    if curl -f http://localhost:3000 > /dev/null 2>&1; then
        print_success "Frontend is healthy"
    else
        print_error "Frontend health check failed"
    fi
}

# Function to show logs
show_logs() {
    print_status "Showing service logs..."
    
    echo "=== Backend Logs ==="
    if [ -f "backend.log" ]; then
        tail -n 20 backend.log
    else
        print_warning "Backend log file not found"
    fi
    
    echo -e "\n=== Frontend Logs ==="
    if [ -f "frontend.log" ]; then
        tail -n 20 frontend.log
    else
        print_warning "Frontend log file not found"
    fi
}

# Function to show usage
show_usage() {
    echo "Usage: $0 [OPTION]"
    echo ""
    echo "Options:"
    echo "  build       Build the application (backend and frontend)"
    echo "  start       Start the services"
    echo "  stop        Stop the services"
    echo "  restart     Restart the services"
    echo "  deploy      Deploy with Docker Compose"
    echo "  health      Check service health"
    echo "  logs        Show service logs"
    echo "  clean       Clean build artifacts"
    echo "  help        Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 build    # Build the application"
    echo "  $0 start    # Start services"
    echo "  $0 deploy   # Deploy with Docker"
}

# Function to clean build artifacts
clean_build() {
    print_status "Cleaning build artifacts..."
    
    # Clean backend
    cd "$BACKEND_DIR"
    mvn clean
    cd - > /dev/null
    
    # Clean frontend
    cd "$FRONTEND_DIR"
    rm -rf build node_modules
    cd - > /dev/null
    
    # Remove log files
    rm -f backend.log frontend.log backend.pid frontend.pid
    
    print_success "Build artifacts cleaned"
}

# Main script logic
case "${1:-help}" in
    "build")
        check_prerequisites
        build_backend
        build_frontend
        print_success "Build completed successfully"
        ;;
    "start")
        check_prerequisites
        start_services
        ;;
    "stop")
        stop_services
        ;;
    "restart")
        stop_services
        sleep 2
        start_services
        ;;
    "deploy")
        check_prerequisites
        deploy_docker
        ;;
    "health")
        check_health
        ;;
    "logs")
        show_logs
        ;;
    "clean")
        clean_build
        ;;
    "help"|*)
        show_usage
        ;;
esac 