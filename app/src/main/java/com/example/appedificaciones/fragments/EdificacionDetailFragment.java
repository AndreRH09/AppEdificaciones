package com.example.appedificaciones.fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appedificaciones.AudioPlayerService;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EdificacionDetailFragment extends Fragment implements OnMapReadyCallback {
    private FusedLocationProviderClient fusedLocationClient;

    private static final String ARG_ID = "id";
    private static final String ARG_TITULO = "titulo";
    private static final String ARG_CATEGORIA = "categoria";
    private static final String ARG_RESUMEN = "resumen";
    private static final String ARG_DESCRIPCION = "descripcion";
    private static final String ARG_IMAGEN = "imagen";
    private static final String ARG_AUDIO = "audio";
    private static final String ARG_ANIO = "anio";

    private String tituloEdificacion;
    private boolean isPlaying = false;
    private boolean isStoppedApp = false;

    private GoogleMap mMap;
    private LatLng coordenadasEdificacion;
    private Button btnVerCroquis, btnMostrarComentarios; // Botón para mostrar comentarios
    private View view;
    private ImageView imgAddFavoriteEdification;
    private RecyclerView recyclerViewComentarios;
    private List<String> comentarios;

    private ImageButton btnPlay,btnPause, btnStop;
    private SeekBar seekBar;
    private TextView passTimeTextView;
    private TextView dueTimeTextView;

    private static final int REQUEST_NOTIFICATION_PERMISSION = 100; // Define your request code
    private ActivityResultLauncher<String> notificationPermissionLauncher;


    public static EdificacionDetailFragment newInstance(EdificationEntity edificacion) {
        EdificacionDetailFragment fragment = new EdificacionDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, edificacion.getId());
        args.putString(ARG_TITULO, edificacion.getTitulo());
        args.putString(ARG_CATEGORIA, edificacion.getCategoria());
        args.putString(ARG_DESCRIPCION, edificacion.getDescripcion());
        args.putString(ARG_IMAGEN, edificacion.getImagen());
        args.putString(ARG_AUDIO, edificacion.getAudio());
        args.putString(ARG_ANIO, edificacion.getAnio());

        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    // Modificar el método onCreateView para asegurarnos de que los comentarios se recarguen siempre
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edificacion_detail, container, false);

        // Inicializa los elementos de la interfaz
        TextView titulo = view.findViewById(R.id.textTitulo);
        TextView categoria = view.findViewById(R.id.textCategoria);
        TextView descripcion = view.findViewById(R.id.textDescripcion);
        ImageView imagen = view.findViewById(R.id.imageView);
        TextView anio = view.findViewById(R.id.textAnio);
        imgAddFavoriteEdification = view.findViewById(R.id.iconFavorite);


        // Configurar título, descripción, etc.
        if (getArguments() != null) {
            titulo.setText(getArguments().getString(ARG_TITULO));
            categoria.setText(getArguments().getString(ARG_CATEGORIA));
            descripcion.setText(getArguments().getString(ARG_DESCRIPCION));
            anio.setText(getString(R.string.year_display, getArguments().getString(ARG_ANIO)));
            Drawable drawable = ImageUtils.getDrawableFromAssets(requireContext(), getArguments().getString(ARG_IMAGEN));
            if (drawable != null) {
                imagen.setImageDrawable(drawable);
            }
        }

        //Reproductor de audio

        btnPlay = view.findViewById(R.id.btnPlay);
        btnPause = view.findViewById(R.id.btnPause);
        btnStop = view.findViewById(R.id.btnStop);
        seekBar = view.findViewById(R.id.seekBar);
        passTimeTextView = view.findViewById(R.id.passTime);
        dueTimeTextView = view.findViewById(R.id.dueTime);

        btnPlay.setOnClickListener(v -> controlAudio("PLAY"));
        btnPause.setOnClickListener(v -> controlAudio("PAUSE"));
        btnStop.setOnClickListener(v -> {
            // Detener la reproducción de audio
            controlAudio("STOP");
            passTimeTextView.setText("00:00");
            // Establecer el SeekBar a 0
            seekBar.setProgress(0);
        });

        seekBar.setMax(100);  // Asumimos que el máximo es 100
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    // Si el usuario mueve el SeekBar, cambia la posición del audio
                    Intent intent = new Intent(requireContext(), AudioPlayerService.class);
                    intent.putExtra("audio", getArguments().getString(ARG_AUDIO));
                    intent.setAction("SEEK");
                    intent.putExtra("seek_position", progress);
                    requireContext().startService(intent);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Registrar el receiver para escuchar el progreso de la música
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(progressReceiver,
                new android.content.IntentFilter("AUDIO_PROGRESS"));


        // Ver croquis
        tituloEdificacion = getArguments().getString(ARG_TITULO);

        btnVerCroquis = view.findViewById(R.id.btnVerCroquis);
        if ("Basílica de Yanahuara".equals(tituloEdificacion)) {
            // Habilitar el botón y poner el texto original
            btnVerCroquis.setEnabled(true);
            btnVerCroquis.setText("Ver Croquis");
            btnVerCroquis.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0); // Elimina cualquier icono si es necesario
        } else {
            // Deshabilitar el botón y mostrar el mensaje "Trabajando en ello"
            btnVerCroquis.setEnabled(false);
            btnVerCroquis.setText("Trabajando...");

            // Cambiar el icono del botón
            Drawable drawableLogo = getResources().getDrawable(R.drawable.logo_trabajando); // Asegúrate de tener este logo en drawable
            // Redimensionar el drawable para que se ajuste al tamaño del botón
            int width = 50; // Ancho deseado (ajústalo según tus necesidades)
            int height = 50; // Alto deseado (ajústalo según tus necesidades)
            drawableLogo.setBounds(0, 0, width, height); // Redimensiona el icono
            btnVerCroquis.setCompoundDrawablesWithIntrinsicBounds(drawableLogo, null, null, null); // Coloca el logo en el lado izquierdo
        }
        btnVerCroquis.setOnClickListener(v -> {
            GalleryFragment nuevoFragment = new GalleryFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, nuevoFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Mostrar comentarios
        btnMostrarComentarios = view.findViewById(R.id.btnMostrarComentarios); // Inicializa el botón
        btnMostrarComentarios.setOnClickListener(v -> showCommentsDialog()); // Asocia el listener

        // Inicializa el mapa
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        //Inicializar favoritos
        initializeFavorites();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar el ActivityResultLauncher
        notificationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // Permiso concedido
                        Toast.makeText(requireContext(), "Permiso para notificaciones concedido", Toast.LENGTH_SHORT).show();
                    } else {
                        // Permiso denegado
                        Toast.makeText(requireContext(), "Permiso para notificaciones denegado", Toast.LENGTH_SHORT).show();
                    }
                });

        // Verificar si se tiene el permiso de notificaciones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Solicitar permiso
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void controlAudio(String action) {
        if (action.equals("PLAY")){
            isPlaying = true;
        } else if (action.equals("STOP") || action.equals("PAUSE")) {
            isPlaying = false;
        }
        Intent intent = new Intent(requireContext(), AudioPlayerService.class);
        intent.putExtra("audio", getArguments().getString(ARG_AUDIO));
        intent.setAction(action);
        requireContext().startService(intent);
    }

    private void cargarComentarios() {
        // Obtener comentarios desde el archivo o la base de datos
        ComentariosEdificacion comentariosEdificacion = getComentarios();

        // Verificar si el RecyclerView existe en la vista
        recyclerViewComentarios = view.findViewById(R.id.recyclerViewComentarios);
        if (recyclerViewComentarios != null) {
            // Configurar el RecyclerView
            recyclerViewComentarios.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerViewComentarios.setAdapter(new ComentariosAdapter(comentariosEdificacion.getComentarios()));

            // Mostrar mensaje si no hay comentarios
            if (comentariosEdificacion.getComentarios().isEmpty()) {
                Log.d("Comentarios", "No hay comentarios para esta edificación.");
            }
        } else {
            Log.e("Comentarios", "RecyclerView no encontrado en la vista.");
        }
    }


    private ComentariosEdificacion getComentarios() {
        int edificationId = getArguments().getInt(ARG_ID);  // Obtener ID de la edificación
        String nombreArchivo = obtenerNombreArchivoComentarios(edificationId);  // Archivo único para esta edificación

        ComentariosEdificacion comentariosEdificacion = new ComentariosEdificacion();
        comentariosEdificacion.setComentarios(new ArrayList<>()); // Inicializamos la lista de comentarios

        try {
            // Verificar si el archivo existe
            FileInputStream fis = requireContext().openFileInput(nombreArchivo);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr);

            StringBuilder stringBuilder = new StringBuilder();
            String linea;
            while ((linea = reader.readLine()) != null) {
                stringBuilder.append(linea);
            }

            String json = stringBuilder.toString();
            Log.d("ComentariosJSON", "Contenido del archivo: " + json);

            if (json.isEmpty()) {
                json = "{}";
            }

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

            JsonArray comentariosArray = jsonObject.has("comentarios") ? jsonObject.getAsJsonArray("comentarios") : null;
            if (comentariosArray != null) {
                for (JsonElement element : comentariosArray) {
                    Comentario comentario = gson.fromJson(element, Comentario.class);
                    if (comentario.getEdificacionId() == edificationId) {
                        comentariosEdificacion.getComentarios().add(comentario);
                    }
                }
            }

            reader.close();
            fis.close();
        } catch (FileNotFoundException e) {
            Log.d("Comentarios", "Archivo no encontrado. Inicializando archivo.");
            comentariosEdificacion.setEdificacion(tituloEdificacion);
            guardarComentarios(comentariosEdificacion);  // Inicializar archivo si no existe
        } catch (IOException e) {
            e.printStackTrace();
        }

        return comentariosEdificacion;
    }

    private void guardarComentarios(ComentariosEdificacion comentariosEdificacion) {
        int edificationId = getArguments().getInt(ARG_ID);  // Obtener ID de la edificación
        String nombreArchivo = obtenerNombreArchivoComentarios(edificationId);  // Archivo único para esta edificación

        if (comentariosEdificacion.getComentarios() == null) {
            comentariosEdificacion.setComentarios(new ArrayList<>());
        }

        try (FileOutputStream fos = requireContext().openFileOutput(nombreArchivo, Context.MODE_PRIVATE);
             OutputStreamWriter osw = new OutputStreamWriter(fos);
             BufferedWriter writer = new BufferedWriter(osw)) {

            Gson gson = new Gson();
            String json = gson.toJson(comentariosEdificacion);
            writer.write(json);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String obtenerNombreArchivoComentarios(int edificationId) {
        return "comentarios_" + edificationId + ".json";  // Archivo único por edificación
    }

    private void showCommentsDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_comentarios, null);
        recyclerViewComentarios = dialogView.findViewById(R.id.recyclerViewComentarios);
        EditText editTextComentario = dialogView.findViewById(R.id.editTextComentario);
        Button btnGuardarComentario = dialogView.findViewById(R.id.btnGuardarComentario);
        RatingBar ratingBarComentario = dialogView.findViewById(R.id.ratingBarComentario); // Agregar RatingBar

        // Obtener los comentarios desde el archivo
        ComentariosEdificacion comentariosEdificacion = getComentarios();

        recyclerViewComentarios.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewComentarios.setAdapter(new ComentariosAdapter(comentariosEdificacion.getComentarios()));

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Comentarios")
                .setView(dialogView)
                .setCancelable(true);

        // Guardar un nuevo comentario
        btnGuardarComentario.setOnClickListener(v -> {
            String nuevoComentario = editTextComentario.getText().toString();
            float rating = ratingBarComentario.getRating();

            if (!nuevoComentario.isEmpty() && rating > 0) {
                // Obtener el ID de la edificación actual
                int edificationId = getArguments().getInt(ARG_ID);

                // Obtener el nombre del usuario logueado desde el SharedViewModel
                SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
                sharedViewModel.getUserLogged().observe(getViewLifecycleOwner(), user -> {
                    if (user != null) {
                        String nombreUsuario = user.getUser();  // Nombre del usuario logueado

                        // Crear el nuevo comentario con el rating, el nombre del usuario y el ID de la edificación
                        Comentario comentario = new Comentario(nombreUsuario, "2024-12-12", rating, nuevoComentario, edificationId);
                        comentariosEdificacion.getComentarios().add(comentario);  // Agregar el comentario a la lista

                        // Actualizar el adaptador y guardar los comentarios
                        recyclerViewComentarios.getAdapter().notifyDataSetChanged();
                        guardarComentarios(comentariosEdificacion);

                        editTextComentario.setText(""); // Limpiar el campo de texto
                        ratingBarComentario.setRating(0); // Restablecer el RatingBar
                        Toast.makeText(requireContext(), "Comentario guardado", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(requireContext(), "Escribe un comentario y selecciona un rating", Toast.LENGTH_SHORT).show();
            }
        });

        builder.create().show();
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

    private void initializeFavorites() {
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getUserLogged().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                int userId = user.getUserId();
                int edificationId = getArguments().getInt(ARG_ID);

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
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarComentarios();  // Recargar los comentarios cada vez que el fragmento se haga visible

    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d("DETAIL", "onPause: EL FRAGMENT ESTA EN PAUSE");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("DETAIL", "onStop: EL FRAGMENT ESTA EN STOP");
        if(!isStoppedApp){
            Intent intent = new Intent(requireContext(), AudioPlayerService.class);
            intent.putExtra("audio", getArguments().getString(ARG_AUDIO));
            intent.setAction("STOP");
            requireContext().startService(intent);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(progressReceiver);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        requireActivity().getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onResume(@NonNull LifecycleOwner owner) {
                if(isPlaying ){
                    isStoppedApp = false;
                    Intent intent = new Intent(requireContext(), AudioPlayerService.class);
                    intent.putExtra("audio", getArguments().getString(ARG_AUDIO));
                    intent.setAction("STOP_FOREGROUND");
                    requireContext().startService(intent);
                }
            }

            @Override
            public void onStop(@NonNull LifecycleOwner owner) {
                // Inicia el servicio en segundo plano al salir de la app
                if (isPlaying) {
                    isStoppedApp = true;
                    Intent intent = new Intent(requireContext(), AudioPlayerService.class);
                    intent.putExtra("audio", getArguments().getString(ARG_AUDIO));
                    intent.setAction("START_FOREGROUND");
                    requireContext().startService(intent);
                }
            }
        });
    }

    // BroadcastReceiver para recibir el progreso del audio
    private final BroadcastReceiver progressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int progress = intent.getIntExtra("progress", 0);
            String passTime = intent.getStringExtra("passTime");
            String dueTime = intent.getStringExtra("dueTime");

            // Actualizar SeekBar
            seekBar.setProgress(progress);
            // Actualizar los TextViews de tiempo

            passTimeTextView.setText(passTime);  // Tiempo transcurrido
            dueTimeTextView.setText(dueTime);    // Duración total
        }
    };
}
