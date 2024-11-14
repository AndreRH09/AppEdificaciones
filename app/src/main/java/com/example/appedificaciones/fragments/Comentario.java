package com.example.appedificaciones.fragments;

public class Comentario {
    private String nombreUsuario;
    private String fotoUsuario;  // ID de la foto o URL (puedes ajustar el tipo si usas URL)
    private float valoracion;
    private String texto;

    // Constructor
    public Comentario(String nombreUsuario, String fotoUsuario, float valoracion, String texto) {
        this.nombreUsuario = nombreUsuario;
        this.fotoUsuario = fotoUsuario;
        this.valoracion = valoracion;
        this.texto = texto;
    }

    // Getters y Setters
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getFotoUsuario() {
        return fotoUsuario;
    }

    public void setFotoUsuario(String fotoUsuario) {
        this.fotoUsuario = fotoUsuario;
    }

    public float getValoracion() {
        return valoracion;
    }

    public void setValoracion(float valoracion) {
        this.valoracion = valoracion;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getComentario() {
        return texto;
    }
}
