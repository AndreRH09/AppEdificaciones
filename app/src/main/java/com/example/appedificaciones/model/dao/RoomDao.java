package com.example.appedificaciones.model.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.appedificaciones.model.ent.RoomAndVertex;
import com.example.appedificaciones.model.ent.RoomEntity;

import java.util.List;

@Dao
public interface RoomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<RoomEntity> roomEntityList);

    @Query("select * from rooms")
    List<RoomEntity> getAll();

    @Query("select * from rooms where roomId=:roomId")
    List<RoomEntity> getByRoomId(int roomId);

    @Query("select * from rooms ")
    List<RoomAndVertex> getRoomWithVertex();

    @Query("select * from rooms where roomId=:roomId")
    RoomAndVertex getRoomWithVertexByRoomId(int roomId);

//    @Query("select * from rooms inner join vertex on rooms.roomId=vertex.roomId")
//    Map<RoomEntity, List<VertexEntity>> getRoomWithVertex();
}
