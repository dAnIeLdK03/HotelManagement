package com.hotelsystemmanegment.Controller;



import com.hotelsystemmanegment.DTO.Response;
import com.hotelsystemmanegment.DTO.RoomDTO;
import com.hotelsystemmanegment.Service.Interface.IRoomService;
import com.hotelsystemmanegment.Repositories.RoomImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private IRoomService roomService;
    @Autowired
    private RoomImageRepository roomImageRepository;

    @PostMapping("/{roomId}/images")
    public ResponseEntity<RoomDTO> addImage(
            @PathVariable Long roomId,
            @RequestParam("file") MultipartFile file
    ) {
        RoomDTO dto = roomService.addRoomImage(roomId, file);
        return ResponseEntity.ok(dto);
    }


    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> addNewRoom(
            @RequestParam(value = "photos", required = false) List<MultipartFile> photos,
            @RequestParam(value = "roomType", required = false) String roomType,
            @RequestParam(value = "roomPrice", required = false) BigDecimal roomPrice,
            @RequestParam(value = "roomDescription", required = false) String roomDescription
    ) {
        if (photos == null || photos.isEmpty() || roomType == null || roomType.isBlank() || roomPrice == null || roomType.isBlank()) {
            Response response = new Response();
            response.setStatusCode(400);
            response.setMessage("Please provide values for all fields(photos, roomType, roomPrice)");
            return ResponseEntity.status(response.getStatusCode()).body(response);
        }

        if (photos.size() > 5) {
            Response response = new Response();
            response.setStatusCode(400);
            response.setMessage("Maximum 5 images allowed per room");
            return ResponseEntity.status(response.getStatusCode()).body(response);
        }

        Response response = roomService.addNewRoom(photos, roomType, roomPrice, roomDescription);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAllRooms() {
        Response response = roomService.getAllRooms();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/types")
    public List<String> getRoomTypes() {
        return roomService.getAllRoomTypes();
    }

    @GetMapping("/room-by-id/{roomId}")
    public ResponseEntity<Response> getRoomById(@PathVariable Long roomId) {
        Response response = roomService.getRoomById(roomId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all-available-rooms")
    public ResponseEntity<Response> getAvailableRooms() {
        Response response = roomService.getAllAvailableRooms();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/available-rooms-by-date-and-type")
    public ResponseEntity<Response> getAvailableRoomsByDateAndType(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @RequestParam(required = false) String roomType
    ) {
        if (checkInDate == null || roomType == null || roomType.isBlank() || checkOutDate == null) {
            Response response = new Response();
            response.setStatusCode(400);
            response.setMessage("Please provide values for all fields(checkInDate, roomType,checkOutDate)");
            return ResponseEntity.status(response.getStatusCode()).body(response);
        }
        Response response = roomService.getAvailableRoomsByDateAndType(checkInDate, checkOutDate, roomType);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/update/{roomId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> updateRoom(
            @PathVariable Long roomId,
            @RequestParam(value = "photos", required = false) List<MultipartFile> photos,
            @RequestParam(value = "roomType", required = false) String roomType,
            @RequestParam(value = "roomPrice", required = false) BigDecimal roomPrice,
            @RequestParam(value = "roomDescription", required = false) String roomDescription
    ) {
        if (photos != null && photos.size() > 5) {
            Response response = new Response();
            response.setStatusCode(400);
            response.setMessage("Maximum 5 images allowed per room");
            return ResponseEntity.status(response.getStatusCode()).body(response);
        }

        Response response = roomService.updateRoom(roomId, roomDescription, roomType, roomPrice, photos);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/delete/{roomId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> deleteRoom(@PathVariable Long roomId) {
        Response response = roomService.deleteRoom(roomId);
        return ResponseEntity.status(response.getStatusCode()).body(response);

    }

    @GetMapping("/image/{imageId}")
    public ResponseEntity<byte[]> getRoomImage(@PathVariable Long imageId) {
        var image = roomImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        
        String contentType = "image/jpeg"; // default
        if (image.getImageName() != null) {
            if (image.getImageName().toLowerCase().endsWith(".png")) {
                contentType = "image/png";
            } else if (image.getImageName().toLowerCase().endsWith(".gif")) {
                contentType = "image/gif";
            }
        }
        
        return ResponseEntity
                .ok()
                .header("Content-Type", contentType)
                .body(image.getPhoto());
    }

}
