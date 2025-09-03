package com.emmanuel.chancita.ui.rifa;

import androidx.lifecycle.ViewModelProvider;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.data.model.RifaGanador;
import com.emmanuel.chancita.data.model.RifaPremio;
import com.emmanuel.chancita.ui.rifa.adapters.EleccionGanadoresAdapter;
import com.emmanuel.chancita.ui.rifa.adapters.GanadorInfoAdapter;
import com.emmanuel.chancita.ui.rifa.adapters.ParticipantesAdapter;
import com.emmanuel.chancita.ui.rifa.model.CandidatoGanador;
import com.emmanuel.chancita.ui.rifa.model.Participante;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RifaOrganizadorFragment extends Fragment {

    private RifaOrganizadorViewModel mViewModel;
    private NavController navController;

    public static RifaOrganizadorFragment newInstance() {
        return new RifaOrganizadorFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rifa_organizador, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = NavHostFragment.findNavController(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        if (hayParticipantes("123123-123123")) {

            // Si hay participantes, NO se muestra el texto "No hay participantes"
            TextView noHayParticipantes = view.findViewById(R.id.rifa_organizador_txt_no_hay_participantes);
            noHayParticipantes.setVisibility(View.GONE);

            // Se muestra el nombre de cada participante y sus numeros comprados
            inflarParticipantes(view);

            if (hayGanadores("123123-123123")) {
                inflarGanadores(view);
            }
            else {
                // Si no hay ganadores, no se muestra la sección "Ganadores"
                TextView tituloGanadores = view.findViewById(R.id.rifa_organizador_txt_seccion_ganadores);
                LinearLayout listaGanadores = view.findViewById(R.id.rifa_organizador_ll_ganadores_info);
                tituloGanadores.setVisibility(View.GONE);
                listaGanadores.setVisibility(View.GONE);

                // Si el metodo de eleccion es "Determinista" y ya se está en fecha de sorteo, deben elegirse ganadores manualmente
                if (debeElegirseGanador("123123-123123")) {
                    inflarEleccionGanadores(view);
                }
            }
        }

        TextView premios = view.findViewById(R.id.rifa_organizador_txt_premios);
        premios.setText(formatearPremios(obtenerPremios("id")));


        // Debe permitirse navegar hacia "Editar rifa" y para ello se debe tener el FAB

        /*
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_rifaOrganizadorFragment_to_editarRifaFragment);
            }
        });
        */
    }

    private boolean debeElegirseGanador(String rifaId) {
        return true;
    }

    // Estos datos son solo de prueba
    private List<RifaPremio> obtenerPremios(String rifaId) {

        List<RifaPremio> premios = new ArrayList<>();
        premios.add(new RifaPremio("id1", "Torta", "Torta de manzana", 1));
        premios.add(new RifaPremio("id2", "Tortita", null, 2));
        premios.add(new RifaPremio("id3", "$5000", null, 3));

        return premios;
    }

    /**
     * Devuelve los premios de la rifa con el siguiente formato:<br/>
     *
     * "Puesto nro. 1: Titulo (descripcion)<br/>
     *  Puesto nro. 2: Titulo (descripcion)<br/>
     *  ...<br/>
     *  Puesto nro. N: TItulo (descripcion)"<br/>
     *
     * */
    private String formatearPremios(List<RifaPremio> rifaPremios) {
        StringBuilder stb = new StringBuilder();

        for (RifaPremio premio : rifaPremios) {
            stb.append("Puesto nro." + premio.getPremioOrden() + ": " + premio.getPremioTitulo() + " (" + premio.getPremioDescripcion() + ")" + "\n");
        }

        return stb.toString();
    }

    private boolean hayGanadores(String rifaId) {
        return false;
    }

    private boolean hayParticipantes(String rifaId) {
        return true;
    }

    private void inflarGanadores(View view) {
        RecyclerView rvGanadorInfo = view.findViewById(R.id.recycler_view_ganadores);
        rvGanadorInfo.setLayoutManager(new LinearLayoutManager(getContext()));

        List<RifaGanador> rifaGanadorList = new ArrayList<>();
        rifaGanadorList.add(new RifaGanador("123123-123123", "321321-321321", "213213-213213", "313131-313131", "212121-212121", LocalDateTime.now()));
        rifaGanadorList.add(new RifaGanador("123123-123123", "321321-321321", "213213-213213", "313131-313131", "212121-212121", LocalDateTime.now()));
        rifaGanadorList.add(new RifaGanador("123123-123123", "321321-321321", "213213-213213", "313131-313131", "212121-212121", LocalDateTime.now()));
        rifaGanadorList.add(new RifaGanador("123123-123123", "321321-321321", "213213-213213", "313131-313131", "212121-212121", LocalDateTime.now()));

        GanadorInfoAdapter ganadorInfoAdapter = new GanadorInfoAdapter(rifaGanadorList);
        rvGanadorInfo.setAdapter(ganadorInfoAdapter);
    }

    private void inflarParticipantes(View view) {
        List<Participante> participantes = new ArrayList<>();
        participantes.add(new Participante("Juan Perez", List.of(5,7,3,2)));
        participantes.add(new Participante("Juan Perez Segundo", List.of(10,11,12)));
        participantes.add(new Participante("Juan Perez Tercero", List.of(13,14,15)));
        participantes.add(new Participante("Juan Perez Cuarto", List.of(16,17,18)));

        RecyclerView rvParticipantes = view.findViewById(R.id.recycler_view_participantes);
        rvParticipantes.setLayoutManager(new LinearLayoutManager(getContext()));
        ParticipantesAdapter participantesAdapter = new ParticipantesAdapter(participantes);
        rvParticipantes.setAdapter(participantesAdapter);
    }

    private void inflarEleccionGanadores(View view) {
        List<CandidatoGanador> candidatosGanadores = new ArrayList<>();
        candidatosGanadores.add(new CandidatoGanador("Juan Perez", true, List.of(1,2,3,4,5)));
        candidatosGanadores.add(new CandidatoGanador("Juan Perez", true, List.of(6,7)));
        candidatosGanadores.add(new CandidatoGanador("Juan Perez", true, List.of(8)));

        RecyclerView rvCandidatosGanadores = view.findViewById(R.id.recycler_view_eleccion_ganadores);
        rvCandidatosGanadores.setLayoutManager(new LinearLayoutManager(getContext()));
        EleccionGanadoresAdapter eleccionGanadoresAdapter = new EleccionGanadoresAdapter(candidatosGanadores);
        rvCandidatosGanadores.setAdapter(eleccionGanadoresAdapter);

        TextView elegirGanadorTitulo = view.findViewById(R.id.rifa_organizador_txt_seccion_ganadores);
        MaterialButton confirmarGanadores = view.findViewById(R.id.rifa_organizador_btn_confirmar_ganadores);

        rvCandidatosGanadores.setVisibility(View.VISIBLE);
        elegirGanadorTitulo.setVisibility(View.VISIBLE);
        confirmarGanadores.setVisibility(View.VISIBLE);
    }

}