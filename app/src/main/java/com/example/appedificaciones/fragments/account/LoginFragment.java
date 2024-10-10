package com.example.appedificaciones.fragments.account;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appedificaciones.AccountEntity;
import com.example.appedificaciones.HomeActivity;
import com.example.appedificaciones.R;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;



public class LoginFragment extends Fragment {
    private FragmentManager fragmentManager = null;
    private FragmentTransaction fragmentTransaction = null;
    private RegisterFragment registerFragment;

    private TextView txtForgotPassword; // Enlace para la recuperación
    private EditText edtUsuario, edtPassword;
    private Button btnLogin, btnGoToRegister;
    private String accountEntityString;
    public static final String USER_LOGGED = "userAccount";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el layout para este fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Referencias a los elementos de la vista según los IDs del layout
        edtUsuario = view.findViewById(R.id.edtUsuario);
        edtPassword = view.findViewById(R.id.edtPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnGoToRegister = view.findViewById(R.id.btnRegister);
        txtForgotPassword = view.findViewById(R.id.txtOlvidarContra); // Asegúrate de tener este TextView en tu layout


        // Acción para el botón de Login
        btnLogin.setOnClickListener(v -> {
            String usuario = edtUsuario.getText().toString();
            String password = edtPassword.getText().toString();

            if (checkCredentials(usuario, password)) {
                Toast.makeText(getActivity(), "Login exitoso", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                intent.putExtra(USER_LOGGED, accountEntityString);
                startActivity(intent);

            } else {
                Toast.makeText(getActivity(), "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        });

        // Acción para el botón de ir a RegisterFragment
        btnGoToRegister.setOnClickListener(v -> {

            fragmentManager = getParentFragmentManager();
            registerFragment = new RegisterFragment();
            loadFragment(registerFragment);
        });

        // Acción para el enlace de recuperación
        txtForgotPassword.setOnClickListener(v -> {
            fragmentManager = getParentFragmentManager();
            RecuperarFragment recuperarFragment = new RecuperarFragment();
            loadFragment(recuperarFragment);
        });


        return view;
    }

   //Verficar las credenciales en el archivo de cuentas
   private boolean checkCredentials(String username, String password) {
       try {
           // ruta completa del archivo en el almacenamiento interno
           File accountsFile = new File(getActivity().getFilesDir(), RegisterFragment.ACCOUNTS_FILE_NAME);

           // Lee el archivo desde esa ruta
           BufferedReader reader = new BufferedReader(new FileReader(accountsFile));
           String line;
           Gson gson = new Gson();
           while ((line = reader.readLine()) != null) {
               AccountEntity account = gson.fromJson(line, AccountEntity.class);

               if (account.getUsername().equals(username) && account.getPassword().equals(password)) {
                   accountEntityString = line;

                   return true;
               }
           }
           reader.close();

       } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    private void loadFragment(Fragment fragment) {
        if (fragmentManager != null) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        }
    }
}
