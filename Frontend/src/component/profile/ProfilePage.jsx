import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import ApiService from '../../service/ApiService';
import { FaChevronLeft, FaChevronRight } from 'react-icons/fa';

const ProfilePage = () => {
    const [user, setUser] = useState(null);
    const [error, setError] = useState(null);
    const navigate = useNavigate();
    const [currentImageIndexes, setCurrentImageIndexes] = useState({});

    useEffect(() => {
        const fetchUserProfile = async () => {
            try {
                const response = await ApiService.getUserProfile();
                // Fetch user bookings using the fetched user ID
                const userPlusBookings = await ApiService.getUserBookings(response.user.id);
                setUser(userPlusBookings.user)
            } catch (error) {
                setError(error.response?.data?.message || error.message);
            }
        };
        fetchUserProfile();
    }, []);

    const handleLogout = () => {
        ApiService.logout();
        navigate('/home');
    };

    const handleEditProfile = () => {
        navigate('/edit-profile');
    };

    const nextImage = (bookingId, photoUrls) => {
        setCurrentImageIndexes(prev => ({
            ...prev,
            [bookingId]: prev[bookingId] === photoUrls.length - 1 ? 0 : (prev[bookingId] || 0) + 1
        }));
    };

    const prevImage = (bookingId, photoUrls) => {
        setCurrentImageIndexes(prev => ({
            ...prev,
            [bookingId]: prev[bookingId] === 0 || !prev[bookingId] ? photoUrls.length - 1 : prev[bookingId] - 1
        }));
    };

    return (
        <div className="profile-page">
            {user && <h2>Welcome, {user.name}</h2>}
            <div className="profile-actions">
                <button className="edit-profile-button" onClick={handleEditProfile}>Edit Profile</button>
                <button className="logout-button" onClick={handleLogout}>Logout</button>
            </div>
            {error && <p className="error-message">{error}</p>}
            {user && (
                <div className="profile-details">
                    <h3>My Profile Details</h3>
                    <p><strong>Email:</strong> {user.email}</p>
                    <p><strong>Phone Number:</strong> {user.phoneNumber}</p>
                </div>
            )}
            <div className="bookings-section">
                <h3>My Booking History</h3>
                <div className="booking-list">
                    {user && user.bookings.length > 0 ? (
                        user.bookings.map((booking) => {
                            const photoUrls = Array.isArray(booking.room.roomPhotoUrls) ? booking.room.roomPhotoUrls : [];
                            const currentIndex = currentImageIndexes[booking.id] || 0;
                            return (
                                <div key={booking.id} className="booking-item">
                                    <p><strong>Booking Code:</strong> {booking.bookingConfirmationCode}</p>
                                    <p><strong>Check-in Date:</strong> {booking.checkInDate}</p>
                                    <p><strong>Check-out Date:</strong> {booking.checkOutDate}</p>
                                    <p><strong>Total Guests:</strong> {booking.numOfAdults + booking.numOfChildren}</p>
                                    <p><strong>Room Type:</strong> {booking.room.roomType}</p>
                                    <div className="room-image-carousel">
                                        {photoUrls.length > 0 ? (
                                            <>
                                                <img
                                                    src={`${ApiService.BASE_URL}${photoUrls[currentIndex]}`}
                                                    alt={booking.room.roomType}
                                                    className="room-photo"
                                                    onError={e => {
                                                        e.target.onerror = null;
                                                        e.target.src = '/placeholder-room.jpg';
                                                    }}
                                                />
                                                {photoUrls.length > 1 && (
                                                    <div className="carousel-controls">
                                                        <button className="carousel-button prev" onClick={() => prevImage(booking.id, photoUrls)}>
                                                            <FaChevronLeft />
                                                        </button>
                                                        <div className="image-indicators">
                                                            {photoUrls.map((_, idx) => (
                                                                <span
                                                                    key={idx}
                                                                    className={`indicator ${idx === currentIndex ? 'active' : ''}`}
                                                                    onClick={() => setCurrentImageIndexes(prev => ({ ...prev, [booking.id]: idx }))}
                                                                />
                                                            ))}
                                                        </div>
                                                        <button className="carousel-button next" onClick={() => nextImage(booking.id, photoUrls)}>
                                                            <FaChevronRight />
                                                        </button>
                                                    </div>
                                                )}
                                            </>
                                        ) : (
                                            <img src="/placeholder-room.jpg" alt={booking.room.roomType} className="room-photo" />
                                        )}
                                    </div>
                                </div>
                            );
                        })
                    ) : (
                        <p>No bookings found.</p>
                    )}
                </div>
            </div>
        </div>
    );
};

export default ProfilePage;
