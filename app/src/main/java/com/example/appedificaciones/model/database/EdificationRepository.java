package com.example.appedificaciones.model.database;

import android.app.Application;

import com.example.appedificaciones.model.dao.FavoriteDao;
import com.example.appedificaciones.model.ent.EdificationEntity;
import com.example.appedificaciones.model.ent.FavoriteEdificationEntity;
import com.example.appedificaciones.model.ent.UserEntity;

import java.util.List;
import java.util.concurrent.Executors;

public class EdificationRepository {

    private final AppDatabase appDatabase;
    private final FavoriteDao favoriteEdificationDao;

    public EdificationRepository(AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
        favoriteEdificationDao = appDatabase.favoriteDao();
    }

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
