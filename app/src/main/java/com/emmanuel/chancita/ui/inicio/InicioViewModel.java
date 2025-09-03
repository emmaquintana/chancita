package com.emmanuel.chancita.ui.inicio;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emmanuel.chancita.data.dto.RifaDTO;
import com.emmanuel.chancita.data.repository.RifaRepository;

import java.util.List;

public class InicioViewModel extends ViewModel {

    private final RifaRepository rifaRepository;

    // Obtener rifas creadas por el usuario actual
    private final MutableLiveData<Boolean> _obteniendoRifasCreadas = new MutableLiveData<>();
    public final LiveData<Boolean> obteniendoRifasCreadas = _obteniendoRifasCreadas;
    private final MutableLiveData<String> _resultadoObtencionRifaCreadas = new MutableLiveData<>();
    public final LiveData<String> resultadoObtencionRifaCreadas = _resultadoObtencionRifaCreadas;

    // Obtener rifas a las que el usuario actual se unió
    private final MutableLiveData<Boolean> _obteniendoRifasUnidas = new MutableLiveData<>();
    public final LiveData<Boolean> obteniendoRifasUnidas = _obteniendoRifasUnidas;
    private final MutableLiveData<String> _resultadoObtencionRifaUnidas = new MutableLiveData<>();
    public final LiveData<String> resultadoObtencionRifaUnidas = _resultadoObtencionRifaUnidas;

    public InicioViewModel() {
        this.rifaRepository = new RifaRepository();
    }

    public LiveData<RifaDTO> obtenerRifa(String rifaId) {
        _obteniendoRifasCreadas.setValue(true);

        return rifaRepository.obtenerRifa(rifaId, task -> {
            _obteniendoRifasCreadas.setValue(false);

            if (!task.isSuccessful()) {
                _resultadoObtencionRifaCreadas.setValue("Algo salió mal");
            }
        });
    }

    public void unirseARifa(String codigo) {
        rifaRepository.unirseARifa(codigo, null);
    }

    public LiveData<List<RifaDTO>> obtenerRifasCreadasPorUsuarioActual() {
        _obteniendoRifasCreadas.setValue(true);

        return rifaRepository.obtenerRifasCreadasPorUsuarioActual(task -> {
            _obteniendoRifasCreadas.setValue(false);

            if (!task.isSuccessful()) {
                _resultadoObtencionRifaCreadas.setValue("Algo salió mal al obtener las rifas que creaste");
            }
        });
    }

    public LiveData<List<RifaDTO>> obtenerRifasUnidasDeUsuarioActual() {
        _obteniendoRifasUnidas.setValue(true);

        return rifaRepository.obtenerRifasUnidasDeUsuarioActual(task -> {
            _obteniendoRifasUnidas.setValue(false);

            if (!task.isSuccessful()) {
                _resultadoObtencionRifaUnidas.setValue("Algo salió mal");
            }
        });
    }

    public LiveData<RifaDTO> obtenerRifaPorCodigo(String codigo) {
        return rifaRepository.obtenerRifaPorCodigo(codigo, null);
    }
}