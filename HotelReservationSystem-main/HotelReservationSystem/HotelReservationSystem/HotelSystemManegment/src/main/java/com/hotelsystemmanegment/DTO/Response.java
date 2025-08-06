package com.hotelsystemmanegment.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Setter;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    private int statusCode;

    private String message;

    private String token;

    private String role;

    private String bookingConfirmationCode;

    private String expirationTime;

    private String totalNumberOfGuests;

    @Setter
    private Object data;

    // Review fields
    private ReviewDTO review;
    private List<ReviewDTO> reviewList;
    private Boolean canReview;

    private UserDTO user;
    private BookingDTO booking;
    private RoomDTO room;

    private List<UserDTO> userList;
    private List<BookingDTO> bookingList;
    private List<RoomDTO> roomList;
}
