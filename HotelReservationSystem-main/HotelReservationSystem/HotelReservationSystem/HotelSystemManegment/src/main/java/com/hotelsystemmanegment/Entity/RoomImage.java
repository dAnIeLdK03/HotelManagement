package com.hotelsystemmanegment.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class RoomImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(columnDefinition = "LONGBLOB")
    private byte[] photo;

    private String imageName;

    public void setImageData(byte[] bytes) {
        this.photo = bytes;
    }
}
