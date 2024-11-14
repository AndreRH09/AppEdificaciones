package com.example.appedificaciones.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.appedificaciones.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RoomFragment extends Fragment {

    private TextView titleTextView;
    private TextView descriptionTextView;
    private ImageView roomImageView;

    public RoomFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        View view = inflater.inflate(R.layout.fragment_room, container, false);

        // Inicializar las vistas
        titleTextView = view.findViewById(R.id.roomNameTextView);
        descriptionTextView = view.findViewById(R.id.roomDescriptionTextView);
        roomImageView = view.findViewById(R.id.roomImageView);

        // Obtener los datos pasados desde el Fragmento anterior
        if (getArguments() != null) {
            String roomName = getArguments().getString("roomName");
            String edificioFolder = getArguments().getString("edificioFolder"); // Carpeta de la edificación

            // Establecer el título de la habitación en el TextView
            titleTextView.setText(roomName);

            // Cargar los datos de RoomsData.txt
            loadRoomData(edificioFolder, roomName);
        }

        return view;
    }

    // Método para cargar la imagen desde los assets
    private void loadRoomImage(String imageName, String edificioFolder) {
        Context context = getContext();
        if (context != null && imageName != null) {
            try {
                // Construir la ruta completa de la imagen
                String imagePath = edificioFolder + "/" + imageName;
                InputStream inputStream = context.getAssets().open(imagePath);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                roomImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("RoomFragment", "Error al cargar la imagen: " + imageName);
            }
        }
    }

    // Método para cargar los datos de RoomsData.txt sin usar JSON
    private void loadRoomData(final String edificioFolder, final String roomName) {
        // Ejecutar en un hilo de fondo para evitar el bloqueo del hilo principal
        new Thread(new Runnable() {
            @Override
            public void run() {
                Context context = getContext();
                if (context != null) {
                    String path = edificioFolder + "/RoomsData.txt"; // Ruta completa del archivo
                    Log.d("RoomFragment", "Cargando datos desde: " + path);

                    try {
                        // Abrir el archivo en los assets con codificación UTF-8
                        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(path), "UTF-8"));
                        String line;

                        // Leer cada línea del archivo
                        int contador = 0;
                        while ((line = reader.readLine()) != null) {
                            Log.d("RoomFragment", "Leyendo línea: " + line);
                            // Separar los datos usando una coma como delimitador
                            String[] roomData = line.split(",");


                            Log.d("RoomFragment", "Este es el Room {0}: " + roomData[0].equals(roomName));
                            Log.d("RoomFragment", "roomData[0]: " + roomData[0]);
                            Log.d("RoomFragment", "roomName: " + roomName);


                            // Comprobar si la línea contiene la habitación que estamos buscando
                            if (roomData[0].equals(roomName)) {
                                String roomTitle = roomData[1].trim();  // 'tituloRoom' es el nombre a mostrar
                                String description = roomData[2].trim();
                                String imageName = roomData[3].trim();


                                // Actualizar la UI en el hilo principal
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Log para asegurarnos que estamos actualizando la UI
                                        Log.d("RoomFragment", "Actualizando UI: " + roomTitle);
                                        titleTextView.setText(roomTitle);  // Mostrar el 'tituloRoom'
                                        descriptionTextView.setText(description);
                                        loadRoomImage(imageName, edificioFolder);  // Actualizar la imagen
                                    }
                                });

                                break; // Salir del bucle después de encontrar la habitación
                            }
                        }
                        reader.close(); // Cerrar el BufferedReader
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
