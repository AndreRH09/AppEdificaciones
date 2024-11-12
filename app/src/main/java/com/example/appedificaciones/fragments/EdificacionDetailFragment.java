package com.example.appedificaciones.fragments;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.example.appedificaciones.ImageUtils;
import android.Manifest;
import com.example.appedificaciones.R;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.example.appedificaciones.Edificacion;
import android.location.Address;
import android.location.Geocoder;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.util.Log;
import android.content.pm.PackageManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import android.location.Location;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class EdificacionDetailFragment extends Fragment implements OnMapReadyCallback {
    private static final String ARG_TITULO = "titulo";
    private static final String ARG_CATEGORIA = "categoria";
    private static final String ARG_RESUMEN = "resumen";
    private static final String ARG_DESCRIPCION = "descripcion";
    private static final String ARG_IMAGEN = "imagen";
    private GoogleMap mMap;
    private LatLng coordenadasEdificacion;
    private FusedLocationProviderClient fusedLocationClient;
    private Button btnVerCroquis; // Declara el botón


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

        // Encuentra el botón y configura el listener
        btnVerCroquis = view.findViewById(R.id.btnVerCroquis);
        btnVerCroquis.setOnClickListener(v -> {
            CroquisFragment nuevoFragment = new CroquisFragment();

            // Obtener el título de la edificación desde los argumentos
            String tituloEdificacion = getArguments().getString(ARG_TITULO);

            // Crear un bundle para pasar el título al fragmento de Croquis
            Bundle args = new Bundle();
            args.putString("tituloEdificacion", tituloEdificacion);
            nuevoFragment.setArguments(args);

            // Reemplazar el fragmento actual con el nuevo fragmento de croquis
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, nuevoFragment)
                    .addToBackStack(null)
                    .commit();
        });


        if (getArguments() != null) {
            titulo.setText(getArguments().getString(ARG_TITULO));
            categoria.setText(getArguments().getString(ARG_CATEGORIA));
            descripcion.setText(getArguments().getString(ARG_DESCRIPCION));
            Drawable drawable = ImageUtils.getDrawableFromAssets(requireContext(), getArguments().getString(ARG_IMAGEN));
            if (drawable != null) {
                imagen.setImageDrawable(drawable);
            }
        }

        // Inicializa el cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Inicializa el mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return view;
    }



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Verifica si el permiso de ubicación está habilitado
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Habilitar la capa de ubicación
            mMap.setMyLocationEnabled(true);

            // Obtener la ubicación actual
            obtenerUbicacionActual();
        } else {
            // Si no tienes permisos, solicita los permisos
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        // Obtener la dirección a partir del título de la edificación
        if (getArguments() != null) {
            String direccionEdificacion = getArguments().getString(ARG_TITULO) + " Arequipa";
            buscarYMostrarDireccion(direccionEdificacion);
        }
    }

    private void obtenerUbicacionActual() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                LatLng ubicacionActual = new LatLng(location.getLatitude(), location.getLongitude());
                                mostrarRuta(ubicacionActual);
                            } else {
                                Log.e("Location", "No se pudo obtener la ubicación actual.");
                            }
                        }
                    });
        } else {
            Log.e("Permission", "Permisos de ubicación no concedidos.");
        }
    }

    private void buscarYMostrarDireccion(String direccion) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> direcciones = geocoder.getFromLocationName(direccion, 1);
            if (direcciones != null && !direcciones.isEmpty()) {
                Address ubicacion = direcciones.get(0);
                coordenadasEdificacion = new LatLng(ubicacion.getLatitude(), ubicacion.getLongitude());

                // Agregar marcador y ajustar la cámara al marcador
                mMap.addMarker(new MarkerOptions().position(coordenadasEdificacion).title(direccion));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordenadasEdificacion, 15));
            } else {
                Log.e("Geocoding", "No se encontraron coordenadas para la dirección: " + direccion);
            }
        } catch (IOException e) {
            Log.e("Geocoding", "Error al obtener la dirección", e);
        }
    }

    private void mostrarRuta(LatLng origen) {
        if (coordenadasEdificacion != null) {
            String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origen.latitude + "," + origen.longitude +
                    "&destination=" + coordenadasEdificacion.latitude + "," + coordenadasEdificacion.longitude +
                    "&key=AIzaSyBFAdlgdqGksJpJi3EuDNdpzjZFBiHjkOQ"; // Reemplaza "TU_API_KEY" con tu clave API de Directions

            // Crear una solicitud Volley
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                // Obtener los pasos de la ruta
                                JSONArray routes = response.getJSONArray("routes");
                                if (routes.length() > 0) {
                                    JSONObject route = routes.getJSONObject(0);
                                    JSONArray legs = route.getJSONArray("legs");
                                    if (legs.length() > 0) {
                                        JSONObject leg = legs.getJSONObject(0);
                                        JSONArray steps = leg.getJSONArray("steps");

                                        // Crear una lista de puntos para la ruta
                                        PolylineOptions polylineOptions = new PolylineOptions().width(5).color(0xFF0000FF);

                                        for (int i = 0; i < steps.length(); i++) {
                                            JSONObject step = steps.getJSONObject(i);
                                            JSONObject endLocation = step.getJSONObject("end_location");
                                            LatLng stepLatLng = new LatLng(endLocation.getDouble("lat"), endLocation.getDouble("lng"));
                                            polylineOptions.add(stepLatLng);
                                        }

                                        // Dibuja la ruta en el mapa
                                        mMap.addPolyline(polylineOptions);
                                    }
                                }
                            } catch (JSONException e) {
                                Log.e("Directions", "Error al procesar la respuesta", e);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Directions", "Error al obtener la ruta", error);
                        }
                    });

            // Añadir la solicitud a la cola de Volley
            Volley.newRequestQueue(requireContext()).add(request);
        } else {
            Log.e("Ruta", "Coordenadas de la edificación no disponibles.");
        }
    }
}