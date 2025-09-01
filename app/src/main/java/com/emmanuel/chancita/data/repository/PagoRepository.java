package com.emmanuel.chancita.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.emmanuel.chancita.data.dao.PagoDAO;
import com.emmanuel.chancita.data.dao.PagoNumeroDAO;
import com.emmanuel.chancita.data.dto.PagoDTO;
import com.emmanuel.chancita.data.model.Pago;
import com.emmanuel.chancita.data.model.PagoEstado;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PagoRepository {
    private final PagoDAO pagoDAO;
    private final PagoNumeroDAO pagoNumeroDAO;

    public PagoRepository() {
        this.pagoDAO = new PagoDAO();
        this.pagoNumeroDAO = new PagoNumeroDAO();
    }

    /**
     * Crea un nuevo pago pendiente
     */
    public void crearPago(PagoDTO pagoDto, OnCompleteListener<String> listener) {
        Pago nuevoPago = new Pago(
                null, // El ID se generará en el DAO
                pagoDto.getMoneda(),
                pagoDto.getEstado(),
                pagoDto.getIdPreferencia(),
                pagoDto.getCreadoEn(),
                LocalDateTime.now(), // actualizadoEn
                pagoDto.getUsuarioId()
        );

        pagoDAO.crearPago(nuevoPago, task -> {
            if (task.isSuccessful()) {
                // Retornar el ID del pago creado para poder asociar números
                listener.onComplete(Tasks.forResult(nuevoPago.getId()));
            } else {
                listener.onComplete(Tasks.forException(task.getException()));
            }
        });
    }

    /**
     * Obtiene los pagos de un usuario
     */
    public LiveData<List<PagoDTO>> obtenerPagosPorUsuario(String usuarioId) {
        MutableLiveData<List<PagoDTO>> liveData = new MutableLiveData<>();

        pagoDAO.obtenerPagosPorUsuario(usuarioId, task -> {
            if (task.isSuccessful()) {
                List<PagoDTO> pagos = task.getResult().getDocuments().stream()
                        .map(document -> {
                            Pago pago = document.toObject(Pago.class);
                            return new PagoDTO(
                                    pago.getMoneda(),
                                    pago.getEstado(),
                                    pago.getIdPreferencia(),
                                    pago.getUsuarioId(),
                                    pago.getCreadoEn()
                            );
                        })
                        .collect(Collectors.toList());
                liveData.setValue(pagos);
            } else {
                liveData.setValue(new ArrayList<>());
            }
        });

        return liveData;
    }

    /**
     * Actualiza el estado de un pago (generalmente llamado por webhook de Mercado Pago)
     */
    public void actualizarEstadoPago(String pagoId, PagoEstado nuevoEstado, OnCompleteListener<Void> listener) {
        pagoDAO.actualizarEstadoPago(pagoId, nuevoEstado, listener);
    }

    /**
     * Obtiene un pago por ID de preferencia de Mercado Pago
     */
    public LiveData<PagoDTO> obtenerPagoPorPreferencia(String idPreferencia) {
        MutableLiveData<PagoDTO> liveData = new MutableLiveData<>();

        pagoDAO.obtenerPagoPorPreferencia(idPreferencia, task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                Pago pago = task.getResult().getDocuments().get(0).toObject(Pago.class);
                if (pago != null) {
                    PagoDTO pagoDTO = new PagoDTO(
                            pago.getMoneda(),
                            pago.getEstado(),
                            pago.getIdPreferencia(),
                            pago.getUsuarioId(),
                            pago.getCreadoEn()
                    );
                    liveData.setValue(pagoDTO);
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
