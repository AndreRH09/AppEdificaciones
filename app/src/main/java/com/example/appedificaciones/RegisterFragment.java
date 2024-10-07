package com.example.appedificaciones;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.io.FileOutputStream;
import java.io.IOException;

public class RegisterFragment extends Fragment {

    private EditText edtUser, edtPassword, edtEmail, edtPhone;
    private Button btnRegister, btnCancelar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Infla el layout para este fragmento
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Referencias a los elementos del layout
        edtUser = view.findViewById(R.id.edtUser);
        edtPassword = view.findViewById(R.id.edtPassword);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPhone = view.findViewById(R.id.edtPhone);
        btnRegister = view.findViewById(R.id.btnRegister);
        btnCancelar = view.findViewById(R.id.btnCancelar);

        // Acción del botón de registrar
        btnRegister.setOnClickListener(v -> {
            String username = edtUser.getText().toString();
            String password = edtPassword.getText().toString();
            String email = edtEmail.getText().toString();
            String phone = edtPhone.getText().toString();

            // Validar los campos
            if (username.isEmpty() || password.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                Toast.makeText(getContext(), "Por favor completa todos los campos.", Toast.LENGTH_SHORT).show();
            } else {
                // Guardar la información en un archivo de texto
                String userInfo = username + "," + password + "," + email + "," + phone + "\n";
                saveToFile(userInfo);
                Toast.makeText(getContext(), "Registro exitoso.", Toast.LENGTH_SHORT).show();
                // Aquí puedes navegar de vuelta al LoginFragment
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new LoginFragment())
                        .commit();
            }
        });

        // Acción del botón de cancelar
        btnCancelar.setOnClickListener(v -> {
            // Aquí puedes navegar de vuelta al LoginFragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new LoginFragment())
                    .commit();
        });

        return view;
    }

    private void saveToFile(String data) {
        FileOutputStream fos;
        try {
            // Cambia "usuarios.txt" a donde desees guardar el archivo
            fos = getContext().openFileOutput("usuarios.txt", getContext().MODE_PRIVATE | getContext().MODE_APPEND);
            fos.write(data.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error al guardar la información.", Toast.LENGTH_SHORT).show();
        }
    }
}