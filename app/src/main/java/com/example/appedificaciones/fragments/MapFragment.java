package com.example.appedificaciones.fragments;

import android.content.res.AssetManager;
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
import com.example.appedificaciones.Edificacion;
import com.example.appedificaciones.model.ent.EdificationEntity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private HashMap<Marker, EdificationEntity> markerMap = new HashMap<>();

    public MapFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return rootView;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.zoomTo(10));  // Configura el zoom inicial
        leerYAgregarMarcadores();

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

    private void leerYAgregarMarcadores() {
        AssetManager assetManager = getContext().getAssets();
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        try (InputStream is = assetManager.open("edificaciones.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            StringBuilder jsonBuilder = new StringBuilder();
            String linea;
            while ((linea = reader.readLine()) != null) {
                jsonBuilder.append(linea);
            }

            JSONObject jsonObject = new JSONObject(jsonBuilder.toString());
            JSONArray edificaciones = jsonObject.getJSONArray("edificaciones");

            for (int i = 0; i < edificaciones.length(); i++) {
                JSONObject edificacionJson = edificaciones.getJSONObject(i);
                String titulo = edificacionJson.getString("titulo");

                try {
                    List<Address> direcciones = geocoder.getFromLocationName(titulo + " Arequipa", 1);
                    if (direcciones != null && !direcciones.isEmpty()) {
                        Address direccion = direcciones.get(0);
                        LatLng ubicacion = new LatLng(direccion.getLatitude(), direccion.getLongitude());

                        // Crear y agregar marcador al mapa
                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(ubicacion)
                                .title(titulo)
                                .snippet(edificacionJson.getString("resumen")));

                        // Crear objeto Edificacion y asociarlo al marcador
                        EdificationEntity edificacion = new EdificationEntity.Builder()
                                .setTitulo(edificacionJson.getString("titulo"))
                                .setCategoria(edificacionJson.getString("categoria"))
                                .setDescripcion(edificacionJson.getString("descripcion"))
                                .setResumen(edificacionJson.getString("resumen"))
                                .setImagen(edificacionJson.getString("imagen"))
                                .setAudio(edificacionJson.getString("audio"))
                                .build();

                        markerMap.put(marker, edificacion);

                        boundsBuilder.include(ubicacion);
                    } else {
                        Log.e("Geocodificación", "No se encontraron coordenadas para: " + titulo);
                    }
                } catch (IOException e) {
                    Log.e("Geocodificación", "Error al geocodificar " + titulo, e);
                }
            }

            // Ajustar la cámara para mostrar todos los marcadores
            LatLngBounds limites = boundsBuilder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(limites, 100));

        } catch (IOException | JSONException e) {
            Log.e("Archivo", "Error al leer o parsear el archivo de edificaciones.", e);
        }
    }
}
