import React, { useState } from 'react';
import ApiService from '../../service/ApiService'; // Assuming your service is in a file called ApiService.js
import { FaChevronLeft, FaChevronRight } from 'react-icons/fa';

// Utility to join BASE_URL and path correctly
function getImageUrl(path) {
  if (!path) return '/placeholder-room.jpg';
  const base = ApiService.BASE_URL.endsWith('/') ? ApiService.BASE_URL.slice(0, -1) : ApiService.BASE_URL;
  const rel = path.startsWith('/') ? path : `/${path}`;
  return `${base}${rel}`;
}

const FindBookingPage = () => {
    const [confirmationCode, setConfirmationCode] = useState(''); // State variable for confirmation code
    const [bookingDetails, setBookingDetails] = useState(null); // State variable for booking details
    const [error, setError] = useState(null); // Track any errors
    const [currentImageIndex, setCurrentImageIndex] = useState(0);

    // Get user info for display
    const isAuthenticated = ApiService.isAuthenticated();
    const isAdmin = ApiService.isAdmin();
    const isUser = ApiService.isUser();

    const getSearchMessage = () => {
       if (isAdmin) {
            return "As an administrator, you can search for any booking by confirmation code.";
        } else {
            return "You can search for your own bookings by confirmation code.";
        }
    };

    const handleSearch = async () => {
        if (!confirmationCode.trim()) {
            setError("Please Enter a booking confirmation code");
            setBookingDetails(null);
            setTimeout(() => setError(''), 5000);
            return;
        }
        try {
            // Check if user is authenticated and their role
            const isAuthenticated = ApiService.isAuthenticated();
            const isAdmin = ApiService.isAdmin();
            
            let response;
            
            if (isAuthenticated) {
                // If user is logged in, use the restricted API that only shows their own bookings
                // (unless they are admin, then they can see all bookings)
                if (isAdmin) {
                    // Admin can search all bookings
                    response = await ApiService.getBookingByConfirmationCode(confirmationCode);
                } else {
                    // Regular users can only search their own bookings
                    response = await ApiService.getMyBookingByConfirmationCode(confirmationCode);
                }
            } else {
                // If not logged in, use the public API (but this might not work for all bookings)
                response = await ApiService.getBookingByConfirmationCode(confirmationCode);
            }
            
            setBookingDetails(response.booking);
            setCurrentImageIndex(0);
            setError(null); // Clear error if successful
        } catch (error) {
            setError(error.response?.data?.message || error.message);
            setBookingDetails(null);
            setTimeout(() => setError(''), 5000);
        }
    };

    const nextImage = () => {
        if (
            bookingDetails &&
            bookingDetails.room &&
            Array.isArray(bookingDetails.room.roomPhotoUrls) &&
            bookingDetails.room.roomPhotoUrls.length > 0
        ) {
            setCurrentImageIndex((prevIndex) =>
                prevIndex === bookingDetails.room.roomPhotoUrls.length - 1 ? 0 : prevIndex + 1
            );
        }
    };

    const prevImage = () => {
        if (
            bookingDetails &&
            bookingDetails.room &&
            Array.isArray(bookingDetails.room.roomPhotoUrls) &&
            bookingDetails.room.roomPhotoUrls.length > 0
        ) {
            setCurrentImageIndex((prevIndex) =>
                prevIndex === 0 ? bookingDetails.room.roomPhotoUrls.length - 1 : prevIndex - 1
            );
        }
    };

    return (
        <div className="find-booking-page">
            <h2>Find Booking</h2>
            
            <div className="search-container">
                <input
                    required
                    type="text"
                    placeholder="Enter your booking confirmation code"
                    value={confirmationCode}
                    onChange={(e) => setConfirmationCode(e.target.value)}
                />
                <button onClick={handleSearch}>Find</button>
            </div>
            {error && <p style={{ color: 'red' }}>{error}</p>}
            {bookingDetails && (
                <div className="booking-details">
                    <h3>Booking Details</h3>
                    <p>Confirmation Code: {bookingDetails.bookingConfirmationCode}</p>
                    <p>Check-in Date: {bookingDetails.checkInDate}</p>
                    <p>Check-out Date: {bookingDetails.checkOutDate}</p>
                    <p>Num Of Adults: {bookingDetails.numOfAdults}</p>
                    <p>Num Of Children: {bookingDetails.numOfChildren}</p>

                    <br />
                    <hr />
                    <br />
                    <h3>Booker Detials</h3>
                    <div>
                        <p> Name: {bookingDetails.user.name}</p>
                        <p> Email: {bookingDetails.user.email}</p>
                        <p> Phone Number: {bookingDetails.user.phoneNumber}</p>
                    </div>

                    <br />
                    <hr />
                    <br />
                    <h3>Room Details</h3>
                    <div>
                        <p> Room Type: {bookingDetails.room.roomType}</p>
                        <div className="room-image-carousel">
                          {Array.isArray(bookingDetails.room.roomPhotoUrls) && bookingDetails.room.roomPhotoUrls.length > 0 ? (
                            <>
                              <img
                                src={getImageUrl(bookingDetails.room.roomPhotoUrls[currentImageIndex])}
                                alt={bookingDetails.room.roomType}
                                className="room-photo"
                                onError={(e) => {
                                  e.target.onerror = null;
                                  e.target.src = '/placeholder-room.jpg';
                                }}
                              />
                              {bookingDetails.room.roomPhotoUrls.length > 1 && (
                                <div className="carousel-controls">
                                  <button className="carousel-button prev" onClick={prevImage}>
                                    <FaChevronLeft />
                                  </button>
                                  <div className="image-indicators">
                                    {bookingDetails.room.roomPhotoUrls.map((_, index) => (
                                      <span
                                        key={index}
                                        className={`indicator ${index === currentImageIndex ? 'active' : ''}`}
                                        onClick={() => setCurrentImageIndex(index)}
                                      />
                                    ))}
                                  </div>
                                  <button className="carousel-button next" onClick={nextImage}>
                                    <FaChevronRight />
                                  </button>
                                </div>
                              )}
                            </>
                          ) : (
                            <img
                              src="/placeholder-room.jpg"
                              alt={bookingDetails.room.roomType}
                              className="room-photo"
                            />
                          )}
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default FindBookingPage;