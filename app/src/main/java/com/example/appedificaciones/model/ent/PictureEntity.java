package com.example.appedificaciones.model.ent;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pictures")
public class PictureEntity {
    @PrimaryKey
    public int pictureId;
    public String title;
    public String author;
    public String description;
    public String link;
    public int roomId;
    public float x;
    public float y;

}
