package com.example.appedificaciones.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appedificaciones.AccountEntity;
import com.example.appedificaciones.R;
import com.example.appedificaciones.SharedViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.lifecycle.ViewModelProvider;


public class HomeFragment extends Fragment {
    private TextView txtTitle;
    private TextView txtDescription;
    private ImageView imgEdificios;
    private Button btnVerEdificaciones;
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
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        txtTitle = view.findViewById(R.id.tv_title);
        btnVerEdificaciones = view.findViewById(R.id.btnVerEdificaciones);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

       // Observar los cambios en el objeto userLogged
        sharedViewModel.getUserLogged().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                txtTitle.setText(getString(R.string.welcome_string) + ", " + user.getUsername());
            }
        });

        // Acción para el botón que lleva a la lista de edificaciones
        btnVerEdificaciones.setOnClickListener(v -> {
            BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
            bottomNavigationView.setSelectedItemId(R.id.menu_lista);
        });
        return view;
    }
}