package com.hotelsystemmanegment.Service.Scheduler;

import com.hotelsystemmanegment.Entity.Booking;
import com.hotelsystemmanegment.Repositories.BookingRepository;
import com.hotelsystemmanegment.Service.Interface.IBookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;

@Component
public class FeedbackScheduler {

    private static final Logger logger = LoggerFactory.getLogger(FeedbackScheduler.class);

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private IBookingService bookingService;

    // This task will run every day at 10:00 AM
    @Scheduled(cron = "0 0 10 * * ?")
    public void sendFeedbackEmails() {
        logger.info("Starting scheduled task to send feedback emails...");

        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<Booking> completedBookings = bookingRepository.findCompletedBookingsForFeedback(yesterday);

        if (completedBookings.isEmpty()) {
            logger.info("No completed bookings found from yesterday. No feedback emails to send.");
            return;
        }

        logger.info("Found {} bookings that ended yesterday. Preparing to send feedback emails.", completedBookings.size());

        for (Booking booking : completedBookings) {
            try {
                // Ensure user and email are not null
                if (booking.getUser() != null && booking.getUser().getEmail() != null) {
                    logger.info("Sending feedback email for booking ID: {} to {}", booking.getId(), booking.getUser().getEmail());
                    
                    bookingService.sendFeedbackRequest(
                            booking.getUser().getEmail(),
                            booking.getUser().getName(),
                            booking.getBookingConfirmationCode()
                    );
                    
                    // Mark the booking as feedback email sent
                    booking.setFeedbackEmailSent(true);
                    bookingRepository.save(booking);
                    
                    logger.info("Successfully sent feedback email and updated booking ID: {}", booking.getId());
                } else {
                    logger.warn("Skipping booking ID: {} because user or user email is null.", booking.getId());
                }
            } catch (Exception e) {
                logger.error("Failed to send feedback email for booking ID: " + booking.getId(), e);
            }
        }
        logger.info("Finished scheduled task for sending feedback emails.");
    }
} 