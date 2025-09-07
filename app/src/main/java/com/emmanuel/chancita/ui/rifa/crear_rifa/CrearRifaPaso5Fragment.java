package com.emmanuel.chancita.ui.rifa.crear_rifa;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.ui.rifa.RifaOrganizadorActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class CrearRifaPaso5Fragment extends Fragment {

    private CrearRifaSharedViewModel sharedViewModel;
    private NavController navController;

    // Views
    private TextInputEditText tietDescripcionMetodo;
    private MaterialButton btnContinuar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crear_rifa_paso5, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(CrearRifaSharedViewModel.class);
        navController = NavHostFragment.findNavController(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupContinueButton();
        observeViewModel();
    }

    private void initViews(View view) {
        tietDescripcionMetodo = view.findViewById(R.id.crear_rifa_paso_5_tiet_descripcion_metodo);
        btnContinuar = view.findViewById(R.id.crear_rifa_paso_5_btn_continuar);
    }

    private void setupContinueButton() {
        btnContinuar.setOnClickListener(v -> {
            if (validarYCrearRifa()) {
                String usuarioActual = obtenerUsuarioActual();

                if (usuarioActual == null) {
                    mostrarError("Error: No se pudo identificar el usuario actual");
                    return;
                }

                sharedViewModel.crearRifa(usuarioActual);
            }
        });
    }

    private boolean validarYCrearRifa() {
        String descripcion = tietDescripcionMetodo.getText().toString().trim();

        if (descripcion.isEmpty()) {
            mostrarError("Debes describir cómo se elegirán los ganadores");
            return false;
        }

        if (descripcion.length() < 20) {
            mostrarError("La descripción debe tener al menos 20 caracteres");
            return false;
        }

        // Actualizar descripción en el ViewModel
        sharedViewModel.actualizarDescripcionMetodo(descripcion);
        return true;
    }

    private String obtenerUsuarioActual() {
        String idUsuarioActual = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return idUsuarioActual;
    }

    private void mostrarError(String mensaje) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Error de validación")
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private void observeViewModel() {
        // Observar estado de creación de rifa
        sharedViewModel.creandoRifa.observe(getViewLifecycleOwner(), creando -> {
            btnContinuar.setEnabled(!creando);
            if (creando) {
                btnContinuar.setText("Creando rifa...");
            } else {
                btnContinuar.setText("Continuar");
            }
        });

        // Observar resultado de creación
        sharedViewModel.resultadoCreacion.observe(getViewLifecycleOwner(), resultado -> {
            if (resultado != null) {
                if (resultado.contains("éxito")) {
                    mostrarExitoYSalir(resultado);
                } else {
                    mostrarError(resultado);
                }
            }
        });

        // Restaurar descripción previa si existe
        sharedViewModel.rifaEnConstruccion.observe(getViewLifecycleOwner(), rifa -> {
            if (rifa != null && rifa.getMotivoEleccionGanador() != null) {
                tietDescripcionMetodo.setText(rifa.getMotivoEleccionGanador());
            }
        });
    }

    private void mostrarExitoYSalir(String mensaje) {
        new AlertDialog.Builder(requireContext())
                .setTitle("¡Éxito!")
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    // Volver a la pantalla principal o lista de rifas
                    requireActivity().finish();
                })
                .setCancelable(false)
                .show();
    }
}