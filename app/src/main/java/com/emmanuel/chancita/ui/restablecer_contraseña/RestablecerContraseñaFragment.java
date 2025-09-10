package com.emmanuel.chancita.ui.restablecer_contraseña;

import androidx.lifecycle.ViewModelProvider;

import androidx.appcompat.app.AlertDialog;;
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

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.databinding.FragmentRestablecerContrasenaBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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
            String correo = binding.restablecerContraseATietCorreo.getText().toString().trim();

            if (correo.isEmpty()) {
                binding.restablecerContraseATietCorreo.setError("Ingrese un correo válido");
                return;
            }

            restablecerContraseñaViewModel.restablecerContraseña(correo);
        });
    }


    private void setupObservers() {
        restablecerContraseñaViewModel.estaEnviando.observe(getViewLifecycleOwner(), estaEnviando -> {
            if (estaEnviando) {
                binding.restablecerContraseABtnRestablecerContraseA.setText("Enviando mail...");
                binding.restablecerContraseABtnRestablecerContraseA.setEnabled(false);
            } else {
                binding.restablecerContraseABtnRestablecerContraseA.setText("Continuar");
                binding.restablecerContraseABtnRestablecerContraseA.setEnabled(true);
            }
        });

        restablecerContraseñaViewModel.envioExitoso.observe(getViewLifecycleOwner(), success -> {
            if (success) {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Correo enviado")
                        .setMessage("Si la dirección ingresada está registrada, se te enviará un mail para restablecer tu contraseña. Por favor, revisa tu bandeja de entrada o la carpeta de spam.")
                        .setPositiveButton("Aceptar", (dialog, which) -> {
                            dialog.dismiss();
                            navController.popBackStack();
                        })
                        .show();
            }
            else {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Error")
                        .setMessage("No pudimos enviar el correo. Verifica la dirección ingresada o inténtalo nuevamente.")
                        .setPositiveButton("Entendido", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
            }
        });
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}