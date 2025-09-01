package com.emmanuel.chancita.data.dao;

import com.emmanuel.chancita.data.model.Rifa;
import com.emmanuel.chancita.data.model.RifaEstado;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class RifaDAO {
    private final FirebaseFirestore db;

    public RifaDAO() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void crearRifa(Rifa rifa, OnCompleteListener<Void> listener) {
        String nuevoId = db.collection("rifas").document().getId();
        rifa.setId(nuevoId);

        db.collection("rifas").document(nuevoId)
                .set(rifa)
                .addOnCompleteListener(listener);
    }

    public void obtenerRifa(String rifaId, OnCompleteListener<DocumentSnapshot> listener) {
        db.collection("rifas").document(rifaId)
                .get()
                .addOnCompleteListener(listener);
    }

    public void obtenerRifasPorUsuario(String usuarioId, OnCompleteListener<QuerySnapshot> listener) {
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
