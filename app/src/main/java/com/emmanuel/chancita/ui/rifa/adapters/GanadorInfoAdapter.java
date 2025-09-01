package com.emmanuel.chancita.ui.rifa.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.data.model.Rifa;
import com.emmanuel.chancita.data.model.RifaGanador;
import com.emmanuel.chancita.data.model.Usuario;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class GanadorInfoAdapter extends RecyclerView.Adapter<GanadorInfoAdapter.ViewHolder> {

    private List<RifaGanador> ganadores; // MAL

    public GanadorInfoAdapter(List<RifaGanador> ganadores) {
        this.ganadores = ganadores;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_ganador_info, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RifaGanador rifaGanador = ganadores.get(position);

        // Obtiene información del usuario
        Usuario usuario = obtenerUsuario(rifaGanador.getUsuarioId());

        holder.ganadorNombre.setText("Nombre: " + usuario.getNombre());
        holder.ganadorCelular.setText("Nro. de celular: " + usuario.getNroCelular());
        holder.ganadorEmail.setText("Email: " + usuario.getCorreo());
    }

    @Override
    public int getItemCount() {
        return ganadores.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView ganadorNombre;
        public TextView ganadorCelular;
        public TextView ganadorEmail;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ganadorNombre = itemView.findViewById(R.id.rifa_txt_ganador_nombre);
            ganadorCelular = itemView.findViewById(R.id.rifa_txt_ganador_contacto);
            ganadorEmail = itemView.findViewById(R.id.rifa_txt_ganador_email);
        }
    }

    private Usuario obtenerUsuario(String usuarioId) {
        return new Usuario(usuarioId, "example@example.com", "Juan", "Perez", "+5493854877069", "123456", LocalDate.now(), LocalDateTime.now(), LocalDateTime.now());
    }
}

