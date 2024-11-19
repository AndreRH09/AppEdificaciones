package com.example.appedificaciones;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appedificaciones.model.ent.UserEntity;

/*
* Se usa para compartir datos entre HomeActivity y los fragments
* Tambien se puede usar para compartir datos entre fragments
* */
public class SharedViewModel extends ViewModel {
    private final MutableLiveData<UserEntity> userLogged = new MutableLiveData<>();

    public void setUserLogged(UserEntity user) {
        Log.d("SharedViewModel", "Setting user: " + (user != null ? user.getUser() : "null"));
        userLogged.setValue(user);
    }

    public LiveData<UserEntity> getUserLogged() {
        return userLogged;
    }
}