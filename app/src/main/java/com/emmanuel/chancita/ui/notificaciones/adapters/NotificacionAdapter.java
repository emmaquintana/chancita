package com.emmanuel.chancita.ui.notificaciones.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.data.model.Notificacion;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NotificacionAdapter extends RecyclerView.Adapter<NotificacionAdapter.NotificationViewHolder> {

    private final List<Notificacion> notificaciones;

    public NotificacionAdapter(List<Notificacion> notificaciones) {
        this.notificaciones = notificaciones;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_notificacion, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notificacion notificacion = notificaciones.get(position);

        // Formatear fecha
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String fechaStr = sdf.format(notificacion.getFecha());

        holder.fecha.setText(fechaStr);
        holder.titulo.setText(notificacion.getTitulo());
        holder.cuerpo.setText(notificacion.getCuerpo());
    }

    @Override
    public int getItemCount() {
        return notificaciones.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        public TextView fecha;
        public TextView titulo;
        public TextView cuerpo;

        public NotificationViewHolder(View view) {
            super(view);
            fecha = view.findViewById(R.id.notificacion_fecha);
            titulo = view.findViewById(R.id.notificacion_titulo);
            cuerpo = view.findViewById(R.id.notificacion_cuerpo);
        }
    }
}