package com.emmanuel.chancita.ui.rifa;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.data.model.MetodoEleccionGanador;
import com.emmanuel.chancita.data.model.Rifa;
import com.emmanuel.chancita.data.model.RifaEstado;
import com.emmanuel.chancita.data.model.RifaPremio;
import com.emmanuel.chancita.ui.rifa.adapters.NumerosAdapter;
import com.emmanuel.chancita.utils.Utilidades;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RifaParticipanteFragment extends Fragment {

    private RifaParticipanteViewModel mViewModel;
    private List<Integer> numerosAComprar;
    private Rifa rifa;

    public static RifaParticipanteFragment newInstance() {
        return new RifaParticipanteFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rifa = obtenerRifa("id");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rifa_participante, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        inflarNumeros(view);

        TextView rifaTitulo = view.findViewById(R.id.rifa_participante_txt_titulo_rifa);
        TextView rifaEstado = view.findViewById(R.id.rifa_participante_txt_estado_rifa); // Debe iniciar con "Estado: "
        TextView rifaFechaSorteo = view.findViewById(R.id.rifa_participante_txt_fecha_sorteo); // Debe iniciar con "Fecha de sorteo: "
        TextView rifaMetodoEleccionGanador = view.findViewById(R.id.rifa_participante_txt_metodo_eleccion); // Debe iniciar con "Método de elección: "
        TextView rifaPremios = view.findViewById(R.id.rifa_participante_txt_premios); // Debe iniciar con "1er puesto: "

        rifaTitulo.setText(rifa.getTitulo());
        rifaEstado.setText("Estado: " + rifa.getEstado());
        rifaFechaSorteo.setText("Fecha de sorteo: " + Utilidades.formatearFechaHora(rifa.getFechaSorteo(), "dd/MM/yyyy hh:mm"));
        rifaMetodoEleccionGanador.setText("Método de elección: " + Utilidades.capitalizar(rifa.getMetodoEleccionGanador().toString()) + (rifa.getMetodoEleccionGanador() == MetodoEleccionGanador.DETERMINISTA ? "(" + rifa.getMotivoEleccionGanador() + ")" : ""));
        rifaPremios.setText(formatearPremios(rifa.getId()));
    }

    /** Infla los numeros de los cuales el usuario participante podrá elegir cuál comprar */
    private void inflarNumeros(View view) {
        RecyclerView rvNumeros = view.findViewById(R.id.rifa_participante_rv_numeros);

        int numeroTotalDeTickets = 100; // Debería ser un valor obtenido desde la BD
        numerosAComprar = new ArrayList<>();
        for (int i = 1; i <= numeroTotalDeTickets; i++) {
            numerosAComprar.add(i);
        }

        NumerosAdapter adapter = new NumerosAdapter(numerosAComprar, new NumerosAdapter.OnNumeroClickListener() {
            @Override
            public void onNumeroClick(int numero) {
                // Lógica para cuando se hace clic en un número
            }
        });

        // Distribuye los items en una grilla
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 5); // 5 columnas
        rvNumeros.setLayoutManager(layoutManager);
        rvNumeros.setAdapter(adapter);
    }

    // Devuelve una rifa de prueba
    private Rifa obtenerRifa(String id) {
        return new Rifa(id, "Salvemos a Pepito", "Descripción", "user2", RifaEstado.SORTEADO, LocalDateTime.now(), "PEPITO2025", MetodoEleccionGanador.ALEATORIO,null,LocalDateTime.of(2025, 07, 03, 00, 00, 00), 5.0);
    }

    // Estos datos son solo de prueba
    private RifaPremio[] obtenerPremios(String rifaId) {
        return new RifaPremio[] {
                new RifaPremio("id1", "Torta", "Torta de manzana", "id1", 1),
                new RifaPremio("id2", "Tortita", null, "id1", 2),
                new RifaPremio("id3", "$5000", null, "id1", 3),
        };
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
    private String formatearPremios(String rifaId) {
        StringBuilder stb = new StringBuilder();
        RifaPremio[] rifaPremios = obtenerPremios(rifaId);

        for (RifaPremio premio : rifaPremios) {
            stb.append("Puesto nro." + premio.getPremioOrden() + ": " + rifa.getTitulo() + " (" + rifa.getDescripcion() + ")" + "\n");
        }

        return stb.toString();
    }
}