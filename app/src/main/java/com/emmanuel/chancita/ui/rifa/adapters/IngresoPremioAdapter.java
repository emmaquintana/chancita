package com.emmanuel.chancita.ui.rifa.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.ui.rifa.model.IngresoPremio;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class IngresoPremioAdapter extends RecyclerView.Adapter<IngresoPremioAdapter.ViewHolder> {

    private final List<IngresoPremio> ingresoPremios;

    public IngresoPremioAdapter(List<IngresoPremio> ingresoPremios) {
        this.ingresoPremios = ingresoPremios;
    }

    @NonNull
    @Override
    public IngresoPremioAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_ingreso_premio, parent, false);
        return new IngresoPremioAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngresoPremioAdapter.ViewHolder holder, int position) {
        IngresoPremio ingresoPremio = ingresoPremios.get(position);
        holder.premioHead.setText(ingresoPremio.getHead());
        holder.premioTitulo.setText(ingresoPremio.getTitulo());
        holder.premioDescripcion.setText(ingresoPremio.getDescripcion());
    }

    @Override
    public int getItemCount() {
        return ingresoPremios.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView premioHead;
        public TextInputEditText premioTitulo;
        public TextInputEditText premioDescripcion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            premioHead = itemView.findViewById(R.id.layout_ingreso_premio_txt_premio);
            premioTitulo = itemView.findViewById(R.id.layout_ingreso_premio_tiet_titulo);
            premioDescripcion = itemView.findViewById(R.id.layout_ingreso_premio_tiet_descripcion);
        }
    }
}