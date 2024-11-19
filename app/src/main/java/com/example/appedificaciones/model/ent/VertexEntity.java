package com.example.appedificaciones.model.ent;

import androidx.room.ForeignKey;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "vertex",
        foreignKeys = @ForeignKey(entity = RoomEntity.class,
                                  parentColumns = "roomId", // Nombre de la columna en la tabla padre
                                  childColumns = "roomId",  // Nombre de la columna en la tabla hijo
                                  onDelete = ForeignKey.CASCADE)) // Comportamiento en caso de eliminación
public class VertexEntity {
    @PrimaryKey(autoGenerate = true)
    private int id; // ID del vértice

    private float x; // Coordenada X
    private float y; // Coordenada Y
    private int roomId;

    // Constructor
    public VertexEntity(int id, int roomId, float x, float y) {
        this.id = id;
        this.roomId = roomId;
        this.x = x;
        this.y = y;
    }

    // Métodos getter
    public int getId() {
        return id;
    }
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float v) {
        this.x = v;
    }

    public void setY(float v) {
        this.y = v;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }
}
