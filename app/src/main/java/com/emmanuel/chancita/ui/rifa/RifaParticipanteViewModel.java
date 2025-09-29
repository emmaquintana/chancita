package com.emmanuel.chancita.ui.rifa;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emmanuel.chancita.data.model.Rifa;
import com.emmanuel.chancita.data.model.Usuario;
import com.emmanuel.chancita.data.repository.RifaRepository;

import java.util.List;

public class RifaParticipanteViewModel extends ViewModel {
    private RifaRepository rifaRepository;

    private final MutableLiveData<Boolean> _obteniendoRifa = new MutableLiveData<>();
    public final LiveData<Boolean> obteniendoRifa = _obteniendoRifa;
    private final MutableLiveData<String> _resultadoObtencionRifa = new MutableLiveData<>();
    public final LiveData<String> resultadoObtencionRifa = _resultadoObtencionRifa;

    private final MutableLiveData<Boolean> _comprandoNumeros = new MutableLiveData<>();
    public final LiveData<Boolean> comprandoNumeros = _comprandoNumeros;
    private final MutableLiveData<Boolean> _compraNumerosExitosa = new MutableLiveData<>();
    public final LiveData<Boolean> compraNumerosExitosa = _compraNumerosExitosa;

    public RifaParticipanteViewModel() {
        this.rifaRepository = new RifaRepository();
    }

    public LiveData<Rifa> obtenerRifa(String rifaId) {
        _obteniendoRifa.setValue(true);

        return rifaRepository.obtenerRifa(rifaId, task -> {
            _obteniendoRifa.setValue(false);

            if (!task.isSuccessful()) {
                _resultadoObtencionRifa.setValue("Algo salió mal");
            }
        });
    }

    public LiveData<List<Integer>> obtenerNumerosCompradosPorUsuarioActual(String rifaId) {
        return rifaRepository.obtenerNumerosCompradosPorUsuarioActual(rifaId);
    }

    public LiveData<List<Integer>> obtenerNumerosGanadores(String rifaId) {
        return rifaRepository.obtenerNumerosGanadores(rifaId);
    }

    public LiveData<Usuario> obenerOrganizador(String rifaId) {
        return rifaRepository.obtenerOrganizador(rifaId);
    }

    public void comprarNumeros(String rifaId, String usuarioId, List<Integer> numeros, double precioUnitario) {
        _comprandoNumeros.setValue(true);

        rifaRepository.comprarNumeros(rifaId, usuarioId, numeros, precioUnitario, task -> {
            _comprandoNumeros.setValue(false);
            if (task.isSuccessful()) {
                _compraNumerosExitosa.setValue(true);
            }
            else {
                _compraNumerosExitosa.setValue(false);
            }
        });
    }

    /**
     * Método para resetear el estado de compra exitosa
     */
    public void resetCompraExitosa() {
        _compraNumerosExitosa.setValue(null);
    }
}