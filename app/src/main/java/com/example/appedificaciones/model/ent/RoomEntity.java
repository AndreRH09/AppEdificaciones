package com.example.appedificaciones.model.ent;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

// Definición de la entidad RoomEntity
@Entity(tableName = "rooms")
public class RoomEntity {
    @PrimaryKey
    public int roomId;
    public String label;

    public RoomEntity(int roomId,String label){
        this.roomId=roomId;
        this.label=label;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}



