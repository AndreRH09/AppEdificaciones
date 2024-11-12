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

    public CroquisFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_croquis, container, false);
        image = view.findViewById(R.id.imageCanvas);


        // Obtener el título de la edificación del argumento
        String tituloEdificacion = getArguments() != null ? getArguments().getString("tituloEdificacion") : "";

        // Cargar datos de habitaciones desde assets
        loadRoomData(tituloEdificacion);


        // Draw on Canvas after the layout is ready
        view.post(new Runnable() {
            @Override
            public void run() {
                drawMap();
            }
        });

        // Set a touch listener to detect clicks
        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    float touchX = event.getX();
                    float touchY = event.getY();

                    // Check if the touch coordinates are within any room
                    checkRoomClick(touchX, touchY);

                    return true;
                }
                return false;
            }
        });

        return view;
    }

    // Check if the touch event is within the boundaries of any room
    private void checkRoomClick(float touchX, float touchY) {
        for (Map.Entry<Integer, List<float[]>> entry : roomVertices.entrySet()) {
            int roomId = entry.getKey();
            List<float[]> vertices = entry.getValue();

            // Simple polygon point-in-polygon test (ray-casting algorithm)
            if (isPointInPolygon(touchX, touchY, vertices)) {
                String roomName = roomNames.get(roomId);
                if (roomName != null) {
                    // Log the name of the room that was clicked
                    Log.d("CroquisFragment", "Room clicked: " + roomName);
                }
                return;
            }
        }
    }

    // Check if the point (touchX, touchY) is inside the polygon of the room
    private boolean isPointInPolygon(float touchX, float touchY, List<float[]> vertices) {
        int n = vertices.size();
        boolean inside = false;

        // Loop through the polygon vertices to apply ray-casting algorithm
        for (int i = 0, j = n - 1; i < n; j = i++) {
            float[] vertex1 = vertices.get(i);
            float[] vertex2 = vertices.get(j);
            float x1 = vertex1[0] * 100; // Scale coordinates
            float y1 = vertex1[1] * 100; // Scale coordinates
            float x2 = vertex2[0] * 100; // Scale coordinates
            float y2 = vertex2[1] * 100; // Scale coordinates

            if (((y1 > touchY) != (y2 > touchY)) && (touchX < (x2 - x1) * (touchY - y1) / (y2 - y1) + x1)) {
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
        Bitmap bitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        // Background color
        canvas.drawColor(Color.parseColor("#FFF5E1")); // Cream background

        // Draw each room
        for (Map.Entry<Integer, List<float[]>> entry : roomVertices.entrySet()) {
            int roomId = entry.getKey();
            List<float[]> vertices = entry.getValue();

            // Set color for each room (use different shades)
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(6);
            paint.setColor(Color.BLACK);

            // Draw room boundaries
            for (int i = 0; i < vertices.size(); i++) {
                float[] start = vertices.get(i);
                float[] end = vertices.get((i + 1) % vertices.size());
                canvas.drawLine(start[0] * 100, start[1] * 100, end[0] * 100, end[1] * 100, paint); // Scale coordinates
            }

            // Draw room name
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(30);
            String roomName = roomNames.get(roomId);
            if (roomName != null && !vertices.isEmpty()) {
                float[] labelPos = vertices.get(0);
                canvas.drawText(roomName, labelPos[0] * 100, labelPos[1] * 100 - 20, paint);
            }
        }

        // Set the Bitmap on the ImageView
        image.setImageBitmap(bitmap);
    }
}
