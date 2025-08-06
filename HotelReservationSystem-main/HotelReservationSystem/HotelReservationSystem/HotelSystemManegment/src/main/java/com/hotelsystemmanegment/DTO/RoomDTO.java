package com.hotelsystemmanegment.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hotelsystemmanegment.Entity.Booking;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoomDTO {

    private Long id;
    private String roomType;
    private String roomPrice;
    private List<String> roomPhotoUrls;
    private String roomDescription;
    private List<BookingDTO> bookings;

}
