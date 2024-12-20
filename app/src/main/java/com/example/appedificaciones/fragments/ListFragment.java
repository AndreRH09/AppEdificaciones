package com.example.appedificaciones.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.text.Editable;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appedificaciones.Edificacion;
import com.example.appedificaciones.EdificacionAdapter;
import com.example.appedificaciones.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import com.example.appedificaciones.model.database.AppDatabase;
import com.example.appedificaciones.model.database.EdificationRepository;
import com.example.appedificaciones.model.database.FileRepository;
import com.example.appedificaciones.model.ent.DoorEntity;
import com.example.appedificaciones.model.ent.EdificationEntity;
import com.example.appedificaciones.model.ent.PictureEntity;
import com.example.appedificaciones.model.ent.RoomAndVertex;
import com.example.appedificaciones.model.ent.RoomEntity;
import com.example.appedificaciones.model.ent.VertexEntity;


public class ListFragment extends Fragment {
    private RecyclerView recyclerView;
    private EdificacionAdapter adapter;
    private EditText searchInput;
    private Spinner spinnerCategory;
    private List<EdificationEntity> edificaciones;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        searchInput = view.findViewById(R.id.searchInput);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));

        // Cargar edificaciones desde la base de datos o archivo de texto
        cargarEdificaciones();
        configurarSearchInput();
        return view;
    }

    private void cargarEdificaciones() {
        EdificationRepository repository = new EdificationRepository(AppDatabase.getInstance(requireContext()));

        Executors.newSingleThreadExecutor().execute(() -> {
            edificaciones = repository.getAllEdifications();
            if (edificaciones.isEmpty()) {
                // Cargar desde archivo si la base de datos está vacía
                FileRepository fileRepository = new FileRepository(requireContext());
                edificaciones = fileRepository.getEdificacionesFromTextFile();

                // Cargar desde archivo si la base de datos está vacía
                fileRepository = new FileRepository(requireContext());
                edificaciones = fileRepository.getEdificacionesFromTextFile();

                String[] vertexFiles = new String[]{"RoomVertex001.txt", "RoomVertex002.txt","RoomVertex003.txt","RoomVertex004.txt","RoomVertex005.txt","RoomVertex006.txt","RoomVertex007.txt"};

                List<VertexEntity> vertexEntityList = fileRepository.getVertexes(vertexFiles);
                Executors.newSingleThreadExecutor().execute(() -> {
                            repository.addVertexes(vertexEntityList);
                        }
                );

                String doorsFilename = "Doors.txt";
                List<DoorEntity> doorEntityList = fileRepository.getDoors(doorsFilename);
                Executors.newSingleThreadExecutor().execute(() -> {
                            repository.addDoors(doorEntityList);
                        }
                );

                String picturesFilename = "Pictures.txt";
                List<PictureEntity> pictureEntityList = fileRepository.getPictures(picturesFilename);
                Executors.newSingleThreadExecutor().execute(() -> {
                            repository.addPictures(pictureEntityList);
                        }
                );

                String roomFilename = "Rooms.txt";
                List<RoomEntity> roomEntityList = fileRepository.getRooms(roomFilename);
                Executors.newSingleThreadExecutor().execute(() -> {
                            repository.addRooms(roomEntityList);
                        }
                );


                Executors.newSingleThreadExecutor().execute(() -> {
                            List<RoomAndVertex> roomAndVertexList = repository.getRoomWithVertexes();
                            roomAndVertexList.forEach(obj -> {
                                Log.d("Room:", obj.roomEntity.label);
                                obj.vertexEntityList.forEach(t -> {
                                    Log.d("Vertex", t.getRoomId() + "," + t.getX() + "," + t.getY());
                                });
                            });

                            pictureEntityList.forEach(obj -> {

                                Log.d("Vertex", obj.roomId + "," + obj.pictureId + "," + obj.title);

                            });
                        }
                );


                repository.addEdifications(edificaciones);

            }
            // Actualizar la vista en el hilo principal
            new Handler(Looper.getMainLooper()).post(() -> {
                adapter = new EdificacionAdapter(edificaciones);
                recyclerView.setAdapter(adapter);

                // Configura el click listener para navegar al DetailFragment
                adapter.setOnItemClickListener(edificacion -> {
                    EdificacionDetailFragment detailFragment = EdificacionDetailFragment.newInstance(edificacion);
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainerView, detailFragment)
                            .addToBackStack(null)
                            .commit();
                });

                //cofigurar spinner de categorias
                configurarSpinnerCategorias();
            });
        });
    }

    private void configurarSpinnerCategorias() {
        EdificationRepository repository = new EdificationRepository(AppDatabase.getInstance(requireContext()));

        Executors.newSingleThreadExecutor().execute(() -> {
            // Obtener las categorías únicas desde el repositorio
            List<String> categorias = repository.getAllUniqueCategories();
            categorias.add(0, "Todas");

            // Actualizar la vista en el hilo principal
            new Handler(Looper.getMainLooper()).post(() -> {
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categorias);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategory.setAdapter(spinnerAdapter);

                // Configurar el listener del spinner
                spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        aplicarFiltro();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
            });
        });
    }

    private void configurarSearchInput() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                aplicarFiltro();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void aplicarFiltro() {
        String textoBusqueda = searchInput.getText().toString();
        String categoriaSeleccionada = spinnerCategory.getSelectedItem().toString();
        adapter.filtrar(textoBusqueda, categoriaSeleccionada);
    }

}