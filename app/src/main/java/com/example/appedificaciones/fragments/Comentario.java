package com.example.appedificaciones.fragments;

public class Comentario {
    private String usuario;
    private String fecha;
    private float rating;
    private String comentario;
    private int edificacionId;  // Nuevo campo para asociar el comentario con la edificación

    // Constructor, getters y setters
    public Comentario(String usuario, String fecha, float rating, String comentario, int edificacionId) {
        this.usuario = usuario;
        this.fecha = fecha;
        this.rating = rating;
        this.comentario = comentario;
        this.edificacionId = edificacionId;
    }

    public int getEdificacionId() {
        return edificacionId;
    }

    public void setEdificacionId(int edificacionId) {
        this.edificacionId = edificacionId;
    }

    // Getters y setters
    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    // Si deseas agregar un getter y setter para el nombre del usuario, puedes hacerlo así:
    public String getNombreUsuario() {
        return usuario; // Este puede ser el nombre del usuario que ya tienes almacenado en 'usuario'
    }

    public void setNombreUsuario(String usuario) {
        this.usuario = usuario;
    }

    public float getValoracion() {
        return rating;
    }
}
