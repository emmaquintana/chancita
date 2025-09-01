package com.emmanuel.chancita.ui.rifa.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.ui.rifa.model.Participante;

import java.util.List;

public class ParticipantesAdapter extends RecyclerView.Adapter<ParticipantesAdapter.ParticipanteViewHolder> {

    private List<Participante> participantes;

    public ParticipantesAdapter(List<Participante> participantes) {
        this.participantes = participantes;
    }

    @NonNull
    @Override
    public ParticipanteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_participante, parent, false);
        return new ParticipanteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipanteViewHolder holder, int position) {
        Participante participante = participantes.get(position);

        holder.nombreParticipante.setText(participante.getNombre());
        holder.numerosComprados.setText(participante.getNumeros().toString());
    }

    @Override
    public int getItemCount() {
        return participantes.size();
    }

    public static class ParticipanteViewHolder extends RecyclerView.ViewHolder {

        public TextView numerosComprados;
        public TextView nombreParticipante;

        public ParticipanteViewHolder(@NonNull View itemView) {
            super(itemView);

            nombreParticipante = itemView.findViewById(R.id.participante_txt_nombre);
            numerosComprados = itemView.findViewById(R.id.participante_txt_numeros_comprados);
        }
    }
}
