package com.emmanuel.chancita.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.emmanuel.chancita.data.dao.RifaDAO;
import com.emmanuel.chancita.data.model.MetodoEleccionGanador;
import com.emmanuel.chancita.data.model.NumeroComprado;
import com.emmanuel.chancita.data.model.Rifa;
import com.emmanuel.chancita.data.model.RifaEstado;
import com.emmanuel.chancita.data.model.RifaPremio;
import com.emmanuel.chancita.data.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class RifaRepository {
    private final RifaDAO rifaDAO;

    public RifaRepository() {
        this.rifaDAO = new RifaDAO();
    }

    /**
     * Crea una nueva rifa en la BD
     */
    public void crearRifa(Rifa rifa, OnCompleteListener<Void> listener) {
        Rifa nuevaRifa = new Rifa(
                null, // El ID se generará en el DAO
                rifa.getTitulo(),
                rifa.getDescripcion(),
                rifa.getCantNumeros(),
                rifa.getCreadoPor(),
                rifa.getEstado(),
                rifa.getCreadoEn(),
                rifa.getCodigo(),
                rifa.getMetodoEleccionGanador(),
                rifa.getMotivoEleccionGanador(),
                rifa.getFechaSorteo(),
                rifa.getPrecioNumero(),
                new ArrayList<String>(),
                rifa.getPremios(),
                new ArrayList<>()
        );

        rifaDAO.crearRifa(nuevaRifa, listener);
    }

    /**
     * Edita una rifa a partir de un RifaDTO
     */
    public void editarRifa(Rifa rifa, OnCompleteListener<Void> listener) {
        if (rifa.getId() == null) {
            throw new IllegalArgumentException("El ID de la rifa no puede ser null al editar");
        }

        Rifa rifaActualizada = new Rifa(
                rifa.getId(),
                rifa.getTitulo(),
                rifa.getDescripcion(),
                rifa.getCantNumeros(),
                rifa.getCreadoPor(),
                rifa.getEstado(),
                rifa.getCreadoEn(),
                rifa.getCodigo(),
                rifa.getMetodoEleccionGanador(),
                rifa.getMotivoEleccionGanador(),
                rifa.getFechaSorteo(),
                rifa.getPrecioNumero(),
                rifa.getParticipantesIds(),
                rifa.getPremios(),
                rifa.getNumerosComprados()
        );

        rifaDAO.editarRifa(rifaActualizada, listener);
    }

    public void unirseARifa(String codigo, OnCompleteListener<Void> listener) {
        rifaDAO.unirseARifa(codigo, listener);
    }

    /**
     * Obtiene una rifa por su ID
     */
    public LiveData<Rifa> obtenerRifa(String rifaId, OnCompleteListener<DocumentSnapshot> listener) {
        MutableLiveData<Rifa> liveData = new MutableLiveData<>();

        rifaDAO.obtenerRifa(rifaId, task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                DocumentSnapshot doc = task.getResult();

                String id = doc.getId();
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
                List<Map<String, Object>> numerosCompradosMap = (List<Map<String, Object>>) doc.get("numerosComprados");
                List<NumeroComprado> numerosComprados = new ArrayList<>();

                if (numerosCompradosMap != null) {
                    for (Map<String, Object> map : numerosCompradosMap) {
                        String usuarioId = (String) map.get("usuarioId");
                        List<Integer> valores = new ArrayList<>();

                        List<?> valoresRaw = (List<?>) map.get("valor");
                        if (valoresRaw != null) {
                            for (Object obj : valoresRaw) {
                                if (obj instanceof Long) {
                                    valores.add(((Long) obj).intValue());
                                } else if (obj instanceof Integer) {
                                    valores.add((Integer) obj);
                                }
                            }
                        }

                        Number precioRaw = (Number) map.get("precioUnitario");
                        double precioUnitario = precioRaw.doubleValue();

                        numerosComprados.add(
                                new NumeroComprado(
                                        valores,
                                        usuarioId,
                                        precioUnitario
                                )
                        );
                    }
                }
                List<String> participantesIds = (List<String>) doc.get("participantesIds");


                Rifa rifa = new Rifa(id, titulo, descripcion, cantNumeros, creadoPor,
                        estado, creadoEn, codigo, metodo, motivo, fechaSorteo,
                        precioNumero, participantesIds, premios, numerosComprados);

                liveData.setValue(rifa);

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
     * Busca una rifa por código
     */
    public LiveData<Rifa> obtenerRifaPorCodigo(String codigo, OnCompleteListener<QuerySnapshot> listener) {
        MutableLiveData<Rifa> liveData = new MutableLiveData<>();

        rifaDAO.obtenerRifasPorCodigo(codigo, task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                DocumentSnapshot doc = task.getResult().getDocuments().get(0);

                // Mapear campos manualmente
                String id = doc.getId();
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
                List<Map<String, Object>> numerosCompradosMap = (List<Map<String, Object>>) doc.get("numerosComprados");
                List<NumeroComprado> numerosComprados = new ArrayList<>();

                if (numerosCompradosMap != null) {
                    for (Map<String, Object> map : numerosCompradosMap) {
                        String usuarioId = (String) map.get("usuarioId");
                        List<Integer> valores = new ArrayList<>();

                        List<?> valoresRaw = (List<?>) map.get("valor");
                        if (valoresRaw != null) {
                            for (Object obj : valoresRaw) {
                                if (obj instanceof Long) {
                                    valores.add(((Long) obj).intValue());
                                } else if (obj instanceof Integer) {
                                    valores.add((Integer) obj);
                                }
                            }
                        }

                        Number precioRaw = (Number) map.get("precioUnitario");
                        double precioUnitario = precioRaw.doubleValue();

                        numerosComprados.add(
                                new NumeroComprado(
                                        valores,
                                        usuarioId,
                                        precioUnitario
                                )
                        );
                    }
                }

                List<String> participantesIds = (List<String>) doc.get("participantesIds");


                Rifa rifa = new Rifa(id, titulo, descripcion, cantNumeros, creadoPor,
                        estado, creadoEn, codigo, metodo, motivo, fechaSorteo,
                        precioNumero, participantesIds, premios, numerosComprados);

                liveData.setValue(rifa);
            } else {
                liveData.setValue(null);
            }

            if (listener != null) {
                listener.onComplete(task);
            }
        });

        return liveData;
    }


    public LiveData<List<Rifa>> obtenerRifasCreadasPorUsuarioActual(OnCompleteListener<QuerySnapshot> listener) {
        MutableLiveData<List<Rifa>> liveData = new MutableLiveData<>();

        rifaDAO.obtenerRifasCreadasPorUsuarioActual(task -> {
            if (task.isSuccessful()) {
                List<Rifa> rifas = task.getResult().getDocuments().stream()
                        .map(document -> {
                            String id = document.getId();
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
                            List<Map<String, Object>> numerosCompradosMap = (List<Map<String, Object>>) document.get("numerosComprados");
                            List<NumeroComprado> numerosComprados = new ArrayList<>();

                            if (numerosCompradosMap != null) {
                                for (Map<String, Object> map : numerosCompradosMap) {
                                    String usuarioId = (String) map.get("usuarioId");
                                    List<Integer> valores = new ArrayList<>();

                                    List<?> valoresRaw = (List<?>) map.get("valor");
                                    if (valoresRaw != null) {
                                        for (Object obj : valoresRaw) {
                                            if (obj instanceof Long) {
                                                valores.add(((Long) obj).intValue());
                                            } else if (obj instanceof Integer) {
                                                valores.add((Integer) obj);
                                            }
                                        }
                                    }

                                    Number precioRaw = (Number) map.get("precioUnitario");
                                    double precioUnitario = precioRaw.doubleValue();

                                    numerosComprados.add(
                                            new NumeroComprado(
                                                    valores,
                                                    usuarioId,
                                                    precioUnitario
                                            )
                                    );
                                }
                            }

                            List<String> participantesIds = (List<String>) document.get("participantesIds");


                            return new Rifa(id, titulo, descripcion, cantNumeros, creadoPor,
                                    estado, creadoEn, codigo, metodo, motivo, fechaSorteo,
                                    precioNumero, participantesIds, premios, numerosComprados);
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

    public LiveData<List<Rifa>> obtenerRifasUnidasDeUsuarioActual(OnCompleteListener<QuerySnapshot> listener) {
        MutableLiveData<List<Rifa>> liveData = new MutableLiveData<>();

        rifaDAO.obtenerRifasUnidasDeUsuarioActual(task -> {
            if (task.isSuccessful()) {
                List<Rifa> rifas = task.getResult().getDocuments().stream()
                        .map(document -> {
                            String id = document.getId();
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
                            List<Map<String, Object>> numerosCompradosMap = (List<Map<String, Object>>) document.get("numerosComprados");
                            List<NumeroComprado> numerosComprados = new ArrayList<>();

                            if (numerosCompradosMap != null) {
                                for (Map<String, Object> map : numerosCompradosMap) {
                                    String usuarioId = (String) map.get("usuarioId");
                                    List<Integer> valores = new ArrayList<>();

                                    List<?> valoresRaw = (List<?>) map.get("valor");
                                    if (valoresRaw != null) {
                                        for (Object obj : valoresRaw) {
                                            if (obj instanceof Long) {
                                                valores.add(((Long) obj).intValue());
                                            } else if (obj instanceof Integer) {
                                                valores.add((Integer) obj);
                                            }
                                        }
                                    }

                                    Number precioRaw = (Number) map.get("precioUnitario");
                                    double precioUnitario = precioRaw.doubleValue();

                                    numerosComprados.add(
                                            new NumeroComprado(
                                                    valores,
                                                    usuarioId,
                                                    precioUnitario
                                            )
                                    );
                                }
                            }

                            List<String> participantesIds = (List<String>) document.get("participantesIds");

                            return new Rifa(id, titulo, descripcion, cantNumeros, creadoPor,
                                    estado, creadoEn, codigo, metodo, motivo, fechaSorteo,
                                    precioNumero, participantesIds, premios, numerosComprados);
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

    public LiveData<List<Usuario>> obtenerParticipantes(String rifaId) {
        MutableLiveData<List<Usuario>> liveData = new MutableLiveData<>();

        rifaDAO.obtenerParticipantes(rifaId, documentos -> {
            List<Usuario> dtos = new ArrayList<>();

            for (DocumentSnapshot doc : documentos) {
                if (doc.exists()) {
                    try {
                        String id = doc.getId();
                        String nombre = doc.getString("nombre");
                        String apellido = doc.getString("apellido");
                        String correo = doc.getString("correo");
                        String nroCelular = doc.getString("nroCelular");
                        String contrasena = doc.getString("contraseña");
                        LocalDate fechaNacimiento = null;
                        String fechaNacimientoStr = doc.getString("fechaNacimiento");
                        if (fechaNacimientoStr != null && !fechaNacimientoStr.isEmpty()) {
                            fechaNacimiento = LocalDate.parse(fechaNacimientoStr);
                        }
                        LocalDateTime creadoEn = null;
                        String creadoEnStr = doc.getString("creadoEn");
                        if (creadoEnStr != null && !creadoEnStr.isEmpty()) {
                            creadoEn = LocalDateTime.parse(creadoEnStr);
                        }
                        LocalDateTime ultimoIngreso = null;
                        String ultimoIngresoStr = doc.getString("ultimoIngreso");
                        if (ultimoIngresoStr != null && !ultimoIngresoStr.isEmpty()) {
                            ultimoIngreso = LocalDateTime.parse(ultimoIngresoStr);
                        }
                        Usuario usuario = new Usuario(id,
                                nombre, apellido, correo, nroCelular, contrasena,
                                fechaNacimiento, creadoEn, ultimoIngreso
                        );

                        dtos.add(usuario);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            liveData.postValue(dtos);
        });

        return liveData;
    }

    public void asignarNumerosGanadores(String rifaId, List<Integer> numerosGanadores, OnCompleteListener<Void> listener) {
        rifaDAO.asignarNumerosGanadores(rifaId, numerosGanadores, listener);
    }

    public LiveData<List<Integer>> obtenerNumerosCompradosPorUsuarioActual(String rifaId) {
        MutableLiveData<List<Integer>> liveData = new MutableLiveData<>();

        rifaDAO.obtenerNumerosCompradosPorUsuarioActual(rifaId, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                liveData.setValue(task.getResult());
            } else {
                liveData.setValue(Collections.emptyList());
            }
        });

        return liveData;
    }


    public LiveData<List<Integer>> obtenerNumerosGanadores(String rifaId) {
        MutableLiveData<List<Integer>> liveData = new MutableLiveData<>();

        rifaDAO.obtenerNumerosGanadores(rifaId, doc -> {
            if (doc.isSuccessful()) {
                DocumentSnapshot document = doc.getResult();
                if (document.exists()) {
                    List<Long> numeros = (List<Long>) document.get("numerosGanadores");
                    if (numeros != null) {
                        List<Integer> convertidos = new ArrayList<>();
                        for (Long n : numeros) convertidos.add(n.intValue());
                        liveData.setValue(convertidos);
                    }
                else {
                    liveData.setValue(null);
                }
            }
            else {
                liveData.setValue(null);
            }
            }
        });

        return liveData;
    }

    public LiveData<Usuario> obtenerOrganizador(String rifaId) {
        MutableLiveData<Usuario> liveData = new MutableLiveData<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        rifaDAO.obtenerUidOrganizador(rifaId, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String uid = task.getResult();

                // Buscar datos completos del usuario en la colección "usuarios"
                db.collection("usuarios")
                        .document(uid)
                        .get()
                        .addOnCompleteListener(userTask -> {
                            if (userTask.isSuccessful() && userTask.getResult() != null && userTask.getResult().exists()) {
                                DocumentSnapshot doc = userTask.getResult();
                                Usuario organizador = new Usuario();
                                organizador.setNombre(doc.getString("nombre"));
                                organizador.setApellido(doc.getString("apellido"));
                                organizador.setCorreo(doc.getString("correo"));
                                organizador.setNroCelular(doc.getString("nroCelular"));
                                liveData.setValue(organizador);
                            } else {
                                liveData.setValue(null);
                            }
                        });

            } else {
                liveData.setValue(null);
            }
        });

        return liveData;
    }

    public void comprarNumeros(String rifaId, String usuarioId, List<Integer> numeros, double precioUnitario, OnCompleteListener<Void> listener) {
        rifaDAO.comprarNumeros(rifaId, usuarioId, numeros, precioUnitario).addOnCompleteListener(listener);
    }

    public void existeRifaConCodigo(String codigo, String rifaIdActual, Consumer<Boolean> callback) {
        rifaDAO.existeRifaConCodigo(codigo, rifaIdActual, callback);
    }

    public LiveData<Boolean> usuarioActualPoseeTokenMercadoPago() {
        MutableLiveData<Boolean> poseeToken = new MutableLiveData<>();

        rifaDAO.obtenerTokenMercadoPagoDeUsuarioActual().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                poseeToken.setValue(true);
            }
            else {
                poseeToken.setValue(false);
            }
        });

        return poseeToken;
    }
}