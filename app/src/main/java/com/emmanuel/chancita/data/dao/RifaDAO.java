package com.emmanuel.chancita.data.dao;

import com.emmanuel.chancita.data.model.Rifa;
import com.emmanuel.chancita.data.model.RifaEstado;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

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

    public void obtenerRifa(String rifaId, OnCompleteListener<DocumentSnapshot> listener) {
        db.collection("rifas").document(rifaId)
                .get()
                .addOnCompleteListener(listener);
    }

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

    public void obtenerRifasUnidasPorUsuario(String usuarioId, OnCompleteListener<QuerySnapshot> listener) {
        db.collection("rifas")
                .whereArrayContains("participantes", usuarioId)
                .get()
                .addOnCompleteListener(listener);
    }

    /**
     * Edita una rifa existente en Firestore
     */
    public void editarRifa(Rifa rifa, OnCompleteListener<Void> listener) {
        if (rifa.getId() == null) {
            throw new IllegalArgumentException("El ID de la rifa no puede ser null al editar");
        }

        db.collection("rifas")
                .document(rifa.getId())
                .set(rifa, SetOptions.merge()) // 👈 merge para no sobrescribir campos no enviados
                .addOnCompleteListener(listener);
    }

    public void obtenerRifasCreadasPorUsuarioActual(OnCompleteListener<QuerySnapshot> listener) {
         FirebaseUser usuarioActual = auth.getCurrentUser();

        db.collection("rifas")
                .whereEqualTo("creadoPor", usuarioActual.getUid())
                .get()
                .addOnCompleteListener(listener);
    }

    public void obtenerRifasCreadasPorUsuario(String usuarioId, OnCompleteListener<QuerySnapshot> listener) {
        db.collection("rifas")
                .whereEqualTo("creadoPor", usuarioId)
                .get()
                .addOnCompleteListener(listener);
    }

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

    public void obtenerRifasUnidasDeUsuarioActual(OnCompleteListener<QuerySnapshot> listener) {
        FirebaseUser usuarioActual = auth.getCurrentUser();

        db.collection("rifas")
                .whereArrayContains("participantesIds", usuarioActual.getUid())
                .get()
                .addOnCompleteListener(listener);
    }

    public void obtenerRifasAbiertas(OnCompleteListener<QuerySnapshot> listener) {
        db.collection("rifas")
                .whereEqualTo("estado", RifaEstado.ABIERTO)
                .get()
                .addOnCompleteListener(listener);
    }

    public void obtenerNumerosPorRifa(String rifaId, OnCompleteListener<QuerySnapshot> listener) {
        db.collection("numeros")
                .whereEqualTo("rifaId", rifaId)
                .get()
                .addOnCompleteListener(listener);
    }
}
