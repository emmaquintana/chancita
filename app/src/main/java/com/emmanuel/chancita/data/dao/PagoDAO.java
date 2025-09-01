package com.emmanuel.chancita.data.dao;

import com.emmanuel.chancita.data.model.Pago;
import com.emmanuel.chancita.data.model.PagoEstado;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class PagoDAO {
    private final FirebaseFirestore db;

    public PagoDAO() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void crearPago(Pago pago, OnCompleteListener<Void> listener) {
        String nuevoId = db.collection("pagos").document().getId();
        pago.setId(nuevoId);

        db.collection("pagos").document(nuevoId)
                .set(pago)
                .addOnCompleteListener(listener);
    }

    public void obtenerPago(String pagoId, OnCompleteListener<DocumentSnapshot> listener) {
        db.collection("pagos").document(pagoId)
                .get()
                .addOnCompleteListener(listener);
    }

    public void obtenerPagosPorUsuario(String usuarioId, OnCompleteListener<QuerySnapshot> listener) {
        db.collection("pagos")
                .whereEqualTo("usuarioId", usuarioId)
                .orderBy("creadoEn", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(listener);
    }

    public void obtenerPagosPorEstado(PagoEstado estado, OnCompleteListener<QuerySnapshot> listener) {
        db.collection("pagos")
                .whereEqualTo("estado", estado)
                .get()
                .addOnCompleteListener(listener);
    }

    public void obtenerPagoPorPreferencia(String idPreferencia, OnCompleteListener<QuerySnapshot> listener) {
        db.collection("pagos")
                .whereEqualTo("idPreferencia", idPreferencia)
                .get()
                .addOnCompleteListener(listener);
    }

    public void actualizarEstadoPago(String pagoId, PagoEstado nuevoEstado, OnCompleteListener<Void> listener) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("estado", nuevoEstado);
        updates.put("actualizadoEn", LocalDateTime.now());

        db.collection("pagos").document(pagoId)
                .update(updates)
                .addOnCompleteListener(listener);
    }

    public void eliminarPago(String pagoId, OnCompleteListener<Void> listener) {
        db.collection("pagos").document(pagoId)
                .delete()
                .addOnCompleteListener(listener);
    }
}
