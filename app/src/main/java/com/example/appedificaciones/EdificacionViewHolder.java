package com.example.appedificaciones;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appedificaciones.model.ent.EdificationEntity;

public class EdificacionViewHolder extends  RecyclerView.ViewHolder {
    private final Context context;
    private TextView titulo, categoria, descripcion;
    private ImageView imagen;

    public EdificacionViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        this.context = context;
        titulo = itemView.findViewById(R.id.titulo);
        categoria = itemView.findViewById(R.id.categoria);
        descripcion = itemView.findViewById(R.id.descripcion);
        imagen = itemView.findViewById(R.id.imagen);
    }

    public void bind(EdificationEntity edificacion) {
        titulo.setText(edificacion.getTitulo());
        categoria.setText(edificacion.getCategoria());
        descripcion.setText(edificacion.getResumen());

        Log.d("VIEWHOLDER", "Intentando cargar imagen: " + edificacion.getImagen());
        Drawable imgDrawable = ImageUtils.getDrawableFromAssets(context, edificacion.getImagen());


        if (imgDrawable != null) {
            imagen.setImageDrawable(imgDrawable);
        }
        else{
            Log.d("VIEWHOLDER", "no hay imagen"+ context);
        }
    }
}
