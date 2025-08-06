package com.hotelsystemmanegment.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hotelsystemmanegment.Entity.Booking;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingDTO {


    private long id;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private int numOfAdults;

    private int numOfChildren;

    private int totalNumberOfGuests;

    private String bookingConfirmationCode;

    private UserDTO user;

    private RoomDTO room;

    public BookingDTO(Booking booking) {
    }


}
