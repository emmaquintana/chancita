package com.emmanuel.chancita.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.emmanuel.chancita.data.dao.UsuarioDAO;
import com.emmanuel.chancita.data.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UsuarioRepository {
    private final UsuarioDAO usuarioDAO;

    public UsuarioRepository() {
        this.usuarioDAO = new UsuarioDAO();
    }

    /**
     * Obtiene un usuario en base a su ID
     *
     * @param userId La ID del usuario cuya info se quiere obtener
     * @return LiveData Un LiveData con los datos del usuario
     * */
    public LiveData<Usuario> obtenerUsuario(String userId) {
        MutableLiveData<Usuario> liveData = new MutableLiveData<>();
        usuarioDAO.obtenerUsuario(userId)
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Usuario usuario = new Usuario();

                        usuario.setId(doc.getId());
                        usuario.setNombre(doc.getString("nombre"));
                        usuario.setApellido(doc.getString("apellido"));
                        usuario.setCorreo(doc.getString("correo"));
                        usuario.setNroCelular(doc.getString("nroCelular"));

                        String fechaNacimientoStr = doc.getString("fechaNacimiento");
                        LocalDate fechaNacimiento = LocalDate.parse(fechaNacimientoStr);

                        String creadoEnStr = doc.getString("creadoEn");
                        LocalDateTime creadoEn = LocalDateTime.parse(creadoEnStr);

                        String ultimoIngresoStr = doc.getString("ultimoIngreso");
                        LocalDateTime ultimoIngreso = ultimoIngresoStr != null ? LocalDateTime.parse(ultimoIngresoStr) : null;

                        usuario.setFechaNacimiento(fechaNacimiento);
                        usuario.setCreadoEn(creadoEn);
                        usuario.setUltimoIngreso(ultimoIngreso);

                        liveData.setValue(usuario);
                        Log.println(Log.INFO, "ENCONTRADO", "EL USUARIO ES ENCONTRADO EN DOCS");
                    } else {
                        Log.println(Log.ERROR, "NO ENCONTRADO", "EL USUARIO NO ES ENCONTRADO EN DOCS " + doc.getId());
                        liveData.setValue(null);
                    }
                })
                .addOnFailureListener(e -> liveData.setValue(null));
        return liveData;
    }

    public LiveData<Usuario> obtenerUsuarioActual() {
        MutableLiveData<Usuario> liveData = new MutableLiveData<>();
        usuarioDAO.obtenerUsuarioActual()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Usuario usuario = new Usuario();

                        usuario.setId(doc.getId());
                        usuario.setNombre(doc.getString("nombre"));
                        usuario.setApellido(doc.getString("apellido"));
                        usuario.setCorreo(doc.getString("correo"));
                        usuario.setNroCelular(doc.getString("nroCelular"));

                        String fechaNacimientoStr = doc.getString("fechaNacimiento");
                        LocalDate fechaNacimiento = LocalDate.parse(fechaNacimientoStr);

                        String creadoEnStr = doc.getString("creadoEn");
                        LocalDateTime creadoEn = LocalDateTime.parse(creadoEnStr);

                        String ultimoIngresoStr = doc.getString("ultimoIngreso");
                        LocalDateTime ultimoIngreso = ultimoIngresoStr != null ? LocalDateTime.parse(ultimoIngresoStr) : null;

                        usuario.setFechaNacimiento(fechaNacimiento);
                        usuario.setCreadoEn(creadoEn);
                        usuario.setUltimoIngreso(ultimoIngreso);

                        liveData.setValue(usuario);
                        Log.println(Log.INFO, "ENCONTRADO", "EL USUARIO ES ENCONTRADO EN DOCS");
                    } else {
                        Log.println(Log.ERROR, "NO ENCONTRADO", "EL USUARIO NO ES ENCONTRADO EN DOCS " + doc.getId());
                        liveData.setValue(null);
                    }
                })
                .addOnFailureListener(e -> liveData.setValue(null));
        return liveData;
    }

    public void crearUsuario(Usuario usuarioDto, OnCompleteListener<Void> listener) {
        // Mapear DTO a la entidad Usuario
        Usuario usuario = new Usuario(
                null,
                usuarioDto.getCorreo(),
                usuarioDto.getNombre(),
                usuarioDto.getApellido(),
                usuarioDto.getNroCelular(),
                usuarioDto.getContraseña(),
                usuarioDto.getFechaNacimiento(),
                LocalDateTime.now(),
                null
        );
        usuarioDAO.crearUsuario(usuario, listener);
    }

    public void actualizarUsuarioActual(String nombre, String apellido, String correo, String nroCelular,
                                        OnCompleteListener<Void> listener) {
        usuarioDAO.actualizarUsuarioActual(nombre, apellido, correo, nroCelular, listener);
    }
}
