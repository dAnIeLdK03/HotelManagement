package com.hotelsystemmanegment.Service.Interface;

import com.hotelsystemmanegment.DTO.Response;
import com.hotelsystemmanegment.DTO.RoomDTO;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IRoomService {

    Response addNewRoom(List<MultipartFile> photos, String roomType, BigDecimal roomPrice, String description);

    List<String> getAllRoomTypes();

    Response getAllRooms();

    Response deleteRoom(Long roomId);

    Response updateRoom(Long roomId, String description, String roomType, BigDecimal roomPrice, List<MultipartFile> photos);

    Response getRoomById(Long roomId);

    Response getAvailableRoomsByDateAndType(LocalDate checkInDate, LocalDate checkOutDate, String roomType);

    Response getAllAvailableRooms();

    RoomDTO addRoomImage(Long roomId, MultipartFile file);
}
