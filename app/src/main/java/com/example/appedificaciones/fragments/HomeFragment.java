package com.example.appedificaciones.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appedificaciones.AccountEntity;
import com.example.appedificaciones.R;
import com.example.appedificaciones.SharedViewModel;
import com.example.appedificaciones.fragments.account.EditProfileFragment;
import com.example.appedificaciones.model.ent.UserEntity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


public class HomeFragment extends Fragment {
    private TextView txtTitle;
    private TextView txtDescription;
    private ImageView imgEdificios;
    private Button btnVerEdificaciones;
    private Button btnEditarPerfil;
    private SharedViewModel sharedViewModel;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Inicializar las vistas estáticas
        txtTitle = view.findViewById(R.id.tv_title);
        btnVerEdificaciones = view.findViewById(R.id.btnVerEdificaciones);
        Button btnEditarPerfil = view.findViewById(R.id.btnEditarPerfil);


        // Acción para el botón que lleva a la lista de edificaciones
        btnVerEdificaciones.setOnClickListener(v -> {
            // Navegar a la sección de lista de edificaciones
            BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
            bottomNavigationView.setSelectedItemId(R.id.menu_lista);
        });

        btnEditarPerfil.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, new EditProfileFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Devolvemos la vista inflada
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtener el SharedViewModel
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Observar los cambios en el objeto userLogged
        sharedViewModel.getUserLogged().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                // Si el usuario no es nulo, mostramos el nombre del usuario
                txtTitle.setText(getString(R.string.welcome_string) + ", " + user.getUser());
                Log.d("AAAAA", "SI encontró nombre de usuario");
            } else {
                // Si el usuario es nulo, mostramos un mensaje por defecto
                txtTitle.setText(getString(R.string.welcome_string) + " -");
                Log.d("GGG", "No se encontró nombre de usuario");
            }
        });
    }

}