package com.example.appedificaciones;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class LoginFragment extends Fragment {

    private EditText edtUsuario, edtPassword;
    private Button btnLogin, btnGoToRegister;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el layout para este fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Referencias a los elementos de la vista según los IDs del layout
        edtUsuario = view.findViewById(R.id.edtUsuario);
        edtPassword = view.findViewById(R.id.edtPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnGoToRegister = view.findViewById(R.id.btnRegister);

        // Acción para el botón de Login
        btnLogin.setOnClickListener(v -> {
            String usuario = edtUsuario.getText().toString();
            String password = edtPassword.getText().toString();

            if (validarLogin(usuario, password)) {
                // Si el login es exitoso, redirige a la HomeActivity
                Toast.makeText(getActivity(), "Login exitoso", Toast.LENGTH_SHORT).show();

                // Iniciar HomeActivity
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                startActivity(intent);
                getActivity().finish(); // Opcional: cierra la actividad de login si no deseas volver a ella
            } else {
                // Si falla el login, muestra un mensaje de error
                Toast.makeText(getActivity(), "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        });

        // Acción para el botón de ir a RegisterFragment
        btnGoToRegister.setOnClickListener(v -> {
            // Navegar al fragment de registro
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registerFragment);
        });

        return view;
    }

    // Método para validar el login leyendo los datos de un archivo .txt
    private boolean validarLogin(String usuario, String password) {
        try {
            FileInputStream fis = getActivity().openFileInput("usuarios.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;

            // Lee cada línea del archivo y compara con el usuario y contraseña ingresados
            while ((line = reader.readLine()) != null) {
                String[] datos = line.split(","); // Formato en el archivo: usuario,contraseña
                if (datos.length == 2 && datos[0].equals(usuario) && datos[1].equals(password)) {
                    reader.close();
                    return true;
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
