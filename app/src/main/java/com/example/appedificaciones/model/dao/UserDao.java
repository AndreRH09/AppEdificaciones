package com.example.appedificaciones.model.dao;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.appedificaciones.model.ent.RoomEntity;
import com.example.appedificaciones.model.ent.UserEntity;

import java.util.List;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert (List<UserEntity> userEntityList);
    //void insert (UserEntity... userEntities); //lista de usuarios

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert (UserEntity user);

    @Query("select * from users")
    List<UserEntity> getAll();

    @Query("select * from users where userId=:userId")
    List<UserEntity> getByUserId(int userId);

    @Query("SELECT * FROM users WHERE user=:username AND password=:password")
    UserEntity getUserByUsernameAndPassword(String username, String password);

    @Query("delete from users where userId=:userId")
    void deleteByUserId(int userId);

    // Método para actualizar un usuario existente
    @Update
    void update(UserEntity user);

}
