import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import ApiService from '../../service/ApiService';
import { FaChevronLeft, FaChevronRight } from 'react-icons/fa';

const AddRoomPage = () => {
    const navigate = useNavigate();
    const [roomDetails, setRoomDetails] = useState({
        roomType: '',
        roomPrice: '',
        roomDescription: '',
    });
    const [files, setFiles] = useState([]);
    const [previews, setPreviews] = useState([]);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [roomTypes, setRoomTypes] = useState([]);
    const [newRoomType, setNewRoomType] = useState(false);
    const [currentImageIndex, setCurrentImageIndex] = useState(0);

    useEffect(() => {
        const fetchRoomTypes = async () => {
            try {
                const types = await ApiService.getRoomTypes();
                setRoomTypes(types);
            } catch (error) {
                console.error('Error fetching room types:', error.message);
            }
        };
        fetchRoomTypes();
    }, []);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setRoomDetails(prevState => ({
            ...prevState,
            [name]: value,
        }));
    };

    const handleRoomTypeChange = (e) => {
        if (e.target.value === 'new') {
            setNewRoomType(true);
            setRoomDetails(prevState => ({ ...prevState, roomType: '' }));
        } else {
            setNewRoomType(false);
            setRoomDetails(prevState => ({ ...prevState, roomType: e.target.value }));
        }
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
        if (previews.length > 0) {
            setCurrentImageIndex((prevIndex) => 
                prevIndex === previews.length - 1 ? 0 : prevIndex + 1
            );
        }
    };

    const prevImage = () => {
        if (previews.length > 0) {
            setCurrentImageIndex((prevIndex) => 
                prevIndex === 0 ? previews.length - 1 : prevIndex - 1
            );
        }
    };

    const addRoom = async () => {
        if (!roomDetails.roomType || !roomDetails.roomPrice || !roomDetails.roomDescription) {
            setError('All room details must be provided.');
            setTimeout(() => setError(''), 5000);
            return;
        }

        if (!window.confirm('Do you want to add this room?')) {
            return;
        }

        try {
            const formData = new FormData();
            formData.append('roomType', roomDetails.roomType);
            formData.append('roomPrice', roomDetails.roomPrice);
            formData.append('roomDescription', roomDetails.roomDescription);

            // Append each file
            files.forEach(file => {
                formData.append('photos', file);
            });

            const result = await ApiService.addRoom(formData);
            if (result.statusCode === 200) {
                setSuccess('Room Added successfully.');
                
                setTimeout(() => {
                    setSuccess('');
                    navigate('/admin/manage-rooms');
                }, 3000);
            }
        } catch (error) {
            setError(error.response?.data?.message || error.message);
            setTimeout(() => setError(''), 5000);
        }
    };

    return (
        <div className="edit-room-container">
            <h2>Add New Room</h2>
            {error && <p className="error-message">{error}</p>}
            {success && <p className="success-message">{success}</p>}
            <div className="edit-room-form">
                <div className="room-image-carousel">
                    {previews.length > 0 ? (
                        <>
                            <img 
                                src={previews[currentImageIndex]} 
                                alt="Room Preview" 
                                className="room-photo-preview"
                            />
                            {previews.length > 1 && (
                                <>
                                    <button className="carousel-button prev" onClick={prevImage}>
                                        <FaChevronLeft />
                                    </button>
                                    <button className="carousel-button next" onClick={nextImage}>
                                        <FaChevronRight />
                                    </button>
                                    <div className="image-indicators">
                                        {previews.map((_, index) => (
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
                    <select value={roomDetails.roomType} onChange={handleRoomTypeChange}>
                        <option value="">Select a room type</option>
                        {roomTypes.map(type => (
                            <option key={type} value={type}>{type}</option>
                        ))}
                        <option value="new">Other (please specify)</option>
                    </select>
                    {newRoomType && (
                        <input
                            type="text"
                            name="roomType"
                            placeholder="Enter new room type"
                            value={roomDetails.roomType}
                            onChange={handleChange}
                        />
                    )}
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
                <button className="add-button" onClick={addRoom}>Add Room</button>
            </div>
        </div>
    );
};

export default AddRoomPage;