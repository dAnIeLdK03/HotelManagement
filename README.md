# 🏨 Hotel Reservation System

A full-stack web application for hotel room booking and management, built with Spring Boot (Backend) and React (Frontend).

## ✨ Features

### 🔐 Authentication & Authorization
- User registration and login
- Role-based access control (ADMIN, USER, RECEPTIONIST)
- JWT token authentication
- Secure password encryption

### 🏠 Room Management
- Add, edit, and delete rooms
- Room type categorization
- Multiple room images support
- Room availability checking
- Room search by date and type

### 📅 Booking System
- Real-time room availability
- Date range selection with validation
- Booking confirmation with unique codes
- Booking history for users
- Booking cancellation functionality

### 👥 User Management
- Admin panel for user role management
- User profile management
- Booking history per user
- Role-based permissions

### 🔍 Search & Filter
- Search available rooms by date
- Filter by room type
- Find bookings by confirmation code
- User-specific booking search

### 📧 Email Notifications
- Booking confirmation emails
- Automated feedback request emails (1 day after checkout)
- Email service integration

### 🎯 Admin Features
- Manage user roles and permissions
- View all bookings
- Room management
- User account management
- Booking moderation

## 🛠️ Technology Stack

### Backend
- **Java 17**
- **Spring Boot 3.x**
- **Spring Security** with JWT
- **Spring Data JPA**
- **MySQL Database**
- **Maven** for dependency management

### Frontend
- **React 18**
- **React Router** for navigation
- **Axios** for API calls
- **CSS3** for styling
- **React DatePicker** for date selection

## 📋 Prerequisites

Before running this application, make sure you have the following installed:

- **Java 17** or higher
- **Node.js 16** or higher
- **MySQL 8.0** or higher
- **Maven 3.6** or higher

## 🚀 Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/hotel-reservation-system.git
cd hotel-reservation-system
```

### 2. Database Setup

1. **Start MySQL server**
2. **Create database:**
```sql
CREATE DATABASE Hotel_Reservation_db;
```

### 3. Backend Setup

1. **Navigate to backend directory:**
```bash
cd HotelReservationSystem-main/HotelReservationSystem/HotelReservationSystem/HotelSystemManegment
```

2. **Configure database connection** in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/Hotel_Reservation_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

3. **Start the backend:**
```bash
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### 4. Frontend Setup

1. **Navigate to frontend directory:**
```bash
cd Frontend
```

2. **Install dependencies:**
```bash
npm install
```

3. **Start the frontend:**
```bash
npm start
```

The frontend will start on `http://localhost:3000`

## 👤 User Roles & Permissions

### ADMIN
- Full system access
- Manage user roles
- View all bookings
- Room management
- Review moderation

### RECEPTIONIST
- Manage bookings
- Add/edit rooms
- View booking details
- Cannot manage user roles

### USER
- Book rooms
- View own bookings
- Manage profile
- Submit reviews

## 📊 Database Schema

### Main Entities
- **User** - User accounts and authentication
- **Room** - Hotel rooms with details
- **Booking** - Room reservations
- **Review** - User feedback and ratings

### Key Relationships
- User → Bookings (One-to-Many)
- Room → Bookings (One-to-Many)
- User → Reviews (One-to-Many)
- Room → Reviews (One-to-Many)

## 🔧 Configuration

### Backend Configuration
- **Port:** 8080 (configurable in application.properties)
- **Database:** MySQL
- **JWT Secret:** Configure in application.properties
- **Email Service:** SMTP configuration for notifications

### Frontend Configuration
- **Port:** 3000
- **API Base URL:** http://localhost:8080
- **Environment:** Development/Production

## 📱 API Endpoints

### Authentication
- `POST /auth/register` - User registration
- `POST /auth/login` - User login

### Users
- `GET /users/all` - Get all users (ADMIN only)
- `PUT /users/change-role/{userId}` - Change user role (ADMIN only)
- `GET /users/get-logged-in-profile-info` - Get current user profile

### Rooms
- `GET /rooms/all` - Get all rooms
- `GET /rooms/all-available-rooms` - Get available rooms
- `POST /rooms/add` - Add new room (ADMIN/RECEPTIONIST)
- `PUT /rooms/update/{roomId}` - Update room (ADMIN/RECEPTIONIST)
- `DELETE /rooms/delete/{roomId}` - Delete room (ADMIN/RECEPTIONIST)

### Bookings
- `POST /bookings/book-room/{roomId}/{userId}` - Create booking
- `GET /bookings/all` - Get all bookings (ADMIN only)
- `GET /bookings/get-by-confirmation-code/{code}` - Find booking by code
- `DELETE /bookings/cancel/{bookingId}` - Cancel booking
- `POST /bookings/achieve/{bookingId}` - Archive booking (ADMIN only)

## 🎯 Usage Guide

### For Guests (Users)
1. **Register/Login** - Create account or sign in
2. **Search Rooms** - Browse available rooms by date
3. **Book Room** - Select dates and confirm booking
4. **View Bookings** - Check booking history and details
5. **Submit Reviews** - Rate your stay experience

### For Hotel Staff (Receptionists)
1. **Login** with receptionist account
2. **Manage Rooms** - Add, edit, or remove rooms
3. **View Bookings** - Check all reservations
4. **Process Check-ins** - Handle guest arrivals

### For Administrators
1. **Login** with admin account
2. **Manage Users** - Change roles and permissions
3. **System Overview** - View statistics and reports
4. **Moderate Reviews** - Approve or reject user reviews
5. **Full System Control** - Access all features

## 🔒 Security Features

- **JWT Authentication** - Secure token-based authentication
- **Role-based Access Control** - Different permissions per role
- **Password Encryption** - BCrypt password hashing
- **Input Validation** - Server-side validation for all inputs
- **SQL Injection Protection** - JPA/Hibernate security
- **CORS Configuration** - Cross-origin resource sharing setup

## 📧 Email Features

- **Booking Confirmations** - Automatic emails for new bookings
- **Feedback Requests** - Automated emails 1 day after checkout
- **SMTP Integration** - Configurable email service

## 🚨 Troubleshooting

### Common Issues

**Backend won't start:**
- Check if port 8080 is available
- Verify MySQL is running
- Check database credentials in application.properties

**Frontend won't start:**
- Ensure Node.js is installed
- Run `npm install` to install dependencies
- Check if port 3000 is available

**Database connection issues:**
- Verify MySQL is running
- Check database name and credentials
- Ensure database exists

**API calls failing:**
- Verify backend is running on port 8080
- Check CORS configuration
- Verify JWT token is valid

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Authors

- **Your Name** - *Initial work* - [YourGitHub](https://github.com/yourusername)

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- React team for the frontend library
- MySQL team for the database system
- All contributors and testers

## 📞 Support

If you encounter any issues or have questions:

1. Check the troubleshooting section above
2. Review the logs for error messages
3. Create an issue on GitHub
4. Contact the development team

---

**Happy coding! 🎉** 