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

import androidx.annotation.Nullable;

import com.example.appedificaciones.controller.EventViewModel;
import com.example.appedificaciones.model.ent.DoorEntity;
import com.example.appedificaciones.model.ent.PictureEntity;
import com.example.appedificaciones.model.ent.RoomAndVertex;
import com.example.appedificaciones.model.ent.RoomEntity;
import com.example.appedificaciones.model.ent.VertexEntity;

import java.util.HashMap;
import java.util.List;

public class RoomView extends View {
    private final String TAG = "RoomView";
    private float WIDTH;
    private float HEIGHT;
    private final float HORIZONTAL_DOOR = 0f;
    private final float VERTICAL_DOOR = 90f;
    private EventViewModel eventViewModel;
    private RoomAndVertex roomAndVertex;
    private List<PictureEntity> pictureEntityList;
    private List<DoorEntity> doorEntityList;
    private HashMap<PictureEntity, Region> pictureRegionList;
    private HashMap<String, Float> parameters;
    private Context context;
    private Region region;
    private final Paint doorPaint;
    private final Paint picturePaint;
    private final Paint pictureIconTextPaint;

    public RoomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        String layout_height = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_height");
        String layout_width = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_width");

        layout_height = layout_height.replace("dip", "");
        layout_width = layout_width.replace("dip", "");

        float density = getResources().getDisplayMetrics().density;
        HEIGHT = density * Float.parseFloat(layout_height);
        WIDTH = density * Float.parseFloat(layout_width);

        Log.d(TAG, "Dimension:" + WIDTH + "x" + HEIGHT);

        region = new Region();

        pictureRegionList = new HashMap<>();

        picturePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        picturePaint.setColor(Color.parseColor("#ff9800"));
        //picturePaint.setStrokeWidth(10f);
        //picturePaint.setStyle(Paint.Style.STROKE);
        picturePaint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.SOLID));

        doorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        doorPaint.setColor(Color.parseColor("#ff9800"));
        doorPaint.setStrokeWidth(10f);
        doorPaint.setStyle(Paint.Style.STROKE);

        pictureIconTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pictureIconTextPaint.setColor(Color.BLACK);
        pictureIconTextPaint.setTextSize(32f);
        pictureIconTextPaint.setFakeBoldText(true);
        pictureIconTextPaint.setTextAlign(Paint.Align.CENTER);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float pointX = event.getX();
        float pointY = event.getY();

        Log.d(TAG, "Touch:(" + pointX + "," + pointY + ")");

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            for (PictureEntity picture : pictureRegionList.keySet()) {
                boolean clicked = pictureRegionList.get(picture).getBounds().contains((int) pointX, (int) pointY);
                if (clicked) {
                    Log.d(TAG, "Picture clicked:" + picture.pictureId);
                    eventViewModel.setPictureSelected(picture.pictureId);
                }
            }
        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (parameters != null) {
            drawPicture(canvas);
            drawRoom(canvas);
            drawDoors(canvas);
        }

    }


    private void drawRoom(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(10f);
        paint.setStyle(Paint.Style.STROKE);

        List<VertexEntity> vertexEntityList = roomAndVertex.vertexEntityList;
        Path path = new Path();
        path.moveTo(vertexEntityList.get(0).getX(), vertexEntityList.get(0).getY());

        for (int i = 1; i < vertexEntityList.size(); i++) {
            path.lineTo(vertexEntityList.get(i).getX(), vertexEntityList.get(i).getY());
        }
        path.close();

        region.setPath(path, new Region(0, 0, (int) WIDTH, (int) HEIGHT));
        canvas.drawPath(path, paint);

        drawRoomTitle(canvas, roomAndVertex.roomEntity.label, region);
    }

    private void drawPicture(Canvas canvas) {
        if (pictureEntityList == null) return;

        for (PictureEntity picture : pictureEntityList) {
            Log.d("TAG", picture.title + " -> " + picture.x + "," + picture.y);
//            Drawable pictureDrawable = AppCompatResources.getDrawable(context, R.drawable.baseline_crop_landscape_24);
            int _x = (int) picture.x;
            int _y = (int) picture.y;

            Path path = new Path();
            path.addCircle(_x, _y, 60f, Path.Direction.CW);
            canvas.drawPath(path, picturePaint);
            Region region = new Region();
            region.setPath(path, new Region(0, 0, (int) WIDTH, (int) HEIGHT));
            pictureRegionList.put(picture, region);

            canvas.drawText("" + picture.pictureId, _x, _y, pictureIconTextPaint);

        }

    }

    private void drawRoomTitle(Canvas canvas, String title, Region region) {
        float x = region.getBounds().centerX();
        float y = region.getBounds().centerY();
        canvas.drawText(title, x, y, pictureIconTextPaint);

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

    private HashMap<String, Float> calculateFactor() {
        if (roomAndVertex.vertexEntityList == null) return null;

        // Calcular los límites del cuarto (bounding box)
        float leftX = Float.MAX_VALUE, topY = Float.MAX_VALUE;
        float rightX = Float.MIN_VALUE, bottomY = Float.MIN_VALUE;

        for (VertexEntity vertex : roomAndVertex.vertexEntityList) {
            float x = vertex.getX();
            float y = vertex.getY();

            if (x < leftX) leftX = x;
            if (x > rightX) rightX = x;
            if (y < topY) topY = y;
            if (y > bottomY) bottomY = y;
        }

        // Dimensiones del cuarto original
        float roomWidth = rightX - leftX;
        float roomHeight = bottomY - topY;

        // Calcular factor de escala (respetar relación de aspecto)
        float scaleFactor = Math.min((WIDTH * 0.9f) / roomWidth, (HEIGHT * 0.9f) / roomHeight);

        // Calcular desplazamientos para centrar el cuarto
        float offsetX = leftX * scaleFactor;
        float offsetY = topY * scaleFactor;
        float marginLeft = (WIDTH - (roomWidth * scaleFactor)) / 2;
        float marginTop = (HEIGHT - (roomHeight * scaleFactor)) / 2;

        // Guardar valores en un HashMap
        HashMap<String, Float> params = new HashMap<>();
        params.put("SCALE", scaleFactor);
        params.put("OFFSET_X", offsetX);
        params.put("OFFSET_Y", offsetY);
        params.put("MARGIN_LEFT", marginLeft);
        params.put("MARGIN_TOP", marginTop);

        Log.d("RoomView", "Scaling parameters: " + params);
        return params;
    }

    public void setRoom(RoomAndVertex roomAndVertex) {
        this.roomAndVertex = roomAndVertex;
        Log.d("TAG", "========setRoom========");
        parameters = calculateFactor();
        if (parameters != null) {
            float scale = (float) parameters.get("SCALE");
            float offsetX = (float) parameters.get("OFFSET_X");
            float offsetY = (float) parameters.get("OFFSET_Y");
            float marginLeft = (float) parameters.get("MARGIN_LEFT");
            float marginTop = (float) parameters.get("MARGIN_TOP");

            this.roomAndVertex.vertexEntityList.forEach(obj -> {
                obj.setX((scale * obj.getX()) - offsetX + marginLeft);
                obj.setY((scale * obj.getY()) - offsetY + marginTop);
            });
        }

        invalidate();
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
                Log.d("TAG", "original:" + " -> " + obj.x + "," + obj.y);
                obj.x = (scale * obj.x) - offsetX + marginLeft;
                obj.y = (scale * obj.y) - offsetY + marginTop;
                Log.d("TAG", "update:" + " -> " + obj.x + "," + obj.y);

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
        }
        invalidate();
    }

    public void setEventViewModel(EventViewModel eventViewModel) {
        this.eventViewModel = eventViewModel;
    }

}
