package com.emmanuel.chancita.ui.rifa.editar_rifa;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emmanuel.chancita.data.model.MetodoEleccionGanador;
import com.emmanuel.chancita.data.model.Rifa;
import com.emmanuel.chancita.data.model.RifaPremio;
import com.emmanuel.chancita.data.repository.RifaRepository;

import java.time.LocalDateTime;
import java.util.List;

public class EditarRifaViewModel extends ViewModel {

    private final RifaRepository rifaRepository;

    public EditarRifaViewModel() {
        rifaRepository = new RifaRepository();
    }

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

    // Validación de rifa
    private final MutableLiveData<String> _errorValidacion = new MutableLiveData<>();
    public LiveData<String> errorValidacion = _errorValidacion;

    // Nueva propiedad para indicar cuando se debe proceder con la edición
    private final MutableLiveData<Rifa> _rifaValidadaParaEditar = new MutableLiveData<>();
    public LiveData<Rifa> rifaValidadaParaEditar = _rifaValidadaParaEditar;

    /** Valida y edita la rifa */
    public void editarRifa(Rifa rifaDTO) {
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

    public LiveData<Rifa> obtenerRifa(String rifaId) {
        _obteniendoRifa.setValue(true);

        return rifaRepository.obtenerRifa(rifaId, task -> {
            _obteniendoRifa.setValue(false);

            if (!task.isSuccessful()) {
                _resultadoObtencionRifa.setValue("Algo salió mal");
            }
        });
    }

    /**
     * Valida una rifa de forma completa (incluye validación de código único)
     */
    public void validarYProcesarRifa(Rifa rifa) {
        if (!validarRifaBasico(rifa)) {
            return; // El error ya se setea en validarRifaBasico
        }

        validarCodigoUnico(rifa);
    }

    /**
     * Validaciones básicas de la rifa
     */
    private boolean validarRifaBasico(Rifa rifa) {
        if (rifa.getTitulo() == null || rifa.getTitulo().trim().isEmpty()) {
            _errorValidacion.setValue("El título no puede estar vacío");
            return false;
        }
        if (rifa.getDescripcion() != null && rifa.getDescripcion().trim().isEmpty()) {
            rifa.setDescripcion(null);
        }
        if (rifa.getFechaSorteo() == null) {
            _errorValidacion.setValue("Debes seleccionar fecha de sorteo");
            return false;
        }
        if (rifa.getFechaSorteo().isBefore(rifa.getCreadoEn())) {
            _errorValidacion.setValue("La fecha de sorteo no puede ser menor a la fecha de creación");
            return false;
        }
        if (rifa.getPrecioNumero() <= 0) {
            _errorValidacion.setValue("El precio del número debe ser mayor a 0");
            return false;
        }
        if (rifa.getCodigo() == null || rifa.getCodigo().trim().isEmpty()) {
            _errorValidacion.setValue("Debes ingresar un código para la rifa");
            return false;
        }
        if (
                rifa.getMetodoEleccionGanador() == MetodoEleccionGanador.DETERMINISTA
                        && (rifa.getMotivoEleccionGanador() == null || rifa.getMotivoEleccionGanador().trim().isEmpty())
        ) {
            _errorValidacion.setValue("Debes indicar el motivo que usarás para seleccionar al ganador");
            return false;
        }

        if (rifa.getFechaSorteo().isBefore(LocalDateTime.now().plusHours(1))) {
            _errorValidacion.setValue("La fecha de sorteo debe ser al menos 1 hora en el futuro");
            return false;
        }

        // Validar premios
        if (!validarPremios(rifa.getPremios())) {
            return false;
        }

        return true;
    }

    /**
     * Valida la lista de premios
     */
    private boolean validarPremios(List<RifaPremio> premios) {
        if (premios == null || premios.isEmpty()) {
            _errorValidacion.setValue("Debes agregar al menos un premio");
            return false;
        }

        for (int i = 0; i < premios.size(); i++) {
            RifaPremio premio = premios.get(i);

            if (premio.getPremioTitulo() == null || premio.getPremioTitulo().trim().isEmpty()) {
                _errorValidacion.setValue("El título del premio " + (i + 1) + " no puede estar vacío");
                return false;
            }

            // La descripción puede ser opcional, pero si se proporciona, debe tener contenido
            if (premio.getPremioDescripcion() != null && premio.getPremioDescripcion().trim().isEmpty()) {
                premio.setPremioDescripcion(null);
            }
        }

        return true;
    }

    /**
     * Valida si el código es único
     */
    private void validarCodigoUnico(Rifa rifa) {
        rifaRepository.existeRifaConCodigo(rifa.getCodigo(), rifa.getId(), exists -> {
            if (exists) {
                _errorValidacion.setValue("Ya existe una rifa con ese código");
            } else {
                _errorValidacion.setValue(null);
                _rifaValidadaParaEditar.setValue(rifa);
            }
        });
    }

    /**
     * Resetea el estado de validación
     */
    public void resetearValidacion() {
        _errorValidacion.setValue(null);
        _rifaValidadaParaEditar.setValue(null);
    }
}