package com.example.appedificaciones.model.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.appedificaciones.model.ent.DoorEntity;
import com.example.appedificaciones.model.ent.EdificationEntity;
import com.example.appedificaciones.model.ent.PictureEntity;
import com.example.appedificaciones.model.ent.RoomEntity;
import com.example.appedificaciones.model.ent.VertexEntity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
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
                        .setAnio(obj.getString("año"))
                        .build();
                edificaciones.add(edification);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return edificaciones;
    }



    public Bitmap getPicture(String filename) {
        InputStream assetInStream = null;
        Bitmap bit = null;
        try {
            assetInStream = context.getAssets().open(filename);
            bit = BitmapFactory.decodeStream(assetInStream);
            //img.setImageBitmap(bit);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (assetInStream != null) {
                try {
                    assetInStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return bit;
    }

    public List<RoomEntity> getRooms(String filename) {
        BufferedReader reader = null;
        List<RoomEntity> roomEntityList = new ArrayList<>();
        try {

            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(filename)));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] vertex = line.split(",");
                RoomEntity vertexEntity = new RoomEntity(
                        Integer.parseInt(vertex[0]),
                        vertex[1]
                );

                roomEntityList.add(vertexEntity);
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return roomEntityList;
    }

    public List<PictureEntity> getPictures(String filename) {
        BufferedReader reader = null;
        List<PictureEntity> pictures = new ArrayList<>();
        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(filename)));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split("\\|");
                PictureEntity picture =
                        new PictureEntity();

                picture.pictureId = Integer.parseInt(columns[0]);
                picture.author = columns[1];
                picture.title = columns[2];
                picture.link = columns[3];
                picture.roomId = Integer.parseInt(columns[4]);
                picture.x = Float.parseFloat(columns[5]);
                picture.y = Float.parseFloat(columns[6]);
                picture.description = columns[7];

                pictures.add(picture);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return pictures;
    }

    public List<VertexEntity> getVertexes(String[] filenames) {
        BufferedReader reader = null;
        List<VertexEntity> vertexEntityList = new ArrayList<>();
        try {
            for (String filename : filenames) {
                reader = new BufferedReader(
                        new InputStreamReader(context.getAssets().open(filename)));

                String line;
                while ((line = reader.readLine()) != null) {
                    String[] vertex = line.split(",");
                    VertexEntity vertexEntity = new VertexEntity(
                            Integer.parseInt(vertex[0]),
                            Integer.parseInt(vertex[1]),
                            Float.parseFloat(vertex[2]),
                            Float.parseFloat(vertex[3])
                    );

                    vertexEntityList.add(vertexEntity);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return vertexEntityList;
    }

    public List<DoorEntity> getDoors(String filename) {
        BufferedReader reader = null;
        List<DoorEntity> doors = new ArrayList<>();
        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(filename)));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] cols = line.split(",");
                DoorEntity doorEntity = new DoorEntity(
                        Integer.parseInt(cols[0]),
                        Float.parseFloat(cols[1]),
                        Float.parseFloat(cols[2]),
                        Float.parseFloat(cols[3]),
                        Float.parseFloat(cols[4])
                );
                doors.add(doorEntity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return doors;
    }


}
