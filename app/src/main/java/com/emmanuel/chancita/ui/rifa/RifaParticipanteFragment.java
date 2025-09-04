package com.emmanuel.chancita.ui.rifa;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.emmanuel.chancita.R;
import com.emmanuel.chancita.data.model.MetodoEleccionGanador;
import com.emmanuel.chancita.data.model.RifaPremio;
import com.emmanuel.chancita.ui.rifa.adapters.NumerosAdapter;
import com.emmanuel.chancita.utils.Utilidades;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class RifaParticipanteFragment extends Fragment {

    private RifaParticipanteViewModel rifaParticipanteViewModel;
    private MaterialButton btnContinuar;
    private double precioNumero;

    public static RifaParticipanteFragment newInstance() {
        return new RifaParticipanteFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rifaParticipanteViewModel = new ViewModelProvider(this).get(RifaParticipanteViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rifa_participante, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        TextView txtRifaTitulo = view.findViewById(R.id.rifa_participante_txt_titulo_rifa);
        TextView txtRifaEstado = view.findViewById(R.id.rifa_participante_txt_estado_rifa); // Debe iniciar con "Estado: "
        TextView txtRifaCodigo = view.findViewById(R.id.rifa_participante_txt_codigo_rifa); // Debe iniciar con "Código: "
        TextView txtRifaFechaSorteo = view.findViewById(R.id.rifa_participante_txt_fecha_sorteo); // Debe iniciar con "Fecha de sorteo: "
        TextView txtRifaMetodoEleccionGanador = view.findViewById(R.id.rifa_participante_txt_metodo_eleccion); // Debe iniciar con "Método de elección: "
        TextView txtRifaPremios = view.findViewById(R.id.rifa_participante_txt_premios); // Debe iniciar con "1er puesto: "
        TextView txtRifaDescripcion = view.findViewById(R.id.rifa_participante_txt_descripcion);
        TextView txtPrecioNumero = view.findViewById(R.id.rifa_participante_txt_precio_numero); // Debe iniciar con "Precio por número: $"
        btnContinuar = view.findViewById(R.id.rifa_participante_btn_comprar_numeros);

        rifaParticipanteViewModel.obtenerRifa("B1EzULChLnb37D7LfC3m").observe(getViewLifecycleOwner(), rifa -> {
            precioNumero = rifa.getPrecioNumero();
            inflarNumeros(view, rifa.getCantNumeros(), rifa.getNumerosComprados());
            txtRifaTitulo.setText(rifa.getTitulo());
            txtRifaDescripcion.setText(rifa.getDescripcion());
            txtRifaEstado.setText("Estado: " + rifa.getEstado());
            txtRifaCodigo.setText("Código: " + rifa.getCodigo());
            txtPrecioNumero.setText("Precio por número: $" + rifa.getPrecioNumero() + " + 2.2% comisión");
            txtRifaFechaSorteo.setText("Fecha de sorteo: " + Utilidades.formatearFechaHora(rifa.getFechaSorteo(), "dd/MM/yyyy hh:mm"));
            txtRifaMetodoEleccionGanador.setText("Método de elección: " + Utilidades.capitalizar(rifa.getMetodoEleccionGanador().toString()) + (rifa.getMetodoEleccionGanador() == MetodoEleccionGanador.DETERMINISTA ? " (" + rifa.getMotivoEleccionGanador() + ")" : ""));
            txtRifaPremios.setText(formatearPremios(rifa.getPremios()));


            btnContinuar.setOnClickListener(v -> {

            });
        });
    }

    /** Infla los numeros de los cuales el usuario participante podrá elegir cuál comprar */
    private void inflarNumeros(View view, int cantNumeros, List<Integer> numerosComprados) {
        RecyclerView rvNumeros = view.findViewById(R.id.rifa_participante_rv_numeros);
        List<Integer> numeros;

        numeros = new ArrayList<>();
        for (int i = 1; i <= cantNumeros; i++) {
            numeros.add(i);
        }

        NumerosAdapter adapter = new NumerosAdapter(numeros, numerosComprados, numerosSeleccionados -> {
            if (numerosSeleccionados.size() > 0) {
                btnContinuar.setEnabled(true);
            }
            else {
                btnContinuar.setEnabled(false);
            }

            if (numerosSeleccionados.size() == 1) {
                btnContinuar.setText("Comprar número ($" + calcularPrecio(numerosSeleccionados.size(), precioNumero, 2.2) + ")");
            }
            else if (numerosSeleccionados.size() > 1) {
                btnContinuar.setText("Comprar números ($" + calcularPrecio(numerosSeleccionados.size(), precioNumero, 2.2) + ")");
            }
        });

        // Distribuye los items en una grilla
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 5); // 5 columnas
        rvNumeros.setLayoutManager(layoutManager);
        rvNumeros.setAdapter(adapter);
    }

    private double calcularPrecio(double cantNumerosSeleccionados, double precioNumero, double comision) {
        double precioBase = cantNumerosSeleccionados * precioNumero;
        return precioBase + (precioBase * comision / 100.0);
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

        if (rifaPremios != null && !rifaPremios.isEmpty()) {
            for (RifaPremio premio : rifaPremios) {
                stb.append("Puesto nro." + premio.getPremioOrden() + ": " + premio.getPremioTitulo() + " (" + premio.getPremioDescripcion() + ")" + "\n");
            }
        }

        return stb.toString();
    }
}