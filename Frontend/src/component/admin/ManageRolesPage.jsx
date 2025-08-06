import React, { useState, useEffect } from 'react';
import ApiService from '../../service/ApiService';
import './ManageRolesPage.css';

const ManageRolesPage = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [roleChanges, setRoleChanges] = useState({});

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const response = await ApiService.getAllUsers();
      if (response.statusCode === 200) {
        setUsers(response.userList);
      } else {
        setError('Failed to fetch users');
      }
    } catch (error) {
      setError('Error fetching users: ' + (error.response?.data?.message || error.message));
    } finally {
      setLoading(false);
    }
  };

  const handleRoleChange = (userId, newRole) => {
    setRoleChanges(prev => ({
      ...prev,
      [userId]: newRole
    }));
  };

  const saveRoleChanges = async (userId) => {
    const newRole = roleChanges[userId];
    if (!newRole) return;

    try {
      const response = await ApiService.changeUserRole(userId, newRole);
      if (response.statusCode === 200) {
        setSuccessMessage(`Role updated successfully for user ${userId}`);
        // Update the user in the local state
        setUsers(prevUsers => 
          prevUsers.map(user => 
            user.id === userId ? { ...user, role: newRole } : user
          )
        );
        // Clear the role change for this user
        setRoleChanges(prev => {
          const newChanges = { ...prev };
          delete newChanges[userId];
          return newChanges;
        });
        
        // Clear success message after 3 seconds
        setTimeout(() => setSuccessMessage(''), 3000);
      } else {
        setError(response.message || 'Failed to update role');
      }
    } catch (error) {
      setError('Error updating role: ' + (error.response?.data?.message || error.message));
    }
  };

  const deleteUser = async (userId, userName) => {
    // Get current user ID from localStorage or context
    const currentUserId = localStorage.getItem('userId'); // You might need to store this during login
    
    // Prevent self-deletion
    if (currentUserId && parseInt(currentUserId) === userId) {
      setError("You cannot delete your own account!");
      return;
    }

    const isConfirmed = window.confirm(`Are you sure you want to delete user "${userName}"? This action cannot be undone.`);
    if (!isConfirmed) return;

    try {
      const response = await ApiService.deleteUser(userId);
      if (response.statusCode === 200) {
        setSuccessMessage(`User "${userName}" deleted successfully`);
        // Remove the user from the local state
        setUsers(prevUsers => prevUsers.filter(user => user.id !== userId));
        
        // Clear success message after 3 seconds
        setTimeout(() => setSuccessMessage(''), 3000);
      } else {
        setError(response.message || 'Failed to delete user');
      }
    } catch (error) {
      setError('Error deleting user: ' + (error.response?.data?.message || error.message));
    }
  };

  const clearMessages = () => {
    setError('');
    setSuccessMessage('');
  };

  if (loading) {
    return <div className="manage-roles-container">Loading users...</div>;
  }

  return (
    <div className="manage-roles-container">
      <h2>Manage User Roles</h2>
      
      {error && (
        <div className="error-message" onClick={clearMessages}>
          {error}
        </div>
      )}
      
      {successMessage && (
        <div className="success-message" onClick={clearMessages}>
          {successMessage}
        </div>
      )}

      <div className="users-table">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Email</th>
              <th>Current Role</th>
              <th>New Role</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {users.map(user => (
              <tr key={user.id}>
                <td>{user.id}</td>
                <td>{user.name}</td>
                <td>{user.email}</td>
                <td>
                  <span className={`role-badge role-${user.role.toLowerCase()}`}>
                    {user.role}
                  </span>
                </td>
                <td>
                  <select
                    value={roleChanges[user.id] || user.role}
                    onChange={(e) => handleRoleChange(user.id, e.target.value)}
                  >
                    <option value="USER">USER</option>
                    <option value="RECEPTIONIST">RECEPTIONIST</option>
                    <option value="ADMIN">ADMIN</option>
                  </select>
                </td>
                <td>
                  {roleChanges[user.id] && roleChanges[user.id] !== user.role && (
                    <button 
                      className="save-role-button"
                      onClick={() => saveRoleChanges(user.id)}
                    >
                      Save
                    </button>
                  )}
                  <button 
                    className="delete-user-button"
                    onClick={() => deleteUser(user.id, user.name)}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default ManageRolesPage; 