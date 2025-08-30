package com.emmanuel.chancita.ui.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.data.model.Usuario;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;

public class EditarPerfilFragment extends Fragment {

    private EditarPerfilViewModel mViewModel;
    private Usuario usuario;

    public static EditarPerfilFragment newInstance() {
        return new EditarPerfilFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Se deben recuperar los datos del usuario actual con tal de presentar los campos pre rellenados
        usuario = obtenerUsuario("id");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_editar_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        TextInputEditText nombreCompleto = view.findViewById(R.id.editar_perfil_tiet_nombre_completo);
        TextInputEditText apellidoCompleto = view.findViewById(R.id.editar_perfil_tiet_apellido_completo);
        TextInputEditText correoElectronico = view.findViewById(R.id.editar_perfil_tiet_email);
        TextInputEditText nroCelular = view.findViewById(R.id.editar_perfil_tiet_nro_celular);

        nombreCompleto.setText(usuario.getNombre());
        apellidoCompleto.setText(usuario.getApellido());
        correoElectronico.setText(usuario.getCorreo());
        nroCelular.setText(usuario.getNroCelular());

        MaterialButton btnGuardarCambios = view.findViewById(R.id.editar_perfil_btn_guardar_cambios);

        btnGuardarCambios.setOnClickListener(v -> {
            // Guardar cambios
            // ...

            // Finaliza la actividad actual, volviendo "hacia atrás"
            requireActivity().finish();
        });
    }

    private Usuario obtenerUsuario(String id) {
        // Usuario de prueba (debería ser una llamada a Firestore)
        Usuario usuario = new Usuario(id, "juanperez@gmail.com", "juan", "perez", "+5493854877069", "123456", LocalDate.of(2003, 07, 03),LocalDate.now(), null);

        return usuario;
    }
}