package com.emmanuel.chancita.ui.rifa.editar_rifa;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.ui.rifa.adapters.IngresoPremioAdapter;
import com.emmanuel.chancita.ui.rifa.model.IngresoPremio;

import java.util.ArrayList;
import java.util.List;

public class EditarRifaFragment extends Fragment {

    private EditarRifaViewModel mViewModel;
    private int cantPremios = 2;

    public static EditarRifaFragment newInstance() {
        return new EditarRifaFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar_rifa, container, false);

        inflarIngresoDePremios(view);

        return view;
    }

    private void inflarIngresoDePremios(View view) {
        List<IngresoPremio> ingresoPremios = new ArrayList<>();

        for (int i = 0; i < cantPremios; i++) {
            ingresoPremios.add(new IngresoPremio("Premio " + (i+1), "Titulo premio", "Descripción premio"));
        }

        RecyclerView rvIngresoPremios = view.findViewById(R.id.editar_rifa_rv_ingreso_premios);
        rvIngresoPremios.setLayoutManager(new LinearLayoutManager(getContext()));
        IngresoPremioAdapter ingresoPremioAdapter = new IngresoPremioAdapter(ingresoPremios);
        rvIngresoPremios.setAdapter(ingresoPremioAdapter);
    }
}