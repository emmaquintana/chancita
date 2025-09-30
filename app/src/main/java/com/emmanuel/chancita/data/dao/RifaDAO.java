package com.emmanuel.chancita.data.dao;

import com.emmanuel.chancita.data.model.Rifa;
import com.emmanuel.chancita.data.model.RifaEstado;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;
import com.google.firebase.firestore.Transaction;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class RifaDAO {
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    public RifaDAO() {
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    public void crearRifa(Rifa rifa, OnCompleteListener<Void> listener) {
        String nuevoId = db.collection("rifas").document().getId();
        rifa.setId(nuevoId);

        Map<String, Object> rifaData = new HashMap<>();
        rifaData.put("titulo", rifa.getTitulo());
        rifaData.put("descripcion", rifa.getDescripcion());
        rifaData.put("cantNumeros", rifa.getCantNumeros());
        rifaData.put("codigo", rifa.getCodigo());
        rifaData.put("creadoEn", rifa.getCreadoEn().toString());
        rifaData.put("creadoPor", rifa.getCreadoPor());
        rifaData.put("estado", rifa.getEstado().toString());
        rifaData.put("fechaSorteo", rifa.getFechaSorteo().toString());
        rifaData.put("metodoEleccionGanador", rifa.getMetodoEleccionGanador().toString());
        rifaData.put("motivoEleccionGanador", rifa.getMotivoEleccionGanador());
        rifaData.put("participantesIds", rifa.getParticipantesIds());
        rifaData.put("precioNumero", rifa.getPrecioNumero());
        rifaData.put("premios", rifa.getPremios());
        rifaData.put("numerosComprados", rifa.getNumerosComprados());

        db.collection("rifas")
                .document(nuevoId)
                .set(rifaData)
                .addOnCompleteListener(listener);
    }

    public void obtenerNumerosCompradosPorUsuarioActual(String rifaId, OnCompleteListener<List<Integer>> listener) {
        FirebaseUser usuarioActual = auth.getCurrentUser();
        if (usuarioActual == null) {
            listener.onComplete(Tasks.forException(new Exception("Usuario no autenticado")));
            return;
        }

        db.collection("rifas")
                .document(rifaId)
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful() || task.getResult() == null) {
                        listener.onComplete(Tasks.forException(
                                task.getException() != null ? task.getException() : new Exception("No se pudo obtener la rifa")
                        ));
                        return;
                    }

                    DocumentSnapshot snapshot = task.getResult();
                    List<Map<String, Object>> numerosComprados = (List<Map<String, Object>>) snapshot.get("numerosComprados");

                    List<Integer> numerosUsuario = new ArrayList<>();
                    if (numerosComprados != null) {
                        for (Map<String, Object> compra : numerosComprados) {
                            String uid = (String) compra.get("usuarioId");
                            if (usuarioActual.getUid().equals(uid)) {
                                List<Long> valores = (List<Long>) compra.get("valor");
                                if (valores != null) {
                                    for (Long num : valores) {
                                        numerosUsuario.add(num.intValue());
                                    }
                                }
                            }
                        }
                    }

                    listener.onComplete(Tasks.forResult(numerosUsuario));
                });
    }

    public void obtenerUidOrganizador(String rifaId, OnCompleteListener<String> listener) {
        db.collection("rifas")
                .document(rifaId)
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful() || task.getResult() == null || !task.getResult().exists()) {
                        listener.onComplete(Tasks.forException(
                                task.getException() != null ? task.getException() : new Exception("Rifa no encontrada")
                        ));
                        return;
                    }

                    String uidOrganizador = task.getResult().getString("creadoPor");
                    if (uidOrganizador == null) {
                        listener.onComplete(Tasks.forException(new Exception("No se encontró el organizador")));
                    } else {
                        listener.onComplete(Tasks.forResult(uidOrganizador));
                    }
                });
    }

    public void asignarNumerosGanadores(String rifaId, List<Integer> numerosGanadores, OnCompleteListener<Void> listener) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("numerosGanadores", numerosGanadores);
        updates.put("estado", RifaEstado.SORTEADO.toString());

        db.collection("rifas")
                .document(rifaId)
                .set(updates, SetOptions.merge())
                .addOnCompleteListener(listener);

    }

    public void obtenerNumerosGanadores(String rifaId, OnCompleteListener<DocumentSnapshot> listener) {
        db.collection("rifas")
                .document(rifaId)
                .get()
                .addOnCompleteListener(listener);
    }

    public void obtenerRifa(String rifaId, OnCompleteListener<DocumentSnapshot> listener) {
        db.collection("rifas").document(rifaId)
                .get()
                .addOnCompleteListener(listener);
    }

    /** Permite al usuario actual unirse a una rifa mediante un código.</br>
     * Internamente, agrega el id del usuario a un campo participantesIds del documento correspondiente a la rifa*/
    public void unirseARifa(String codigo, OnCompleteListener<Void> listener) {
            FirebaseUser usuarioActual = auth.getCurrentUser();

            db.collection("rifas")
                    .whereEqualTo("codigo", codigo)
                    .limit(1)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            String rifaId = task.getResult().getDocuments().get(0).getId();

                            db.collection("rifas")
                                    .document(rifaId)
                                    .update("participantesIds", FieldValue.arrayUnion(usuarioActual.getUid()))
                                    .addOnCompleteListener(listener);
                        } else {
                            if (listener != null) {
                                listener.onComplete(Tasks.forException(new Exception("No se encontró la rifa con ese código")));
                            }
                        }
                    });
        }

    /**
     * Edita una rifa existente en Firestore
     */
    public void editarRifa(Rifa rifa, OnCompleteListener<Void> listener) {
        if (rifa.getId() == null) {
            throw new IllegalArgumentException("El ID de la rifa no puede ser null al editar");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        Map<String, Object> datos = new HashMap<>();
        datos.put("titulo", rifa.getTitulo());
        datos.put("descripcion", rifa.getDescripcion());
        datos.put("codigo", rifa.getCodigo());
        datos.put("precioNumero", rifa.getPrecioNumero());
        datos.put("premios", rifa.getPremios());
        datos.put("motivoEleccionGanador", rifa.getMotivoEleccionGanador());

        if (rifa.getFechaSorteo() != null) {
            datos.put("fechaSorteo", rifa.getFechaSorteo().format(formatter));
        }
        if (rifa.getCreadoEn() != null) {
            datos.put("creadoEn", rifa.getCreadoEn().format(formatter));
        }


        db.collection("rifas")
                .document(rifa.getId())
                .set(datos, SetOptions.merge())
                .addOnCompleteListener(listener);
    }

    /**Obtiene las rifas creadas por el usuario actual*/
    public void obtenerRifasCreadasPorUsuarioActual(OnCompleteListener<QuerySnapshot> listener) {
         FirebaseUser usuarioActual = auth.getCurrentUser();

        db.collection("rifas")
                .whereEqualTo("creadoPor", usuarioActual.getUid())
                .get()
                .addOnCompleteListener(listener);
    }

    /**Obtiene las rifas creadas por un usuario*/
    public void obtenerRifasCreadasPorUsuario(String usuarioId, OnCompleteListener<QuerySnapshot> listener) {
        db.collection("rifas")
                .whereEqualTo("creadoPor", usuarioId)
                .get()
                .addOnCompleteListener(listener);
    }

    /**Obtiene la rifa en base a un codigo*/
    public void obtenerRifasPorCodigo(String codigo, OnCompleteListener<QuerySnapshot> listener) {
        db.collection("rifas")
                .whereEqualTo("codigo", codigo)
                .get()
                .addOnCompleteListener(listener);
    }

    public void actualizarEstadoRifa(String rifaId, RifaEstado nuevoEstado, OnCompleteListener<Void> listener) {
        db.collection("rifas").document(rifaId)
                .update("estado", nuevoEstado)
                .addOnCompleteListener(listener);
    }

    /**Obtiene las rifas a las que se unió el usuario actual*/
    public void obtenerRifasUnidasDeUsuarioActual(OnCompleteListener<QuerySnapshot> listener) {
        FirebaseUser usuarioActual = auth.getCurrentUser();

        db.collection("rifas")
                .whereArrayContains("participantesIds", usuarioActual.getUid())
                .get()
                .addOnCompleteListener(listener);
    }

    /**Obtiene los números de una rifa*/
    public void obtenerNumerosPorRifa(String rifaId, OnCompleteListener<QuerySnapshot> listener) {
        db.collection("numeros")
                .whereEqualTo("rifaId", rifaId)
                .get()
                .addOnCompleteListener(listener);
    }

    public interface OnParticipantesDocsListener {
        void onComplete(List<DocumentSnapshot> documentos);
    }
    /**
     * Obtiene los DocumentSnapshot de los participantes de una rifa, usando chunks de 10 IDs.
     */
    public void obtenerParticipantes(String rifaId, OnParticipantesDocsListener listener) {
        db.collection("rifas").document(rifaId).get().addOnCompleteListener(rifaTask -> {
            if (!rifaTask.isSuccessful() || !rifaTask.getResult().exists()) {
                listener.onComplete(Collections.emptyList());
                return;
            }

            List<String> participantesIds = (List<String>) rifaTask.getResult().get("participantesIds");
            if (participantesIds == null || participantesIds.isEmpty()) {
                listener.onComplete(Collections.emptyList());
                return;
            }

            List<DocumentSnapshot> resultadoFinal = new ArrayList<>();
            List<List<String>> chunks = chunkList(participantesIds, 10);
            AtomicInteger consultasPendientes = new AtomicInteger(chunks.size());

            for (List<String> chunk : chunks) {
                db.collection("usuarios")
                        .whereIn("usuario_id", chunk)
                        .get()
                        .addOnCompleteListener(usuariosTask -> {
                            if (usuariosTask.isSuccessful() && usuariosTask.getResult() != null) {
                                resultadoFinal.addAll(usuariosTask.getResult().getDocuments());
                            }

                            if (consultasPendientes.decrementAndGet() == 0) {
                                listener.onComplete(resultadoFinal);
                            }
                        });
            }
        });
    }

    /**Permite a un usuario comprar números de una rifa solo si esta no está cerrada ni sorteada.*/
    public Task<Void> comprarNumeros(String rifaId, String usuarioId, List<Integer> numeros, double precioUnitario) {
        DocumentReference rifaRef = db.collection("rifas").document(rifaId);

        return db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot snapshot = transaction.get(rifaRef);

            Long cantNumeros = snapshot.getLong("cantNumeros");
            String estado = snapshot.getString("estado");
            if ("CERRADO".equals(estado) || "SORTEADO".equals(estado)) {
                throw new FirebaseFirestoreException("Rifa no disponible", FirebaseFirestoreException.Code.ABORTED);
            }

            // Leer compras actuales
            List<Map<String, Object>> compras = (List<Map<String, Object>>) snapshot.get("numerosComprados");
            if (compras == null) compras = new ArrayList<>();

            int max = cantNumeros.intValue();
            boolean[] taken = new boolean[max + 1]; // +1 para incluir el número máximo
            int usados = 0;
            for (Map<String, Object> c : compras) {
                List<Long> valor = (List<Long>) c.get("valor");
                if (valor != null) {
                    usados += valor.size();
                    for (Long n : valor) {
                        int i = n.intValue();
                        if (i >= 1 && i <= max) {
                            taken[i] = true; // Usar el número directamente como índice
                        }
                    }
                }
            }

            // Validar entrada y convertir a Long
            List<Long> numerosLong = new ArrayList<>(numeros.size());
            for (Integer n : numeros) {
                if (n == null || n < 1 || n > max)
                    throw new FirebaseFirestoreException("Número fuera de rango: " + n, FirebaseFirestoreException.Code.ABORTED);
                if (taken[n])
                    throw new FirebaseFirestoreException("Número ya vendido: " + n, FirebaseFirestoreException.Code.ABORTED);
                numerosLong.add(n.longValue());
            }

            if (usados + numerosLong.size() > cantNumeros)
                throw new FirebaseFirestoreException("No quedan suficientes números", FirebaseFirestoreException.Code.ABORTED);

            // Agregar compra
            Map<String, Object> nuevaCompra = new HashMap<>();
            nuevaCompra.put("usuarioId", usuarioId);
            nuevaCompra.put("precioUnitario", precioUnitario);
            nuevaCompra.put("valor", numerosLong);

            compras.add(nuevaCompra);
            transaction.update(rifaRef, "numerosComprados", compras);

            // Cerrar si se llegó al máximo
            if (usados + numerosLong.size() == cantNumeros) {
                transaction.update(rifaRef, "estado", "CERRADO");
            }

            return null;
        });
    }

    /**Verifica si existe una rifa con cierto código*/
    public void existeRifaConCodigo(String codigo, String rifaIdActual, Consumer<Boolean> callback) {
        db.collection("rifas")
                .whereEqualTo("codigo", codigo)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean existe = false;
                        for (DocumentSnapshot doc : task.getResult()) {
                            if (!doc.getId().equals(rifaIdActual)) {
                                existe = true;
                                break;
                            }
                        }
                        callback.accept(existe);
                    } else {
                        callback.accept(false);
                    }
                });
    }

    public Task<DocumentSnapshot> obtenerTokenMercadoPagoDeUsuarioActual() {
        return db.collection("mercado_pago_tokens")
                .document(auth.getUid())
                .get();
    }

    /** Divide una lista en chunks de tamaño n */
    private <T> List<List<T>> chunkList(List<T> list, int n) {
        List<List<T>> chunks = new ArrayList<>();
        for (int i = 0; i < list.size(); i += n) {
            chunks.add(list.subList(i, Math.min(list.size(), i + n)));
        }
        return chunks;
    }
}
