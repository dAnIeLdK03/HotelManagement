import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import ApiService from '../../service/ApiService';
import './EditProfilePage.css';

const EditProfilePage = () => {
    const [user, setUser] = useState(null);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(null);
    const [isEditing, setIsEditing] = useState(false);
    const [password, setPassword] = useState('');
    const [formData, setFormData] = useState({
        name: '',
        email: '',
        phoneNumber: ''
    });
    const navigate = useNavigate();

    useEffect(() => {
        const fetchUserProfile = async () => {
            try {
                const response = await ApiService.getUserProfile();
                setUser(response.user);
                setFormData({
                    name: response.user.name,
                    email: response.user.email,
                    phoneNumber: response.user.phoneNumber
                });
            } catch (error) {
                setError(error.message);
            }
        };

        fetchUserProfile();
    }, []);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleEditClick = () => {
        setIsEditing(true);
        setError(null);
        setSuccess(null);
    };

    const handleCancelEdit = () => {
        setIsEditing(false);
        setPassword('');
        setFormData({
            name: user.name,
            email: user.email,
            phoneNumber: user.phoneNumber
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);
        setSuccess(null);

        if (!password) {
            setError('Please enter your password to confirm changes');
            return;
        }

        try {
            // Here you would typically verify the password first
            // For now, we'll just update the profile
            // TODO: Add password verification endpoint
            await ApiService.updateUserProfile(user.id, {
                ...formData,
                password // Include password for verification
            });
            
            setSuccess('Profile updated successfully');
            setIsEditing(false);
            setPassword('');
            
            // Refresh user data
            const response = await ApiService.getUserProfile();
            setUser(response.user);
        } catch (error) {
            if (error.response?.status === 403) {
                setError('You are not authorized to make these changes. Please check your password and try again.');
            } else {
                setError(error.response?.data?.message || error.message || 'Failed to update profile');
            }
        }
    };

    const handleDeleteProfile = async () => {
        if (!window.confirm('Are you sure you want to delete your account?')) {
            return;
        }
        try {
            await ApiService.deleteUser(user.id);
            navigate('/signup');
        } catch (error) {
            setError(error.message);
        }
    };

    return (
        <div className="edit-profile-page">
            <h2>Edit Profile</h2>
            {error && <p className="error-message">{error}</p>}
            {success && <p className="success-message">{success}</p>}
            
            {user && (
                <div className="profile-details">
                    {!isEditing ? (
                        <>
                    <p><strong>Name:</strong> {user.name}</p>
                    <p><strong>Email:</strong> {user.email}</p>
                    <p><strong>Phone Number:</strong> {user.phoneNumber}</p>
                            <button className="edit-button" onClick={handleEditClick}>Edit Profile</button>
                    <button className="delete-profile-button" onClick={handleDeleteProfile}>Delete Profile</button>
                        </>
                    ) : (
                        <form onSubmit={handleSubmit} className="edit-form">
                            <div className="form-group">
                                <label htmlFor="name">Name:</label>
                                <input
                                    type="text"
                                    id="name"
                                    name="name"
                                    value={formData.name}
                                    onChange={handleInputChange}
                                    required
                                />
                            </div>
                            
                            <div className="form-group">
                                <label htmlFor="email">Email:</label>
                                <input
                                    type="email"
                                    id="email"
                                    name="email"
                                    value={formData.email}
                                    onChange={handleInputChange}
                                    required
                                />
                            </div>
                            
                            <div className="form-group">
                                <label htmlFor="phoneNumber">Phone Number:</label>
                                <input
                                    type="tel"
                                    id="phoneNumber"
                                    name="phoneNumber"
                                    value={formData.phoneNumber}
                                    onChange={handleInputChange}
                                    required
                                />
                            </div>
                            
                            <div className="form-group">
                                <label htmlFor="password">Enter Password to Confirm Changes:</label>
                                <input
                                    type="password"
                                    id="password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    required
                                />
                            </div>
                            
                            <div className="button-group">
                                <button type="submit" className="save-button">Save Changes</button>
                                <button type="button" className="cancel-button" onClick={handleCancelEdit}>Cancel</button>
                            </div>
                        </form>
                    )}
                </div>
            )}
        </div>
    );
};

export default EditProfilePage;