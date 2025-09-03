package com.emmanuel.chancita.ui.rifa;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emmanuel.chancita.data.dto.RifaDTO;
import com.emmanuel.chancita.data.repository.RifaRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class RifaOrganizadorViewModel extends ViewModel {
    private final RifaRepository rifaRepository;

    // Creación de rifa
    private final MutableLiveData<Boolean> _creandoRifa = new MutableLiveData<>();
    public final LiveData<Boolean> creandoRifa = _creandoRifa;
    private final MutableLiveData<String> _resultadoCreacionRifa = new MutableLiveData<>();
    public final LiveData<String> resultadoCreacionRifa = _resultadoCreacionRifa;

    // Edición de rifa
    private final MutableLiveData<Boolean> _editandoRifa = new MutableLiveData<>();
    public final LiveData<Boolean> editandoRifa = _editandoRifa;
    private final MutableLiveData<String> _resultadoEdicionRifa = new MutableLiveData<>();
    public final LiveData<String> resultadoEdicionRifa = _resultadoEdicionRifa;

    // Obtener rifa
    private final MutableLiveData<Boolean> _obteniendoRifa = new MutableLiveData<>();
    public final LiveData<Boolean> obteniendoRifa = _obteniendoRifa;
    private final MutableLiveData<String> _resultadoObtencionRifa = new MutableLiveData<>();
    public final LiveData<String> resultadoObtencionRifa = _resultadoObtencionRifa;


    public RifaOrganizadorViewModel() {
        this.rifaRepository = new RifaRepository();
    }

    public void crearRifa(RifaDTO rifaDTO) {
        _creandoRifa.setValue(true);

        rifaRepository.crearRifa(rifaDTO, task -> {
            _creandoRifa.setValue(false);
            if (task.isSuccessful()) {
                _resultadoCreacionRifa.setValue("Rifa creada con éxito!");
            } else {
                _resultadoCreacionRifa.setValue("Algo salió mal");
            }
        });
    }

    public void editarRifa(RifaDTO rifaDTO) {
        _editandoRifa.setValue(true);

        rifaRepository.editarRifa(rifaDTO, task -> {
            _editandoRifa.setValue(false);

            if (task.isSuccessful()) {
                _resultadoEdicionRifa.setValue("Rifa editada con éxito!");
            }
            else {
                _resultadoEdicionRifa.setValue("Algo salió mal");
            }
        });
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

    public LiveData<List<RifaDTO>> obtenerRifasCreadasPorUsuarioActual(OnCompleteListener<QuerySnapshot> listener) {
        _obteniendoRifa.setValue(true);

        return rifaRepository.obtenerRifasCreadasPorUsuarioActual(task -> {
            _obteniendoRifa.setValue(false);

            if (!task.isSuccessful()) {
                _resultadoObtencionRifa.setValue("Algo salió mal");
            }
        });
    }
}