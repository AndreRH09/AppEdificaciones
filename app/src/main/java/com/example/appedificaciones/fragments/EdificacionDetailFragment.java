package com.example.appedificaciones.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appedificaciones.ImageUtils;
import android.Manifest;
import com.example.appedificaciones.R;
import com.example.appedificaciones.SharedViewModel;
import com.example.appedificaciones.model.ent.EdificationEntity;
import com.example.appedificaciones.model.database.EdificationRepository;
import com.example.appedificaciones.model.database.AppDatabase;
import com.google.android.gms.maps.model.PolylineOptions;

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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EdificacionDetailFragment extends Fragment implements OnMapReadyCallback {
    private static final String ARG_ID = "id";
    private static final String ARG_TITULO = "titulo";
    private static final String ARG_CATEGORIA = "categoria";
    private static final String ARG_RESUMEN = "resumen";
    private static final String ARG_DESCRIPCION = "descripcion";
    private static final String ARG_IMAGEN = "imagen";
    private static final String ARG_AUDIO = "audio";

    private GoogleMap mMap;
    private LatLng coordenadasEdificacion;
    private FusedLocationProviderClient fusedLocationClient;
    private Button btnVerCroquis; // Declara el botón
    private View view;
    private EditText editTextComentario;
    private Button btnGuardarComentario;
    private RatingBar ratingBar;
    private ImageView imgAddFavoriteEdification;

    public static EdificacionDetailFragment newInstance(EdificationEntity edificacion) {
        EdificacionDetailFragment fragment = new EdificacionDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, edificacion.getId());
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
        view = inflater.inflate(R.layout.fragment_edificacion_detail, container, false);

        // Inicializa los elementos de la interfaz
        TextView titulo = view.findViewById(R.id.textTitulo);
        TextView categoria = view.findViewById(R.id.textCategoria);
        TextView descripcion = view.findViewById(R.id.textDescripcion);
        ImageView imagen = view.findViewById(R.id.imageView);
        editTextComentario = view.findViewById(R.id.editTextComentario);
        btnGuardarComentario = view.findViewById(R.id.btnGuardarComentario);
        ratingBar = view.findViewById(R.id.ratingBar);
        imgAddFavoriteEdification = view.findViewById(R.id.iconFavorite);

        // Llama a cargarComentarios() para cargar los comentarios al inicio
        String tituloEdificacion = getArguments().getString(ARG_TITULO);
        cargarComentarios(tituloEdificacion);

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getUserLogged().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                int userId = user.getUserId();
                int edificationId = getArguments().getInt(ARG_ID); // Asegúrate de pasar el ID de la edificación al fragmento

                // Verificar estado favorito al cargar
                EdificationRepository repository = new EdificationRepository(AppDatabase.getInstance(requireContext()));
                repository.isEdificationFavorite(userId, edificationId, isFavorite -> {
                    if (isFavorite) {
                        imgAddFavoriteEdification.setColorFilter(Color.RED); // Favorito
                    } else {
                        imgAddFavoriteEdification.setColorFilter(Color.BLACK); // No favorito
                    }
                });

                // Configurar el click listener para alternar estado favorito
                imgAddFavoriteEdification.setOnClickListener(v -> {
                    repository.isEdificationFavorite(userId, edificationId, isFavorite -> {
                        if (isFavorite) {
                            // Eliminar de favoritos
                            repository.removeFavoriteEdification(userId, edificationId);
                            imgAddFavoriteEdification.setColorFilter(Color.BLACK);
                        } else {
                            // Agregar a favoritos
                            repository.addFavoriteEdification(userId, edificationId);
                            imgAddFavoriteEdification.setColorFilter(Color.RED);
                        }
                    });
                });
            }
        });

        btnGuardarComentario.setOnClickListener(v -> {
            String nombreUsuario = "NombreEjemplo"; // Obtén el nombre del usuario actual
            int fotoUsuario = R.drawable.userlogo; // Usa una imagen de ejemplo o elige el icono
            float valoracion = ratingBar.getRating(); // Obtén la valoración seleccionada

            guardarComentario(tituloEdificacion, editTextComentario.getText().toString(), nombreUsuario, fotoUsuario, valoracion);
            cargarComentarios(tituloEdificacion);
            editTextComentario.setText("");
        });

        // Encuentra el botón y configura el listener
        btnVerCroquis = view.findViewById(R.id.btnVerCroquis);
        btnVerCroquis.setOnClickListener(v -> {
            CroquisFragment nuevoFragment = new CroquisFragment();
            Bundle args = new Bundle();
            args.putString("tituloEdificacion", tituloEdificacion);
            nuevoFragment.setArguments(args);
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
            mMap.setMyLocationEnabled(true);
            obtenerUbicacionActual();
        } else {
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
                    "&key=AIzaSyBFAdlgdqGksJpJi3EuDNdpzjZFBiHjkOQ"; // Reemplaza con tu clave API de Directions

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray routes = response.getJSONArray("routes");
                                if (routes.length() > 0) {
                                    JSONObject route = routes.getJSONObject(0);
                                    JSONArray legs = route.getJSONArray("legs");
                                    if (legs.length() > 0) {
                                        JSONObject leg = legs.getJSONObject(0);
                                        JSONArray steps = leg.getJSONArray("steps");

                                        PolylineOptions polylineOptions = new PolylineOptions().width(5).color(0xFF0000FF);
                                        for (int i = 0; i < steps.length(); i++) {
                                            JSONObject step = steps.getJSONObject(i);
                                            JSONObject endLocation = step.getJSONObject("end_location");
                                            LatLng stepLatLng = new LatLng(endLocation.getDouble("lat"), endLocation.getDouble("lng"));
                                            polylineOptions.add(stepLatLng);
                                        }
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
                            Log.e("Directions", "Error en la solicitud", error);
                        }
                    });
            Volley.newRequestQueue(requireContext()).add(request);
        }
    }

    private void guardarComentario(String tituloEdificacion, String comentarioTexto, String nombreUsuario, int fotoUsuario, float valoracion) {
        String nombreArchivo = tituloEdificacion + "_comentarios.json";
        JSONObject comentarioJson = new JSONObject();
        try {
            comentarioJson.put("nombreUsuario", nombreUsuario);
            comentarioJson.put("comentario", comentarioTexto);
            comentarioJson.put("valoracion", valoracion);
            comentarioJson.put("fotoUsuario", fotoUsuario); // Podrías guardar solo un ID o URL

            // Guarda el JSON en el archivo
            try (FileOutputStream fos = requireContext().openFileOutput(nombreArchivo, Context.MODE_APPEND)) {
                fos.write((comentarioJson.toString() + "\n").getBytes());

            }
        } catch (JSONException | IOException e) {
            Log.e("GuardarComentario", "Error al guardar el comentario", e);
        }
    }

    private void cargarComentarios(String tituloEdificacion) {
        RecyclerView recyclerView = view.findViewById(R.id.contenedorComentarios);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        String nombreArchivo = tituloEdificacion + "_comentarios.json";
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader reader = null;

        List<Comentario> listaComentarios = new ArrayList<>();

        try {
            // Intentamos abrir el archivo de comentarios
            fis = requireContext().openFileInput(nombreArchivo);
            isr = new InputStreamReader(fis);
            reader = new BufferedReader(isr);

            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    JSONObject comentarioJson = new JSONObject(line);
                    Comentario comentario = new Comentario(
                            "nothing", // O el valor correcto que quieras pasar para fotoUsuario
                            comentarioJson.getString("nombreUsuario"),
                            (float) comentarioJson.getDouble("valoracion"),
                            comentarioJson.getString("comentario")
                    );

                    listaComentarios.add(comentario);
                } catch (JSONException e) {
                    Log.e("CargarComentarios", "Error al procesar el comentario", e);
                }
            }

        } catch (FileNotFoundException e) {
            Log.i("CargarComentarios", "Archivo de comentarios no existe, se iniciará vacío.");
        } catch (IOException e) {
            Log.e("CargarComentarios", "Error al leer el archivo de comentarios", e);
        } finally {
            try {
                if (reader != null) reader.close();
                if (isr != null) isr.close();
                if (fis != null) fis.close();
            } catch (IOException e) {
                Log.e("CargarComentarios", "Error al cerrar los recursos", e);
            }
        }

        // Configurar el adaptador del RecyclerView
        ComentariosAdapter adapter = new ComentariosAdapter(listaComentarios);
        recyclerView.setAdapter(adapter);
    }
}
