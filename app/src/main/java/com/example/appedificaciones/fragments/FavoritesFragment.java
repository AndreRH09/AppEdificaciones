package com.example.appedificaciones.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.appedificaciones.EdificacionAdapter;
import com.example.appedificaciones.R;
import com.example.appedificaciones.SharedViewModel;
import com.example.appedificaciones.model.database.AppDatabase;
import com.example.appedificaciones.model.database.EdificationRepository;
import com.example.appedificaciones.model.ent.EdificationEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;


public class FavoritesFragment extends Fragment {
    private RecyclerView recyclerView;
    private EdificacionAdapter adapter;
    private List<EdificationEntity> favoriteEdifications;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Observar el usuario logueado
        sharedViewModel.getUserLogged().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                cargarEdificacionesFavoritas(user.getUserId());
            } else {
                Log.d("FAVORITES", "No hay usuario logueado.");
            }
        });
    }

    private void cargarEdificacionesFavoritas(int userId) {
        EdificationRepository repository = new EdificationRepository(AppDatabase.getInstance(requireContext()));

        Executors.newSingleThreadExecutor().execute(() -> {
            favoriteEdifications = repository.getFavoriteEdificationsByUser(userId);

            if (favoriteEdifications == null || favoriteEdifications.isEmpty()) {
                Log.d("FAVORITES", "No se encontraron edificaciones favoritas para el usuario.");
                favoriteEdifications = new ArrayList<>(); // Lista vacía
            }

            // Actualizar la vista en el hilo principal
            new Handler(Looper.getMainLooper()).post(() -> {
                adapter = new EdificacionAdapter(favoriteEdifications);
                recyclerView.setAdapter(adapter);

                // Configura el click listener para navegar al DetailFragment
                adapter.setOnItemClickListener(edificacion -> {
                    EdificacionDetailFragment detailFragment = EdificacionDetailFragment.newInstance(edificacion);
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainerView, detailFragment)
                            .addToBackStack(null)
                            .commit();
                });
            });
        });
    }
}