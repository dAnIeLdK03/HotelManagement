package com.hotelsystemmanegment.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String comment;

    @Column(nullable = false)
    private Integer overallRating; // 1-5 stars

    // Category ratings
    @Column(nullable = false)
    private Integer cleanlinessRating; // 1-5 stars

    @Column(nullable = false)
    private Integer serviceRating; // 1-5 stars

    @Column(nullable = false)
    private Integer locationRating; // 1-5 stars

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Boolean isApproved = false; // For moderation

    @Column(nullable = false)
    private Boolean isHotelReview = true; // true for hotel, false for room

    // Relationships
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    @JsonIgnore
    private Room room; // null for hotel reviews

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    @JsonIgnore
    private Booking booking; // to link review to specific booking

    // Pre-persist to set creation date
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Calculate average rating from categories
    public Double getAverageCategoryRating() {
        return (cleanlinessRating + serviceRating + locationRating) / 3.0;
    }
} 