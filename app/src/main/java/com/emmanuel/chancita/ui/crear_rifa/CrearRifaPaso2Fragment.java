package com.emmanuel.chancita.ui.crear_rifa;

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

public class CrearRifaPaso2Fragment extends Fragment {

    private CrearRifaPaso2ViewModel mViewModel;
    private NavController navController;

    public static CrearRifaPaso2Fragment newInstance() {
        return new CrearRifaPaso2Fragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crear_rifa_paso2, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = NavHostFragment.findNavController(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button btnContinuar = view.findViewById(R.id.crear_rifa_paso_2_btn_continuar);

        btnContinuar.setOnClickListener(v -> {
            navController.navigate(R.id.action_crearRifaPaso2Fragment_to_crearRifaPaso3Fragment);
        });
    }
}