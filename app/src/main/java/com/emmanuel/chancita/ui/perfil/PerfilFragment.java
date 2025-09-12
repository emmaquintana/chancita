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
import com.emmanuel.chancita.data.model.Usuario;
import com.emmanuel.chancita.ui.SharedViewModel;
import com.emmanuel.chancita.utils.Utilidades;
import com.google.android.material.button.MaterialButton;

public class PerfilFragment extends Fragment {

    private PerfilViewModel perfilViewModel;
    private SharedViewModel sharedViewModel;
    private TextView nombreApellido;
    private TextView edad;
    private TextView correoElectronico;
    private TextView numeroCelular;
    private MaterialButton btnEditarPerfil;

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
    public void onResume() {
        super.onResume();
        perfilViewModel.obtenerUsuarioActual().observe(getViewLifecycleOwner(), usuario -> {
            if (usuario != null) {
                actualizarUI(usuario);
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        sharedViewModel.setToolbarTitle("Perfil");

        nombreApellido = view.findViewById(R.id.perfil_txt_nombre_completo);
        edad = view.findViewById(R.id.perfil_txt_edad);
        correoElectronico = view.findViewById(R.id.perfil_txt_email);
        numeroCelular = view.findViewById(R.id.perfil_txt_celular);
        btnEditarPerfil = view.findViewById(R.id.perfil_btn_editar);

        setearObservers();
        setearListeners();
    }

    private void setearObservers() {
        perfilViewModel.obtenerUsuarioActual().observe(getViewLifecycleOwner(), usuario -> {
            if (usuario != null) {
                actualizarUI(usuario);
            }
        });
    }

    private void setearListeners() {
        btnEditarPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), EditarPerfilActivity.class);
            startActivity(intent);
        });
    }

    private void actualizarUI(Usuario usuario) {
        nombreApellido.setText(usuario.getNombre() + " " + usuario.getApellido());
        edad.setText(Utilidades.calcularEdad(usuario.getFechaNacimiento()) + " años");
        correoElectronico.setText(usuario.getCorreo());
        numeroCelular.setText(usuario.getNroCelular());
    }
}