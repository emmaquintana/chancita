package com.emmanuel.chancita.ui.restablecer_contraseña;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RestablecerContraseñaViewModel extends ViewModel {
    private final FirebaseAuth firebaseAuth;
    private final MutableLiveData<Boolean> _estaRestableciendo = new MutableLiveData<>();
    public LiveData<Boolean> estaRestableciendo = _estaRestableciendo;
    private final MutableLiveData<String> _resultadoRestablecimiento = new MutableLiveData<>();
    public LiveData<String> resultadoRestablecimiento = _resultadoRestablecimiento;
    private final MutableLiveData<Boolean> _restablecimientoExitoso = new MutableLiveData<>();
    public LiveData<Boolean> restablecimientoExitoso = _restablecimientoExitoso;

    public RestablecerContraseñaViewModel() {
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    /**
     * Valida y actualiza la contraseña del usuario.
     *
     * @param newPassword La nueva contraseña.
     * @param confirmPassword La confirmación de la nueva contraseña.
     */
    public void restablecerContraseña(String newPassword, String confirmPassword) {
        _estaRestableciendo.setValue(true);
        _restablecimientoExitoso.setValue(false);

        if (!validarContraseñas(newPassword, confirmPassword)) {
            _estaRestableciendo.setValue(false);
            return;
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            user.updatePassword(newPassword)
                    .addOnCompleteListener(task -> {
                        _estaRestableciendo.setValue(false);
                        if (task.isSuccessful()) {
                            _resultadoRestablecimiento.setValue("Contraseña restablecida exitosamente.");
                            _restablecimientoExitoso.setValue(true);
                        } else {
                            _resultadoRestablecimiento.setValue("Error al restablecer la contraseña. Por favor, vuelva a iniciar sesión.");
                        }
                    });
        } else {
            _estaRestableciendo.setValue(false);
            _resultadoRestablecimiento.setValue("No se encontró un usuario autenticado.");
        }
    }

    private boolean validarContraseñas(String newPassword, String confirmPassword) {
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            _resultadoRestablecimiento.setValue("Todos los campos son obligatorios.");
            return false;
        }

        if (newPassword.length() < 6) {
            _resultadoRestablecimiento.setValue("La contraseña debe tener al menos 6 caracteres.");
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            _resultadoRestablecimiento.setValue("Las contraseñas no coinciden.");
            return false;
        }

        return true;
    }
}