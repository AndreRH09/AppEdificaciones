package com.example.appedificaciones.fragments.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import com.example.appedificaciones.SharedViewModel;
import com.example.appedificaciones.model.dao.UserDao;
import com.example.appedificaciones.model.database.AppDatabase;
import com.example.appedificaciones.model.database.EdificationRepository;
import com.example.appedificaciones.model.ent.UserEntity;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Executors;

import com.example.appedificaciones.model.database.AppDatabase;


public class LoginFragment extends Fragment {
    private FragmentManager fragmentManager = null;
    private FragmentTransaction fragmentTransaction = null;
    private RegisterFragment registerFragment;

    private TextView txtForgotPassword; // Enlace para la recuperación
    private EditText edtUsuario, edtPassword;
    private Button btnLogin, btnGoToRegister;
    private String accountEntityString;
    public static final String USER_LOGGED = "userAccount";
    private EdificationRepository edificationRepository;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el layout para este fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        edificationRepository = new EdificationRepository(AppDatabase.getInstance(requireContext()));

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

            getUserByUsernameAndPassword (usuario, password, user -> {
                if (user != null) {
                    /*// Usuario encontrado
                    Toast.makeText(getActivity(), "Login exitoso", Toast.LENGTH_SHORT).show();
                    // Obtener el ViewModel
                    SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

                    // Establecer el usuario en el ViewModel
                    viewModel.setUserLogged(user);

                    // Redirigir a la siguiente actividad
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    startActivity(intent);*/

                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    intent.putExtra(USER_LOGGED, accountEntityString);
                    startActivity(intent);
                } else {
                    // Usuario no encontrado
                    Toast.makeText(getActivity(), "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
            });
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

    public void getUserByUsernameAndPassword(String username, String password, OnUserResultCallback callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            UserEntity user = edificationRepository.getUserByUsernameAndPassword(username, password);
            Gson gson = new Gson();
            accountEntityString = gson.toJson(user);

            // Llamar al callback con el resultado
            new Handler(Looper.getMainLooper()).post(() -> callback.onResult(user));
        });
    }

    // Interfaz para manejar el callback
    public interface OnUserResultCallback {
        void onResult(UserEntity user);
    }

    private void loadFragment(Fragment fragment) {
        if (fragmentManager != null) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        }
    }
}
