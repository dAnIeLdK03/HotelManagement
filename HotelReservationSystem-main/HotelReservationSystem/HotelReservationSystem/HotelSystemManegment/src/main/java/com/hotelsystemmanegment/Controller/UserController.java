package com.hotelsystemmanegment.Controller;



import com.hotelsystemmanegment.DTO.Response;
import com.hotelsystemmanegment.Entity.Booking;
import com.hotelsystemmanegment.Service.Interface.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.hotelsystemmanegment.Repositories.BookingRepository;
import com.hotelsystemmanegment.Entity.User;


import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {


    @Autowired
    private IUserService userService;
    @Autowired
    private BookingRepository bookingRepository;


    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> getAllUsers() {
        Response response = userService.getAllUsers();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/get-by-id/{userId}")
    public ResponseEntity<Response> getUserById(@PathVariable("userId") String userId) {
        Response response = userService.getUserById(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/delete/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> deleteUser(@PathVariable("userId") String userId) {
        Response response = userService.deleteUser(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/get-logged-in-profile-info")
    public ResponseEntity<Response> getLoggedInUserProfile() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Response response = userService.getMyInfo(email);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/get-user-bookings/{userId}")
    public ResponseEntity<Response> getUserBookingHistory(@PathVariable("userId") Long userId) {
        List<Booking> bookings = bookingRepository.findByUserId(userId);

        Response response = new Response();
        if (bookings.isEmpty()) {
            response.setStatusCode(404);
            response.setMessage("No bookings found for user ID: " + userId);
        } else {
            response.setStatusCode(200);
            response.setMessage("Bookings retrieved successfully");
            response = userService.getUserBookingHistory(String.valueOf(userId));
        }

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<Response> updateUser(
            @PathVariable("userId") String userId,
            @RequestBody User user,
            @RequestParam("currentPassword") String currentPassword) {
        Response response = userService.updateUser(userId, user, currentPassword);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/change-role/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> changeUserRole(@PathVariable Long userId, @RequestParam String newRole) {
        Response response = userService.changeUserRole(userId, newRole);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/role-stats")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> getRoleStatistics() {
        Response response = userService.getRoleStatistics();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
