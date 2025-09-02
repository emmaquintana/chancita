package com.emmanuel.chancita.ui.crear_cuenta;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emmanuel.chancita.data.dto.UsuarioDTO;
import com.emmanuel.chancita.data.repository.UsuarioRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import java.time.LocalDate;

public class CrearCuentaViewModel extends ViewModel {
    private final UsuarioRepository usuarioRepository;
    private final FirebaseAuth firebaseAuth;
    private final MutableLiveData<Boolean> _estaRegistrandose = new MutableLiveData<>();
    public LiveData<Boolean> estaRegistrandose = _estaRegistrandose;
    private final MutableLiveData<String> _resultadoRegistro = new MutableLiveData<>();
    public LiveData<String> resultadoRegistro = _resultadoRegistro;

    public CrearCuentaViewModel() {
        this.usuarioRepository = new UsuarioRepository();
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    /**
     * Valida los datos del formulario y crea un nuevo usuario.
     *
     * @param nombre El nombre del usuario.
     * @param apellido El apellido del usuario.
     * @param correo El correo electrónico del usuario.
     * @param fechaNacimiento La fecha de nacimiento del usuario.
     * @param nroCelular El número de celular del usuario.
     * @param contraseña La contraseña del usuario.
     * @param confirmarContraseña La confirmación de la contraseña.
     */
    public void crearUsuario(String nombre, String apellido, String correo, LocalDate fechaNacimiento, String nroCelular, String contraseña, String confirmarContraseña) {
        _estaRegistrandose.setValue(true);

        if (!validarEntrada(nombre, apellido, correo, fechaNacimiento, nroCelular, contraseña, confirmarContraseña)) {
            _estaRegistrandose.setValue(false);
            return;
        }

        // Crear usuario en Firebase Authentication
        firebaseAuth.createUserWithEmailAndPassword(correo, contraseña)
                .addOnCompleteListener(authTask -> {
                    if (authTask.isSuccessful()) {
                        // Si la autenticación es exitosa, guardar el usuario en el repositorio
                        UsuarioDTO usuarioDto = new UsuarioDTO(nombre, apellido, correo, nroCelular, contraseña, fechaNacimiento, null, null);
                        usuarioRepository.crearUsuario(usuarioDto, repoTask -> {
                            _estaRegistrandose.setValue(false);
                            if (repoTask.isSuccessful()) {
                                _resultadoRegistro.setValue("Registro exitoso.");
                            } else {
                                // Si falla la creación en el repositorio, se elimina el usuario de autenticación
                                firebaseAuth.getCurrentUser().delete();
                                _resultadoRegistro.setValue("Error al guardar los datos del usuario.");
                            }
                        });
                    } else {
                        _estaRegistrandose.setValue(false);
                        if (authTask.getException() instanceof FirebaseAuthUserCollisionException) {
                            _resultadoRegistro.setValue("Ya existe una cuenta con este correo electrónico.");
                        } else {
                            _resultadoRegistro.setValue("Error en el registro. Intente de nuevo.");
                        }
                    }
                });
    }

    private boolean validarEntrada(String nombre, String apellido, String correo, LocalDate fechaNacimiento, String nroCelular, String contraseña, String confirmarContraseña) {
        // Implementar lógica de validación aquí
        if (nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() || fechaNacimiento == null || nroCelular.isEmpty() || contraseña.isEmpty() || confirmarContraseña.isEmpty()) {
            _resultadoRegistro.setValue("Todos los campos son obligatorios.");
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            _resultadoRegistro.setValue("El formato del correo electrónico no es válido.");
            return false;
        }

        if (contraseña.length() < 6) {
            _resultadoRegistro.setValue("La contraseña debe tener al menos 6 caracteres.");
            return false;
        }

        if (!contraseña.equals(confirmarContraseña)) {
            _resultadoRegistro.setValue("Las contraseñas no coinciden.");
            return false;
        }

        return true;
    }
}