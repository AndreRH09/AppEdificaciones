package com.example.appedificaciones.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.appedificaciones.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CroquisFragment extends Fragment {

    private ImageView image;
    private Map<Integer, List<float[]>> roomVertices = new HashMap<>();
    private Map<Integer, String> roomNames = new HashMap<>();
    private List<float[]> doorSegments = new ArrayList<>();
    private float maxX = 0;
    private float maxY = 0;

    public CroquisFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_croquis, container, false);
        image = view.findViewById(R.id.roomImageView);

        // Obtener el título de la edificación del argumento
        String tituloEdificacion = getArguments() != null ? getArguments().getString("tituloEdificacion") : "";

        // Cargar datos de habitaciones desde assets
        loadRoomData(tituloEdificacion);

        // Usamos onGlobalLayout para redibujar la imagen cuando las dimensiones del ImageView cambien (por ejemplo, en la rotación)
        image.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            // Después de que el layout se haya completado, redibujamos el mapa
            drawMap();
        });

        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    int paddingStart = image.getPaddingStart();
                    int paddingTop = image.getPaddingTop();

                    float touchX = event.getX() - paddingStart;
                    float touchY = event.getY() - paddingTop;

                    // Log para verificar las coordenadas táctiles ajustadas
                    Log.d("CroquisFragment", "Touch coordinates adjusted: X=" + touchX + " Y=" + touchY);

                    // Check if the touch coordinates are within any room
                    checkRoomClick(touchX, touchY);

                    return true;
                }
                return false;
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Limpiar los datos antiguos al volver a este fragmento
        clearData();

        // Obtener el título de la edificación del argumento
        String tituloEdificacion = getArguments() != null ? getArguments().getString("tituloEdificacion") : "";

        // Cargar datos de habitaciones desde assets
        loadRoomData(tituloEdificacion);

        // Redibujar el mapa
        getView().post(this::drawMap);
    }

    private void clearData() {
        roomVertices.clear();
        roomNames.clear();
    }

    // Check if the touch event is within the boundaries of any room
    // Método para manejar el toque en el mapa
    private void checkRoomClick(float touchX, float touchY) {
        for (Map.Entry<Integer, List<float[]>> entry : roomVertices.entrySet()) {
            int roomId = entry.getKey();
            List<float[]> vertices = entry.getValue();

            // Verificar si el toque está dentro del polígono de la habitación
            if (isPointInPolygon(touchX, touchY, vertices)) {
                String roomName = roomNames.get(roomId);

                if (roomName != null) {
                    // Obtener el título de la edificación y pasar la carpeta asociada
                    String tituloEdificacion = getArguments() != null ? getArguments().getString("tituloEdificacion") : "";

                    // Reemplazar los espacios en el nombre de la carpeta
                    String edificioFolder = tituloEdificacion.replace(" ", "");  // Eliminar los espacios

                    // Crear un Bundle para pasar los datos de la habitación
                    Bundle args = new Bundle();
                    args.putSerializable("roomVertices", new ArrayList<>(vertices)); // Pasar los vértices
                    args.putString("roomName", roomName); // Pasar el nombre de la habitación
                    args.putSerializable("doorSegments", new ArrayList<>(doorSegments)); // Pasar las puertas
                    args.putString("edificioFolder", edificioFolder); // Pasar la carpeta de la edificación sin espacios

                    // Crear una instancia de RoomFragment y pasar los datos
                    RoomFragment roomFragment = new RoomFragment();
                    roomFragment.setArguments(args); // Asignar los datos al fragmento

                    // Navegar al fragmento de la habitación
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainerView, roomFragment) // Reemplazar el fragmento
                            .addToBackStack(null) // Agregar a la pila de retroceso
                            .commit();
                }
                return;
            }
        }
    }

    // Check if the point (touchX, touchY) is inside the polygon of the room
    private boolean isPointInPolygon(float touchX, float touchY, List<float[]> vertices) {
        int n = vertices.size();
        boolean inside = false;

        // Ajustar las coordenadas del toque según maxX y maxY para coincidir con el dibujo
        float scaledTouchX = (touchX / image.getWidth()) * maxX;
        float scaledTouchY = (touchY / image.getHeight()) * maxY;

        // Aplicar el algoritmo de ray-casting en las coordenadas escaladas
        for (int i = 0, j = n - 1; i < n; j = i++) {
            float[] vertex1 = vertices.get(i);
            float[] vertex2 = vertices.get(j);
            float x1 = vertex1[0];
            float y1 = vertex1[1];
            float x2 = vertex2[0];
            float y2 = vertex2[1];

            if (((y1 > scaledTouchY) != (y2 > scaledTouchY)) &&
                    (scaledTouchX < (x2 - x1) * (scaledTouchY - y1) / (y2 - y1) + x1)) {
                inside = !inside;
            }
        }
        return inside;
    }

    private void loadRoomData(String tituloEdificacion) {
        Context context = getContext();
        if (context == null) return;

        // Ruta de la carpeta basada en el título de la edificación, eliminando espacios en blanco
        String carpetaEdificacion = tituloEdificacion.replace(" ", "");

        // Intenta cargar el archivo "Rooms.txt" desde la carpeta de la edificación
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(carpetaEdificacion + "/Rooms.txt")));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");
                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                roomNames.put(id, name);
            }
            reader.close();
        } catch (IOException e) {
            Log.e("CroquisFragment", "Error al cargar Rooms.txt para la edificación: " + carpetaEdificacion, e);
        }

        // Cargar dinámicamente todos los archivos RoomVertex dentro de la carpeta
        try {
            String[] files = context.getAssets().list(carpetaEdificacion);
            if (files != null) {
                for (String fileName : files) {
                    if (fileName.startsWith("RoomVertex") && fileName.endsWith(".txt")) {
                        loadVerticesFile(context, carpetaEdificacion + "/" + fileName);
                    }
                }
            }
        } catch (IOException e) {
            Log.e("CroquisFragment", "Error al listar archivos en la carpeta: " + carpetaEdificacion, e);
        }

        // Cargar archivos de puertas (door00X.txt)
        try {
            String[] files = context.getAssets().list(carpetaEdificacion);
            if (files != null) {
                for (String fileName : files) {
                    if (fileName.startsWith("Doors00") && fileName.endsWith(".txt")) {
                        loadDoorFile(context, carpetaEdificacion + "/" + fileName);
                    }
                }
            }
        } catch (IOException e) {
            Log.e("CroquisFragment", "Error al listar archivos de puertas: " + carpetaEdificacion, e);
        }
    }

    private void loadDoorFile(Context context, String rutaArchivo) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(rutaArchivo)));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                float x1 = Float.parseFloat(parts[0]);
                float y1 = Float.parseFloat(parts[1]);
                float x2 = Float.parseFloat(parts[2]);
                float y2 = Float.parseFloat(parts[3]);

                // Almacenar los segmentos de la puerta
                doorSegments.add(new float[]{x1, y1, x2, y2});
            }
            reader.close();
        } catch (IOException e) {
            Log.e("CroquisFragment", "Error al cargar archivo de puertas: " + rutaArchivo, e);
        }
    }


    private void loadVerticesFile(Context context, String rutaArchivo) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(rutaArchivo)));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int vertexId = Integer.parseInt(parts[0]);
                int roomId = Integer.parseInt(parts[1]);
                float x = Float.parseFloat(parts[2]);
                float y = Float.parseFloat(parts[3]);

                if (!roomVertices.containsKey(roomId)) {
                    roomVertices.put(roomId, new ArrayList<>());
                }
                roomVertices.get(roomId).add(new float[]{x, y});
            }
            reader.close();
        } catch (IOException e) {
            Log.e("CroquisFragment", "Error al cargar archivo de vértices: " + rutaArchivo, e);
        }
    }

    private void drawMap() {
        // Obtener las dimensiones del ImageView
        int imageViewWidth = image.getWidth();
        int imageViewHeight = image.getHeight();

        // Crear un Bitmap con las dimensiones del ImageView
        Bitmap bitmap = Bitmap.createBitmap(imageViewWidth, imageViewHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        // Color de fondo
        canvas.drawColor(Color.parseColor("#FFF5E1")); // Fondo color crema

        // Encuentra el valor máximo de X y Y para escalar los vértices
        maxX = 0;
        maxY = 0;
        for (Map.Entry<Integer, List<float[]>> entry : roomVertices.entrySet()) {
            List<float[]> vertices = entry.getValue();
            for (float[] vertex : vertices) {
                maxX = Math.max(maxX, vertex[0]);
                maxY = Math.max(maxY, vertex[1]);
            }
        }

        // Dibujar las habitaciones
        for (Map.Entry<Integer, List<float[]>> entry : roomVertices.entrySet()) {
            int roomId = entry.getKey();
            List<float[]> vertices = entry.getValue();

            // Configurar color y estilo de las habitaciones
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(6);
            paint.setColor(Color.BLACK);

            // Dibujar el contorno de la habitación
            for (int i = 0; i < vertices.size(); i++) {
                float[] start = vertices.get(i);
                float[] end = vertices.get((i + 1) % vertices.size());

                float startX = (start[0] / maxX) * imageViewWidth;
                float startY = (start[1] / maxY) * imageViewHeight;
                float endX = (end[0] / maxX) * imageViewWidth;
                float endY = (end[1] / maxY) * imageViewHeight;

                canvas.drawLine(startX, startY, endX, endY, paint);
            }

            // Dibujar el nombre de la habitación
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(30);
            String roomName = roomNames.get(roomId);
            if (roomName != null && !vertices.isEmpty()) {
                float[] labelPos = vertices.get(0);
                float labelX = (labelPos[0] / maxX) * imageViewWidth;
                float labelY = (labelPos[1] / maxY) * imageViewHeight;

                canvas.drawText(roomName, labelX, labelY - 20, paint);
            }
        }

        // Dibujar las puertas en color naranja
        paint.setColor(Color.parseColor("#FFA500")); // Naranja
        paint.setStrokeWidth(8);
        for (float[] door : doorSegments) {
            float startX = (door[0] / maxX) * imageViewWidth;
            float startY = (door[1] / maxY) * imageViewHeight;
            float endX = (door[2] / maxX) * imageViewWidth;
            float endY = (door[3] / maxY) * imageViewHeight;

            canvas.drawLine(startX, startY, endX, endY, paint);
        }

        // Establecer el Bitmap en el ImageView
        image.setImageBitmap(bitmap);
    }
}
