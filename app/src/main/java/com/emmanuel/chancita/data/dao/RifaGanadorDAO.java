package com.emmanuel.chancita.data.dao;

import com.emmanuel.chancita.data.model.RifaGanador;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class RifaGanadorDAO {
    private final FirebaseFirestore db;

    public RifaGanadorDAO() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void asignarGanador(RifaGanador ganador, OnCompleteListener<Void> listener) {
        String nuevoId = db.collection("rifa_ganadores").document().getId();
        ganador.setId(nuevoId);

        db.collection("rifa_ganadores").document(nuevoId)
                .set(ganador)
                .addOnCompleteListener(listener);
    }

    public void obtenerGanadoresPorRifa(String rifaId, OnCompleteListener<QuerySnapshot> listener) {
        db.collection("rifa_ganadores")
                .whereEqualTo("rifaId", rifaId)
                .orderBy("asignadoEn")
                .get()
                .addOnCompleteListener(listener);
    }

    public void obtenerGanadoresPorUsuario(String usuarioId, OnCompleteListener<QuerySnapshot> listener) {
        db.collection("rifa_ganadores")
                .whereEqualTo("usuarioId", usuarioId)
                .orderBy("asignadoEn")
                .get()
                .addOnCompleteListener(listener);
    }
}
