package com.example.appedificaciones.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appedificaciones.R;
import com.example.appedificaciones.EdificioAdapter;
import com.example.appedificaciones.Edificio;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {

    private List<Edificio> listaEdificios;
    private EdificioAdapter edificioAdapter;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inicializa la lista de edificios
        listaEdificios = new ArrayList<>();
        cargarEdificios();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        SearchView searchView = view.findViewById(R.id.search_view);

        // Configura el RecyclerView
        edificioAdapter = new EdificioAdapter(listaEdificios, getContext());
        recyclerView.setAdapter(edificioAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Implementa la búsqueda
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        return view;
    }

    private void cargarEdificios() {
        // Agrega tus edificaciones aquí
        listaEdificios.add(new Edificio("Edificio 1", R.drawable.edificio1));
        listaEdificios.add(new Edificio("Edificio 2", R.drawable.edificio2));
        listaEdificios.add(new Edificio("Edificio 3", R.drawable.edificio3));
        listaEdificios.add(new Edificio("Edificio 4", R.drawable.edificio4));
        listaEdificios.add(new Edificio("Edificio 5", R.drawable.edificio5));
        listaEdificios.add(new Edificio("Edificio 6", R.drawable.edificio6));
        listaEdificios.add(new Edificio("Edificio 7", R.drawable.edificio7));
        listaEdificios.add(new Edificio("Edificio 8", R.drawable.edificio8));
        listaEdificios.add(new Edificio("Edificio 9", R.drawable.edificio9));
        listaEdificios.add(new Edificio("Edificio 10", R.drawable.edificio10));
    }

    private void filter(String text) {
        List<Edificio> filteredList = new ArrayList<>();
        for (Edificio item : listaEdificios) {
            if (item.getTitulo().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        edificioAdapter.setListaEdificios(filteredList); // Actualiza la lista usando el método
    }
}