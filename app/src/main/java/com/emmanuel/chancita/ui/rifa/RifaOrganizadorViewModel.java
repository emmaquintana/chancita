package com.emmanuel.chancita.ui.rifa;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emmanuel.chancita.data.dto.RifaDTO;
import com.emmanuel.chancita.data.dto.UsuarioDTO;
import com.emmanuel.chancita.data.model.Usuario;
import com.emmanuel.chancita.data.repository.RifaRepository;
import com.emmanuel.chancita.data.repository.UsuarioRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class RifaOrganizadorViewModel extends ViewModel {
    private final RifaRepository rifaRepository;
    private final UsuarioRepository usuarioRepository;

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

    // Asignar numeros ganadores
    private final MutableLiveData<Boolean> _asignandoNumerosGanadores = new MutableLiveData<>();
    public final LiveData<Boolean> asignandoNumerosGanadores = _asignandoNumerosGanadores;
    private final MutableLiveData<String> _resultadoAsignacionNumerosGanadores = new MutableLiveData<>();
    public final LiveData<String> resultadoAsignacionNumerosGanadores = _resultadoAsignacionNumerosGanadores;


    public RifaOrganizadorViewModel() {
        this.rifaRepository = new RifaRepository();
        this.usuarioRepository = new UsuarioRepository();
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

    public LiveData<Usuario> obtenerUsuario(String usuarioId) {
        return usuarioRepository.obtenerUsuario(usuarioId);
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

    public LiveData<List<Integer>> obtenerNumerosGanadores(String rifaId) {
        return rifaRepository.obtenerNumerosGanadores(rifaId);
    }

    public void asignarNumerosGanadores(String rifaId, List<Integer> numerosGanadores) {
        _asignandoNumerosGanadores.setValue(true);

        rifaRepository.asignarNumerosGanadores(rifaId, numerosGanadores, task -> {
            if (task.isSuccessful()) {
                _asignandoNumerosGanadores.setValue(false);
                _resultadoAsignacionNumerosGanadores.setValue("¡Los ganadores han sido asignados con éxito!");
            }
            else {
                _resultadoAsignacionNumerosGanadores.setValue("Algo salió mal. Intente nuevamente");
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

    public LiveData<List<UsuarioDTO>> obtenerParticipantes(String rifaId) {
        return rifaRepository.obtenerParticipantes(rifaId);
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