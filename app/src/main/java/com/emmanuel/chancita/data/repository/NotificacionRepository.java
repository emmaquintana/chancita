package com.emmanuel.chancita.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.emmanuel.chancita.data.dao.NotificacionDAO;
import com.emmanuel.chancita.data.dto.NotificacionDTO;
import com.emmanuel.chancita.data.model.Notificacion;
import com.google.android.gms.tasks.OnCompleteListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NotificacionRepository {
    private final NotificacionDAO notificacionDAO;

    public NotificacionRepository() {
        this.notificacionDAO = new NotificacionDAO();
    }

    /**
     * Crea una nueva notificación para un usuario específico
     */
    public void crearNotificacion(NotificacionDTO notificacionDto, OnCompleteListener<Void> listener) {
        Notificacion nuevaNotificacion = new Notificacion(
                null, // El ID se generará en el DAO
                notificacionDto.getFecha(),
                notificacionDto.getTitulo(),
                notificacionDto.getCuerpo(),
                notificacionDto.getUsuarioId()
        );

        notificacionDAO.crearNotificacion(nuevaNotificacion, listener);
    }

    /**
     * Crea una notificación general (sin usuario específico)
     */
    public void crearNotificacionGeneral(NotificacionDTO notificacionDto, OnCompleteListener<Void> listener) {
        Notificacion nuevaNotificacion = new Notificacion(
                notificacionDto.getFecha(),
                notificacionDto.getTitulo(),
                notificacionDto.getCuerpo()
        );

        notificacionDAO.crearNotificacion(nuevaNotificacion, listener);
    }

    /**
     * Obtiene las notificaciones de un usuario específico
     */
    public LiveData<List<NotificacionDTO>> obtenerNotificacionesPorUsuario(String usuarioId) {
        MutableLiveData<List<NotificacionDTO>> liveData = new MutableLiveData<>();

        notificacionDAO.obtenerNotificacionesPorUsuario(usuarioId, task -> {
            if (task.isSuccessful()) {
                List<NotificacionDTO> notificaciones = task.getResult().getDocuments().stream()
                        .map(document -> {
                            Notificacion notificacion = document.toObject(Notificacion.class);
                            return new NotificacionDTO(
                                    notificacion.getFecha(),
                                    notificacion.getTitulo(),
                                    notificacion.getCuerpo(),
                                    notificacion.getUsuarioId()
                            );
                        })
                        .collect(Collectors.toList());
                liveData.setValue(notificaciones);
            } else {
                liveData.setValue(new ArrayList<>());
            }
        });

        return liveData;
    }
}
