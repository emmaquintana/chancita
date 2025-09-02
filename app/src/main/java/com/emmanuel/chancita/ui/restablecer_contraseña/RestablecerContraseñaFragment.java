package com.emmanuel.chancita.ui.restablecer_contraseña;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.emmanuel.chancita.databinding.FragmentRestablecerContrasenaBinding;

public class RestablecerContraseñaFragment extends Fragment {

    private FragmentRestablecerContrasenaBinding binding;
    private RestablecerContraseñaViewModel restablecerContraseñaViewModel;
    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRestablecerContrasenaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        restablecerContraseñaViewModel = new ViewModelProvider(this).get(RestablecerContraseñaViewModel.class);

        setupListeners();
        setupObservers();
    }

    private void setupListeners() {
        binding.restablecerContraseABtnRestablecerContraseA.setOnClickListener(v -> {
            String newPassword = binding.restablecerContraseATietPassword.getText().toString();
            String confirmPassword = binding.restablecerContraseATietPassword.getText().toString();
            restablecerContraseñaViewModel.restablecerContraseña(newPassword, confirmPassword);
        });
    }

    private void setupObservers() {
        restablecerContraseñaViewModel.estaRestableciendo.observe(getViewLifecycleOwner(), isResetting -> {
            // Manejar el estado de carga (por ejemplo, mostrar un ProgressBar)
        });

        restablecerContraseñaViewModel.resultadoRestablecimiento.observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show();
            }
        });

        restablecerContraseñaViewModel.restablecimientoExitoso.observe(getViewLifecycleOwner(), success -> {
            if (success) {
                // Navegar de regreso a la pantalla de inicio de sesión
                navController.popBackStack();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}