package com.example.appedificaciones.model.ent;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "favorite_edification", primaryKeys = {"idUser", "idEdification"})
public class FavoriteEdificationEntity {

    @ColumnInfo(name = "idUser")
    private int idUser;

    @ColumnInfo(name = "idEdification")
    private int idEdification;

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public void setIdEdification(int idEdification) {
        this.idEdification = idEdification;
    }

    public int getIdUser() {
        return idUser;
    }

    public int getIdEdification() {
        return idEdification;
    }

    // Constructor vacío para Room
    public FavoriteEdificationEntity() {}

    // Constructor para establecer los valores
    public FavoriteEdificationEntity(int idUser, int idEdification) {
        this.idUser = idUser;
        this.idEdification = idEdification;
    }
}
