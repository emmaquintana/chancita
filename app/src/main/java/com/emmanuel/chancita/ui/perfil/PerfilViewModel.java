package com.emmanuel.chancita.ui.perfil;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emmanuel.chancita.data.model.Usuario;
import com.emmanuel.chancita.data.repository.UsuarioRepository;

public class PerfilViewModel extends ViewModel {
    private final UsuarioRepository usuarioRepository;
    private final MutableLiveData<Boolean> _estaActualizando = new MutableLiveData<>();
    public LiveData<Boolean> estaActualizando = _estaActualizando;
    private final MutableLiveData<String> _resultadoActualizacion = new MutableLiveData<>();
    public LiveData<String> resultadoActualizacion = _resultadoActualizacion;

    public PerfilViewModel() {
        usuarioRepository = new UsuarioRepository();
    }

    public LiveData<Usuario> obtenerUsuarioActual() {
        return usuarioRepository.obtenerUsuarioActual();
    }

    public void actualizarUsuarioActual(String nombre, String apellido, String correo, String nroCelular) {
        _estaActualizando.setValue(true);

        usuarioRepository.actualizarUsuarioActual(nombre, apellido, correo, nroCelular, task -> {
            _estaActualizando.postValue(false);
            if (task.isSuccessful()) {
                _resultadoActualizacion.postValue("Perfil actualizado con éxito");
            } else {
                _resultadoActualizacion.postValue("Error al actualizar el perfil: " + task.getException().getMessage());
            }
        });
    }
}