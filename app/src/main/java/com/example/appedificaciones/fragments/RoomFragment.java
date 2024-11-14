package com.example.appedificaciones.fragments;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.graphics.Rect;

import com.example.appedificaciones.R;

import java.util.ArrayList;
import java.util.List;

public class RoomFragment extends Fragment {

    private ImageView roomImageView;
    private List<float[]> roomVertices = new ArrayList<>();
    private List<float[]> doorSegments = new ArrayList<>();
    private String roomName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room, container, false);
        roomImageView = view.findViewById(R.id.roomImageView);

        // Obtener los datos del Bundle
        Bundle args = getArguments();
        if (args != null) {
            roomVertices = (List<float[]>) args.getSerializable("roomVertices");
            roomName = args.getString("roomName", "");
            doorSegments = (List<float[]>) args.getSerializable("doorSegments"); // Obtener las puertas
        }

        // Dibujar la habitación con las puertas
        view.post(this::drawRoom);
        return view;
    }

    private void drawRoom() {
        if (roomVertices.isEmpty()) return;

        // Calcular los límites del bounding box
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE;
        for (float[] vertex : roomVertices) {
            minX = Math.min(minX, vertex[0]);
            minY = Math.min(minY, vertex[1]);
            maxX = Math.max(maxX, vertex[0]);
            maxY = Math.max(maxY, vertex[1]);
        }

        // Calcular el tamaño de la habitación
        float width = maxX - minX;
        float height = maxY - minY;

        // Obtener el tamaño del ImageView
        int imageViewWidth = roomImageView.getWidth();
        int imageViewHeight = roomImageView.getHeight();

        // Calcular la escala para que la habitación se ajuste al ImageView
        float scaleX = imageViewWidth / width;
        float scaleY = imageViewHeight / height;
        float scale = Math.min(scaleX, scaleY);

        // Calcular el desplazamiento para centrar el dibujo
        float offsetX = (imageViewWidth - width * scale) / 2;
        float offsetY = (imageViewHeight - height * scale) / 2;

        // Crear el bitmap y canvas
        Bitmap bitmap = Bitmap.createBitmap(imageViewWidth, imageViewHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        // Fondo
        canvas.drawColor(Color.parseColor("#FFF5E1"));

        // Dibujar los límites del cuarto
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6);

        for (int i = 0; i < roomVertices.size(); i++) {
            float[] start = roomVertices.get(i);
            float[] end = roomVertices.get((i + 1) % roomVertices.size());

            float startX = (start[0] - minX) * scale + offsetX;
            float startY = (start[1] - minY) * scale + offsetY;
            float endX = (end[0] - minX) * scale + offsetX;
            float endY = (end[1] - minY) * scale + offsetY;

            canvas.drawLine(startX, startY, endX, endY, paint);
        }


        // Dibujar el nombre de la habitación
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(60);
        if (roomName != null) {
            Rect textBounds = new Rect();
            paint.getTextBounds(roomName, 0, roomName.length(), textBounds);
            float x = (imageViewWidth - textBounds.width()) / 2f;
            float y = (imageViewHeight + textBounds.height()) / 2f;
            canvas.drawText(roomName, x, y, paint);
        }

        // Dibujar las puertas (en color naranja, por ejemplo)
        paint.setColor(Color.parseColor("#FFA500")); // Naranja
        paint.setStrokeWidth(8);

        for (float[] door : doorSegments) {
            float startX = (door[0] - minX) * scale + offsetX;
            float startY = (door[1] - minY) * scale + offsetY;
            float endX = (door[2] - minX) * scale + offsetX;
            float endY = (door[3] - minY) * scale + offsetY;

            canvas.drawLine(startX, startY, endX, endY, paint);
        }


        // Establecer el bitmap en el ImageView
        roomImageView.setImageBitmap(bitmap);
    }
}
