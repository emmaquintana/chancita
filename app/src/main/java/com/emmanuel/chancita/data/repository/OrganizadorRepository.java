package com.emmanuel.chancita.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.emmanuel.chancita.data.dao.OrganizadorDAO;
import com.emmanuel.chancita.data.dto.OrganizadorDTO;
import com.emmanuel.chancita.data.model.Organizador;
import com.google.android.gms.tasks.OnCompleteListener;

import java.time.LocalDateTime;

public class OrganizadorRepository {
    private final OrganizadorDAO organizadorDAO;

    public OrganizadorRepository() {
        this.organizadorDAO = new OrganizadorDAO();
    }

    /**
     * Registra un nuevo organizador con tokens de Mercado Pago
     */
    public void crearOrganizador(OrganizadorDTO organizadorDto, OnCompleteListener<Void> listener) {
        Organizador nuevoOrganizador = new Organizador(
                organizadorDto.getUsuarioId(),
                organizadorDto.getMpUserid(),
                organizadorDto.getMpTokenAcceso(),
                organizadorDto.getTokenRefresh(),
                organizadorDto.getExpiraEn(),
                organizadorDto.getMpScope(),
                LocalDateTime.now()
        );

        organizadorDAO.crearOrganizador(nuevoOrganizador, listener);
    }

    /**
     * Obtiene información del organizador
     */
    public LiveData<OrganizadorDTO> obtenerOrganizador(String usuarioId) {
        MutableLiveData<OrganizadorDTO> liveData = new MutableLiveData<>();

        organizadorDAO.obtenerOrganizador(usuarioId, task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                Organizador organizador = task.getResult().toObject(Organizador.class);
                if (organizador != null) {
                    OrganizadorDTO organizadorDTO = new OrganizadorDTO(
                            organizador.getUsuarioId(),
                            organizador.getMpUserid(),
                            organizador.getMpTokenAcceso(),
                            organizador.getTokenRefresh(),
                            organizador.getExpiraEn(),
                            organizador.getMpScope()
                    );
                    liveData.setValue(organizadorDTO);
                } else {
                    liveData.setValue(null);
                }
            } else {
                liveData.setValue(null);
            }
        });

        return liveData;
    }

    /**
     * Actualiza los tokens de acceso de Mercado Pago
     */
    public void actualizarTokens(String usuarioId, String tokenAcceso, String tokenRefresh,
                                 LocalDateTime expiraEn, OnCompleteListener<Void> listener) {
        organizadorDAO.actualizarTokens(usuarioId, tokenAcceso, tokenRefresh, expiraEn, listener);
    }
}
