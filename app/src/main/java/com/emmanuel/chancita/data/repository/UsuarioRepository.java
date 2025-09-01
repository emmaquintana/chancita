package com.emmanuel.chancita.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.emmanuel.chancita.data.dao.OrganizadorDAO;
import com.emmanuel.chancita.data.dao.RifaDAO;
import com.emmanuel.chancita.data.dao.UsuarioDAO;
import com.emmanuel.chancita.data.dto.UsuarioDTO;
import com.emmanuel.chancita.data.model.Organizador;
import com.emmanuel.chancita.data.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UsuarioRepository {
    private final UsuarioDAO usuarioDAO;
    private final RifaDAO rifaDAO;
    private final OrganizadorDAO organizadorDAO;

    public UsuarioRepository() {
        this.usuarioDAO = new UsuarioDAO();
        this.rifaDAO = new RifaDAO();
        this.organizadorDAO = new OrganizadorDAO();
    }

    /**
     * Obtiene un usuario en base a su ID
     *
     * @param userId La ID del usuario cuya info se quiere obtener
     * @return LiveData Un LiveData con los datos del usuario
     * */
    public LiveData<Usuario> getUsuario(String userId) {
        MutableLiveData<Usuario> liveData = new MutableLiveData<>();
        usuarioDAO.getUsuario(userId, task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                Usuario usuario = task.getResult().toObject(Usuario.class);
                liveData.setValue(usuario);
            } else {
                liveData.setValue(null);
            }
        });
        return liveData;
    }

    /**
     * Crea un nuevo usuario en la BD a partir de un UsuarioDTO.<br/>
     * El DTO solo contiene los datos que el usuario ingresó en el formulario.<br/>
     *
     * @param usuarioDto Una referencia al objeto UsuarioDTO con los datos que el usuario ingresó
     * @param listener Listener para manejar el resultado de la operación
     * */
    public void crearUsuario(UsuarioDTO usuarioDto, OnCompleteListener<Void> listener) {
        // Mapear el DTO a la entidad de la base de datos (modelo completo)
        Usuario nuevoUsuario = new Usuario(
                null, // El ID se generará en el DAO
                usuarioDto.getCorreo(),
                usuarioDto.getNombre(),
                usuarioDto.getApellido(),
                usuarioDto.getNroCelular(),
                usuarioDto.getContraseña(),
                usuarioDto.getFechaNacimiento(), // Se establece la fecha de creación en el repositorio
                LocalDateTime.now(), // El último ingreso es nulo al crearse
                null
        );

        // Llamar al DAO para guardar la entidad completa
        usuarioDAO.crearUsuario(nuevoUsuario, listener);
    }

    public void eliminarUsuario(String usuarioId, OnCompleteListener<Void> listener) {
        usuarioDAO.eliminarUsuario(usuarioId, listener);
    }

    public LiveData<List<UsuarioDTO>> obtenerParticipantes(String rifaId) {
        MutableLiveData<List<UsuarioDTO>> liveData = new MutableLiveData<>();

        rifaDAO.obtenerNumerosPorRifa(rifaId, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<String> participanteIds = task.getResult().getDocuments().stream()
                        .map(document -> document.getString("usuarioId"))
                        .collect(Collectors.toList());

                if (!participanteIds.isEmpty()) {
                    usuarioDAO.obtenerParticipantes(participanteIds, userTask -> {
                        if (userTask.isSuccessful() && userTask.getResult() != null) {
                            List<UsuarioDTO> participantesDTO = userTask.getResult().getDocuments().stream()
                                    .map(document -> {
                                        Usuario usuario = document.toObject(Usuario.class);
                                        return new UsuarioDTO(usuario.getNombre(), usuario.getApellido(), usuario.getCorreo(), usuario.getNroCelular(), usuario.getContraseña(), usuario.getFechaNacimiento(), usuario.getCreadoEn(), usuario.getUltimoIngreso());
                                    })
                                    .collect(Collectors.toList());
                            liveData.setValue(participantesDTO);
                        } else {
                            liveData.setValue(new ArrayList<>());
                        }
                    });
                } else {
                    liveData.setValue(new ArrayList<>());
                }
            } else {
                liveData.setValue(null);
            }
        });
        return liveData;
    }

    public LiveData<UsuarioDTO> obtenerOrganizador(String rifaId) {
        MutableLiveData<UsuarioDTO> liveData = new MutableLiveData<>();

        organizadorDAO.obtenerOrganizadorPorRifaId(rifaId, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Organizador organizador = task.getResult().toObject(Organizador.class);
                if (organizador != null) {
                    usuarioDAO.obtenerOrganizador(organizador.getUsuarioId(), userTask -> {
                        if (userTask.isSuccessful() && userTask.getResult() != null) {
                            Usuario usuario = userTask.getResult();
                            UsuarioDTO organizadorDTO = new UsuarioDTO(usuario.getNombre(), usuario.getApellido(), usuario.getCorreo(), usuario.getNroCelular(), usuario.getContraseña(), usuario.getFechaNacimiento(), LocalDateTime.now(), null);
                            liveData.setValue(organizadorDTO);
                        } else {
                            liveData.setValue(null);
                        }
                    });
                } else {
                    liveData.setValue(null);
                }
            } else {
                liveData.setValue(null);
            }
        });
        return liveData;
    }
}
