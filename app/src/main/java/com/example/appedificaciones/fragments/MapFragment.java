package com.example.appedificaciones.fragments;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.appedificaciones.R;
import com.example.appedificaciones.model.database.AppDatabase;
import com.example.appedificaciones.model.database.EdificationRepository;
import com.example.appedificaciones.model.ent.EdificationEntity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private final HashMap<Marker, EdificationEntity> markerMap = new HashMap<>();
    private EdificationRepository repository;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        repository = new EdificationRepository(AppDatabase.getInstance(requireContext()));

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return rootView;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.zoomTo(10)); // Configura el zoom inicial
        cargarMarcadoresDesdeBaseDeDatos();

        // Configura el listener para clics en marcadores
        mMap.setOnMarkerClickListener(marker -> {
            EdificationEntity edificacion = markerMap.get(marker);
            if (edificacion != null) {
                // Crear instancia de EdificacionDetailFragment
                EdificacionDetailFragment detailFragment = EdificacionDetailFragment.newInstance(edificacion);

                // Reemplazar el fragment actual con EdificacionDetailFragment
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, detailFragment)
                        .addToBackStack(null)
                        .commit();
            }
            return true;
        });
    }

    private void cargarMarcadoresDesdeBaseDeDatos() {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        Executors.newSingleThreadExecutor().execute(() -> {
            List<EdificationEntity> edificaciones = repository.getAllEdifications();

            if (edificaciones.isEmpty()) {
                Log.e("BaseDeDatos", "No hay edificaciones en la base de datos.");
                return;
            }

            for (EdificationEntity edificacion : edificaciones) {
                try {
                    List<Address> direcciones = geocoder.getFromLocationName(edificacion.getTitulo() + " Arequipa", 1);
                    if (direcciones != null && !direcciones.isEmpty()) {
                        Address direccion = direcciones.get(0);
                        LatLng ubicacion = new LatLng(direccion.getLatitude(), direccion.getLongitude());

                        // Cambiar al hilo principal para interactuar con el mapa
                        requireActivity().runOnUiThread(() -> {
                            // Crear y agregar marcador al mapa
                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(ubicacion)
                                    .title(edificacion.getTitulo())
                                    .snippet(edificacion.getResumen()));

                            // Asociar la edificación al marcador
                            markerMap.put(marker, edificacion);

                            boundsBuilder.include(ubicacion);
                        });
                    } else {
                        Log.e("Geocodificación", "No se encontraron coordenadas para: " + edificacion.getTitulo());
                    }
                } catch (IOException e) {
                    Log.e("Geocodificación", "Error al geocodificar " + edificacion.getTitulo(), e);
                }
            }

            // Ajustar la cámara para mostrar todos los marcadores en el hilo principal
            requireActivity().runOnUiThread(() -> {
                if (!markerMap.isEmpty()) {
                    LatLngBounds limites = boundsBuilder.build();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(limites, 100));
                }
            });
        });
    }
}