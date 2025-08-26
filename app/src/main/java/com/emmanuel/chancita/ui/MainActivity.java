package com.emmanuel.chancita.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.emmanuel.chancita.R;
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
            Toast.makeText(this, "About", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}