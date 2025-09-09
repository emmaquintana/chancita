package com.emmanuel.chancita.ui.restablecer_contraseña;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

public class RestablecerContraseñaViewModel extends ViewModel {
    private final FirebaseAuth firebaseAuth;
    private final MutableLiveData<Boolean> _estaEnviando = new MutableLiveData<>();
    public LiveData<Boolean> estaEnviando = _estaEnviando;
    private final MutableLiveData<Boolean> _envioExitoso = new MutableLiveData<>();
    public LiveData<Boolean> envioExitoso = _envioExitoso;

    public RestablecerContraseñaViewModel() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.setLanguageCode("es");
    }

    /**
     * Actualiza la contraseña del usuario mediante un mail que se le manda
     *
     * @param email El mail del usuario
     * */
    public void restablecerContraseña(String email) {
        _estaEnviando.setValue(true);

        firebaseAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        _estaEnviando.setValue(false);
                        if (task.isSuccessful()) {
                            _envioExitoso.setValue(true);
                        } else {
                            _envioExitoso.setValue(false);
                        }
                    });
    }
}