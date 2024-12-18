package com.example.appedificaciones.model.database;

import android.app.Application;
import android.util.Log;

import com.example.appedificaciones.model.dao.FavoriteDao;
import com.example.appedificaciones.model.ent.EdificationEntity;
import com.example.appedificaciones.model.ent.FavoriteEdificationEntity;
import com.example.appedificaciones.model.ent.UserEntity;

import java.util.List;
import com.example.appedificaciones.model.ent.DoorEntity;
import com.example.appedificaciones.model.ent.PictureEntity;
import com.example.appedificaciones.model.ent.RoomAndVertex;
import com.example.appedificaciones.model.ent.RoomEntity;
import com.example.appedificaciones.model.ent.VertexEntity;
import java.util.concurrent.Executors;

public class EdificationRepository {

    private final AppDatabase appDatabase;
    private final FavoriteDao favoriteEdificationDao;

    public EdificationRepository(AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
        favoriteEdificationDao = appDatabase.favoriteDao();
    }


    //Fin metodos bd para canvas
    public List<PictureEntity> getPictures() {
        return appDatabase.pictureDao().getAll();
    }

    public List<VertexEntity> getVertexes() {
        return appDatabase.vertexDao().getAll();
    }

    public List<RoomEntity> getRoomById(int roomId) {
        return appDatabase.roomVertexDao().getByRoomId(roomId);
    }

    public List<RoomAndVertex> getRoomWithVertexes() {
        return appDatabase.roomVertexDao().getRoomWithVertex();
    }

    public RoomAndVertex getRoomWithVertexByRoomId(int roomId) {
        return appDatabase.roomVertexDao().getRoomWithVertexByRoomId(roomId);
    }

    public List<DoorEntity> getDoors() {
        return appDatabase.doorDao().getAll();
    }

    public List<PictureEntity> getPicturesByRoomId(int roomId) {
        return appDatabase.pictureDao().getPicturesByRoomId(roomId);
    }

    public PictureEntity getPictureById(int pictureId) {
        Log.d("TAG", "GalleryRepository pictureId:" + pictureId);
        return appDatabase.pictureDao().getPictureById(pictureId);
    }

    public void addPictures(List<PictureEntity> pictureEntityList) {
        appDatabase.pictureDao().insert(pictureEntityList);
    }

    public void addDoors(List<DoorEntity> doorEntityList) {
        appDatabase.doorDao().insert(doorEntityList);
    }

    public void addRooms(List<RoomEntity> roomEntityList) {
        appDatabase.roomVertexDao().insert(roomEntityList);
    }

    public void addVertexes(List<VertexEntity> vertexEntityList) {
        appDatabase.vertexDao().insert(vertexEntityList);
    }


    //Fin metodos bd para canvas



    // Verificar si una edificación ya está en favoritos
    public void isEdificationFavorite(int userId, int edificationId, OnFavoriteCheckCallback callback) {
        new Thread(() -> {
            // Consultar si ya existe la relación de la edificación favorita
            FavoriteEdificationEntity favorite = favoriteEdificationDao.getFavoriteEdificationByUserAndEdification(userId, edificationId);
            if (favorite != null) {
                callback.onResult(true); // Ya está en favoritos
            } else {
                callback.onResult(false); // No está en favoritos
            }
        }).start();
    }

    // Agregar una edificación a los favoritos
    public void addFavoriteEdification(int userId, int edificationId) {
        new Thread(() -> {
            FavoriteEdificationEntity favorite = new FavoriteEdificationEntity(userId, edificationId);
            favoriteEdificationDao.insert(favorite);
        }).start();
    }

    // Eliminar una edificación de los favoritos
    public void removeFavoriteEdification(int userId, int edificationId) {
        new Thread(() -> {
            FavoriteEdificationEntity favorite = new FavoriteEdificationEntity(userId, edificationId);
            favoriteEdificationDao.delete(favorite);
        }).start();
    }

    // Callback para verificar si una edificación es favorita
    public interface OnFavoriteCheckCallback {
        void onResult(boolean isFavorite);
    }

    public void addUser (UserEntity user){
        appDatabase.userDao().insert(user);
    }

    public UserEntity getUserByUsernameAndPassword(String username, String password) {
        return  appDatabase.userDao().getUserByUsernameAndPassword(username,password);
    }

    public List<EdificationEntity> getFavoriteEdificationsByUser(int userId) {
        return appDatabase.favoriteDao().getFavoriteEdificationsByUser(userId);
    }

    public List<EdificationEntity> getAllEdifications() {
        return appDatabase.edificationDao().getAllEdifications();
    }

    public void addEdifications (List<EdificationEntity> edifications) {
        appDatabase.edificationDao().insert(edifications);
    }

    public void updateUser (UserEntity user){
        appDatabase.userDao().update(user);
    }


}
