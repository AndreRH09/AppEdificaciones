package com.example.appedificaciones.model.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.appedificaciones.model.ent.EdificationEntity;
import com.example.appedificaciones.model.ent.FavoriteEdificationEntity;

import java.util.List;
@Dao
public interface FavoriteDao {
    // Insertar un favorito
    @Insert
    void insert(FavoriteEdificationEntity favorite);

    // Eliminar un favorito por la combinación de idUser y idEdification
    @Query("DELETE FROM favorite_edification WHERE idUser = :userId AND idEdification = :edificationId")
    void deleteFavoriteByUserAndEdification(int userId, int edificationId);

    @Query("SELECT * FROM favorite_edification WHERE idUser = :userId AND idEdification = :edificationId")
    FavoriteEdificationEntity getFavoriteEdificationByUserAndEdification(int userId, int edificationId);

    @Query("SELECT e.* FROM favorite_edification f INNER JOIN edification e ON f.idEdification = e.id WHERE f.idUser = :userId")
    List<EdificationEntity> getFavoriteEdificationsByUser(int userId);

    // Eliminar un favorito
    @Delete
    void delete(FavoriteEdificationEntity favorite);

    // Obtener todos los favoritos de un usuario específico
    @Query("SELECT * FROM favorite_edification WHERE idUser = :userId")
    List<FavoriteEdificationEntity> getFavoritesByUserId(int userId);


    // Verificar si existe un favorito (combinación de idUser y idEdification)
    @Query("SELECT COUNT(*) FROM favorite_edification WHERE idUser = :userId AND idEdification = :edificationId")
    int isFavoriteExist(int userId, int edificationId);


}
