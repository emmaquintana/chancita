package com.emmanuel.chancita.data.dao;

import com.emmanuel.chancita.data.model.Notificacion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class NotificacionDAO {
    private final FirebaseFirestore db;

    public NotificacionDAO() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void crearNotificacion(Notificacion notificacion, OnCompleteListener<Void> listener) {
        String nuevoId = db.collection("notificaciones").document().getId();
        notificacion.setId(nuevoId);

        db.collection("notificaciones").document(nuevoId)
                .set(notificacion)
                .addOnCompleteListener(listener);
    }

    public void obtenerNotificacionesPorUsuario(String usuarioId, OnCompleteListener<QuerySnapshot> listener) {
        db.collection("notificaciones")
                .whereEqualTo("usuarioId", usuarioId)
                .orderBy("fecha", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(listener);
    }

    public void obtenerNotificacionesGenerales(OnCompleteListener<QuerySnapshot> listener) {
        db.collection("notificaciones")
                .whereEqualTo("usuarioId", null)
                .orderBy("fecha", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(listener);
    }

    public void eliminarNotificacion(String notificacionId, OnCompleteListener<Void> listener) {
        db.collection("notificaciones").document(notificacionId)
                .delete()
                .addOnCompleteListener(listener);
    }
}
