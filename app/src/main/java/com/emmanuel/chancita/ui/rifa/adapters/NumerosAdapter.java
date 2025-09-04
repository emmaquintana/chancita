package com.emmanuel.chancita.ui.rifa.adapters;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emmanuel.chancita.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class NumerosAdapter extends RecyclerView.Adapter<NumerosAdapter.NumeroViewHolder> {

    private List<Integer> numeros;
    private List<Integer> numerosSeleccionados;
    private List<Integer> numerosComprados; // <-- NUEVO
    private OnNumeroClickListener listener;

    public interface OnNumeroClickListener {
        void onNumeroClick(List<Integer> numerosSeleccionados);
    }

    public NumerosAdapter(List<Integer> numeros, List<Integer> numerosComprados, OnNumeroClickListener listener) {
        this.numeros = numeros != null ? numeros : new ArrayList<>();
        this.numerosComprados = numerosComprados != null ? numerosComprados : new ArrayList<>();
        this.listener = listener;
        this.numerosSeleccionados = new ArrayList<>();
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
            Log.d("NUMEROS", "numero=" + numero + " | numerosComprados=" + numerosComprados.toString());

            boolean estaComprado = false;
            for (Object obj : numerosComprados) {
                if (obj != null && Long.valueOf(numero).equals(obj)) {
                    estaComprado = true;
                    break;
                }
            }

            if (estaComprado) {
                Log.println(Log.INFO, "NUMERO COMPRADO (ADAPTER)", "ENTRA AQUI CON EL NUMERO " + numero);
                btnNumero.setEnabled(false);
                btnNumero.setBackgroundColor(Color.LTGRAY);
                btnNumero.setTextColor(Color.WHITE);
                return;
            }

            // si NO está comprado, sí se puede seleccionar
            btnNumero.setEnabled(true);
            btnNumero.setChecked(numerosSeleccionados.contains(numero));

            btnNumero.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) return;

                if (numerosSeleccionados.contains(numero)) {
                    numerosSeleccionados.remove(Integer.valueOf(numero));
                } else {
                    numerosSeleccionados.add(numero);
                }
                notifyItemChanged(pos);
                listener.onNumeroClick(numerosSeleccionados);
            });
        }
    }
}
