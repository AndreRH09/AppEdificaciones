package com.example.appedificaciones;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.example.appedificaciones.model.ent.EdificationEntity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Edificacion {
    private String titulo;
    private String categoria;
    private String resumen;
    private String descripcion;
    private String imagen;
    private Context context;


    public Edificacion(Context context, String titulo, String categoria, String resumen,String descripcion, String imagen) {
        this.context = context;
        this.titulo = titulo;
        this.categoria = categoria;
        this.resumen = resumen;
        this.descripcion = descripcion;
        this.imagen = imagen;
    }

     // Getters
    public String getTitulo() { return titulo; }
    public String getCategoria() { return categoria; }
    public String getResumen() { return resumen; }
    public String getDescripcion(){return descripcion;}
    public String getImagen(){return imagen;}

    public Drawable getImagenDrawable() {
        Drawable drawable = null;
        try {

            InputStream imageInputStream = context.getAssets().open("images/"+this.imagen);

            // Cargar la imagen como Drawable
            drawable = Drawable.createFromStream(imageInputStream, null);

        } catch (IOException e) {
            Log.e("Error", "No se pudo cargar la imagen: " + e.getMessage());
        }
        return drawable;
    }

    public static ArrayList<String> getCategoriesInList(List<Edificacion> edificaciones){
        HashSet<String> categoriesSet = new HashSet<>();
        for (Edificacion edificacion : edificaciones) {
            categoriesSet.add(edificacion.getCategoria());
        }
        return new ArrayList<>(categoriesSet);
    }

}
