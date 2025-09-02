package com.emmanuel.chancita.ui.perfil;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.emmanuel.chancita.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class EditarPerfilFragment extends Fragment {

    private PerfilViewModel perfilViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        perfilViewModel = new ViewModelProvider(requireActivity()).get(PerfilViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar_perfil, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        MaterialButton btnGuardarCambios = view.findViewById(R.id.editar_perfil_btn_guardar_cambios);
        TextInputEditText tietNombreCompleto = view.findViewById(R.id.editar_perfil_tiet_nombre_completo);
        TextInputEditText tietApellidoCompleto = view.findViewById(R.id.editar_perfil_tiet_apellido_completo);
        TextInputEditText tietCorreoElectronico = view.findViewById(R.id.editar_perfil_tiet_email);
        TextInputEditText tietNroCelular = view.findViewById(R.id.editar_perfil_tiet_nro_celular);

        perfilViewModel.obtenerUsuarioActual().observe(getViewLifecycleOwner(), usuario -> {
            tietNombreCompleto.setText(usuario.getNombre());
            tietApellidoCompleto.setText(usuario.getApellido());
            tietCorreoElectronico.setText(usuario.getCorreo());
            tietNroCelular.setText(usuario.getNroCelular());
        });

        btnGuardarCambios.setOnClickListener(v -> {
            String nombre = tietNombreCompleto.getText().toString();
            String apellido = tietApellidoCompleto.getText().toString();
            String correo = tietCorreoElectronico.getText().toString();
            String numCelular = tietNroCelular.getText().toString();

            perfilViewModel.actualizarUsuarioActual(nombre, apellido, correo, numCelular);
        });

        perfilViewModel.resultadoActualizacion.observe(getViewLifecycleOwner(), mensaje -> {
            Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show();
        });
    }
}