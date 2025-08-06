package com.hotelsystemmanegment.Service.Implementation;

import com.hotelsystemmanegment.DTO.LoginRequest;
import com.hotelsystemmanegment.DTO.Response;
import com.hotelsystemmanegment.DTO.UserDTO;
import com.hotelsystemmanegment.Entity.User;
import com.hotelsystemmanegment.Exception.OurException;
import com.hotelsystemmanegment.Repositories.UserRepository;
import com.hotelsystemmanegment.Service.Interface.IUserService;
import com.hotelsystemmanegment.Utils.JWTUtils;
import com.hotelsystemmanegment.Utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public Response register(User user) {
        Response response = new Response();
        try{
            if (user.getRole() == null || user.getRole().isBlank()){
                // First 2 users become ADMIN, rest become USER
                long userCount = userRepository.count();
                if (userCount < 2) {
                    user.setRole("ADMIN");
                } else {
                    user.setRole("USER");
                }
            }
            if (userRepository.existsByEmail(user.getEmail())){
                throw new OurException(user.getEmail() + " is already registered");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepository.save(user);
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(savedUser);
            response.setStatusCode(200);
            response.setUser(userDTO);
        }catch(OurException e){
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error Occurred during user registration" + e.getMessage());
        }
        return response;
    }

    @Override
    public Response login(LoginRequest loginRequest) {
        Response response = new Response();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            var user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new OurException("User Not Found"));

            var jwt = jwtUtils.generateToken(user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRole(user.getRole());
            response.setExpirationTime("7 Days");
            response.setMessage("Successfully logged in");
            response.setUser(Utils.mapUserEntityToUserDTO(user));

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during user login: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAllUsers() {
        Response response = new Response();
        try{
            List<User> userList = userRepository.findAll();
            List<UserDTO> userDTOList = Utils.mapUserEntityToUserListDTO(userList);
            response.setStatusCode(200);
            response.setMessage("Successfully retrieved all users");
            response.setUserList(userDTOList);

        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error getting all users: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getUserBookingHistory(String userId) {
        Response response = new Response();
        try {
            User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(() -> new OurException("User Not Found"));
            UserDTO userDTO = Utils.mapUserEntityToUserDTOPlusUserBookingsAndRoom(user);
            response.setStatusCode(200);
            response.setMessage("Successfully retrieved user bookings");
            response.setUser(userDTO);

        }catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error getting users: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response deleteUser(String userId) {
        Response response = new Response();
        try {
            User userToDelete = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new OurException("User Not Found"));
            
            // Prevent self-deletion
            // Note: This would require passing the current user's ID from the controller
            // For now, we'll rely on frontend validation
            
            // Prevent deletion of the last admin
            if (userToDelete.getRole().equals("ADMIN")) {
                long adminCount = userRepository.countByRole("ADMIN");
                if (adminCount <= 1) {
                    throw new OurException("Cannot delete the last admin user. At least one admin must remain in the system.");
                }
            }
            
            userRepository.deleteById(Long.valueOf(userId));
            response.setStatusCode(200);
            response.setMessage("Successfully deleted user");

        }catch (OurException e){
            response.setStatusCode(400);
            response.setMessage(e.getMessage());

        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error deleting user: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getUserById(String userId) {
        Response response = new Response();
        try {
           User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(() -> new OurException("User Not Found"));
           UserDTO userDTO = Utils.mapUserEntityToUserDTO(user);
            response.setStatusCode(200);
            response.setMessage("Successfully retrieved user");
            response.setUser(userDTO);

        }catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error getting user: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getMyInfo(String email) {
        Response response = new Response();
        try {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new OurException("User Not Found"));
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(user);
            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setUser(userDTO);

        }catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error getting user: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response updateUser(String userId, User updatedUser, String currentPassword) {
        Response response = new Response();
        try {
            User existingUser = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new OurException("User Not Found"));

            // Verify current password
            if (!passwordEncoder.matches(currentPassword, existingUser.getPassword())) {
                throw new OurException("Current password is incorrect");
            }

            // Update user fields
            existingUser.setName(updatedUser.getName());
            existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
            
            // Only update email if it's changed and not already taken
            if (!existingUser.getEmail().equals(updatedUser.getEmail())) {
                if (userRepository.existsByEmail(updatedUser.getEmail())) {
                    throw new OurException("Email is already taken");
                }
                existingUser.setEmail(updatedUser.getEmail());
            }

            // Save updated user
            User savedUser = userRepository.save(existingUser);
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(savedUser);
            
            response.setStatusCode(200);
            response.setMessage("Profile updated successfully");
            response.setUser(userDTO);

        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error updating user: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response changeUserRole(Long userId, String newRole) {
        Response response = new Response();
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new OurException("User not found"));
            
            // Validate the new role
            if (newRole == null || newRole.trim().isEmpty()) {
                throw new OurException("Role cannot be empty");
            }
            
            // Validate role types
            if (!newRole.equals("ADMIN") && !newRole.equals("USER") && !newRole.equals("RECEPTIONIST")) {
                throw new OurException("Invalid role. Only ADMIN, USER, and RECEPTIONIST roles are allowed.");
            }
            
            // Check if trying to create more than 2 ADMIN users
            if (newRole.equals("ADMIN")) {
                long adminCount = userRepository.countByRole("ADMIN");
                // If the user is already an admin, we don't need to count them again
                if (!user.getRole().equals("ADMIN")) {
                    adminCount++;
                }
                if (adminCount > 2) {
                    throw new OurException("Maximum 2 ADMIN users allowed. Cannot create more administrators.");
                }
            }
            
            user.setRole(newRole);
            userRepository.save(user);
            
            response.setStatusCode(200);
            response.setMessage("User role updated successfully");
            
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error updating user role: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getRoleStatistics() {
        Response response = new Response();
        try {
            long adminCount = userRepository.countByRole("ADMIN");
            long userCount = userRepository.countByRole("USER");
            long receptionistCount = userRepository.countByRole("RECEPTIONIST");
            
            response.setStatusCode(200);
            response.setMessage("Role statistics retrieved successfully");
            // You can add these counts to the response if needed
            // For now, we'll just return success
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting role statistics: " + e.getMessage());
        }
        return response;
    }
}
