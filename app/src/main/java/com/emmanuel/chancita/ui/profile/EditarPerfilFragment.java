package com.emmanuel.chancita.ui.profile;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.ui.SharedViewModel;
import com.google.android.material.button.MaterialButton;

public class EditarPerfilFragment extends Fragment {

    private EditarPerfilViewModel mViewModel;

    public static EditarPerfilFragment newInstance() {
        return new EditarPerfilFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Se deben recuperar los datos del usuario actual con tal de presentar los campos pre rellenados
        // ...
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_editar_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        MaterialButton btnGuardarCambios = view.findViewById(R.id.btn_guardar_cambios_editar_perfil);

        btnGuardarCambios.setOnClickListener(v -> {
            // Guardar cambios
            // ...

            // Finaliza la actividad actual, volviendo "hacia atrás"
            requireActivity().finish();
        });
    }
}