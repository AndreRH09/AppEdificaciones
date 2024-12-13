package com.example.appedificaciones.fragments;

import java.util.ArrayList;
import java.util.List;

public class ComentariosEdificacion {
    private String edificacion;
    private List<Comentario> comentarios;

    public ComentariosEdificacion() {
        comentarios = new ArrayList<>();
    }

    // Getters y setters
    public String getEdificacion() {
        return edificacion;
    }

    public void setEdificacion(String edificacion) {
        this.edificacion = edificacion;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }
}
