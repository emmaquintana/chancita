package com.emmanuel.chancita.ui.rifa.crear_rifa;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.ui.rifa.adapters.IngresoPremioAdapter;
import com.emmanuel.chancita.ui.rifa.model.IngresoPremio;

import java.util.ArrayList;
import java.util.List;

public class CrearRifaPaso3Fragment extends Fragment {

    private CrearRifaPaso3ViewModel mViewModel;
    private NavController navController;
    private int cantPremios;

    public static CrearRifaPaso3Fragment newInstance() {
        return new CrearRifaPaso3Fragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crear_rifa_paso3, container, false);

        inflarIngresoDePremios(view);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = NavHostFragment.findNavController(this);
        cantPremios = 3;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button btnContinuar = view.findViewById(R.id.crear_rifa_paso_3_btn_continuar);

        btnContinuar.setOnClickListener(v -> {
            navController.navigate(R.id.action_crearRifaPaso3Fragment_to_crearRifaPaso4Fragment);
        });
    }

    private void inflarIngresoDePremios(View view) {
        List<IngresoPremio> ingresoPremios = new ArrayList<>();

        for (int i = 0; i < cantPremios; i++) {
            ingresoPremios.add(new IngresoPremio("Premio " + (i+1), null, null));
        }

        RecyclerView rvIngresoPremios = view.findViewById(R.id.crear_rifa_paso_3_rv_ingreso_premios);
        rvIngresoPremios.setLayoutManager(new LinearLayoutManager(getContext()));
        IngresoPremioAdapter ingresoPremioAdapter = new IngresoPremioAdapter(ingresoPremios);
        rvIngresoPremios.setAdapter(ingresoPremioAdapter);
    }

}