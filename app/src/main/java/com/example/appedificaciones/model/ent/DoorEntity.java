package com.example.appedificaciones.model.ent;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "doors")

public class DoorEntity {
    @PrimaryKey
    private int doorId;
    private float x;
    private float y;
    private float width;
    private float angle;

    public DoorEntity(int doorId,float x, float y, float width, float angle) {
        this.doorId = doorId;
        this.x = x;
        this.y = y;
        this.width = width;
        this.angle = angle;
    }

     // Métodos getter
     public int getDoorId() {
         return doorId;
     }

    public void setDoorId(int doorId) {
        this.doorId = doorId;
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

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }
}
