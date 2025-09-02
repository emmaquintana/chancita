package com.emmanuel.chancita.ui.crear_rifa;

import android.content.Intent;
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
import com.emmanuel.chancita.ui.rifa.RifaOrganizadorActivity;

public class CrearRifaPaso5Fragment extends Fragment {

    private CrearRifaPaso5ViewModel mViewModel;
    private NavController navController;

    public static CrearRifaPaso5Fragment newInstance() {
        return new CrearRifaPaso5Fragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        navController = NavHostFragment.findNavController(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crear_rifa_paso5, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button btnContinuar = view.findViewById(R.id.crear_rifa_paso_5_btn_continuar);

        btnContinuar.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), RifaOrganizadorActivity.class);
            startActivity(intent);
        });
    }
}