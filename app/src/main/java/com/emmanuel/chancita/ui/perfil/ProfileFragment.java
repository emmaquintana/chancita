package com.emmanuel.chancita.ui.perfil;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.data.model.Usuario;
import com.emmanuel.chancita.ui.SharedViewModel;
import com.emmanuel.chancita.utils.Utilidades;
import com.google.android.material.button.MaterialButton;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ProfileFragment extends Fragment {

    private ProfileViewModel mViewModel;
    private SharedViewModel sharedViewModel;
    private Usuario usuario;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtiene la instancia del ViewModel compartida con la MainActivity
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        usuario = obtenerUsuario("a6sdta7-2873ff-a87sdbs");
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

        nombreApellido.setText(usuario.getNombre() + " " + usuario.getApellido());
        edad.setText(Utilidades.calcularEdad(usuario.getFechaNacimiento()) + " años");
        correoElectronico.setText(usuario.getCorreo());

        MaterialButton btnEditarPerfil = view.findViewById(R.id.perfil_btn_editar);

        btnEditarPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), EditarPerfilActivity.class);
            startActivity(intent);
        });
    }

    private Usuario obtenerUsuario(String id) {
        // Usuario de prueba (debería ser una llamada a Firestore)
        Usuario usuario = new Usuario(id, "juanperez@gmail.com", "juan", "perez", "+5493854877069", "123456", LocalDate.of(2003, 07, 03), LocalDateTime.now(), null);

        return usuario;
    }
}