package com.emmanuel.chancita.ui.perfil;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.ui.SharedViewModel;
import com.emmanuel.chancita.utils.Utilidades;
import com.google.android.material.button.MaterialButton;

public class PerfilFragment extends Fragment {

    private PerfilViewModel perfilViewModel;
    private SharedViewModel sharedViewModel;

    public static PerfilFragment newInstance() {
        return new PerfilFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtiene la instancia del ViewModel compartida con la MainActivity
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        perfilViewModel = new ViewModelProvider(requireActivity()).get(PerfilViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        sharedViewModel.setToolbarTitle("Perfil");

        TextView nombreApellido = view.findViewById(R.id.perfil_txt_nombre_completo);
        TextView edad = view.findViewById(R.id.perfil_txt_edad);
        TextView correoElectronico = view.findViewById(R.id.perfil_txt_email);
        TextView numeroCelular = view.findViewById(R.id.perfil_txt_celular);
        MaterialButton btnEditarPerfil = view.findViewById(R.id.perfil_btn_editar);

        perfilViewModel.getUsuarioActual().observe(getViewLifecycleOwner(), usuario -> {
            if (usuario != null) {
                // Actualizar los TextViews con los datos del usuario
                nombreApellido.setText(usuario.getNombre() + " " + usuario.getApellido());
                edad.setText(Utilidades.calcularEdad(usuario.getFechaNacimiento()) + " años");
                correoElectronico.setText(usuario.getCorreo());
                numeroCelular.setText(usuario.getNroCelular());
            }
            else {
                Log.println(Log.WARN, "WARN", "USUARIO NULO");
            }
        });

        btnEditarPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), EditarPerfilActivity.class);
            startActivity(intent);
        });
    }
}