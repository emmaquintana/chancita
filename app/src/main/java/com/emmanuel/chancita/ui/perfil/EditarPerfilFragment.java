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

    // Vistas
    private MaterialButton btnGuardarCambios;
    private TextInputEditText tietNombreCompleto;
    private TextInputEditText tietApellidoCompleto;
    private TextInputEditText tietCorreoElectronico;
    private TextInputEditText tietNroCelular;

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
        btnGuardarCambios = view.findViewById(R.id.editar_perfil_btn_guardar_cambios);
        tietNombreCompleto = view.findViewById(R.id.editar_perfil_tiet_nombre_completo);
        tietApellidoCompleto = view.findViewById(R.id.editar_perfil_tiet_apellido_completo);
        tietCorreoElectronico = view.findViewById(R.id.editar_perfil_tiet_email);
        tietNroCelular = view.findViewById(R.id.editar_perfil_tiet_nro_celular);

        setearObservers();
        setearListeners();
    }

    private void setearObservers() {
        perfilViewModel.obtenerUsuarioActual().observe(getViewLifecycleOwner(), usuario -> {
            tietNombreCompleto.setText(usuario.getNombre());
            tietApellidoCompleto.setText(usuario.getApellido());
            tietCorreoElectronico.setText(usuario.getCorreo());
            tietNroCelular.setText(usuario.getNroCelular());
        });

        perfilViewModel.resultadoActualizacion.observe(getViewLifecycleOwner(), mensaje -> {
            Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show();
        });
    }

    private void setearListeners() {
        btnGuardarCambios.setOnClickListener(v -> {
            String nombre = tietNombreCompleto.getText().toString();
            String apellido = tietApellidoCompleto.getText().toString();
            String correo = tietCorreoElectronico.getText().toString();
            String numCelular = tietNroCelular.getText().toString();

            perfilViewModel.actualizarUsuarioActual(nombre, apellido, correo, numCelular);
        });
    }
}