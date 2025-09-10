package com.emmanuel.chancita.ui.rifa.crear_rifa;

import androidx.appcompat.app.AlertDialog;;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.data.model.RifaPremio;
import com.emmanuel.chancita.ui.rifa.adapters.EditarPremioAdapter;
import com.emmanuel.chancita.ui.rifa.adapters.IngresoPremioAdapter;
import com.emmanuel.chancita.ui.rifa.model.IngresoPremio;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class CrearRifaPaso3Fragment extends Fragment implements EditarPremioAdapter.OnPremioChangedListener {

    private CrearRifaSharedViewModel sharedViewModel;
    private NavController navController;
    private EditarPremioAdapter premiosAdapter;

    // Views
    private RecyclerView rvPremios;
    private MaterialButton btnContinuar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crear_rifa_paso3, container, false);
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
        setupRecyclerView();
        setupContinueButton();
        observeViewModel();
    }

    private void initViews(View view) {
        rvPremios = view.findViewById(R.id.crear_rifa_paso_3_rv_ingreso_premios);
        btnContinuar = view.findViewById(R.id.crear_rifa_paso_3_btn_continuar);
    }

    private void setupRecyclerView() {
        rvPremios.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPremios.setHasFixedSize(true);
    }

    private void setupContinueButton() {
        btnContinuar.setOnClickListener(v -> {
            if (validarYContinuar()) {
                navController.navigate(R.id.action_crearRifaPaso3Fragment_to_crearRifaPaso4Fragment);
            }
        });
    }

    private boolean validarYContinuar() {
        if (premiosAdapter == null) {
            mostrarError("No se han cargado los premios");
            return false;
        }

        List<RifaPremio> premios = premiosAdapter.getPremios();

        if (premios == null || premios.isEmpty()) {
            mostrarError("Debes agregar al menos un premio");
            return false;
        }

        // Validar cada premio
        for (int i = 0; i < premios.size(); i++) {
            RifaPremio premio = premios.get(i);

            if (premio.getPremioTitulo() == null || premio.getPremioTitulo().trim().isEmpty()) {
                mostrarError("El título del premio " + (i + 1) + " no puede estar vacío");
                return false;
            }

            // Limpiar descripción vacía
            if (premio.getPremioDescripcion() != null && premio.getPremioDescripcion().trim().isEmpty()) {
                premio.setPremioDescripcion(null);
            }
        }

        // Actualizar premios en el ViewModel
        sharedViewModel.actualizarPremios(premios);
        return true;
    }

    private void mostrarError(String mensaje) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Error de validación")
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private void observeViewModel() {
        sharedViewModel.rifaEnConstruccion.observe(getViewLifecycleOwner(), rifa -> {
            if (rifa != null && rifa.getPremios() != null && !rifa.getPremios().isEmpty()) {
                setupPremiosAdapter(rifa.getPremios());
            }
        });
    }

    private void setupPremiosAdapter(List<RifaPremio> premios) {
        // Crear copia mutable de la lista
        List<RifaPremio> premiosMutable = new ArrayList<>(premios);
        premiosAdapter = new EditarPremioAdapter(premiosMutable, this);
        rvPremios.setAdapter(premiosAdapter);
    }

    @Override
    public void onPremioChanged(int position, RifaPremio premio) {
        // Los cambios se reflejan automáticamente en el adapter
        // No es necesario hacer nada adicional aquí
    }
}