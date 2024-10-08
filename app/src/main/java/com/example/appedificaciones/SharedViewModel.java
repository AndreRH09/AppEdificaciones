package com.example.appedificaciones;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/*
* Se usa para compartir datos entre HomeActivity y los fragments
* Tambien se puede usar para compartir datos entre fragments
* */
public class SharedViewModel extends ViewModel {
    private final MutableLiveData<AccountEntity> userLogged = new MutableLiveData<>();

    public void setUserLogged(AccountEntity user) {
        userLogged.setValue(user);
    }

    public LiveData<AccountEntity> getUserLogged() {
        return userLogged;
    }
}