package com.example.appedificaciones.model.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.appedificaciones.model.ent.EdificationEntity;
import com.example.appedificaciones.model.ent.FavoriteEdificationEntity;

import java.util.List;

@Dao
public interface EdificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<EdificationEntity> edificationEntityList);

    // Método para obtener todas las edificaciones
    @Query("SELECT * FROM edification")
    List<EdificationEntity> getAllEdifications();

    // Método para obtener una edificación por su ID
    @Query("SELECT * FROM edification WHERE id = :id")
    EdificationEntity getEdificationById(int id);

    @Query("SELECT * FROM edification " +
            "JOIN favorite_edification ON edification.id = favorite_edification.idEdification " +
            "WHERE favorite_edification.idUser = :userId")
    List<EdificationEntity> getFavoriteEdificationsByUser(int userId);

    // Método para insertar una nueva edificación
    @Insert
    void insertEdification(EdificationEntity edification);

    // Método para actualizar una edificación
    @Update
    void updateEdification(EdificationEntity edification);

    // Método para eliminar una edificación
    @Delete
    void deleteEdification(EdificationEntity edification);

    // Método para contar cuántas edificaciones existen en la base de datos
    @Query("SELECT COUNT(*) FROM edification")
    int countEdifications();
}

