package com.example.appedificaciones.model.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.example.appedificaciones.model.ent.PictureEntity;

import java.util.List;

@Dao
public interface PictureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<PictureEntity> pictureEntityList);

    @Query("select * from pictures")
    List<PictureEntity> getAll();

    @Query("select * from pictures where roomId=:roomId")
    List<PictureEntity> getPicturesByRoomId(int roomId);

    @Query("select * from pictures where pictureId=:pictureId")
    PictureEntity getPictureById(int pictureId);
}
