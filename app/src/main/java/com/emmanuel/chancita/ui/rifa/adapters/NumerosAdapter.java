package com.emmanuel.chancita.ui.rifa.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emmanuel.chancita.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class NumerosAdapter extends RecyclerView.Adapter<NumerosAdapter.ViewHolder> {

    private List<Integer> mNumeros;
    private OnNumeroClickListener mListener;

    public interface OnNumeroClickListener {
        void onNumeroClick(int numero);
    }

    public NumerosAdapter(List<Integer> numeros, OnNumeroClickListener listener) {
        mNumeros = numeros;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_numero, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int numero = mNumeros.get(position);
        holder.btnNumero.setText(String.valueOf(numero));

        holder.btnNumero.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onNumeroClick(numero);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNumeros.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public MaterialButton btnNumero;

        public ViewHolder(View itemView) {
            super(itemView);
            btnNumero = itemView.findViewById(R.id.layout_btn_numero);
        }
    }
}