package com.emmanuel.chancita.ui.home;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.ui.SharedViewModel;
import com.emmanuel.chancita.ui.crear_rifa.CrearRifaActivity;
import com.emmanuel.chancita.ui.rifa.RifaOrganizadorActivity;
import com.emmanuel.chancita.ui.rifa.RifaParticipanteActivity;
import com.google.android.material.card.MaterialCardView;

public class HomeFragment extends Fragment {

    private HomeViewModel mViewModel;
    private SharedViewModel sharedViewModel;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtiene la instancia del ViewModel compartida con la MainActivity
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        sharedViewModel.setToolbarTitle("Inicio");

        TextView btnCrearRifa = view.findViewById(R.id.btn_create_raffle);
        MaterialCardView rifaUnida = view.findViewById(R.id.mcv_rifa_unida);
        MaterialCardView rifaCreada = view.findViewById(R.id.mcv_rifa_creada);

        btnCrearRifa.setOnClickListener(view1 -> {
            Intent intent = new Intent(requireActivity(), CrearRifaActivity.class);
            startActivity(intent);
        });

        rifaUnida.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), RifaParticipanteActivity.class);
            startActivity(intent);
        });

        rifaCreada.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), RifaOrganizadorActivity.class);
            startActivity(intent);
        });
    }
}