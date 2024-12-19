package com.example.appedificaciones.fragments;



import com.example.appedificaciones.model.ent.DoorEntity;
import com.example.appedificaciones.model.ent.PictureEntity;
import com.example.appedificaciones.model.ent.RoomAndVertex;

import java.util.List;

public interface GalleryFragmentListener {
    void onResultRoomVertex(List<RoomAndVertex> data);
    void onResultDoors(List<DoorEntity> data);
    void onResultPictures(List<PictureEntity> data);
}
