package com.emmanuel.chancita.ui.inicio.adapters; // Cambia el paquete a tu ruta

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.data.dto.RifaDTO;
import com.emmanuel.chancita.data.model.Rifa;
import com.emmanuel.chancita.utils.Utilidades;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class RifaAdapter extends RecyclerView.Adapter<RifaAdapter.RaffleViewHolder> {

    private List<RifaDTO> rifas;
    private OnItemClickListener listener;

    public RifaAdapter(List<RifaDTO> rifas, OnItemClickListener listener) {
        this.rifas = rifas;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(RifaDTO rifa);
    }

    @NonNull
    @Override
    public RaffleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_rifa, parent, false);
        return new RaffleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RaffleViewHolder holder, int position) {
        RifaDTO rifa = rifas.get(position);

        // Formateador de fecha para "dd/MM/yyyy"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Rellenar los datos en los TextViews
        holder.nombreRifa.setText(rifa.getTitulo());
        holder.codigoRifa.setText("Código: " + rifa.getCodigo());

        // Formatear la fecha del sorteo
        String fechaFormateada = rifa.getFechaSorteo().format(formatter);
        holder.fechaSorteoRifa.setText("Sorteo: " + fechaFormateada);

        // Cambiar el texto y el color del estado
        holder.estadoRifa.setText(Utilidades.capitalizar(rifa.getEstado().toString()));

        switch (rifa.getEstado()) {
            case ABIERTO:
                holder.estadoRifa.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
                holder.estadoRifa.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.lightgreen));
                break;
            case CERRADO:
                holder.estadoRifa.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.md_theme_error));
                holder.estadoRifa.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.md_theme_errorContainer));
                break;
            case SORTEADO:
                holder.estadoRifa.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.md_theme_error));
                holder.estadoRifa.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.md_theme_errorContainer));
                break;
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(rifa);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rifas.size();
    }

    public static class RaffleViewHolder extends RecyclerView.ViewHolder {
        public TextView nombreRifa;
        public TextView codigoRifa;
        public TextView fechaSorteoRifa;
        public TextView estadoRifa;

        public RaffleViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreRifa = itemView.findViewById(R.id.rifa_txt_nombre);
            codigoRifa = itemView.findViewById(R.id.rifa_txt_codigo_rifa);
            fechaSorteoRifa = itemView.findViewById(R.id.rifa_txt_fecha_sorteo);
            estadoRifa = itemView.findViewById(R.id.rifa_txt_estado_rifa);
        }
    }
}