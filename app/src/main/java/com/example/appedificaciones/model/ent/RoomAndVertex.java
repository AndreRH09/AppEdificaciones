package com.example.appedificaciones.model.ent;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class RoomAndVertex {
    @Embedded
    public RoomEntity roomEntity;

    @Relation(
            parentColumn = "roomId",
            entityColumn = "roomId"
    )
    public List<VertexEntity> vertexEntityList;

}