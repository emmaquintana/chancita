package com.emmanuel.chancita.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.emmanuel.chancita.data.dao.NumeroDAO;
import com.emmanuel.chancita.data.dao.RifaDAO;
import com.emmanuel.chancita.data.dao.RifaGanadorDAO;
import com.emmanuel.chancita.data.dao.RifaPremioDAO;
import com.emmanuel.chancita.data.dto.RifaDTO;
import com.emmanuel.chancita.data.model.Rifa;
import com.emmanuel.chancita.data.model.RifaEstado;
import com.google.android.gms.tasks.OnCompleteListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RifaRepository {
    private final RifaDAO rifaDAO;
    private final NumeroDAO numeroDAO;
    private final RifaPremioDAO rifaPremioDAO;
    private final RifaGanadorDAO rifaGanadorDAO;

    public RifaRepository() {
        this.rifaDAO = new RifaDAO();
        this.numeroDAO = new NumeroDAO();
        this.rifaPremioDAO = new RifaPremioDAO();
        this.rifaGanadorDAO = new RifaGanadorDAO();
    }

    /**
     * Crea una nueva rifa en la BD
     */
    public void crearRifa(RifaDTO rifaDto, OnCompleteListener<Void> listener) {
        Rifa nuevaRifa = new Rifa(
                null, // El ID se generará en el DAO
                rifaDto.getTitulo(),
                rifaDto.getDescripcion(),
                rifaDto.getCreadoPor(),
                rifaDto.getEstado(),
                rifaDto.getCreadoEn(),
                rifaDto.getCodigo(),
                rifaDto.getMetodoEleccionGanador(),
                rifaDto.getMotivoEleccionGanador(),
                rifaDto.getFechaSorteo(),
                rifaDto.getPrecioNumero()
        );

        rifaDAO.crearRifa(nuevaRifa, listener);
    }

    /**
     * Obtiene una rifa por su ID
     */
    public LiveData<RifaDTO> obtenerRifa(String rifaId) {
        MutableLiveData<RifaDTO> liveData = new MutableLiveData<>();

        rifaDAO.obtenerRifa(rifaId, task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                Rifa rifa = task.getResult().toObject(Rifa.class);
                if (rifa != null) {
                    RifaDTO rifaDTO = new RifaDTO(
                            rifa.getTitulo(),
                            rifa.getDescripcion(),
                            rifa.getCreadoPor(),
                            rifa.getEstado(),
                            rifa.getCodigo(),
                            rifa.getMetodoEleccionGanador(),
                            rifa.getMotivoEleccionGanador(),
                            rifa.getFechaSorteo(),
                            rifa.getPrecioNumero(),
                            rifa.getCreadoEn()
                    );
                    liveData.setValue(rifaDTO);
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
     * Obtiene las rifas creadas por un usuario
     */
    public LiveData<List<RifaDTO>> obtenerRifasPorUsuario(String usuarioId) {
        MutableLiveData<List<RifaDTO>> liveData = new MutableLiveData<>();

        rifaDAO.obtenerRifasPorUsuario(usuarioId, task -> {
            if (task.isSuccessful()) {
                List<RifaDTO> rifas = task.getResult().getDocuments().stream()
                        .map(document -> {
                            Rifa rifa = document.toObject(Rifa.class);
                            return new RifaDTO(
                                    rifa.getTitulo(),
                                    rifa.getDescripcion(),
                                    rifa.getCreadoPor(),
                                    rifa.getEstado(),
                                    rifa.getCodigo(),
                                    rifa.getMetodoEleccionGanador(),
                                    rifa.getMotivoEleccionGanador(),
                                    rifa.getFechaSorteo(),
                                    rifa.getPrecioNumero(),
                                    rifa.getCreadoEn()
                            );
                        })
                        .collect(Collectors.toList());
                liveData.setValue(rifas);
            } else {
                liveData.setValue(new ArrayList<>());
            }
        });

        return liveData;
    }

    /**
     * Busca una rifa por código
     */
    public LiveData<RifaDTO> buscarRifaPorCodigo(String codigo) {
        MutableLiveData<RifaDTO> liveData = new MutableLiveData<>();

        rifaDAO.obtenerRifasPorCodigo(codigo, task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                Rifa rifa = task.getResult().getDocuments().get(0).toObject(Rifa.class);
                if (rifa != null) {
                    RifaDTO rifaDTO = new RifaDTO(
                            rifa.getTitulo(),
                            rifa.getDescripcion(),
                            rifa.getCreadoPor(),
                            rifa.getEstado(),
                            rifa.getCodigo(),
                            rifa.getMetodoEleccionGanador(),
                            rifa.getMotivoEleccionGanador(),
                            rifa.getFechaSorteo(),
                            rifa.getPrecioNumero(),
                            rifa.getCreadoEn()
                    );
                    liveData.setValue(rifaDTO);
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
     * Actualiza el estado de una rifa
     */
    public void actualizarEstadoRifa(String rifaId, RifaEstado nuevoEstado, OnCompleteListener<Void> listener) {
        rifaDAO.actualizarEstadoRifa(rifaId, nuevoEstado, listener);
    }
}
