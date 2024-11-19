package com.example.appedificaciones.model.ent;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "doors")

public class DoorEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;         // Identificador de la puerta

    private float x;       // Coordenada X del centro de la puerta
    private float y;       // Coordenada Y del centro de la puerta
    private float width;   // Ancho de la puerta
    private float angle;     // Ángulo de la puerta (horizontal o vertical)

    // Constructor
      public DoorEntity(int id, float x, float y, float width, float angle) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.angle = angle;
    }

     // Métodos getter
    public int getId() {
        return id; // Retorna el identificador
    }

    public float getX() {
        return x; // Retorna la coordenada X
    }

    public float getY() {
        return y; // Retorna la coordenada Y
    }

    public float getWidth() {
        return width; // Retorna el ancho de la puerta
    }

    public float getAngle() {
        return angle; // Retorna el ángulo de la puerta
    }

    public void setX(float v) {
          this.x = v;

    }
    public void setY(float v) {
          this.y = v;

    }
    public void setWidth(float v) {
          this.width = v;

    }
}
