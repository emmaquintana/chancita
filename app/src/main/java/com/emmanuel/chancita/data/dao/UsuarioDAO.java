package com.emmanuel.chancita.data.dao;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.emmanuel.chancita.data.model.Usuario;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsuarioDAO {
    private final FirebaseFirestore db;

    public UsuarioDAO() {
        this.db = FirebaseFirestore.getInstance();
    }

    // Método para obtener un usuario por ID
    public void getUsuario(String userId, OnCompleteListener<DocumentSnapshot> listener) {
        db.collection("usuarios").document(userId)
                .get()
                .addOnCompleteListener(listener);
    }

    public void crearUsuario(Usuario usuario, OnCompleteListener<Void> listener) {
        // Genera un ID único para el documento en la colección "usuarios"
        String nuevoId = db.collection("usuarios").document().getId();

        // Se lo asigna al usuario pasado por parametro
        usuario.setId(nuevoId);

        // Se crea un usuario con
        db.collection("usuarios").document(nuevoId)
                .set(usuario)
                .addOnCompleteListener(listener);

    }

    public void eliminarUsuario(String usuarioId, OnCompleteListener<Void> listener) {
        db.collection("usuarios").document(usuarioId)
                .delete()
                .addOnCompleteListener(listener);
    }

    public void obtenerParticipantes(List<String> participanteIds, OnCompleteListener<QuerySnapshot> listener) {
        // Firestore solo permite 10 elementos en la lista de 'whereIn'
        // Si tu lista puede ser más grande, necesitarías un manejo adicional (ej. paginación o múltiples consultas).
        if (participanteIds == null || participanteIds.isEmpty()) {
            return;
        }

        db.collection("usuarios")
                .whereIn("id", participanteIds)
                .get()
                .addOnCompleteListener(listener);
    }

    public void obtenerOrganizador(String organizadorId, OnCompleteListener<Usuario> listener) {
        db.collection("usuarios").document(organizadorId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        Usuario usuario = task.getResult().toObject(Usuario.class);
                        listener.onComplete(task.continueWith(t -> {
                            return usuario;
                        }));
                    } else {
                        listener.onComplete(task.continueWith(t -> {
                            return null;
                        }));
                    }
                });
    }
}