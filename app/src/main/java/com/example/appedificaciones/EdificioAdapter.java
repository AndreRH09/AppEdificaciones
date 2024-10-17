package com.example.appedificaciones;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EdificioAdapter extends RecyclerView.Adapter<EdificioAdapter.ViewHolder> {
    private List<Edificio> listaEdificios;
    private Context context;

    // Constructor
    public EdificioAdapter(List<Edificio> listaEdificios, Context context) {
        this.listaEdificios = listaEdificios;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_edificio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Edificio edificio = listaEdificios.get(position);
        holder.tituloTextView.setText(edificio.getTitulo());
        holder.imagenImageView.setImageResource(edificio.getImagenId());
    }

    @Override
    public int getItemCount() {
        return listaEdificios.size();
    }

    public void setListaEdificios(List<Edificio> nuevosEdificios) {
        this.listaEdificios = nuevosEdificios;
        notifyDataSetChanged();
    }

    // ViewHolder class to hold the views
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tituloTextView;
        ImageView imagenImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tituloTextView = itemView.findViewById(R.id.titulo_edificio);
            imagenImageView = itemView.findViewById(R.id.imagen_edificio);
        }
    }
}
