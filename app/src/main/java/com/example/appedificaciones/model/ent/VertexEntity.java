package com.example.appedificaciones.model.ent;

import androidx.room.ForeignKey;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "vertex")
public class VertexEntity {
    @PrimaryKey
    private int vertexId;
    private int roomId;
    private float x;
    private float y;

    public VertexEntity(int vertexId, int roomId, float x, float y) {
        this.vertexId = vertexId;
        this.roomId = roomId;
        this.x = x;
        this.y = y;
    }

    public int getVertexId() {
        return vertexId;
    }

    public void setVertexId(int vertexId) {
        this.vertexId = vertexId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
