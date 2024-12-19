package com.example.appedificaciones.controller;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EventViewModel extends ViewModel {
    private final MutableLiveData<Integer> roomSelected = new MutableLiveData<>();
    private final MutableLiveData<Integer> pictureSelected = new MutableLiveData<>();
    private final MutableLiveData<Integer> closeFragment = new MutableLiveData<>();

    public LiveData<Integer> onRoomSelected(){
        return roomSelected;
    }
    public LiveData<Integer> onPictureSelected(){
        return pictureSelected;
    }
    public LiveData<Integer> onCloseFragment(){
        return closeFragment;
    }

    public void setRoomSelected(Integer roomId){
        roomSelected.setValue(roomId);
    }
    public void setPictureSelected(Integer roomId){
        pictureSelected.setValue(roomId);
    }
    public void setCloseFragment(int code){
        closeFragment.setValue(code);
    }
}
