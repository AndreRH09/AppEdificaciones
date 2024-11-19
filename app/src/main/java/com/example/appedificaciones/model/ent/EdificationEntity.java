package com.example.appedificaciones.model.ent;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Entity(tableName = "edification")
public class EdificationEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String titulo;
    public String categoria;
    public String descripcion;
    public String resumen;
    public String imagen;
    public String audio;

    // Constructor vacío necesario para Room
    public EdificationEntity() {
    }

    // Constructor privado para el Builder
    private EdificationEntity(Builder builder) {
        this.titulo = builder.titulo;
        this.categoria = builder.categoria;
        this.descripcion = builder.descripcion;
        this.resumen = builder.resumen;
        this.imagen = builder.imagen;
        this.audio = builder.audio;
    }

    // Métodos getter y setter
    public String getTitulo() {
        return titulo;
    }

    public int getId() {
        return id;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getResumen() {
        return resumen;
    }

    public String getImagen() {
        return imagen;
    }

    public String getAudio() {
        return audio;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    // Método utilitario
    public static ArrayList<String> getCategoriesInList(List<EdificationEntity> edificaciones) {
        HashSet<String> categoriesSet = new HashSet<>();
        for (EdificationEntity edificacion : edificaciones) {
            categoriesSet.add(edificacion.getCategoria());
        }
        return new ArrayList<>(categoriesSet);
    }

    // Clase Builder interna
    public static class Builder {
        private String titulo;
        private String categoria;
        private String descripcion;
        private String resumen;
        private String imagen;
        private String audio;

        public Builder setTitulo(String titulo) {
            this.titulo = titulo;
            return this;
        }

        public Builder setCategoria(String categoria) {
            this.categoria = categoria;
            return this;
        }

        public Builder setDescripcion(String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        public Builder setResumen(String resumen) {
            this.resumen = resumen;
            return this;
        }

        public Builder setImagen(String imagen) {
            this.imagen = imagen;
            return this;
        }

        public Builder setAudio(String audio) {
            this.audio = audio;
            return this;
        }

        public EdificationEntity build() {
            return new EdificationEntity(this);
        }
    }
}
