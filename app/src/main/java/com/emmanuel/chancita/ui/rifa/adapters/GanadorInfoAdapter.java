package com.emmanuel.chancita.ui.rifa.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.data.model.GanadorInfo;

import java.util.List;

public class GanadorInfoAdapter extends RecyclerView.Adapter<GanadorInfoAdapter.ViewHolder> {

    private List<GanadorInfo> ganadores;

    public GanadorInfoAdapter(List<GanadorInfo> ganadores) {
        this.ganadores = ganadores;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_ganador_info, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GanadorInfo ganador = ganadores.get(position);

        holder.ganadorNombre.setText("Nombre: " + ganador.getNombre());
        holder.ganadorCelular.setText("Nro. de celular: " + ganador.getCelular());
        holder.ganadorEmail.setText("Email: " + ganador.getEmail());
        holder.ganadorPuesto.setText("Premio del puesto nro.: " + ganador.getPuesto() + " - Número ganador: " + ganador.getNumeroGanador());
    }

    @Override
    public int getItemCount() {
        return ganadores.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView ganadorNombre;
        TextView ganadorCelular;
        TextView ganadorEmail;
        TextView ganadorPuesto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ganadorNombre = itemView.findViewById(R.id.rifa_txt_ganador_nombre);
            ganadorCelular = itemView.findViewById(R.id.rifa_txt_ganador_contacto);
            ganadorEmail = itemView.findViewById(R.id.rifa_txt_ganador_email);
            ganadorPuesto = itemView.findViewById(R.id.rifa_txt_ganador_puesto);
        }
    }
}
