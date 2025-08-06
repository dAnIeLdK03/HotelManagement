package com.hotelsystemmanegment.Repositories;

import com.hotelsystemmanegment.Entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Find approved reviews
    List<Review> findByIsApprovedTrueOrderByCreatedAtDesc();
    
    // Find reviews by user
    List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // Find reviews by room
    List<Review> findByRoomIdAndIsApprovedTrueOrderByCreatedAtDesc(Long roomId);
    
    // Find hotel reviews (where room is null)
    List<Review> findByRoomIsNullAndIsApprovedTrueOrderByCreatedAtDesc();
    
    // Find pending reviews for moderation
    List<Review> findByIsApprovedFalseOrderByCreatedAtDesc();
    
    // Find reviews by booking
    Optional<Review> findByBookingId(Long bookingId);
    
    // Check if user has already reviewed a booking
    boolean existsByBookingIdAndUserId(Long bookingId, Long userId);
    
    // Get average ratings
    @Query("SELECT AVG(r.overallRating) FROM Review r WHERE r.isApproved = true")
    Double getAverageOverallRating();
    
    @Query("SELECT AVG(r.cleanlinessRating) FROM Review r WHERE r.isApproved = true")
    Double getAverageCleanlinessRating();
    
    @Query("SELECT AVG(r.serviceRating) FROM Review r WHERE r.isApproved = true")
    Double getAverageServiceRating();
    
    @Query("SELECT AVG(r.locationRating) FROM Review r WHERE r.isApproved = true")
    Double getAverageLocationRating();
    
    // Get reviews for pagination
    Page<Review> findByIsApprovedTrue(Pageable pageable);
} 