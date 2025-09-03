package com.emmanuel.chancita.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.data.dto.RifaDTO;
import com.emmanuel.chancita.data.model.MetodoEleccionGanador;
import com.emmanuel.chancita.data.model.RifaEstado;
import com.emmanuel.chancita.data.model.RifaPremio;
import com.emmanuel.chancita.ui.rifa.RifaOrganizadorViewModel;
import com.emmanuel.chancita.utils.Utilidades;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.emmanuel.chancita.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;
    private SharedViewModel sharedViewModel;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtiene una referencia al NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();

        // Conecta el BottomNavigationView con el NavController
        NavigationUI.setupWithNavController(binding.navView, navController);

        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        // Obtén el ViewModel compartido entre la Activity y los Fragmentos
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        // Observa el LiveData del título
        sharedViewModel.getToolbarTitle().observe(this, title -> {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(title);
            }
        });
/*
        RifaOrganizadorViewModel rifaOrganizadorViewModel = new ViewModelProvider(this).get(RifaOrganizadorViewModel.class);

        List<RifaPremio> premios = new ArrayList<>();
        premios.add(new RifaPremio(Utilidades.generarCodigo(10,10), "Smartphone", "Un celular de última generación", 2));
        premios.add(new RifaPremio(Utilidades.generarCodigo(10,10), "Smartphone", "Un celular de última generación", 2));
        premios.add(new RifaPremio(Utilidades.generarCodigo(10,10), "Smartphone", "Un celular de última generación", 2));


        RifaDTO nuevaRifa = new RifaDTO(
                null, // id (lo genera Firestore en el DAO)
                "Rifa de Tecnología", // titulo
                "Participa para ganar premios tecnológicos", // descripcion
                100, // cantidad de números
                "usuario123", // creadoPor (id del usuario actual)
                RifaEstado.ABIERTO, // estado
                "TECNO2025", // código de rifa
                MetodoEleccionGanador.DETERMINISTA, // método de elección del ganador
                "El número más alto será el ganador", // motivo (si aplica)
                LocalDateTime.now().plusDays(7), // fecha del sorteo
                10.0, // precio del número
                LocalDateTime.now(), // creadoEn
                premios // lista de premios
        );

        rifaOrganizadorViewModel.crearRifa(nuevaRifa);
        */

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sign_out) {
            // Cierra la sesión en Firebase
            FirebaseAuth.getInstance().signOut();

            // Borra la preferencia de usuario
            SharedPreferences sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("is_logged_in", false);
            editor.apply();

            // Navega a la actividad de autenticación
            Intent intent = new Intent(this, AuthActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}