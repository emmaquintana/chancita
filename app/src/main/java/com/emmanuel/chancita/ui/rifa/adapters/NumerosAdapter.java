package com.emmanuel.chancita.ui.rifa.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.data.model.NumeroComprado;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class NumerosAdapter extends RecyclerView.Adapter<NumerosAdapter.NumeroViewHolder> {

    private List<Integer> numeros;
    private List<Integer> numerosSeleccionados;
    private List<NumeroComprado> numerosComprados;
    private OnNumeroClickListener listener;

    public interface OnNumeroClickListener {
        void onNumeroClick(List<Integer> numerosSeleccionados);
    }

    public NumerosAdapter(List<Integer> numeros,
                          List<NumeroComprado> numerosComprados,
                          OnNumeroClickListener listener) {
        this.numeros = numeros != null ? numeros : new ArrayList<>();
        this.numerosComprados = numerosComprados != null ? numerosComprados : new ArrayList<>();
        this.listener = listener;
        this.numerosSeleccionados = new ArrayList<>();
    }

    /**
     * Método para limpiar la selección de números
     */
    public void limpiarSeleccion() {
        if (!numerosSeleccionados.isEmpty()) {
            numerosSeleccionados.clear();
            notifyDataSetChanged(); // Actualizar toda la vista para reflejar los cambios
        }
    }

    /**
     * NUEVO: Método para actualizar la lista de números comprados
     */
    public void actualizarNumerosComprados(List<NumeroComprado> nuevosNumerosComprados) {
        this.numerosComprados = nuevosNumerosComprados != null ? nuevosNumerosComprados : new ArrayList<>();
        notifyDataSetChanged(); // Refrescar toda la vista para mostrar los números recién comprados como bloqueados
    }

    /**
     * Método para obtener la lista actual de números seleccionados
     */
    public List<Integer> getNumerosSeleccionados() {
        return new ArrayList<>(numerosSeleccionados);
    }

    @NonNull
    @Override
    public NumeroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MaterialButton button = (MaterialButton) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_numero, parent, false);
        return new NumeroViewHolder(button);
    }

    @Override
    public void onBindViewHolder(@NonNull NumeroViewHolder holder, int position) {
        int numero = numeros.get(position);
        holder.bind(numero);
    }

    @Override
    public int getItemCount() {
        return numeros.size();
    }

    class NumeroViewHolder extends RecyclerView.ViewHolder {
        MaterialButton btnNumero;

        public NumeroViewHolder(@NonNull View itemView) {
            super(itemView);
            btnNumero = (MaterialButton) itemView;
        }

        void bind(int numero) {
            btnNumero.setText(String.valueOf(numero));

            // Verificar si el número está comprado por cualquier usuario
            boolean comprado = isNumeroComprado(numero);

            if (comprado) {
                btnNumero.setEnabled(false);
                btnNumero.setChecked(false);
                // Cambiar el estilo visual para números comprados
                btnNumero.setAlpha(0.5f);
                // Limpiar el listener para evitar clics accidentales
                btnNumero.setOnClickListener(null);
                return;
            }

            // Número disponible
            btnNumero.setEnabled(true);
            btnNumero.setAlpha(1.0f);
            btnNumero.setChecked(numerosSeleccionados.contains(numero));

            btnNumero.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) return;

                if (numerosSeleccionados.contains(numero)) {
                    numerosSeleccionados.remove(Integer.valueOf(numero));
                } else {
                    numerosSeleccionados.add(numero);
                }

                // Actualizar solo este item específico
                notifyItemChanged(pos);

                // Notificar al listener
                if (listener != null) {
                    listener.onNumeroClick(new ArrayList<>(numerosSeleccionados));
                }
            });
        }

        /**
         * Método auxiliar para verificar si un número está comprado
         */
        private boolean isNumeroComprado(int numero) {
            for (NumeroComprado nc : numerosComprados) {
                if (nc.getNumerosComprados() != null && nc.getNumerosComprados().contains(numero)) {
                    return true;
                }
            }
            return false;
        }
    }
}