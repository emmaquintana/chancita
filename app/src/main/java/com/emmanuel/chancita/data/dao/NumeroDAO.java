package com.emmanuel.chancita.data.dao;

import com.emmanuel.chancita.data.model.Numero;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class NumeroDAO {
    private final FirebaseFirestore db;

    public NumeroDAO() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void crearNumero(Numero numero, OnCompleteListener<Void> listener) {
        String nuevoId = db.collection("numeros").document().getId();
        numero.setId(nuevoId);

        db.collection("numeros").document(nuevoId)
                .set(numero)
                .addOnCompleteListener(listener);
    }

    public void obtenerNumerosPorRifa(String rifaId, OnCompleteListener<QuerySnapshot> listener) {
        db.collection("numeros")
                .whereEqualTo("rifaId", rifaId)
                .orderBy("valor")
                .get()
                .addOnCompleteListener(listener);
    }

    public void obtenerNumerosPorUsuario(String usuarioId, OnCompleteListener<QuerySnapshot> listener) {
        db.collection("numeros")
                .whereEqualTo("usuarioId", usuarioId)
                .get()
                .addOnCompleteListener(listener);
    }

    public void obtenerNumerosPorUsuarioYRifa(String usuarioId, String rifaId, OnCompleteListener<QuerySnapshot> listener) {
        db.collection("numeros")
                .whereEqualTo("usuarioId", usuarioId)
                .whereEqualTo("rifaId", rifaId)
                .get()
                .addOnCompleteListener(listener);
    }

    public void verificarNumeroDisponible(String rifaId, int valor, OnCompleteListener<QuerySnapshot> listener) {
        db.collection("numeros")
                .whereEqualTo("rifaId", rifaId)
                .whereEqualTo("valor", valor)
                .get()
                .addOnCompleteListener(listener);
    }

    public void obtenerNumero(String numeroId, OnCompleteListener<DocumentSnapshot> listener) {
        db.collection("numeros").document(numeroId)
                .get()
                .addOnCompleteListener(listener);
    }

    public void eliminarNumero(String numeroId, OnCompleteListener<Void> listener) {
        db.collection("numeros").document(numeroId)
                .delete()
                .addOnCompleteListener(listener);
    }
}
