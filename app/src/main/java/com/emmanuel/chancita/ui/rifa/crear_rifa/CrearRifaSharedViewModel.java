package com.emmanuel.chancita.ui.rifa.crear_rifa;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emmanuel.chancita.data.dto.RifaDTO;
import com.emmanuel.chancita.data.model.MetodoEleccionGanador;
import com.emmanuel.chancita.data.model.RifaEstado;
import com.emmanuel.chancita.data.model.RifaPremio;
import com.emmanuel.chancita.data.repository.RifaRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrearRifaSharedViewModel extends ViewModel {

    private final RifaRepository rifaRepository;

    // Datos de la rifa en construcción
    private final MutableLiveData<RifaDTO> _rifaEnConstruccion = new MutableLiveData<>();
    public LiveData<RifaDTO> rifaEnConstruccion = _rifaEnConstruccion;

    // Estado de creación
    private final MutableLiveData<Boolean> _creandoRifa = new MutableLiveData<>();
    public LiveData<Boolean> creandoRifa = _creandoRifa;

    private final MutableLiveData<String> _resultadoCreacion = new MutableLiveData<>();
    public LiveData<String> resultadoCreacion = _resultadoCreacion;

    // Validación de código único
    private final MutableLiveData<String> _errorValidacionCodigo = new MutableLiveData<>();
    public LiveData<String> errorValidacionCodigo = _errorValidacionCodigo;

    private final MutableLiveData<Boolean> _codigoValidado = new MutableLiveData<>();
    public LiveData<Boolean> codigoValidado = _codigoValidado;

    public CrearRifaSharedViewModel() {
        rifaRepository = new RifaRepository();
        inicializarRifa();
    }

    private void inicializarRifa() {
        RifaDTO nuevaRifa = new RifaDTO();
        nuevaRifa.setCreadoEn(LocalDateTime.now());
        nuevaRifa.setEstado(RifaEstado.ABIERTO);
        nuevaRifa.setParticipantesIds(new ArrayList<>());
        nuevaRifa.setPremios(new ArrayList<>());
        nuevaRifa.setNumerosComprados(new ArrayList<>());
        _rifaEnConstruccion.setValue(nuevaRifa);
    }

    // Métodos para actualizar datos por pasos

    /**
     * Paso 2: Información básica de la rifa
     */
    public void actualizarDatosBasicos(String titulo, String descripcion, int cantPremios,
                                       int cantNumeros, double precio, LocalDateTime fechaSorteo) {
        RifaDTO rifa = _rifaEnConstruccion.getValue();
        if (rifa != null) {
            rifa.setTitulo(titulo);
            rifa.setDescripcion(descripcion);
            rifa.setCantNumeros(cantNumeros);
            rifa.setPrecioNumero(precio);
            rifa.setFechaSorteo(fechaSorteo);

            // Generar lista inicial de premios vacíos
            List<RifaPremio> premios = new ArrayList<>();
            for (int i = 1; i <= cantPremios; i++) {
                premios.add(new RifaPremio(
                        UUID.randomUUID().toString(),
                        "",
                        "",
                        i
                ));
            }
            rifa.setPremios(premios);

            _rifaEnConstruccion.setValue(rifa);
        }
    }

    /**
     * Paso 3: Información de premios
     */
    public void actualizarPremios(List<RifaPremio> premios) {
        RifaDTO rifa = _rifaEnConstruccion.getValue();
        if (rifa != null) {
            rifa.setPremios(premios);
            _rifaEnConstruccion.setValue(rifa);
        }
    }

    /**
     * Paso 4: Método de elección
     */
    public void actualizarMetodoEleccion(MetodoEleccionGanador metodo) {
        RifaDTO rifa = _rifaEnConstruccion.getValue();
        if (rifa != null) {
            rifa.setMetodoEleccionGanador(metodo);
            _rifaEnConstruccion.setValue(rifa);
        }
    }

    /**
     * Paso 5: Descripción del método (si es determinista)
     */
    public void actualizarDescripcionMetodo(String descripcion) {
        RifaDTO rifa = _rifaEnConstruccion.getValue();
        if (rifa != null) {
            rifa.setMotivoEleccionGanador(descripcion);
            _rifaEnConstruccion.setValue(rifa);
        }
    }

    // Validaciones

    public boolean validarDatosBasicos(String titulo, String descripcion, int cantPremios,
                                       int cantNumeros, double precio, LocalDateTime fechaSorteo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            return false;
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            return false;
        }
        if (cantPremios <= 0) {
            return false;
        }
        if (cantNumeros <= 0 || cantNumeros > 200) {
            return false;
        }
        if (precio <= 0) {
            return false;
        }
        if (fechaSorteo == null || fechaSorteo.isBefore(LocalDateTime.now())) {
            return false;
        }
        return true;
    }

    public boolean validarPremios(List<RifaPremio> premios) {
        if (premios == null || premios.isEmpty()) {
            return false;
        }

        for (RifaPremio premio : premios) {
            if (premio.getPremioTitulo() == null || premio.getPremioTitulo().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean validarDescripcionMetodo(String descripcion) {
        return descripcion != null && !descripcion.trim().isEmpty();
    }

    /**
     * Validar código único antes de crear la rifa
     */
    public void validarCodigoUnico(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            _errorValidacionCodigo.setValue("Debes generar un código para la rifa");
            return;
        }

        rifaRepository.existeRifaConCodigo(codigo, null, exists -> {
            if (exists) {
                _errorValidacionCodigo.setValue("Ya existe una rifa con ese código");
                _codigoValidado.setValue(false);
            } else {
                _errorValidacionCodigo.setValue(null);
                _codigoValidado.setValue(true);
            }
        });
    }

    /**
     * Generar código único para la rifa
     */
    public String generarCodigoRifa() {
        return "RIFA" + System.currentTimeMillis();
    }

    /**
     * Crear la rifa final
     */
    public void crearRifa(String creadoPor) {
        RifaDTO rifa = _rifaEnConstruccion.getValue();
        if (rifa == null) {
            _resultadoCreacion.setValue("Error: No hay datos de rifa para crear");
            return;
        }

        // Generar código si no existe
        if (rifa.getCodigo() == null || rifa.getCodigo().trim().isEmpty()) {
            rifa.setCodigo(generarCodigoRifa());
        }

        rifa.setCreadoPor(creadoPor);

        _creandoRifa.setValue(true);

        rifaRepository.crearRifa(rifa, task -> {
            _creandoRifa.setValue(false);

            if (task.isSuccessful()) {
                _resultadoCreacion.setValue("¡Rifa creada con éxito!");
            } else {
                _resultadoCreacion.setValue("Error al crear la rifa: " +
                        (task.getException() != null ? task.getException().getMessage() : "Error desconocido"));
            }
        });
    }

    /**
     * Resetear el ViewModel para crear una nueva rifa
     */
    public void resetear() {
        inicializarRifa();
        _resultadoCreacion.setValue(null);
        _errorValidacionCodigo.setValue(null);
        _codigoValidado.setValue(null);
    }

    /**
     * Obtener datos actuales de la rifa
     */
    public RifaDTO getRifaActual() {
        return _rifaEnConstruccion.getValue();
    }
}