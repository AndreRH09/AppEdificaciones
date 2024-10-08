package com.example.appedificaciones;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appedificaciones.fragments.account.LoginFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Cargar el LoginFragment en la actividad principal
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new LoginFragment()) // Asegúrate de que el contenedor tenga este ID
                    .commit();
        }
    }
}