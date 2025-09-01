package com.emmanuel.chancita.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.emmanuel.chancita.data.dao.NumeroDAO;
import com.emmanuel.chancita.data.dao.PagoDAO;
import com.emmanuel.chancita.data.dao.PagoNumeroDAO;
import com.emmanuel.chancita.data.dto.NumeroDTO;
import com.emmanuel.chancita.data.model.Numero;
import com.emmanuel.chancita.data.model.PagoNumero;
import com.google.android.gms.tasks.OnCompleteListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NumeroRepository {
    private final NumeroDAO numeroDAO;
    private final PagoDAO pagoDAO;
    private final PagoNumeroDAO pagoNumeroDAO;

    public NumeroRepository() {
        this.numeroDAO = new NumeroDAO();
        this.pagoDAO = new PagoDAO();
        this.pagoNumeroDAO = new PagoNumeroDAO();
    }

    /**
     * Crea un nuevo número y lo asocia a un pago
     */
    public void crearNumero(NumeroDTO numeroDto, String pagoId, OnCompleteListener<Void> listener) {
        Numero nuevoNumero = new Numero(
                null, // El ID se generará en el DAO
                numeroDto.getValor(),
                numeroDto.getCreadoEn(),
                numeroDto.getUsuarioId(),
                numeroDto.getRifaId()
        );

        numeroDAO.crearNumero(nuevoNumero, task -> {
            if (task.isSuccessful()) {
                // Crear la relación pago-número
                PagoNumero pagoNumero = new PagoNumero(nuevoNumero.getId(), pagoId);
                pagoNumeroDAO.crearRelacionPagoNumero(pagoNumero, listener);
            } else {
                listener.onComplete(task);
            }
        });
    }

    /**
     * Obtiene los números de una rifa específica
     */
    public LiveData<List<NumeroDTO>> obtenerNumerosPorRifa(String rifaId) {
        MutableLiveData<List<NumeroDTO>> liveData = new MutableLiveData<>();

        numeroDAO.obtenerNumerosPorRifa(rifaId, task -> {
            if (task.isSuccessful()) {
                List<NumeroDTO> numeros = task.getResult().getDocuments().stream()
                        .map(document -> {
                            Numero numero = document.toObject(Numero.class);
                            return new NumeroDTO(
                                    numero.getValor(),
                                    numero.getUsuarioId(),
                                    numero.getRifaId(),
                                    numero.getCreadoEn()
                            );
                        })
                        .collect(Collectors.toList());
                liveData.setValue(numeros);
            } else {
                liveData.setValue(new ArrayList<>());
            }
        });

        return liveData;
    }

    /**
     * Obtiene los números de un usuario específico
     */
    public LiveData<List<NumeroDTO>> obtenerNumerosPorUsuario(String usuarioId) {
        MutableLiveData<List<NumeroDTO>> liveData = new MutableLiveData<>();

        numeroDAO.obtenerNumerosPorUsuario(usuarioId, task -> {
            if (task.isSuccessful()) {
                List<NumeroDTO> numeros = task.getResult().getDocuments().stream()
                        .map(document -> {
                            Numero numero = document.toObject(Numero.class);
                            return new NumeroDTO(
                                    numero.getValor(),
                                    numero.getUsuarioId(),
                                    numero.getRifaId(),
                                    numero.getCreadoEn()
                            );
                        })
                        .collect(Collectors.toList());
                liveData.setValue(numeros);
            } else {
                liveData.setValue(new ArrayList<>());
            }
        });

        return liveData;
    }

    /**
     * Verifica si un número está disponible en una rifa
     */
    public LiveData<Boolean> verificarNumeroDisponible(String rifaId, int valor) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();

        numeroDAO.verificarNumeroDisponible(rifaId, valor, task -> {
            if (task.isSuccessful()) {
                boolean disponible = task.getResult().isEmpty();
                liveData.setValue(disponible);
            } else {
                liveData.setValue(false);
            }
        });

        return liveData;
    }
}
