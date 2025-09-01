package com.emmanuel.chancita.data.dao;

import com.emmanuel.chancita.data.model.PagoNumero;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class PagoNumeroDAO {
    private final FirebaseFirestore db;

    public PagoNumeroDAO() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void crearRelacionPagoNumero(PagoNumero pagoNumero, OnCompleteListener<Void> listener) {
        String docId = pagoNumero.getPagoId() + "_" + pagoNumero.getNumeroId();

        db.collection("pago_numeros").document(docId)
                .set(pagoNumero)
                .addOnCompleteListener(listener);
    }

    public void obtenerNumerosPorPago(String pagoId, OnCompleteListener<QuerySnapshot> listener) {
        db.collection("pago_numeros")
                .whereEqualTo("pagoId", pagoId)
                .get()
                .addOnCompleteListener(listener);
    }

    public void obtenerPagosPorNumero(String numeroId, OnCompleteListener<QuerySnapshot> listener) {
        db.collection("pago_numeros")
                .whereEqualTo("numeroId", numeroId)
                .get()
                .addOnCompleteListener(listener);
    }

    public void eliminarRelacionPagoNumero(String pagoId, String numeroId, OnCompleteListener<Void> listener) {
        String docId = pagoId + "_" + numeroId;

        db.collection("pago_numeros").document(docId)
                .delete()
                .addOnCompleteListener(listener);
    }
}
