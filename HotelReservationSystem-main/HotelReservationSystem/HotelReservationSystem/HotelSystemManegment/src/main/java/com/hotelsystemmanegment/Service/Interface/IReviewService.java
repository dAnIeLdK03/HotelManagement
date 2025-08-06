package com.hotelsystemmanegment.Service.Interface;

import com.hotelsystemmanegment.DTO.Response;
import com.hotelsystemmanegment.Entity.Review;

public interface IReviewService {
    
    // Create and manage reviews
    Response createReview(Review review);
    Response updateReview(Long reviewId, Review review);
    Response deleteReview(Long reviewId);
    
    // Get reviews
    Response getAllApprovedReviews();
    Response getReviewsByRoom(Long roomId);
    Response getHotelReviews();
    Response getReviewsByUser(Long userId);
    Response getReviewById(Long reviewId);
    
    // Moderation
    Response getPendingReviews();
    Response approveReview(Long reviewId);
    Response rejectReview(Long reviewId);
    
    // Statistics
    Response getReviewStatistics();
    Response getAverageRatings();
    
    // Check if user can review booking
    Response canUserReviewBooking(Long bookingId, Long userId);
} 