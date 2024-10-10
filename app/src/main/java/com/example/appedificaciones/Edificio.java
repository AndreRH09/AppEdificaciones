package com.example.appedificaciones;

public class Edificio {
    private String titulo;
    private int imagenId; // ID de la imagen en los recursos

    public Edificio(String titulo, int imagenId) {
        this.titulo = titulo;
        this.imagenId = imagenId;
    }

    public String getTitulo() {
        return titulo;
    }

    public int getImagenId() {
        return imagenId;
    }
}