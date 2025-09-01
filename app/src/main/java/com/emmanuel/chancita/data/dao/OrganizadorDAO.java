package com.emmanuel.chancita.data.dao;

import com.emmanuel.chancita.data.model.Organizador;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class OrganizadorDAO {
    private final FirebaseFirestore db;

    public OrganizadorDAO() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void crearOrganizador(Organizador organizador, OnCompleteListener<Void> listener) {
        // Usar el usuarioId como ID del documento para evitar duplicados
        db.collection("organizadores").document(organizador.getUsuarioId())
                .set(organizador)
                .addOnCompleteListener(listener);
    }

    public void obtenerOrganizador(String usuarioId, OnCompleteListener<DocumentSnapshot> listener) {
        db.collection("organizadores").document(usuarioId)
                .get()
                .addOnCompleteListener(listener);
    }

    public void obtenerOrganizadorPorRifaId(String rifaId, OnCompleteListener<DocumentSnapshot> listener) {
        // Primero obtenemos la rifa para saber quién la creó
        db.collection("rifas").document(rifaId)
                .get()
                .addOnCompleteListener(rifaTask -> {
                    if (rifaTask.isSuccessful() && rifaTask.getResult().exists()) {
                        String creadoPor = rifaTask.getResult().getString("creadoPor");
                        if (creadoPor != null) {
                            obtenerOrganizador(creadoPor, listener);
                        }
                    }
                });
    }

    public void actualizarTokens(String usuarioId, String tokenAcceso, String tokenRefresh,
                                 LocalDateTime expiraEn, OnCompleteListener<Void> listener) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("mpTokenAcceso", tokenAcceso);
        updates.put("tokenRefresh", tokenRefresh);
        updates.put("expiraEn", expiraEn);

        db.collection("organizadores").document(usuarioId)
                .update(updates)
                .addOnCompleteListener(listener);
    }

    public void eliminarOrganizador(String usuarioId, OnCompleteListener<Void> listener) {
        db.collection("organizadores").document(usuarioId)
                .delete()
                .addOnCompleteListener(listener);
    }
}
