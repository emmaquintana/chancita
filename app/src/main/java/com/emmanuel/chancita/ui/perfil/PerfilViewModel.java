package com.emmanuel.chancita.ui.perfil;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emmanuel.chancita.data.dto.UsuarioDTO;
import com.emmanuel.chancita.data.model.Usuario;
import com.emmanuel.chancita.data.repository.UsuarioRepository;

public class PerfilViewModel extends ViewModel {
    private final UsuarioRepository usuarioRepository = new UsuarioRepository();
    private final LiveData<Usuario> usuarioActual;

    public PerfilViewModel() {
        usuarioActual = usuarioRepository.obtenerUsuarioActual();
    }

    public LiveData<Usuario> getUsuarioActual() {
        return usuarioActual;
    }
}