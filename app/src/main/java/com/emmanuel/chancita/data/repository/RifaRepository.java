package com.emmanuel.chancita.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.emmanuel.chancita.data.dao.NumeroDAO;
import com.emmanuel.chancita.data.dao.RifaDAO;
import com.emmanuel.chancita.data.dao.RifaGanadorDAO;
import com.emmanuel.chancita.data.dto.RifaDTO;
import com.emmanuel.chancita.data.model.MetodoEleccionGanador;
import com.emmanuel.chancita.data.model.Rifa;
import com.emmanuel.chancita.data.model.RifaEstado;
import com.emmanuel.chancita.data.model.RifaPremio;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RifaRepository {
    private final RifaDAO rifaDAO;
    private final NumeroDAO numeroDAO;
    private final RifaGanadorDAO rifaGanadorDAO;

    public RifaRepository() {
        this.rifaDAO = new RifaDAO();
        this.numeroDAO = new NumeroDAO();
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
                rifaDto.getCantNumeros(),
                rifaDto.getCreadoPor(),
                rifaDto.getEstado(),
                rifaDto.getCreadoEn(),
                rifaDto.getCodigo(),
                rifaDto.getMetodoEleccionGanador(),
                rifaDto.getMotivoEleccionGanador(),
                rifaDto.getFechaSorteo(),
                rifaDto.getPrecioNumero(),
                new ArrayList<String>(),
                rifaDto.getPremios()
        );

        rifaDAO.crearRifa(nuevaRifa, listener);
    }

    /**
     * Edita una rifa a partir de un RifaDTO
     */
    public void editarRifa(RifaDTO rifaDto, OnCompleteListener<Void> listener) {
        if (rifaDto.getId() == null) {
            throw new IllegalArgumentException("El ID de la rifa no puede ser null al editar");
        }

        Rifa rifaActualizada = new Rifa(
                rifaDto.getId(),
                rifaDto.getTitulo(),
                rifaDto.getDescripcion(),
                rifaDto.getCantNumeros(),
                rifaDto.getCreadoPor(),
                rifaDto.getEstado(),
                rifaDto.getCreadoEn(),
                rifaDto.getCodigo(),
                rifaDto.getMetodoEleccionGanador(),
                rifaDto.getMotivoEleccionGanador(),
                rifaDto.getFechaSorteo(),
                rifaDto.getPrecioNumero(),
                rifaDto.getParticipantesIds() != null ? rifaDto.getParticipantesIds() : new ArrayList<String>(),
                rifaDto.getPremios()
        );

        rifaDAO.editarRifa(rifaActualizada, listener);
    }

    public void unirseARifa(String codigo, OnCompleteListener<Void> listener) {
        rifaDAO.unirseARifa(codigo, listener);
    }

    /**
     * Obtiene una rifa por su ID
     */
    public LiveData<RifaDTO> obtenerRifa(String rifaId, OnCompleteListener<DocumentSnapshot> listener) {
        MutableLiveData<RifaDTO> liveData = new MutableLiveData<>();

        rifaDAO.obtenerRifa(rifaId, task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                DocumentSnapshot doc = task.getResult();

                String id = doc.getString("id");
                String titulo = doc.getString("titulo");
                String descripcion = doc.getString("descripcion");
                int cantNumeros = doc.getLong("cantNumeros").intValue();
                String creadoPor = doc.getString("creadoPor");
                String estadoStr = doc.getString("estado");
                RifaEstado estado = RifaEstado.valueOf(estadoStr);
                String codigo = doc.getString("codigo");
                String metodoStr = doc.getString("metodoEleccionGanador");
                MetodoEleccionGanador metodo = MetodoEleccionGanador.valueOf(metodoStr);
                String motivo = doc.getString("motivoEleccionGanador");
                String fechaSorteoStr = doc.getString("fechaSorteo");
                LocalDateTime fechaSorteo = LocalDateTime.parse(fechaSorteoStr);
                String creadoEnStr = doc.getString("creadoEn");
                LocalDateTime creadoEn = LocalDateTime.parse(creadoEnStr);
                double precioNumero = doc.getDouble("precioNumero");
                List<Map<String, Object>> premiosMap = (List<Map<String, Object>>) doc.get("premios");
                List<RifaPremio> premios = new ArrayList<>();
                if (premiosMap != null) {
                    for (Map<String, Object> map : premiosMap) {
                        RifaPremio premio = new RifaPremio(
                                (String) map.get("premioId"),
                                (String) map.get("premioTitulo"),
                                (String) map.get("premioDescripcion"),
                                map.get("premioOrden") != null ? ((Long) map.get("premioOrden")).intValue() : 0
                        );
                        premios.add(premio);
                    }
                }

                RifaDTO rifaDTO = new RifaDTO(
                        id, titulo, descripcion, cantNumeros, creadoPor,
                        estado, codigo, metodo, motivo, fechaSorteo, precioNumero, creadoEn, premios
                );

                liveData.setValue(rifaDTO);

            } else {
                liveData.setValue(null);
            }

            if (listener != null) {
                listener.onComplete(task);
            }
        });


        return liveData;
    }

    /**
     * Obtiene las rifas creadas por un usuario
     */
    public LiveData<List<RifaDTO>> obtenerRifasCreadasPorUsuario(String usuarioId, OnCompleteListener<QuerySnapshot> listener) {
        MutableLiveData<List<RifaDTO>> liveData = new MutableLiveData<>();

        rifaDAO.obtenerRifasCreadasPorUsuario(usuarioId, task -> {
            if (task.isSuccessful()) {
                List<RifaDTO> rifas = task.getResult().getDocuments().stream()
                        .map(document -> {
                            Rifa rifa = document.toObject(Rifa.class);
                            return new RifaDTO(
                                    null,
                                    rifa.getTitulo(),
                                    rifa.getDescripcion(),
                                    rifa.getCantNumeros(),
                                    rifa.getCreadoPor(),
                                    rifa.getEstado(),
                                    rifa.getCodigo(),
                                    rifa.getMetodoEleccionGanador(),
                                    rifa.getMotivoEleccionGanador(),
                                    rifa.getFechaSorteo(),
                                    rifa.getPrecioNumero(),
                                    rifa.getCreadoEn(),
                                    rifa.getPremios()
                            );
                        })
                        .collect(Collectors.toList());
                liveData.setValue(rifas);
            } else {
                liveData.setValue(new ArrayList<>());
            }

            if (listener != null) {
                listener.onComplete(task);
            }
        });

        return liveData;
    }

    /**
     * Busca una rifa por código
     */
    public LiveData<RifaDTO> obtenerRifaPorCodigo(String codigo, OnCompleteListener<QuerySnapshot> listener) {
        MutableLiveData<RifaDTO> liveData = new MutableLiveData<>();

        rifaDAO.obtenerRifasPorCodigo(codigo, task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                DocumentSnapshot doc = task.getResult().getDocuments().get(0);

                // Mapear campos manualmente
                String id = doc.getString("id");
                String titulo = doc.getString("titulo");
                String descripcion = doc.getString("descripcion");
                int cantNumeros = doc.getLong("cantNumeros").intValue();
                String creadoPor = doc.getString("creadoPor");
                String estadoStr = doc.getString("estado");
                RifaEstado estado = RifaEstado.valueOf(estadoStr);
                String codigoVal = doc.getString("codigo");
                String metodoStr = doc.getString("metodoEleccionGanador");
                MetodoEleccionGanador metodo = MetodoEleccionGanador.valueOf(metodoStr);
                String motivo = doc.getString("motivoEleccionGanador");
                String fechaSorteoStr = doc.getString("fechaSorteo");
                LocalDateTime fechaSorteo = LocalDateTime.parse(fechaSorteoStr);
                String creadoEnStr = doc.getString("creadoEn");
                LocalDateTime creadoEn =LocalDateTime.parse(creadoEnStr);
                double precioNumero = doc.getDouble("precioNumero");

                // Premios: convertir lista de HashMaps → lista de RifaPremio
                List<RifaPremio> premios = new ArrayList<>();
                List<Map<String, Object>> premiosRaw = (List<Map<String, Object>>) doc.get("premios");
                if (premiosRaw != null) {
                    for (Map<String, Object> premioMap : premiosRaw) {
                        RifaPremio premio = new RifaPremio(
                                (String) premioMap.get("premioId"),
                                (String) premioMap.get("premioTitulo"),
                                (String) premioMap.get("premioDescripcion"),
                                ((Long) premioMap.get("premioOrden")).intValue()
                        );
                        premios.add(premio);
                    }
                }

                // Construir DTO
                RifaDTO rifaDTO = new RifaDTO(id,titulo,descripcion,cantNumeros,creadoPor,estado,codigoVal,metodo,motivo,fechaSorteo,precioNumero,creadoEn,premios);

                liveData.setValue(rifaDTO);
            } else {
                liveData.setValue(null);
            }

            if (listener != null) {
                listener.onComplete(task);
            }
        });

        return liveData;
    }


    public LiveData<List<RifaDTO>> obtenerRifasCreadasPorUsuarioActual(OnCompleteListener<QuerySnapshot> listener) {
        MutableLiveData<List<RifaDTO>> liveData = new MutableLiveData<>();

        rifaDAO.obtenerRifasCreadasPorUsuarioActual(task -> {
            if (task.isSuccessful()) {
                List<RifaDTO> rifas = task.getResult().getDocuments().stream()
                        .map(document -> {
                            String id = document.getString("id");
                            String titulo = document.getString("titulo");
                            String descripcion = document.getString("descripcion");
                            int cantNumeros = document.getLong("cantNumeros").intValue();
                            String creadoPor = document.getString("creadoPor");
                            String estadoStr = document.getString("estado");
                            RifaEstado estado = RifaEstado.valueOf(estadoStr);
                            String codigo = document.getString("codigo");
                            String metodoStr = document.getString("metodoEleccionGanador");
                            MetodoEleccionGanador metodo = MetodoEleccionGanador.valueOf(metodoStr);
                            String motivo = document.getString("motivoEleccionGanador");
                            String fechaSorteoStr = document.getString("fechaSorteo");
                            LocalDateTime fechaSorteo = LocalDateTime.parse(fechaSorteoStr);
                            String creadoEnStr = document.getString("creadoEn");
                            LocalDateTime creadoEn = LocalDateTime.parse(creadoEnStr);
                            double precioNumero = document.getDouble("precioNumero");
                            List<Map<String, Object>> premiosMap = (List<Map<String, Object>>) document.get("premios");
                            List<RifaPremio> premios = new ArrayList<>();
                            if (premiosMap != null) {
                                for (Map<String, Object> map : premiosMap) {
                                    RifaPremio premio = new RifaPremio(
                                            (String) map.get("premioId"),
                                            (String) map.get("premioTitulo"),
                                            (String) map.get("premioDescripcion"),
                                            map.get("premioOrden") != null ? ((Long) map.get("premioOrden")).intValue() : 0
                                    );
                                    premios.add(premio);
                                }
                            }

                            return new RifaDTO(
                                    id, titulo, descripcion, cantNumeros, creadoPor,
                                    estado, codigo, metodo, motivo, fechaSorteo, precioNumero, creadoEn, premios
                            );
                        })
                        .collect(Collectors.toList());
                liveData.setValue(rifas);
            } else {
                liveData.setValue(new ArrayList<>());
            }

            if (listener != null) {
                listener.onComplete(task);
            }
        });

        return liveData;

    }

    public LiveData<List<RifaDTO>> obtenerRifasUnidasDeUsuarioActual(OnCompleteListener<QuerySnapshot> listener) {
        MutableLiveData<List<RifaDTO>> liveData = new MutableLiveData<>();

        rifaDAO.obtenerRifasUnidasDeUsuarioActual(task -> {
            if (task.isSuccessful()) {
                List<RifaDTO> rifas = task.getResult().getDocuments().stream()
                        .map(document -> {
                            String id = document.getString("id");
                            String titulo = document.getString("titulo");
                            String descripcion = document.getString("descripcion");
                            int cantNumeros = document.getLong("cantNumeros").intValue();
                            String creadoPor = document.getString("creadoPor");
                            String estadoStr = document.getString("estado");
                            RifaEstado estado = RifaEstado.valueOf(estadoStr);
                            String codigo = document.getString("codigo");
                            String metodoStr = document.getString("metodoEleccionGanador");
                            MetodoEleccionGanador metodo = MetodoEleccionGanador.valueOf(metodoStr);
                            String motivo = document.getString("motivoEleccionGanador");
                            String fechaSorteoStr = document.getString("fechaSorteo");
                            LocalDateTime fechaSorteo = LocalDateTime.parse(fechaSorteoStr);
                            String creadoEnStr = document.getString("creadoEn");
                            LocalDateTime creadoEn = LocalDateTime.parse(creadoEnStr);
                            double precioNumero = document.getDouble("precioNumero");
                            List<Map<String, Object>> premiosMap = (List<Map<String, Object>>) document.get("premios");
                            List<RifaPremio> premios = new ArrayList<>();
                            if (premiosMap != null) {
                                for (Map<String, Object> map : premiosMap) {
                                    RifaPremio premio = new RifaPremio(
                                            (String) map.get("premioId"),
                                            (String) map.get("premioTitulo"),
                                            (String) map.get("premioDescripcion"),
                                            map.get("premioOrden") != null ? ((Long) map.get("premioOrden")).intValue() : 0
                                    );
                                    premios.add(premio);
                                }
                            }

                            return new RifaDTO(
                                    id, titulo, descripcion, cantNumeros, creadoPor,
                                    estado, codigo, metodo, motivo, fechaSorteo, precioNumero, creadoEn, premios
                            );
                        })
                        .collect(Collectors.toList());
                liveData.setValue(rifas);
            } else {
                liveData.setValue(new ArrayList<>());
            }

            if (listener != null) {
                listener.onComplete(task);
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