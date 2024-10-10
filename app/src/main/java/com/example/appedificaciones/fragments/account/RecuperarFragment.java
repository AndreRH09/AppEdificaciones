package com.example.appedificaciones.fragments.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.appedificaciones.AccountEntity;
import com.example.appedificaciones.R;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class RecuperarFragment extends Fragment {

    private EditText edtUserRecuperar, edtEmailRecuperar, edtPhoneRecuperar;
    private Button btnRecuperar, btnCancelarRecuperar;
    public static final String ACCOUNTS_FILE_NAME = "accounts.txt";
    private File accountsFile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el layout para este fragment
        View view = inflater.inflate(R.layout.fragment_recuperar, container, false);

        // Referencias a los elementos del layout
        edtUserRecuperar = view.findViewById(R.id.edtUserRecuperar);
        edtEmailRecuperar = view.findViewById(R.id.edtEmailRecuperar);
        edtPhoneRecuperar = view.findViewById(R.id.edtPhoneRecuperar);
        btnRecuperar = view.findViewById(R.id.btnRecuperar);
        btnCancelarRecuperar = view.findViewById(R.id.btnCancelarRecuperar);

        // Ruta al archivo de cuentas
        accountsFile = new File(getActivity().getFilesDir(), ACCOUNTS_FILE_NAME);

        // Acción del botón de recuperar contraseña
        btnRecuperar.setOnClickListener(v -> {
            String username = edtUserRecuperar.getText().toString();
            String email = edtEmailRecuperar.getText().toString();
            String phone = edtPhoneRecuperar.getText().toString();

            if (username.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                Toast.makeText(getContext(), "Por favor completa todos los campos.", Toast.LENGTH_SHORT).show();
            } else {
                String password = validateAccount(username, email, phone);
                if (password != null) {
                    // Mostrar la contraseña
                    Toast.makeText(getContext(), "Tu contraseña es: " + password, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "No se encontraron coincidencias.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Acción del botón cancelar
        btnCancelarRecuperar.setOnClickListener(v -> {
            // Regresar al LoginFragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new LoginFragment())
                    .commit();
        });

        return view;
    }

    // Método para validar las credenciales del usuario y devolver la contraseña
    private String validateAccount(String username, String email, String phone) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(accountsFile));
            String line;
            Gson gson = new Gson();
            while ((line = reader.readLine()) != null) {
                AccountEntity account = gson.fromJson(line, AccountEntity.class);
                if (account.getUsername().equals(username) &&
                        account.getEmail().equals(email) &&
                        account.getPhone().equals(phone)) {
                    // Usuario encontrado, devolver la contraseña
                    reader.close();
                    return account.getPassword(); // Asegúrate de que esta propiedad exista en AccountEntity
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // No se encontró
    }
}
