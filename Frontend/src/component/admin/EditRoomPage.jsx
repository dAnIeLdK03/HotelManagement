import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import ApiService from '../../service/ApiService';
import { FaChevronLeft, FaChevronRight } from 'react-icons/fa';

const EditRoomPage = () => {
    const { roomId } = useParams();
    const navigate = useNavigate();
    const [roomDetails, setRoomDetails] = useState({
        roomPhotoUrls: [],
        roomType: '',
        roomPrice: '',
        roomDescription: '',
    });
    const [files, setFiles] = useState([]);
    const [previews, setPreviews] = useState([]);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [currentImageIndex, setCurrentImageIndex] = useState(0);

    useEffect(() => {
        const fetchRoomDetails = async () => {
            try {
                const response = await ApiService.getRoomById(roomId);
                setRoomDetails({
                    roomPhotoUrls: response.room.roomPhotoUrls || [],
                    roomType: response.room.roomType,
                    roomPrice: response.room.roomPrice,
                    roomDescription: response.room.roomDescription,
                });
            } catch (error) {
                setError(error.response?.data?.message || error.message);
            }
        };
        fetchRoomDetails();
    }, [roomId]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setRoomDetails(prevState => ({
            ...prevState,
            [name]: value,
        }));
    };

    const handleFileChange = (e) => {
        const selectedFiles = Array.from(e.target.files);
        if (selectedFiles.length > 0) {
            // Check if adding new files would exceed the limit of 5
            if (files.length + selectedFiles.length > 5) {
                setError('Maximum 5 images allowed per room');
                setTimeout(() => setError(''), 5000);
                return;
            }

            setFiles(prevFiles => [...prevFiles, ...selectedFiles]);
            const newPreviews = selectedFiles.map(file => URL.createObjectURL(file));
            setPreviews(prevPreviews => [...prevPreviews, ...newPreviews]);
        }
    };

    const nextImage = () => {
        const totalImages = roomDetails.roomPhotoUrls.length + previews.length;
        if (totalImages > 0) {
            setCurrentImageIndex((prevIndex) => 
                prevIndex === totalImages - 1 ? 0 : prevIndex + 1
            );
        }
    };

    const prevImage = () => {
        const totalImages = roomDetails.roomPhotoUrls.length + previews.length;
        if (totalImages > 0) {
            setCurrentImageIndex((prevIndex) => 
                prevIndex === 0 ? totalImages - 1 : prevIndex - 1
            );
        }
    };

    const handleUpdate = async () => {
        try {
            const formData = new FormData();
            formData.append('roomType', roomDetails.roomType);
            formData.append('roomPrice', roomDetails.roomPrice);
            formData.append('roomDescription', roomDetails.roomDescription);

            // Append each new file
            files.forEach(file => {
                formData.append('photos', file);
            });

            const result = await ApiService.updateRoom(roomId, formData);
            if (result.statusCode === 200) {
                setSuccess('Room updated successfully.');
                
                setTimeout(() => {
                    setSuccess('');
                    navigate('/admin/manage-rooms');
                }, 3000);
            }
            setTimeout(() => setSuccess(''), 5000);
        } catch (error) {
            setError(error.response?.data?.message || error.message);
            setTimeout(() => setError(''), 5000);
        }
    };

    const handleDelete = async () => {
        if (window.confirm('Do you want to delete this room?')) {
            try {
                const result = await ApiService.deleteRoom(roomId);
                if (result.statusCode === 200) {
                    setSuccess('Room Deleted successfully.');
                    
                    setTimeout(() => {
                        setSuccess('');
                        navigate('/admin/manage-rooms');
                    }, 3000);
                }
            } catch (error) {
                setError(error.response?.data?.message || error.message);
                setTimeout(() => setError(''), 5000);
            }
        }
    };

    const totalImages = roomDetails.roomPhotoUrls.length + previews.length;
    const currentImage = currentImageIndex < roomDetails.roomPhotoUrls.length
        ? `${ApiService.BASE_URL}${roomDetails.roomPhotoUrls[currentImageIndex]}`
        : previews[currentImageIndex - roomDetails.roomPhotoUrls.length];

    return (
        <div className="edit-room-container">
            <h2>Edit Room</h2>
            {error && <p className="error-message">{error}</p>}
            {success && <p className="success-message">{success}</p>}
            <div className="edit-room-form">
                <div className="room-image-carousel">
                    {totalImages > 0 ? (
                        <>
                            <img 
                                src={currentImage}
                                alt="Room Preview" 
                                className="room-photo-preview"
                                onError={(e) => {
                                    e.target.onerror = null;
                                    e.target.src = '/placeholder-room.jpg';
                                }}
                            />
                            {totalImages > 1 && (
                                <>
                                    <button className="carousel-button prev" onClick={prevImage}>
                                        <FaChevronLeft />
                                    </button>
                                    <button className="carousel-button next" onClick={nextImage}>
                                        <FaChevronRight />
                                    </button>
                                    <div className="image-indicators">
                                        {Array.from({ length: totalImages }).map((_, index) => (
                                            <span
                                                key={index}
                                                className={`indicator ${index === currentImageIndex ? 'active' : ''}`}
                                                onClick={() => setCurrentImageIndex(index)}
                                            />
                                        ))}
                                    </div>
                                </>
                            )}
                        </>
                    ) : (
                        <img 
                            src="/placeholder-room.jpg"
                            alt="Room Preview"
                            className="room-photo-preview"
                        />
                    )}
                    <input
                        type="file"
                        name="roomPhotos"
                        onChange={handleFileChange}
                        multiple
                        accept="image/*"
                    />
                    <p className="image-limit-info">Maximum 5 images allowed</p>
                </div>
                <div className="form-group">
                    <label>Room Type</label>
                    <input
                        type="text"
                        name="roomType"
                        value={roomDetails.roomType}
                        onChange={handleChange}
                    />
                </div>
                <div className="form-group">
                    <label>Room Price</label>
                    <input
                        type="text"
                        name="roomPrice"
                        value={roomDetails.roomPrice}
                        onChange={handleChange}
                    />
                </div>
                <div className="form-group">
                    <label>Room Description</label>
                    <textarea
                        name="roomDescription"
                        value={roomDetails.roomDescription}
                        onChange={handleChange}
                    />
                </div>
                <div className="button-group">
                    <button onClick={handleUpdate} className="update-button">Update Room</button>
                    <button onClick={handleDelete} className="delete-button">Delete Room</button>
                </div>
            </div>
        </div>
    );
};

export default EditRoomPage;