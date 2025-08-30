package com.emmanuel.chancita.ui.home.adapters; // Cambia el paquete a tu ruta

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.data.model.Rifa;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class RifaAdapter extends RecyclerView.Adapter<RifaAdapter.RaffleViewHolder> {

    private List<Rifa> rifas;
    private OnItemClickListener listener;

    public RifaAdapter(List<Rifa> rifas, OnItemClickListener listener) {
        this.rifas = rifas;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Rifa rifa);
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
        Rifa rifa = rifas.get(position);

        // Formateador de fecha para "dd/MM/yyyy"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Rellenar los datos en los TextViews
        holder.nombreRifa.setText(rifa.getTitulo());
        holder.codigoRifa.setText("Código: " + rifa.getCodigo());

        // Formatear la fecha del sorteo
        String fechaFormateada = rifa.getFechaSorteo().format(formatter);
        holder.fechaSorteoRifa.setText("Sorteo: " + fechaFormateada);

        // Cambiar el texto y el color del estado
        holder.estadoRifa.setText(rifa.getEstado().toString());

        switch (rifa.getEstado()) {
            case ABIERTO:
                holder.estadoRifa.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
                break;
            case CERRADO:

                break;
            case SORTEADO:
                holder.estadoRifa.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.md_theme_error));
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