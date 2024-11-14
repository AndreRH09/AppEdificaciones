package com.example.appedificaciones.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appedificaciones.R;

import java.util.List;

public class ComentariosAdapter extends RecyclerView.Adapter<ComentariosAdapter.ComentarioViewHolder> {

    private List<Comentario> comentarios;

    public ComentariosAdapter(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    @NonNull
    @Override
    public ComentarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comentario, parent, false);
        return new ComentarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComentarioViewHolder holder, int position) {
        Comentario comentario = comentarios.get(position);

        holder.imgUsuario.setVisibility(View.GONE);  // Ocultar el ImageView si no necesitas la foto

        holder.textNombreUsuario.setText(comentario.getNombreUsuario());
        holder.ratingValoracion.setRating(comentario.getValoracion());
        holder.textComentario.setText(comentario.getComentario());
    }

    @Override
    public int getItemCount() {
        return comentarios.size();
    }

    public static class ComentarioViewHolder extends RecyclerView.ViewHolder {
        ImageView imgUsuario;
        TextView textNombreUsuario;
        RatingBar ratingValoracion;
        TextView textComentario;

        public ComentarioViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUsuario = itemView.findViewById(R.id.imgUsuario);
            textNombreUsuario = itemView.findViewById(R.id.textNombreUsuario);
            ratingValoracion = itemView.findViewById(R.id.ratingValoracion);
            textComentario = itemView.findViewById(R.id.textComentario);
        }
    }
}
