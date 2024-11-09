package com.example.appedificaciones;

import android.content.Context;
import android.graphics.drawable.Drawable;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {

    // Cargar una imagen desde 'assets' y convertirla a Drawable
    public static Drawable getDrawableFromAssets(Context context, String assetPath) {
        try {
            InputStream imageInputStream = context.getAssets().open("images/"+ assetPath);
            return Drawable.createFromStream(imageInputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
            return null;  // Retorna null si ocurre un error
        }
    }
}
