package com.emmanuel.chancita.ui.crear_rifa;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.emmanuel.chancita.R;

public class CrearRifaPaso4Fragment extends Fragment {

    private CrearRifaPaso4ViewModel mViewModel;
    private NavController navController;

    public static CrearRifaPaso4Fragment newInstance() {
        return new CrearRifaPaso4Fragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crear_rifa_paso4, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = NavHostFragment.findNavController(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button btnContinuar = view.findViewById(R.id.btn_continuar_paso_4);

        boolean metodoDeterminista = true;

        btnContinuar.setOnClickListener(v -> {
            if (metodoDeterminista) {
                navController.navigate(R.id.action_crearRifaPaso4Fragment_to_crearRifaPaso5Fragment);
            }
            else {
                // Muestra la rifa creada
            }

        });
    }

}