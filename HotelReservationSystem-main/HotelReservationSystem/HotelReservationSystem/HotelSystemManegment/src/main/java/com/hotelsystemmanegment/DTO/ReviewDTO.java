package com.hotelsystemmanegment.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewDTO {
    private Long id;
    private String title;
    private String comment;
    private Integer overallRating;
    private Integer cleanlinessRating;
    private Integer serviceRating;
    private Integer locationRating;
    private LocalDateTime createdAt;
    private Boolean isApproved;
    private Boolean isHotelReview;
    
    // User info
    private String userName;
    private String userEmail;
    
    // Room info (if room review)
    private String roomType;
    private Long roomId;
    
    // Booking info
    private String bookingConfirmationCode;
    private Long bookingId;
    
    // Calculated fields
    private Double averageCategoryRating;
} 