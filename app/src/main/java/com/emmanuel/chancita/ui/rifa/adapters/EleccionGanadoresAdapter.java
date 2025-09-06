package com.emmanuel.chancita.ui.rifa.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.data.dto.PremioAsignacionDTO;

import java.util.List;

public class EleccionGanadoresAdapter extends RecyclerView.Adapter<EleccionGanadoresAdapter.ViewHolder> {

    private List<PremioAsignacionDTO> premios;

    public EleccionGanadoresAdapter(List<PremioAsignacionDTO> premios) {
        this.premios = premios;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_eleccion_ganador, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PremioAsignacionDTO asignacion = premios.get(position);

        holder.txtPremioTitulo.setText(asignacion.getPremio().getPremioTitulo());
        holder.txtPremioDescripcion.setText(asignacion.getPremio().getPremioDescripcion());

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(
                holder.itemView.getContext(),
                android.R.layout.simple_spinner_item,
                asignacion.getNumerosDisponibles()
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerNumeros.setAdapter(adapter);

        // Si ya se seleccionó antes
        if (asignacion.getNumeroSeleccionado() != null) {
            int index = asignacion.getNumerosDisponibles().indexOf(asignacion.getNumeroSeleccionado());
            if (index >= 0) holder.spinnerNumeros.setSelection(index);
        }

        holder.spinnerNumeros.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                asignacion.setNumeroSeleccionado(asignacion.getNumerosDisponibles().get(pos));
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    public int getItemCount() {
        return premios.size();
    }

    public List<PremioAsignacionDTO> getResultados() {
        return premios;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtPremioTitulo, txtPremioDescripcion;
        Spinner spinnerNumeros;

        ViewHolder(View itemView) {
            super(itemView);
            txtPremioTitulo = itemView.findViewById(R.id.layout_eleccion_ganador_txt_premio_titulo);
            txtPremioDescripcion = itemView.findViewById(R.id.layout_eleccion_ganador_txt_premio_descripcion);
            spinnerNumeros = itemView.findViewById(R.id.layout_eleccion_ganador_spinner_numeros);
        }
    }
}
