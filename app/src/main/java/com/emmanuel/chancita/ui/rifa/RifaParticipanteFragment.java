package com.emmanuel.chancita.ui.rifa;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.ui.rifa.adapters.NumerosAdapter;

import java.util.ArrayList;
import java.util.List;

public class RifaParticipanteFragment extends Fragment {

    private RifaParticipanteViewModel mViewModel;
    private List<Integer> numerosAComprar;

    public static RifaParticipanteFragment newInstance() {
        return new RifaParticipanteFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rifa_participante, container, false);

        RecyclerView rvNumeros = view.findViewById(R.id.rv_numeros);

        int numeroTotalDeTickets = 100; // Debería ser un valor obtenido desde la BD
        List<Integer> numeros = new ArrayList<>();
        for (int i = 1; i <= numeroTotalDeTickets; i++) {
            numeros.add(i);
        }

        NumerosAdapter adapter = new NumerosAdapter(numeros, new NumerosAdapter.OnNumeroClickListener() {
            @Override
            public void onNumeroClick(int numero) {
                // Lógica para cuando se hace clic en un número
            }
        });

        // Distribuye los items en una grilla
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 5); // 5 columnas
        rvNumeros.setLayoutManager(layoutManager);
        rvNumeros.setAdapter(adapter);

        return view;
    }


}