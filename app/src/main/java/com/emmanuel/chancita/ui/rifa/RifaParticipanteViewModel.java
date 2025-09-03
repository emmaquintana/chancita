package com.emmanuel.chancita.ui.rifa;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emmanuel.chancita.data.dto.RifaDTO;
import com.emmanuel.chancita.data.repository.RifaRepository;

public class RifaParticipanteViewModel extends ViewModel {
    private RifaRepository rifaRepository;

    private final MutableLiveData<Boolean> _obteniendoRifa = new MutableLiveData<>();
    public final LiveData<Boolean> obteniendoRifa = _obteniendoRifa;
    private final MutableLiveData<String> _resultadoObtencionRifa = new MutableLiveData<>();
    public final LiveData<String> resultadoObtencionRifa = _resultadoObtencionRifa;

    public RifaParticipanteViewModel() {
        this.rifaRepository = new RifaRepository();
    }

    public LiveData<RifaDTO> obtenerRifa(String rifaId) {
        _obteniendoRifa.setValue(true);

        return rifaRepository.obtenerRifa(rifaId, task -> {
            _obteniendoRifa.setValue(false);

            if (!task.isSuccessful()) {
                _resultadoObtencionRifa.setValue("Algo salió mal");
            }
        });
    }
}