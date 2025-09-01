package com.emmanuel.chancita.ui.rifa.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.ui.rifa.model.CandidatoGanador;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.List;

public class EleccionGanadoresAdapter extends RecyclerView.Adapter<EleccionGanadoresAdapter.ViewHolder> {

    private List<CandidatoGanador> candidatosGanadores;

    public EleccionGanadoresAdapter(List<CandidatoGanador> candidatosGanadores) {
        this.candidatosGanadores = candidatosGanadores;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_eleccion_ganador, parent, false);
        return new EleccionGanadoresAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CandidatoGanador candidatoGanador = candidatosGanadores.get(position);
        holder.checkbox.setText(candidatoGanador.getNombreApellido() + " - Números comprados: " + candidatoGanador.getNumeros());
        holder.checkbox.setChecked(candidatoGanador.esGanador());
    }

    @Override
    public int getItemCount() {
        return candidatosGanadores.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public MaterialCheckBox checkbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkbox = itemView.findViewById(R.id.layout_eleccion_ganador_checkbox);
        }
    }
}
