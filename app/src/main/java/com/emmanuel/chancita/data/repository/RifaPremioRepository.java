package com.emmanuel.chancita.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.emmanuel.chancita.data.dao.RifaPremioDAO;
import com.emmanuel.chancita.data.dto.RifaPremioDTO;
import com.emmanuel.chancita.data.model.RifaPremio;
import com.google.android.gms.tasks.OnCompleteListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RifaPremioRepository {
    private final RifaPremioDAO rifaPremioDAO;

    public RifaPremioRepository() {
        this.rifaPremioDAO = new RifaPremioDAO();
    }

    /**
     * Crea un nuevo premio para una rifa
     */
    public void crearPremio(RifaPremioDTO premioDto, OnCompleteListener<Void> listener) {
        RifaPremio nuevoPremio = new RifaPremio(
                null, // El ID se generará en el DAO
                premioDto.getPremioTitulo(),
                premioDto.getPremioDescripcion(),
                premioDto.getRifaId(),
                premioDto.getPremioOrden()
        );

        rifaPremioDAO.crearPremio(nuevoPremio, listener);
    }

    /**
     * Obtiene todos los premios de una rifa ordenados por puesto
     */
    public LiveData<List<RifaPremioDTO>> obtenerPremiosPorRifa(String rifaId) {
        MutableLiveData<List<RifaPremioDTO>> liveData = new MutableLiveData<>();

        rifaPremioDAO.obtenerPremiosPorRifa(rifaId, task -> {
            if (task.isSuccessful()) {
                List<RifaPremioDTO> premios = task.getResult().getDocuments().stream()
                        .map(document -> {
                            RifaPremio premio = document.toObject(RifaPremio.class);
                            return new RifaPremioDTO(
                                    premio.getPremioTitulo(),
                                    premio.getPremioDescripcion(),
                                    premio.getRifaId(),
                                    premio.getPremioOrden()
                            );
                        })
                        .collect(Collectors.toList());
                liveData.setValue(premios);
            } else {
                liveData.setValue(new ArrayList<>());
            }
        });

        return liveData;
    }
}
