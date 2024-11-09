package com.example.appedificaciones;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EdificacionViewHolder extends  RecyclerView.ViewHolder {
    private TextView titulo, categoria, descripcion;
    private ImageView imagen;

    public EdificacionViewHolder(@NonNull View itemView) {
        super(itemView);
        titulo = itemView.findViewById(R.id.titulo);
        categoria = itemView.findViewById(R.id.categoria);
        descripcion = itemView.findViewById(R.id.descripcion);
        imagen = itemView.findViewById(R.id.imagen);
    }

    public void bind(Edificacion edificacion) {
        titulo.setText(edificacion.getTitulo());
        categoria.setText(edificacion.getCategoria());
        descripcion.setText(edificacion.getResumen());
        Drawable imgDrawable = edificacion.getImagenDrawable();
        if (imgDrawable != null) {
            imagen.setImageDrawable(imgDrawable);
        }

    }
}
