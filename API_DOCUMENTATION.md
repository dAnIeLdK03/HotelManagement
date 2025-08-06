# API Documentation

## Overview

The Hotel Reservation System API provides RESTful endpoints for managing hotel bookings, rooms, users, and reviews. The API is built with Spring Boot and uses JWT authentication.

**Base URL:** `http://localhost:8080`

## Authentication

All API endpoints (except authentication endpoints) require a valid JWT token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

## Endpoints

### Authentication

#### Register User
```http
POST /auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "User registered successfully",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "name": "John Doe",
      "email": "john@example.com",
      "role": "USER"
    }
  }
}
```

#### Login User
```http
POST /auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "name": "John Doe",
      "email": "john@example.com",
      "role": "USER"
    }
  }
}
```

### Users

#### Get All Users (ADMIN only)
```http
GET /users/all
Authorization: Bearer <admin-token>
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Users retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "John Doe",
      "email": "john@example.com",
      "role": "USER"
    }
  ]
}
```

#### Change User Role (ADMIN only)
```http
PUT /users/change-role/{userId}?newRole=RECEPTIONIST
Authorization: Bearer <admin-token>
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "User role updated successfully"
}
```

#### Get Current User Profile
```http
GET /users/get-logged-in-profile-info
Authorization: Bearer <user-token>
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Profile retrieved successfully",
  "data": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "role": "USER"
  }
}
```

#### Delete User (ADMIN only)
```http
DELETE /users/delete/{userId}
Authorization: Bearer <admin-token>
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "User deleted successfully"
}
```

#### Get Role Statistics (ADMIN only)
```http
GET /users/role-stats
Authorization: Bearer <admin-token>
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Role statistics retrieved successfully",
  "data": {
    "ADMIN": 2,
    "USER": 10,
    "RECEPTIONIST": 3
  }
}
```

### Rooms

#### Get All Rooms
```http
GET /rooms/all
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Rooms retrieved successfully",
  "data": [
    {
      "id": 1,
      "roomNumber": "101",
      "roomType": "STANDARD",
      "price": 100.00,
      "description": "Comfortable standard room",
      "isAvailable": true,
      "images": ["image1.jpg", "image2.jpg"]
    }
  ]
}
```

#### Get Available Rooms
```http
GET /rooms/all-available-rooms
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Available rooms retrieved successfully",
  "data": [
    {
      "id": 1,
      "roomNumber": "101",
      "roomType": "STANDARD",
      "price": 100.00,
      "description": "Comfortable standard room",
      "isAvailable": true,
      "images": ["image1.jpg", "image2.jpg"]
    }
  ]
}
```

#### Add Room (ADMIN/RECEPTIONIST only)
```http
POST /rooms/add
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "roomNumber": "102",
  "roomType": "DELUXE",
  "price": 150.00,
  "description": "Luxury deluxe room"
}
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Room added successfully"
}
```

#### Update Room (ADMIN/RECEPTIONIST only)
```http
PUT /rooms/update/{roomId}
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "roomNumber": "102",
  "roomType": "DELUXE",
  "price": 160.00,
  "description": "Updated luxury deluxe room"
}
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Room updated successfully"
}
```

#### Delete Room (ADMIN/RECEPTIONIST only)
```http
DELETE /rooms/delete/{roomId}
Authorization: Bearer <admin-token>
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Room deleted successfully"
}
```

### Bookings

#### Create Booking
```http
POST /bookings/book-room/{roomId}/{userId}
Authorization: Bearer <user-token>
Content-Type: application/json

{
  "checkInDate": "2024-08-10",
  "checkOutDate": "2024-08-12",
  "guestCount": 2
}
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Booking created successfully",
  "data": {
    "id": 1,
    "confirmationCode": "BK20240810001",
    "checkInDate": "2024-08-10",
    "checkOutDate": "2024-08-12",
    "guestCount": 2,
    "totalPrice": 200.00,
    "status": "CONFIRMED"
  }
}
```

#### Get All Bookings (ADMIN only)
```http
GET /bookings/all
Authorization: Bearer <admin-token>
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Bookings retrieved successfully",
  "data": [
    {
      "id": 1,
      "confirmationCode": "BK20240810001",
      "checkInDate": "2024-08-10",
      "checkOutDate": "2024-08-12",
      "guestCount": 2,
      "totalPrice": 200.00,
      "status": "CONFIRMED",
      "user": {
        "id": 1,
        "name": "John Doe",
        "email": "john@example.com"
      },
      "room": {
        "id": 1,
        "roomNumber": "101",
        "roomType": "STANDARD"
      }
    }
  ]
}
```

#### Find Booking by Confirmation Code
```http
GET /bookings/get-by-confirmation-code/{code}
Authorization: Bearer <user-token>
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Booking found successfully",
  "data": {
    "id": 1,
    "confirmationCode": "BK20240810001",
    "checkInDate": "2024-08-10",
    "checkOutDate": "2024-08-12",
    "guestCount": 2,
    "totalPrice": 200.00,
    "status": "CONFIRMED"
  }
}
```

#### Find My Booking by Confirmation Code
```http
GET /bookings/get-my-booking/{confirmationCode}
Authorization: Bearer <user-token>
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Booking found successfully",
  "data": {
    "id": 1,
    "confirmationCode": "BK20240810001",
    "checkInDate": "2024-08-10",
    "checkOutDate": "2024-08-12",
    "guestCount": 2,
    "totalPrice": 200.00,
    "status": "CONFIRMED"
  }
}
```

#### Cancel Booking
```http
DELETE /bookings/cancel/{bookingId}
Authorization: Bearer <user-token>
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Booking cancelled successfully"
}
```

#### Archive Booking (ADMIN only)
```http
POST /bookings/achieve/{bookingId}
Authorization: Bearer <admin-token>
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "успешно Achieving"
}
```

### Reviews

#### Create Review
```http
POST /reviews/create
Authorization: Bearer <user-token>
Content-Type: application/json

{
  "title": "Great stay!",
  "comment": "Excellent service and clean rooms",
  "overallRating": 5,
  "cleanlinessRating": 5,
  "serviceRating": 4,
  "locationRating": 5,
  "roomId": 1,
  "bookingId": 1,
  "isHotelReview": false
}
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Review created successfully",
  "data": {
    "id": 1,
    "title": "Great stay!",
    "comment": "Excellent service and clean rooms",
    "overallRating": 5,
    "cleanlinessRating": 5,
    "serviceRating": 4,
    "locationRating": 5,
    "isApproved": false,
    "createdAt": "2024-08-06T10:30:00Z"
  }
}
```

#### Get All Reviews (ADMIN only)
```http
GET /reviews/all
Authorization: Bearer <admin-token>
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Reviews retrieved successfully",
  "data": [
    {
      "id": 1,
      "title": "Great stay!",
      "comment": "Excellent service and clean rooms",
      "overallRating": 5,
      "cleanlinessRating": 5,
      "serviceRating": 4,
      "locationRating": 5,
      "isApproved": true,
      "createdAt": "2024-08-06T10:30:00Z",
      "user": {
        "id": 1,
        "name": "John Doe"
      },
      "room": {
        "id": 1,
        "roomNumber": "101"
      }
    }
  ]
}
```

#### Get Approved Reviews
```http
GET /reviews/approved
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Approved reviews retrieved successfully",
  "data": [
    {
      "id": 1,
      "title": "Great stay!",
      "comment": "Excellent service and clean rooms",
      "overallRating": 5,
      "cleanlinessRating": 5,
      "serviceRating": 4,
      "locationRating": 5,
      "createdAt": "2024-08-06T10:30:00Z"
    }
  ]
}
```

#### Approve Review (ADMIN only)
```http
PUT /reviews/{reviewId}/approve
Authorization: Bearer <admin-token>
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Review approved successfully"
}
```

#### Reject Review (ADMIN only)
```http
PUT /reviews/{reviewId}/reject
Authorization: Bearer <admin-token>
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Review rejected successfully"
}
```

#### Delete Review (ADMIN only)
```http
DELETE /reviews/{reviewId}
Authorization: Bearer <admin-token>
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Review deleted successfully"
}
```

## Error Responses

### 400 Bad Request
```json
{
  "status": "ERROR",
  "message": "Invalid request data",
  "data": null
}
```

### 401 Unauthorized
```json
{
  "status": "ERROR",
  "message": "Authentication required",
  "data": null
}
```

### 403 Forbidden
```json
{
  "status": "ERROR",
  "message": "Access denied",
  "data": null
}
```

### 404 Not Found
```json
{
  "status": "ERROR",
  "message": "Resource not found",
  "data": null
}
```

### 500 Internal Server Error
```json
{
  "status": "ERROR",
  "message": "Internal server error",
  "data": null
}
```

## Data Models

### User
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "role": "USER"
}
```

### Room
```json
{
  "id": 1,
  "roomNumber": "101",
  "roomType": "STANDARD",
  "price": 100.00,
  "description": "Comfortable standard room",
  "isAvailable": true,
  "images": ["image1.jpg", "image2.jpg"]
}
```

### Booking
```json
{
  "id": 1,
  "confirmationCode": "BK20240810001",
  "checkInDate": "2024-08-10",
  "checkOutDate": "2024-08-12",
  "guestCount": 2,
  "totalPrice": 200.00,
  "status": "CONFIRMED",
  "user": {
    "id": 1,
    "name": "John Doe"
  },
  "room": {
    "id": 1,
    "roomNumber": "101"
  }
}
```

### Review
```json
{
  "id": 1,
  "title": "Great stay!",
  "comment": "Excellent service and clean rooms",
  "overallRating": 5,
  "cleanlinessRating": 5,
  "serviceRating": 4,
  "locationRating": 5,
  "isApproved": true,
  "isHotelReview": false,
  "createdAt": "2024-08-06T10:30:00Z",
  "user": {
    "id": 1,
    "name": "John Doe"
  },
  "room": {
    "id": 1,
    "roomNumber": "101"
  }
}
```

## Rate Limiting

The API implements rate limiting to prevent abuse:
- **Authentication endpoints:** 5 requests per minute
- **Other endpoints:** 100 requests per minute per user

## Pagination

For endpoints that return lists, pagination is supported:
```
GET /rooms/all?page=0&size=10&sort=price,desc
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Rooms retrieved successfully",
  "data": {
    "content": [...],
    "totalElements": 50,
    "totalPages": 5,
    "currentPage": 0,
    "size": 10
  }
}
```

## Testing

### Using cURL

```bash
# Register a user
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com","password":"password123"}'

# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"password123"}'

# Get rooms (with token)
curl -X GET http://localhost:8080/rooms/all \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Using Postman

1. Import the API collection
2. Set the base URL to `http://localhost:8080`
3. Use the authentication endpoints to get a token
4. Add the token to the Authorization header for other requests

## Support

For API support and questions:
- Create an issue on GitHub
- Contact the development team
- Check the troubleshooting section in README.md 