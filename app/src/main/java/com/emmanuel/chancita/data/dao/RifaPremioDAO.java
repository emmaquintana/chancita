package com.emmanuel.chancita.data.dao;

import com.emmanuel.chancita.data.model.RifaPremio;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

public class RifaPremioDAO {
    private final FirebaseFirestore db;

    public RifaPremioDAO() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void crearPremio(RifaPremio premio, OnCompleteListener<Void> listener) {
        String nuevoId = db.collection("rifa_premios").document().getId();
        premio.setPremioId(nuevoId);

        db.collection("rifa_premios").document(nuevoId)
                .set(premio)
                .addOnCompleteListener(listener);
    }

    public void obtenerPremiosPorRifa(String rifaId, OnCompleteListener<QuerySnapshot> listener) {
        db.collection("rifa_premios")
                .whereEqualTo("rifaId", rifaId)
                .orderBy("premioOrden")
                .get()
                .addOnCompleteListener(listener);
    }

    public void obtenerPremio(String premioId, OnCompleteListener<DocumentSnapshot> listener) {
        db.collection("rifa_premios").document(premioId)
                .get()
                .addOnCompleteListener(listener);
    }

    public void actualizarPremio(String premioId, RifaPremio premio, OnCompleteListener<Void> listener) {
        db.collection("rifa_premios").document(premioId)
                .set(premio)
                .addOnCompleteListener(listener);
    }

    public void eliminarPremio(String premioId, OnCompleteListener<Void> listener) {
        db.collection("rifa_premios").document(premioId)
                .delete()
                .addOnCompleteListener(listener);
    }

    public void eliminarPremiosPorRifa(String rifaId, OnCompleteListener<Void> listener) {
        obtenerPremiosPorRifa(rifaId, task -> {
            if (task.isSuccessful()) {
                WriteBatch batch = db.batch();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    batch.delete(doc.getReference());
                }
                batch.commit().addOnCompleteListener(listener);
            }
        });
    }
}
