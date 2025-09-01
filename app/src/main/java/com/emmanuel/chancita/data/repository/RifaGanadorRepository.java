package com.emmanuel.chancita.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.emmanuel.chancita.data.dao.RifaGanadorDAO;
import com.emmanuel.chancita.data.dto.RifaGanadorDTO;
import com.emmanuel.chancita.data.model.RifaGanador;
import com.google.android.gms.tasks.OnCompleteListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RifaGanadorRepository {
    private final RifaGanadorDAO rifaGanadorDAO;

    public RifaGanadorRepository() {
        this.rifaGanadorDAO = new RifaGanadorDAO();
    }

    /**
     * Asigna un ganador a un premio específico
     */
    public void asignarGanador(RifaGanadorDTO ganadorDto, OnCompleteListener<Void> listener) {
        RifaGanador nuevoGanador = new RifaGanador(
                null, // El ID se generará en el DAO
                ganadorDto.getRifaId(),
                ganadorDto.getPremioId(),
                ganadorDto.getUsuarioId(),
                ganadorDto.getNumeroId(),
                ganadorDto.getAsignadoEn()
        );

        rifaGanadorDAO.asignarGanador(nuevoGanador, listener);
    }

    /**
     * Obtiene todos los ganadores de una rifa
     */
    public LiveData<List<RifaGanadorDTO>> obtenerGanadoresPorRifa(String rifaId) {
        MutableLiveData<List<RifaGanadorDTO>> liveData = new MutableLiveData<>();

        rifaGanadorDAO.obtenerGanadoresPorRifa(rifaId, task -> {
            if (task.isSuccessful()) {
                List<RifaGanadorDTO> ganadores = task.getResult().getDocuments().stream()
                        .map(document -> {
                            RifaGanador ganador = document.toObject(RifaGanador.class);
                            return new RifaGanadorDTO(
                                    ganador.getRifaId(),
                                    ganador.getPremioId(),
                                    ganador.getUsuarioId(),
                                    ganador.getNumeroId(),
                                    ganador.getAsignadoEn()
                            );
                        })
                        .collect(Collectors.toList());
                liveData.setValue(ganadores);
            } else {
                liveData.setValue(new ArrayList<>());
            }
        });

        return liveData;
    }

    /**
     * Obtiene los premios ganados por un usuario
     */
    public LiveData<List<RifaGanadorDTO>> obtenerGanadoresPorUsuario(String usuarioId) {
        MutableLiveData<List<RifaGanadorDTO>> liveData = new MutableLiveData<>();

        rifaGanadorDAO.obtenerGanadoresPorUsuario(usuarioId, task -> {
            if (task.isSuccessful()) {
                List<RifaGanadorDTO> ganadores = task.getResult().getDocuments().stream()
                        .map(document -> {
                            RifaGanador ganador = document.toObject(RifaGanador.class);
                            return new RifaGanadorDTO(
                                    ganador.getRifaId(),
                                    ganador.getPremioId(),
                                    ganador.getUsuarioId(),
                                    ganador.getNumeroId(),
                                    ganador.getAsignadoEn()
                            );
                        })
                        .collect(Collectors.toList());
                liveData.setValue(ganadores);
            } else {
                liveData.setValue(new ArrayList<>());
            }
        });

        return liveData;
    }
}
