package com.example.appedificaciones.canvas;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.appedificaciones.controller.EventViewModel;
import com.example.appedificaciones.model.ent.DoorEntity;
import com.example.appedificaciones.model.ent.PictureEntity;
import com.example.appedificaciones.model.ent.RoomAndVertex;
import com.example.appedificaciones.model.ent.RoomEntity;
import com.example.appedificaciones.model.ent.VertexEntity;

import java.util.HashMap;
import java.util.List;

public class GalleryView extends View {

    private List<RoomAndVertex> roomAndVertexList;
    private List<DoorEntity> doorEntityList;
    private List<PictureEntity> pictureEntityList;
    private HashMap<RoomEntity, Region> regions;
    private HashMap<String, Float> parameters;
    private final Paint wallPaint;
    private final Paint doorPaint;
    private final Paint picturePaint;
    private final Paint pictureIconTextPaint;
    private float pictureRadius;
    //private float scaleFactor = 1f;
    private float WIDTH;
    private float HEIGHT;
    private final float HORIZONTAL_DOOR = 0f;
    private final float VERTICAL_DOOR = 90f;
    private EventViewModel eventViewModel;


    public GalleryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        String layout_height = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_height");
        String layout_width = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_width");

        layout_height = layout_height.replace("dip", "");
        layout_width = layout_width.replace("dip", "");

        float density = getResources().getDisplayMetrics().density;
        HEIGHT = density * Float.parseFloat(layout_height);
        WIDTH = density * Float.parseFloat(layout_width);

        wallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wallPaint.setColor(Color.parseColor("#2e1700"));
        wallPaint.setStrokeWidth(10f);
        wallPaint.setStyle(Paint.Style.STROKE);

        doorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        doorPaint.setColor(Color.parseColor("#ff9800"));
        doorPaint.setStrokeWidth(10f);
        doorPaint.setStyle(Paint.Style.STROKE);

        picturePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        picturePaint.setColor(Color.parseColor("#ff9800"));
        picturePaint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.SOLID));

        pictureIconTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pictureIconTextPaint.setColor(Color.BLACK);
        pictureIconTextPaint.setTextSize(32f);
        pictureIconTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float pointX = event.getX();
        float pointY = event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            Log.d("TAG", "Clicked");

            for (RoomEntity key : regions.keySet()) {
                boolean clicked = regions.get(key).contains((int) pointX, (int) pointY);
                if (clicked) {
                    Log.d("TAG", key.label);
                    eventViewModel.setRoomSelected(key.roomId);
                    // También notificar la acción de cambiar al fragmento de imágenes
                    eventViewModel.setPictureSelected(key.roomId); // Suponiendo que el ID de la sala es relevante para las imágenes
                }
            }

            return true;
        }

        return false;


    }

    protected void onDraw(@NonNull Canvas canvas) {
        drawRooms(canvas);
        drawDoors(canvas);
        //drawPictures(canvas);
    }

    private void drawPictures(Canvas canvas) {
        if (pictureEntityList == null) return;
        for (PictureEntity picture : pictureEntityList) {
            canvas.drawCircle(picture.x, picture.y, pictureRadius, picturePaint);
            canvas.drawText(String.valueOf(picture.pictureId), picture.x, picture.y, pictureIconTextPaint);
        }
    }

    private void drawDoors(Canvas canvas) {
        if (doorEntityList == null) return;

        for (DoorEntity doorEntity : doorEntityList) {
            float x0 = 0;
            float x1 = 0;
            float y0 = 0;
            float y1 = 0;
            if (doorEntity.getAngle() == HORIZONTAL_DOOR) {
                x0 = doorEntity.getX() - doorEntity.getWidth() / 2;
                x1 = doorEntity.getX() + doorEntity.getWidth() / 2;
                y0 = doorEntity.getY();
                y1 = doorEntity.getY();
            } else if (doorEntity.getAngle() == VERTICAL_DOOR) {
                x0 = doorEntity.getX();
                x1 = doorEntity.getX();
                y0 = doorEntity.getY() - doorEntity.getWidth() / 2;
                y1 = doorEntity.getY() + doorEntity.getWidth() / 2;
            }
            canvas.drawLine(x0, y0, x1, y1, doorPaint);
        }
    }

    private void drawRooms(Canvas canvas) {
        if (roomAndVertexList == null) return;

        regions = new HashMap<>();

        for (RoomAndVertex data : roomAndVertexList) {
            Path path = new Path();
            List<VertexEntity> vertexEntityList = data.vertexEntityList;
            path.moveTo(vertexEntityList.get(0).getX(), vertexEntityList.get(0).getY());
            for (int i = 1; i < vertexEntityList.size(); i++) {
                path.lineTo(vertexEntityList.get(i).getX(), vertexEntityList.get(i).getY());
            }
            path.close();

            canvas.drawPath(path, wallPaint);

            Region region = new Region();
            region.setPath(path, new Region(0, 0, (int) WIDTH, (int) HEIGHT));
            drawRoomTitle(canvas, data.roomEntity.label, region);
            regions.put(data.roomEntity, region);
        }

    }

    private void drawRoomTitle(Canvas canvas, String title, Region region) {
        float x = region.getBounds().centerX();
        float y = region.getBounds().centerY();
        canvas.drawText(title, x, y, pictureIconTextPaint);

    }

    private HashMap<String, Float> calculateFactor() {
        if (roomAndVertexList == null) return null;

        int leftX = (int) WIDTH;
        int topY = (int) HEIGHT;
        int rightX = 0;
        int bottomY = 0;

        for (RoomAndVertex roomAndVertex : roomAndVertexList) {
            for (VertexEntity vertex : roomAndVertex.vertexEntityList) {
                int x = (int) vertex.getX();
                int y = (int) vertex.getY();

                if (x < leftX)
                    leftX = x;

                if (x > rightX)
                    rightX = x;

                if (y > bottomY)
                    bottomY = y;

                if (y < topY)
                    topY = y;

            }
        }

        float _factorX = WIDTH / (rightX - leftX);
        float _factorY = HEIGHT / (bottomY - topY);
        float scaleFactor = (0.86f) * Math.min(_factorX, _factorY);

        Log.d("TAG", "scaleFactor:" + scaleFactor);
        Log.d("TAG", "leftX:" + leftX + ", topY:" + topY + ", rightX:" + rightX + ", bottomY:" + bottomY);

        float room_width = scaleFactor * (rightX - leftX);
        float room_height = scaleFactor * (bottomY - topY);

//        offsetY = (int) (Math.abs(getHeight() - room_height) / 2);
        float marginLeft = (int) (Math.abs(WIDTH - room_width) / 2);
        float offsetX = leftX * scaleFactor;
        float offsetY = topY * scaleFactor;

        //return new float[]{scaleFactor, offsetX, offsetY, marginLeft};

        HashMap<String, Float> params = new HashMap<>();
        params.put("SCALE", scaleFactor);
        params.put("OFFSET_X", offsetX);
        params.put("OFFSET_Y", offsetY);
        params.put("MARGIN_LEFT", marginLeft);
        params.put("MARGIN_TOP", 80f);

        return params;
    }

    public void setRooms(List<RoomAndVertex> roomsVertexes) {
        this.roomAndVertexList = roomsVertexes;
        parameters = calculateFactor();
        if (parameters != null) {
            float scale = (float) parameters.get("SCALE");
            float offsetX = (float) parameters.get("OFFSET_X");
            float offsetY = (float) parameters.get("OFFSET_Y");
            float marginLeft = (float) parameters.get("MARGIN_LEFT");
            float marginTop = (float) parameters.get("MARGIN_TOP");
            roomsVertexes.forEach(obj -> {
                obj.vertexEntityList.forEach(vertex -> {
                    vertex.setX((scale * vertex.getX()) - offsetX + marginLeft);
                    vertex.setY((scale * vertex.getY()) - offsetY + marginTop);
                });
            });
        }

        invalidate();

    }

    public void setDoors(List<DoorEntity> doorEntityList) {
        this.doorEntityList = doorEntityList;
        if (parameters != null) {
            float scale = (float) parameters.get("SCALE");
            float offsetX = (float) parameters.get("OFFSET_X");
            float offsetY = (float) parameters.get("OFFSET_Y");
            float marginLeft = (float) parameters.get("MARGIN_LEFT");
            float marginTop = (float) parameters.get("MARGIN_TOP");

            this.doorEntityList.forEach(obj -> {
                obj.setX((scale * obj.getX()) - offsetX + marginLeft);
                obj.setY((scale * obj.getY()) - offsetY + marginTop);
                obj.setWidth(scale * obj.getWidth());
            });

            invalidate();
        }
    }

    public void setPictures(List<PictureEntity> pictureEntityList) {
        this.pictureEntityList = pictureEntityList;
        if (parameters != null) {
            float scale = (float) parameters.get("SCALE");
            float offsetX = (float) parameters.get("OFFSET_X");
            float offsetY = (float) parameters.get("OFFSET_Y");
            float marginLeft = (float) parameters.get("MARGIN_LEFT");
            float marginTop = (float) parameters.get("MARGIN_TOP");

            this.pictureEntityList.forEach(obj -> {
                obj.x = (scale * obj.x) - offsetX + marginLeft;
                obj.y = (scale * obj.y) - offsetY + marginTop;
            });
            invalidate();
        }
    }

    public void setEventViewModel(EventViewModel eventViewModel) {
        this.eventViewModel = eventViewModel;
    }

    public void setRadiusPicture(float pictureRadius) {
        this.pictureRadius = pictureRadius;
    }

}
