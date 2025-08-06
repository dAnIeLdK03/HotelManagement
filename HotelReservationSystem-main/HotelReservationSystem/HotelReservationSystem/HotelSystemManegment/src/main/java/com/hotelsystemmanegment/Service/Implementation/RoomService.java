package com.hotelsystemmanegment.Service.Implementation;

import com.hotelsystemmanegment.DTO.Response;
import com.hotelsystemmanegment.DTO.RoomDTO;
import com.hotelsystemmanegment.Exception.OurException;
import com.hotelsystemmanegment.Repositories.BookingRepository;
import com.hotelsystemmanegment.Repositories.RoomRepository;
import com.hotelsystemmanegment.Repositories.RoomImageRepository;
import com.hotelsystemmanegment.Service.Interface.IRoomService;
import com.hotelsystemmanegment.Utils.Utils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.hotelsystemmanegment.Entity.Room;
import com.hotelsystemmanegment.Entity.RoomImage;

@Service
public class RoomService implements IRoomService {

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private RoomImageRepository roomImageRepository;

    @Transactional
    public RoomDTO addRoomImage(Long roomId, MultipartFile file) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new OurException("Room not found"));

        // Check if room already has 5 images
        if (room.getImages().size() >= 5) {
            throw new OurException("Room already has maximum number of images (5)");
        }

        try {
            RoomImage roomImage = new RoomImage();
            roomImage.setImageData(file.getBytes());
            roomImage.setRoom(room);
            roomImage.setImageName(file.getOriginalFilename());
            roomImageRepository.save(roomImage);
        } catch (IOException e) {
            throw new OurException("Failed to read image file");
        }

        room = roomRepository.findById(roomId).orElseThrow();
        return mapRoomToDTO(room);
    }

    private RoomDTO mapRoomToDTO(Room room) {
        RoomDTO dto = new RoomDTO();
        dto.setId(room.getId());
        dto.setRoomType(room.getRoomType());
        dto.setRoomPrice(room.getRoomPrice());
        dto.setRoomDescription(room.getRoomDescription());

        if (!room.getImages().isEmpty()) {
            List<String> photoUrls = room.getImages().stream()
                .limit(5) // Limit to 5 images per room
                .map(this::generatePhotoUrl)
                .collect(Collectors.toList());
            dto.setRoomPhotoUrls(photoUrls);
        }

        return dto;
    }

    private String generatePhotoUrl(RoomImage image) {
        return "/rooms/image/" + image.getId();
    }

    @Override
    public Response addNewRoom(List<MultipartFile> photos, String roomType, BigDecimal roomPrice, String description) {
        Response response = new Response();
        try {
            Room room = new Room();
            room.setRoomType(roomType);
            room.setRoomPrice(roomPrice.toString());
            room.setRoomDescription(description);
            Room savedRoom = roomRepository.save(room);

            if (photos != null && !photos.isEmpty()) {
                for (MultipartFile photo : photos) {
                RoomImage roomImage = new RoomImage();
                roomImage.setRoom(savedRoom);
                roomImage.setPhoto(photo.getBytes());
                roomImage.setImageName(photo.getOriginalFilename());
                roomImageRepository.save(roomImage);
                }
            }
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTO(savedRoom);
            response.setStatusCode(200);
            response.setMessage("Room added successfully");
            response.setRoom(roomDTO);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public List<String> getAllRoomTypes() {
        return roomRepository.findDistinctRoomTypes();
    }

    @Override
    public Response getAllRooms() {
        Response response = new Response();
        try {
            List<Room> roomList = roomRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
            List<RoomDTO> roomDTOList = Utils.mapRoomEntityToRoomListDTO(roomList);
            response.setStatusCode(200);
            response.setMessage("successfully");
            response.setRoomList(roomDTOList);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting rooms " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response deleteRoom(Long roomId) {
        Response response = new Response();
        try {
            roomRepository.findById(roomId).orElseThrow(() -> new OurException("Room not found"));
            roomRepository.deleteById(roomId);
            response.setStatusCode(200);
            response.setMessage("Room deleted successfully");
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error deleting room " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response updateRoom(Long roomId, String description, String roomType, BigDecimal roomPrice, List<MultipartFile> photos) {
        Response response = new Response();
        try {
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new OurException("Room not found"));
            if (roomType != null) room.setRoomType(roomType);
            if (roomPrice != null) room.setRoomPrice(roomPrice.toString());
            if (description != null) room.setRoomDescription(description);
            Room updatedRoom = roomRepository.save(room);

            if (photos != null && !photos.isEmpty()) {
                // Check if adding new photos would exceed the limit
                if (room.getImages().size() + photos.size() > 5) {
                    throw new OurException("Room already has maximum number of images (5)");
                }

                for (MultipartFile photo : photos) {
                RoomImage roomImage = new RoomImage();
                roomImage.setRoom(updatedRoom);
                roomImage.setPhoto(photo.getBytes());
                roomImage.setImageName(photo.getOriginalFilename());
                roomImageRepository.save(roomImage);
                }
            }
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTO(updatedRoom);
            response.setStatusCode(200);
            response.setMessage("Room updated successfully");
            response.setRoom(roomDTO);
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error updating room " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getRoomById(Long roomId) {
        Response response = new Response();
        try {
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new OurException("Room not found"));
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTOPlusBookings(room);
            response.setStatusCode(200);
            response.setMessage("successfully");
            response.setRoom(roomDTO);
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting room " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAvailableRoomsByDateAndType(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
        Response response = new Response();

        try {
            // Вземи всички валидни типове стаи от базата
            List<String> validRoomTypes = roomRepository.findAllRoomTypes(); // трябва да създадеш този метод

            // Провери дали подаденият тип съществува
            if (!validRoomTypes.contains(roomType)) {
                response.setStatusCode(404);
                response.setMessage("Room type '" + roomType + "' not found.");
                response.setRoomList(List.of());
                return response;
            }

            // Извлечи наличните стаи
            List<Room> availableRooms = roomRepository.findAvailableRoomsByDatesAndTyoes(checkInDate, checkOutDate, roomType);
            List<RoomDTO> roomDTOList = Utils.mapRoomEntityToRoomListDTO(availableRooms);

            response.setStatusCode(200);
            response.setMessage("Successfully retrieved available rooms.");
            response.setRoomList(roomDTOList);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting available rooms: " + e.getMessage());
        }

        return response;
    }

    @Override
    public Response getAllAvailableRooms() {
        Response response = new Response();
        try {
            List<Room> roomList = roomRepository.getAllAvailableRooms();
            List<RoomDTO> roomDTOList = Utils.mapRoomEntityToRoomListDTO(roomList);
            response.setStatusCode(200);
            response.setMessage("successfully");
            response.setRoomList(roomDTOList);
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting available rooms " + e.getMessage());
        }
        return response;
    }
}
