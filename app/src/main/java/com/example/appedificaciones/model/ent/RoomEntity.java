package com.example.appedificaciones.model.ent;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

// Definición de la entidad RoomEntity
@Entity(
    tableName = "Room",
    foreignKeys = @ForeignKey(
        entity = EdificationEntity.class, // Clase que representa la tabla "Edification"
        parentColumns = "id",            // Columna de la tabla padre
        childColumns = "idEdification",  // Columna de la tabla hija
        onDelete = ForeignKey.CASCADE    // Elimina los registros hijos si se elimina el padre
    )
)
public class RoomEntity {

    @PrimaryKey(autoGenerate = true) // Clave primaria autogenerada
    public int roomId;

    public int idEdification; // Clave foránea que referencia a Edification

    public String titulo;     // Título de la habitación
    public String descripcion; // Descripción de la habitación
    public String imagen;     // Ruta o URL de la imagen
    public String audio;      // Ruta o URL del archivo de audio

    // Constructor vacío (requerido por Room)
    public RoomEntity() {
    }

    // Constructor completo (opcional para instanciar objetos)
    public RoomEntity(String titulo, String descripcion, String imagen, String audio) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.audio = audio;
    }
}


