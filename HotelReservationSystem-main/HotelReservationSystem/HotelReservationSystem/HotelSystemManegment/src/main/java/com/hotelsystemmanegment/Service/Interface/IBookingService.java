package com.hotelsystemmanegment.Service.Interface;

import com.hotelsystemmanegment.DTO.Response;
import com.hotelsystemmanegment.Entity.Booking;
import jakarta.validation.constraints.NotBlank;

public interface IBookingService {

    Response saveBooking(Long roomId, Long userId, Booking bookingRequest);
    Response findBookingByConfirmationCode(String confirmationCode);
    Response findMyBookingByConfirmationCode(String confirmationCode);
    Response getAllBookings();
    Response cancelBooking(Long bookingId);

    Response achieveBooking(Long bookingId);

    Booking findBookingById(Long bookingId);

    void sendFeedbackRequest(@NotBlank(message = "email is required") String email, @NotBlank(message = "name is required") String name, String bookingConfirmationCode);

    void saveBooking(Booking booking);
}
