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

public class CrearRifaPaso3Fragment extends Fragment {

    private CrearRifaPaso3ViewModel mViewModel;
    private NavController navController;

    public static CrearRifaPaso3Fragment newInstance() {
        return new CrearRifaPaso3Fragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crear_rifa_paso3, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = NavHostFragment.findNavController(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button btnContinuar = view.findViewById(R.id.crear_rifa_paso_3_btn_continuar);

        btnContinuar.setOnClickListener(v -> {
            navController.navigate(R.id.action_crearRifaPaso3Fragment_to_crearRifaPaso4Fragment);
        });
    }

}