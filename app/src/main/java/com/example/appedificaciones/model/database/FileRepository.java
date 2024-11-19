package com.example.appedificaciones.model.database;

import android.content.Context;

import com.example.appedificaciones.model.ent.EdificationEntity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileRepository {
     private Context context;

    // Constructor que recibe un Context
    public FileRepository(Context context) {
        this.context = context;
    }

    public List<EdificationEntity> getEdificacionesFromTextFile() {
        List<EdificationEntity> edificaciones = new ArrayList<>();
        try {
            InputStream inputStream = context.getAssets().open("edificaciones.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            JSONObject jsonObject = new JSONObject(jsonBuilder.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("edificaciones");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                EdificationEntity edification = new EdificationEntity.Builder()
                        .setTitulo(obj.getString("titulo"))
                        .setCategoria(obj.getString("categoria"))
                        .setDescripcion(obj.getString("descripcion"))
                        .setResumen(obj.getString("resumen"))
                        .setImagen(obj.getString("imagen"))
                        .setAudio(obj.getString("audio"))
                        .build();
                edificaciones.add(edification);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return edificaciones;
    }


}
