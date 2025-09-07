package com.emmanuel.chancita.ui.rifa.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.data.model.RifaPremio;
import com.emmanuel.chancita.ui.rifa.model.IngresoPremio;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class EditarPremioAdapter extends RecyclerView.Adapter<EditarPremioAdapter.ViewHolder> {

    private final List<RifaPremio> premios;
    private final OnPremioChangedListener listener;

    public interface OnPremioChangedListener {
        void onPremioChanged(int position, RifaPremio premio);
    }

    public EditarPremioAdapter(List<RifaPremio> premios, OnPremioChangedListener listener) {
        this.premios = premios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EditarPremioAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_ingreso_premio, parent, false);
        return new EditarPremioAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EditarPremioAdapter.ViewHolder holder, int position) {
        RifaPremio premio = premios.get(position);

        // Configurar el encabezado
        holder.premioHead.setText("Premio " + premio.getPremioOrden());

        // Configurar los campos de texto
        holder.premioTitulo.setText(premio.getPremioTitulo());
        holder.premioDescripcion.setText(premio.getPremioDescripcion());

        // Configurar listeners para detectar cambios
        holder.setupTextWatchers(position, premio, listener);
    }

    @Override
    public int getItemCount() {
        return premios.size();
    }

    public List<RifaPremio> getPremios() {
        return premios;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView premioHead;
        public TextInputEditText premioTitulo;
        public TextInputEditText premioDescripcion;

        private TextWatcher tituloWatcher;
        private TextWatcher descripcionWatcher;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            premioHead = itemView.findViewById(R.id.layout_ingreso_premio_txt_premio);
            premioTitulo = itemView.findViewById(R.id.layout_ingreso_premio_tiet_titulo);
            premioDescripcion = itemView.findViewById(R.id.layout_ingreso_premio_tiet_descripcion);
        }

        public void setupTextWatchers(int position, RifaPremio premio, OnPremioChangedListener listener) {
            // Remover watchers anteriores para evitar conflictos
            if (tituloWatcher != null) {
                premioTitulo.removeTextChangedListener(tituloWatcher);
            }
            if (descripcionWatcher != null) {
                premioDescripcion.removeTextChangedListener(descripcionWatcher);
            }

            // TextWatcher para el título
            tituloWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    String nuevoTitulo = s.toString().trim();
                    if (!nuevoTitulo.equals(premio.getPremioTitulo())) {
                        premio.setPremioTitulo(nuevoTitulo);
                        if (listener != null) {
                            listener.onPremioChanged(position, premio);
                        }
                    }
                }
            };

            // TextWatcher para la descripción
            descripcionWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    String nuevaDescripcion = s.toString().trim();
                    if (!nuevaDescripcion.equals(premio.getPremioDescripcion())) {
                        premio.setPremioDescripcion(nuevaDescripcion);
                        if (listener != null) {
                            listener.onPremioChanged(position, premio);
                        }
                    }
                }
            };

            // Agregar los watchers
            premioTitulo.addTextChangedListener(tituloWatcher);
            premioDescripcion.addTextChangedListener(descripcionWatcher);
        }
    }
}