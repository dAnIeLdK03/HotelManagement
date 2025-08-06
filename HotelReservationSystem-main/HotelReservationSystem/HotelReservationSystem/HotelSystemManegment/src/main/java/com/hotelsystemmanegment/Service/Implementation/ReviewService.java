package com.hotelsystemmanegment.Service.Implementation;

import com.hotelsystemmanegment.DTO.Response;
import com.hotelsystemmanegment.DTO.ReviewDTO;
import com.hotelsystemmanegment.Entity.Booking;
import com.hotelsystemmanegment.Entity.Review;
import com.hotelsystemmanegment.Entity.Room;
import com.hotelsystemmanegment.Entity.User;
import com.hotelsystemmanegment.Exception.OurException;
import com.hotelsystemmanegment.Repositories.BookingRepository;
import com.hotelsystemmanegment.Repositories.ReviewRepository;
import com.hotelsystemmanegment.Repositories.RoomRepository;
import com.hotelsystemmanegment.Repositories.UserRepository;
import com.hotelsystemmanegment.Service.Interface.IReviewService;
import com.hotelsystemmanegment.Utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService implements IReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Override
    public Response createReview(Review review) {
        Response response = new Response();
        try {
            // Validate ratings
            if (review.getOverallRating() < 1 || review.getOverallRating() > 5) {
                throw new OurException("Overall rating must be between 1 and 5");
            }
            if (review.getCleanlinessRating() < 1 || review.getCleanlinessRating() > 5) {
                throw new OurException("Cleanliness rating must be between 1 and 5");
            }
            if (review.getServiceRating() < 1 || review.getServiceRating() > 5) {
                throw new OurException("Service rating must be between 1 and 5");
            }
            if (review.getLocationRating() < 1 || review.getLocationRating() > 5) {
                throw new OurException("Location rating must be between 1 and 5");
            }

            // Get current user
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new OurException("Current user not found"));

            // Check if user has already reviewed this booking
            if (review.getBooking() != null && reviewRepository.existsByBookingIdAndUserId(
                    review.getBooking().getId(), currentUser.getId())) {
                throw new OurException("You have already reviewed this booking");
            }

            // Set user and default values
            review.setUser(currentUser);
            review.setIsApproved(false); // Requires moderation

            Review savedReview = reviewRepository.save(review);
            ReviewDTO reviewDTO = mapReviewToDTO(savedReview);

            response.setStatusCode(200);
            response.setMessage("Review submitted successfully and is pending approval");
            response.setReview(reviewDTO);

        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error creating review: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response updateReview(Long reviewId, Review review) {
        Response response = new Response();
        try {
            Review existingReview = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new OurException("Review not found"));

            // Get current user
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new OurException("Current user not found"));

            // Check if user owns the review or is admin
            if (!currentUser.getRole().equals("ADMIN") && existingReview.getUser().getId() != currentUser.getId()) {
                throw new OurException("You can only update your own reviews");
            }

            // Update fields
            existingReview.setTitle(review.getTitle());
            existingReview.setComment(review.getComment());
            existingReview.setOverallRating(review.getOverallRating());
            existingReview.setCleanlinessRating(review.getCleanlinessRating());
            existingReview.setServiceRating(review.getServiceRating());
            existingReview.setLocationRating(review.getLocationRating());
            existingReview.setIsApproved(false); // Reset approval status

            Review savedReview = reviewRepository.save(existingReview);
            ReviewDTO reviewDTO = mapReviewToDTO(savedReview);

            response.setStatusCode(200);
            response.setMessage("Review updated successfully");
            response.setReview(reviewDTO);

        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error updating review: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response deleteReview(Long reviewId) {
        Response response = new Response();
        try {
            Review review = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new OurException("Review not found"));

            // Get current user
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new OurException("Current user not found"));

            // Check if user owns the review or is admin
            if (!currentUser.getRole().equals("ADMIN") && review.getUser().getId() != currentUser.getId()) {
                throw new OurException("You can only delete your own reviews");
            }

            reviewRepository.delete(review);

            response.setStatusCode(200);
            response.setMessage("Review deleted successfully");

        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error deleting review: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAllApprovedReviews() {
        Response response = new Response();
        try {
            List<Review> reviews = reviewRepository.findByIsApprovedTrueOrderByCreatedAtDesc();
            List<ReviewDTO> reviewDTOs = reviews.stream()
                    .map(this::mapReviewToDTO)
                    .collect(Collectors.toList());

            response.setStatusCode(200);
            response.setMessage("Reviews retrieved successfully");
            response.setReviewList(reviewDTOs);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving reviews: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getReviewsByRoom(Long roomId) {
        Response response = new Response();
        try {
            List<Review> reviews = reviewRepository.findByRoomIdAndIsApprovedTrueOrderByCreatedAtDesc(roomId);
            List<ReviewDTO> reviewDTOs = reviews.stream()
                    .map(this::mapReviewToDTO)
                    .collect(Collectors.toList());

            response.setStatusCode(200);
            response.setMessage("Room reviews retrieved successfully");
            response.setReviewList(reviewDTOs);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving room reviews: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getHotelReviews() {
        Response response = new Response();
        try {
            List<Review> reviews = reviewRepository.findByRoomIsNullAndIsApprovedTrueOrderByCreatedAtDesc();
            List<ReviewDTO> reviewDTOs = reviews.stream()
                    .map(this::mapReviewToDTO)
                    .collect(Collectors.toList());

            response.setStatusCode(200);
            response.setMessage("Hotel reviews retrieved successfully");
            response.setReviewList(reviewDTOs);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving hotel reviews: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getReviewsByUser(Long userId) {
        Response response = new Response();
        try {
            List<Review> reviews = reviewRepository.findByUserIdOrderByCreatedAtDesc(userId);
            List<ReviewDTO> reviewDTOs = reviews.stream()
                    .map(this::mapReviewToDTO)
                    .collect(Collectors.toList());

            response.setStatusCode(200);
            response.setMessage("User reviews retrieved successfully");
            response.setReviewList(reviewDTOs);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving user reviews: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getReviewById(Long reviewId) {
        Response response = new Response();
        try {
            Review review = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new OurException("Review not found"));

            ReviewDTO reviewDTO = mapReviewToDTO(review);

            response.setStatusCode(200);
            response.setMessage("Review retrieved successfully");
            response.setReview(reviewDTO);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving review: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getPendingReviews() {
        Response response = new Response();
        try {
            List<Review> reviews = reviewRepository.findByIsApprovedFalseOrderByCreatedAtDesc();
            List<ReviewDTO> reviewDTOs = reviews.stream()
                    .map(this::mapReviewToDTO)
                    .collect(Collectors.toList());

            response.setStatusCode(200);
            response.setMessage("Pending reviews retrieved successfully");
            response.setReviewList(reviewDTOs);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving pending reviews: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response approveReview(Long reviewId) {
        Response response = new Response();
        try {
            Review review = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new OurException("Review not found"));

            review.setIsApproved(true);
            reviewRepository.save(review);

            response.setStatusCode(200);
            response.setMessage("Review approved successfully");

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error approving review: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response rejectReview(Long reviewId) {
        Response response = new Response();
        try {
            Review review = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new OurException("Review not found"));

            reviewRepository.delete(review);

            response.setStatusCode(200);
            response.setMessage("Review rejected and deleted successfully");

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error rejecting review: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getReviewStatistics() {
        Response response = new Response();
        try {
            long totalReviews = reviewRepository.count();
            long approvedReviews = reviewRepository.findByIsApprovedTrueOrderByCreatedAtDesc().size();
            long pendingReviews = reviewRepository.findByIsApprovedFalseOrderByCreatedAtDesc().size();

            response.setStatusCode(200);
            response.setMessage("Review statistics retrieved successfully");

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving review statistics: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAverageRatings() {
        Response response = new Response();
        try {
            Double avgOverall = reviewRepository.getAverageOverallRating();
            Double avgCleanliness = reviewRepository.getAverageCleanlinessRating();
            Double avgService = reviewRepository.getAverageServiceRating();
            Double avgLocation = reviewRepository.getAverageLocationRating();

            response.setStatusCode(200);
            response.setMessage("Average ratings retrieved successfully");

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving average ratings: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response canUserReviewBooking(Long bookingId, Long userId) {
        Response response = new Response();
        try {
            boolean hasReviewed = reviewRepository.existsByBookingIdAndUserId(bookingId, userId);
            
            response.setStatusCode(200);
            response.setMessage(hasReviewed ? "User has already reviewed this booking" : "User can review this booking");
            response.setCanReview(!hasReviewed);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error checking review eligibility: " + e.getMessage());
        }
        return response;
    }

    // Helper method to map Review to ReviewDTO
    private ReviewDTO mapReviewToDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setTitle(review.getTitle());
        dto.setComment(review.getComment());
        dto.setOverallRating(review.getOverallRating());
        dto.setCleanlinessRating(review.getCleanlinessRating());
        dto.setServiceRating(review.getServiceRating());
        dto.setLocationRating(review.getLocationRating());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setIsApproved(review.getIsApproved());
        dto.setIsHotelReview(review.getIsHotelReview());
        dto.setAverageCategoryRating(review.getAverageCategoryRating());

        // Set user info
        if (review.getUser() != null) {
            dto.setUserName(review.getUser().getName());
            dto.setUserEmail(review.getUser().getEmail());
        }

        // Set room info
        if (review.getRoom() != null) {
            dto.setRoomType(review.getRoom().getRoomType());
            dto.setRoomId(review.getRoom().getId());
        }

        // Set booking info
        if (review.getBooking() != null) {
            dto.setBookingConfirmationCode(review.getBooking().getBookingConfirmationCode());
            dto.setBookingId(review.getBooking().getId());
        }

        return dto;
    }
} 