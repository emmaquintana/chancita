package com.emmanuel.chancita.ui.iniciar_sesion;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

public class InicioSesionViewModel extends ViewModel {
    private final FirebaseAuth firebaseAuth;
    private final MutableLiveData<Boolean> _estaIniciandoSesion = new MutableLiveData<>();
    public LiveData<Boolean> estaIniciandoSesion = _estaIniciandoSesion;
    private final MutableLiveData<String> _resultadoInicioSesion = new MutableLiveData<>();
    public LiveData<String> resultadoInicioSesion = _resultadoInicioSesion;
    private final MutableLiveData<Boolean> _inicioSesionExitoso = new MutableLiveData<>();
    public LiveData<Boolean> inicioSesionExitoso = _inicioSesionExitoso;

    public InicioSesionViewModel() {
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    /**
     * Inicia sesión con el correo electrónico y la contraseña proporcionados.
     *
     * @param email El correo electrónico del usuario.
     * @param password La contraseña del usuario.
     */
    public void iniciarSesion(String email, String password) {
        _estaIniciandoSesion.setValue(true);
        _inicioSesionExitoso.setValue(false);

        if (email.isEmpty() || password.isEmpty()) {
            _resultadoInicioSesion.setValue("Por favor, ingrese su correo y contraseña.");
            _estaIniciandoSesion.setValue(false);
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    _estaIniciandoSesion.setValue(false);
                    if (task.isSuccessful()) {
                        _inicioSesionExitoso.setValue(true);
                    } else {
                        _inicioSesionExitoso.setValue(false);
                        _resultadoInicioSesion.setValue("Error al iniciar sesión. Verifique su correo y contraseña.");
                    }
                });
    }
}