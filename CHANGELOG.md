# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Review system with rating categories (cleanliness, service, location)
- User role management (ADMIN, USER, RECEPTIONIST)
- Booking archiving functionality
- Email notification system
- Date validation for bookings (no past dates)
- Enhanced search functionality with role-based access
- Admin panel for user management

### Changed
- Updated user registration logic (first 2 users become ADMIN)
- Improved booking search with role-based restrictions
- Enhanced navigation with role-specific menu items
- Updated date picker components with better UX

### Fixed
- Port conflict issues during startup
- Date validation in booking system
- Role-based access control implementation
- Navigation menu visibility based on authentication

## [1.0.0] - 2024-08-06

### Added
- Initial release of Hotel Reservation System
- User authentication and authorization
- Room management system
- Booking system with confirmation codes
- Basic admin functionality
- Frontend React application
- MySQL database integration
- JWT token authentication
- Email service integration
- Room image management
- Booking history for users
- Room availability checking
- Date range validation
- User profile management

### Features
- **Authentication System**
  - User registration and login
  - JWT token-based authentication
  - Password encryption with BCrypt
  - Role-based access control

- **Room Management**
  - Add, edit, and delete rooms
  - Room type categorization
  - Multiple room images support
  - Room availability checking

- **Booking System**
  - Real-time room availability
  - Date range selection with validation
  - Booking confirmation with unique codes
  - Booking history for users
  - Booking cancellation functionality

- **Admin Features**
  - User management
  - Room management
  - Booking overview
  - System administration

- **Frontend Features**
  - Responsive React application
  - Modern UI/UX design
  - Date picker integration
  - Image carousel for rooms
  - Form validation

### Technical Stack
- **Backend**: Spring Boot 3.x, Java 17, Spring Security, JPA/Hibernate
- **Frontend**: React 18, React Router, Axios, CSS3
- **Database**: MySQL 8.0
- **Build Tools**: Maven, npm
- **Authentication**: JWT tokens
- **Email**: SMTP integration

## [0.9.0] - 2024-08-01

### Added
- Basic project structure
- Spring Boot backend setup
- React frontend setup
- Database schema design
- Basic authentication system

### Changed
- Initial project configuration
- Development environment setup

## [0.8.0] - 2024-07-25

### Added
- Project initialization
- Repository setup
- Development environment configuration

---

## Version History

- **1.0.0**: First stable release with all core features
- **0.9.0**: Beta version with basic functionality
- **0.8.0**: Alpha version with project setup

## Release Notes

### Version 1.0.0
This is the first stable release of the Hotel Reservation System. It includes all core features needed for a functional hotel booking system.

**Key Features:**
- Complete user authentication system
- Full room management capabilities
- Comprehensive booking system
- Admin panel for system management
- Modern, responsive frontend

**System Requirements:**
- Java 17 or higher
- Node.js 16 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

**Installation:**
1. Clone the repository
2. Set up MySQL database
3. Configure application.properties
4. Start backend with `mvn spring-boot:run`
5. Start frontend with `npm start`

**Known Issues:**
- None reported in this release

**Future Plans:**
- Payment system integration
- Advanced review system
- Mobile application
- API documentation
- Unit test coverage
- Performance optimizations 