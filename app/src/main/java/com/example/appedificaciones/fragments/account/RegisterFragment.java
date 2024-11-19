package com.example.appedificaciones.fragments.account;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.appedificaciones.AccountEntity;
import com.example.appedificaciones.R;
import com.example.appedificaciones.model.database.EdificationRepository;
import com.example.appedificaciones.model.ent.UserEntity;
import com.example.appedificaciones.model.database.AppDatabase;
import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.concurrent.Executors;

public class RegisterFragment extends Fragment {

    private EditText edtUser, edtPassword, edtEmail, edtPhone;
    private Button btnRegister, btnCancelar;

    public static final String ACCOUNTS_FILE_NAME= "accounts.txt";
    private File accountsFile;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Infla el layout para este fragmento
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Referencias a los elementos del layout
        edtUser = view.findViewById(R.id.edtUser);
        edtPassword = view.findViewById(R.id.edtPassword);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPhone = view.findViewById(R.id.edtPhone);
        btnRegister = view.findViewById(R.id.btnRegister);
        btnCancelar = view.findViewById(R.id.btnCancelar);

        accountsFile = new File(getActivity().getFilesDir(), ACCOUNTS_FILE_NAME);

        // Acción del botón de registrar
        btnRegister.setOnClickListener(v -> {
            String username = edtUser.getText().toString();
            String password = edtPassword.getText().toString();
            String email = edtEmail.getText().toString();
            String phone = edtPhone.getText().toString();

            if (username.isEmpty() || password.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                Toast.makeText(getContext(), "Por favor completa todos los campos.", Toast.LENGTH_SHORT).show();
            } else {

                Context context = requireContext();
                AppDatabase database = AppDatabase.getInstance(context);
                EdificationRepository edificationRepository = new EdificationRepository(database);
                UserEntity newUser = new UserEntity.Builder()
                        .setUser(username)
                        .setPassword(password)
                        .setEmail(email)
                        .setPhone(phone)
                        .build();

                Executors.newSingleThreadExecutor().execute(() -> {
                    edificationRepository.addUser(newUser);
                });

                Toast.makeText(getActivity().getApplicationContext(), "Usuario registrado: " + username, Toast.LENGTH_LONG).show();
                Log.d("LoginActivity", "Usuario registrado:" + username);

                // Regresar al LoginFragment
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new LoginFragment())
                        .commit();
            }
        });

        //Boton cancelar
        btnCancelar.setOnClickListener(v -> {
            // Regresar al LoginFragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new LoginFragment())
                    .commit();
        });

        return view;
    }

     private void saveAccountToFile(String accountJson) {
        try {
            FileWriter writer = new FileWriter(accountsFile, true);
            writer.write(accountJson + "\n");
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}