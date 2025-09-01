package com.emmanuel.chancita.ui.home;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.data.model.MetodoEleccionGanador;
import com.emmanuel.chancita.data.model.Rifa;
import com.emmanuel.chancita.data.model.RifaEstado;
import com.emmanuel.chancita.ui.SharedViewModel;
import com.emmanuel.chancita.ui.crear_rifa.CrearRifaActivity;
import com.emmanuel.chancita.ui.home.adapters.RifaAdapter;
import com.emmanuel.chancita.ui.rifa.RifaOrganizadorActivity;
import com.emmanuel.chancita.ui.rifa.RifaParticipanteActivity;
import com.google.android.material.card.MaterialCardView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

        btnCrearRifa.setOnClickListener(view1 -> {
            Intent intent = new Intent(requireActivity(), CrearRifaActivity.class);
            startActivity(intent);
        });

        inflarRifasCreadas(view);
    }

    private void inflarRifasCreadas(View view) {
        RecyclerView rvRifasCreadas = view.findViewById(R.id.recycler_view_rifas_creadas);
        RecyclerView rvRifasUnidas = view.findViewById(R.id.recycler_view_rifas_unidas);

        List<Rifa> rifasCreadas = new ArrayList<>();
        rifasCreadas.add(new Rifa("id1", "Salvemos a Pepito", "Descripción", "user2", RifaEstado.SORTEADO, LocalDateTime.now(), "PEPITO2025", MetodoEleccionGanador.ALEATORIO,null,LocalDateTime.of(2025, 7, 15, 0,0,0), 5.0));

        List<Rifa> rifasUnidas = new ArrayList<>();
        rifasUnidas.add(new Rifa("id3", "Salvemos a Pepito", "Descripción", "user2", RifaEstado.SORTEADO, LocalDateTime.now(), "PEPITO2025", MetodoEleccionGanador.ALEATORIO,null,LocalDateTime.of(2025, 7, 15, 0,0,0), 5.0));
        rifasUnidas.add(new Rifa("id4", "Salvemos a Pepito 2", "Descripción", "user2", RifaEstado.SORTEADO, LocalDateTime.now(), "PEPITO2025",  MetodoEleccionGanador.ALEATORIO,null,LocalDateTime.of(2025, 8, 15,0,0), 5.0));
        rifasUnidas.add(new Rifa("id5", "Salvemos a Pepito 3", "Descripción", "user2", RifaEstado.ABIERTO, LocalDateTime.now(), "PEPITO2025",  MetodoEleccionGanador.ALEATORIO,null,LocalDateTime.of(2025, 9, 15,0,0), 5.0));

        // Si hay rifas creadas, se las muestra
        if (rifasCreadas.size() != 0) {

            // Si se "toca" una rifa, se redirige a la actividad correspondiente a esa rifa
            RifaAdapter.OnItemClickListener rifaCreadaListener = new RifaAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Rifa rifa) {
                    Intent intent = new Intent(getContext(), RifaOrganizadorActivity.class);
                    intent.putExtra("rifa_id", rifa.getId());
                    startActivity(intent);
                }
            };

            // Genera el layout
            rvRifasCreadas.setLayoutManager(new LinearLayoutManager(getContext()));
            RifaAdapter rifasAdapter = new RifaAdapter(rifasCreadas, rifaCreadaListener);
            rvRifasCreadas.setAdapter(rifasAdapter);
        }
        else {
            // Si el usuario no posee rifas creadas, se le informa de ello al usuario
            TextView msgNoRifasCreadas = view.findViewById(R.id.inicio_txt_no_rifas_creadas);
            msgNoRifasCreadas.setVisibility(View.VISIBLE);
        }

        if (rifasUnidas.size() != 0) {

            // Si se "toca" una rifa, se redirige a la actividad correspondiente
            RifaAdapter.OnItemClickListener rifaUnidaListener = new RifaAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Rifa rifa) {
                    Intent intent = new Intent(getContext(), RifaParticipanteActivity.class);
                    intent.putExtra("rifa_id", rifa.getId());
                    startActivity(intent);
                }
            };

            // Genera el layout
            rvRifasUnidas.setLayoutManager(new LinearLayoutManager(getContext()));
            RifaAdapter joinedAdapter = new RifaAdapter(rifasUnidas, rifaUnidaListener);
            rvRifasUnidas.setAdapter(joinedAdapter);
        }
        else {
            // Si el usuario no se unió a rifas, se le informa de ello al usuario
            TextView msgNoRifasUnidas = view.findViewById(R.id.inicio_txt_no_rifas_unidas);
            msgNoRifasUnidas.setVisibility(View.VISIBLE);
        }
    }
}