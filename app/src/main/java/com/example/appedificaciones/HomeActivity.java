package com.example.appedificaciones;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.appedificaciones.fragments.FavoritesFragment;
import com.example.appedificaciones.fragments.HomeFragment;
import com.example.appedificaciones.fragments.ListFragment;
import com.example.appedificaciones.fragments.MapFragment;
import com.example.appedificaciones.fragments.account.LoginFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;

public class HomeActivity extends AppCompatActivity {
    private FragmentManager fragmentManager = null;
    private FragmentTransaction fragmentTransaction = null;
    private HomeFragment homeFragment;
    private ListFragment listaFragment = null;
    private MapFragment mapaFragment = null;
    private FavoritesFragment favoritesFragment = null;
    private AccountEntity userLogged;
    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);// Layout de la actividad con fragments

        // Obtener el ViewModel
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        Intent intent = getIntent();
        if (intent != null) {

            String accountEntityString = intent.getStringExtra(LoginFragment.USER_LOGGED);
            Gson gson = new Gson();
            userLogged = gson.fromJson(accountEntityString,AccountEntity.class);

            sharedViewModel.setUserLogged(userLogged);
        }

        fragmentManager = getSupportFragmentManager();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setSelectedItemId(R.id.menu_home);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.menu_home) {
                    homeFragment = new HomeFragment();
                    loadFragment(homeFragment);
                    return true;
                } else if (menuItem.getItemId() == R.id.menu_lista) {
                    listaFragment = new ListFragment();
                    loadFragment(listaFragment);
                    return true;
                } else if (menuItem.getItemId() == R.id.menu_mapa) {
                    mapaFragment = new MapFragment();
                    loadFragment(mapaFragment);
                    return true;
                }else if(menuItem.getItemId() == R.id.menu_favorites){
                    favoritesFragment = new FavoritesFragment();
                    loadFragment(favoritesFragment);
                    return true;
                }
                else {
                    return false;
                }
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        if (fragmentManager != null) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainerView, fragment);
            fragmentTransaction.commit();
        }
    }

}