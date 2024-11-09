package com.example.appedificaciones.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appedificaciones.ImageUtils;
import com.example.appedificaciones.R;

import com.example.appedificaciones.Edificacion;

public class EdificacionDetailFragment extends Fragment {
    private static final String ARG_TITULO = "titulo";
    private static final String ARG_CATEGORIA = "categoria";
    private static final String ARG_RESUMEN = "resumen";
    private static final String ARG_DESCRIPCION = "descripcion";
    private static final String ARG_IMAGEN = "imagen";

    public static EdificacionDetailFragment newInstance(Edificacion edificacion) {
        EdificacionDetailFragment fragment = new EdificacionDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITULO, edificacion.getTitulo());
        args.putString(ARG_CATEGORIA, edificacion.getCategoria());
        args.putString(ARG_DESCRIPCION, edificacion.getDescripcion());
        args.putString(ARG_IMAGEN, edificacion.getImagen());
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edificacion_detail, container, false);

        TextView titulo = view.findViewById(R.id.textTitulo);
        TextView categoria = view.findViewById(R.id.textCategoria);
        TextView descripcion = view.findViewById(R.id.textDescripcion);
        ImageView imagen = view.findViewById(R.id.imageView);

        if (getArguments() != null) {
            titulo.setText(getArguments().getString(ARG_TITULO));
            categoria.setText(getArguments().getString(ARG_CATEGORIA));
            descripcion.setText(getArguments().getString(ARG_DESCRIPCION));
            Drawable drawable = ImageUtils.getDrawableFromAssets(requireContext(), getArguments().getString(ARG_IMAGEN));
            if (drawable != null) {
                imagen.setImageDrawable(drawable);
            }
        }

        return view;
    }
}
