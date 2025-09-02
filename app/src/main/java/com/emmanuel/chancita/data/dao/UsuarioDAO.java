package com.emmanuel.chancita.data.dao;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.emmanuel.chancita.data.model.Usuario;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsuarioDAO {
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    public UsuarioDAO() {
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    // Método para obtener un usuario por ID
    public void getUsuario(String userId, OnCompleteListener<DocumentSnapshot> listener) {
        db.collection("usuarios").document(userId)
                .get()
                .addOnCompleteListener(listener);
    }

    public Task<DocumentSnapshot> obtenerUsuarioActual() {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null) {
            return Tasks.forException(new Exception("No hay usuario logueado."));
        }
        Log.println(Log.INFO, "ID", firebaseUser.getUid());
        return db.collection("usuarios").document(firebaseUser.getUid()).get();
    }

    public void crearUsuario(Usuario usuario, OnCompleteListener<Void> listener) {
// Crear usuario en Firebase Auth primero
        auth.createUserWithEmailAndPassword(usuario.getCorreo(), usuario.getContraseña())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Usuario creado exitosamente en Auth
                        FirebaseUser firebaseUser = task.getResult().getUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();

                            // Actualizar el ID del usuario con el UID de Firebase Auth
                            usuario.setId(userId);

                            // Crear el documento en Firestore (sin la contraseña por seguridad)
                            Map<String, Object> usuarioData = new HashMap<>();
                            usuarioData.put("correo", usuario.getCorreo());
                            usuarioData.put("nombre", usuario.getNombre());
                            usuarioData.put("apellido", usuario.getApellido());
                            usuarioData.put("nroCelular", usuario.getNroCelular());
                            usuarioData.put("fechaNacimiento", usuario.getFechaNacimiento().toString());
                            usuarioData.put("creadoEn", usuario.getCreadoEn().toString());
                            usuarioData.put("ultimoIngreso", null);


                            // Guardar en Firestore usando el mismo UID
                            db.collection("usuarios")
                                    .document(userId)
                                    .set(usuarioData)
                                    .addOnCompleteListener(firestoreTask -> {
                                        if (firestoreTask.isSuccessful()) {
                                            listener.onComplete(Tasks.forResult(null));
                                        } else {
                                            // Error al guardar en Firestore, eliminar usuario de Auth
                                            firebaseUser.delete().addOnCompleteListener(deleteTask -> {
                                                // Notificar el error original de Firestore
                                                listener.onComplete(Tasks.forException(
                                                        firestoreTask.getException() != null ?
                                                                firestoreTask.getException() :
                                                                new Exception("Error al guardar en Firestore")
                                                ));
                                            });
                                        }
                                    });
                        } else {
                            listener.onComplete(Tasks.forException(new Exception("Error al obtener usuario de Auth")));
                        }
                    } else {
                        // Error al crear usuario en Auth
                        listener.onComplete(Tasks.forException(
                                task.getException() != null ?
                                        task.getException() :
                                        new Exception("Error al crear usuario en Auth")
                        ));
                    }
                });
    }

    /**
     * Actualiza los datos del usuario actual en Firestore y su correo en Auth si cambia.
     */
    public void actualizarUsuarioActual(String nuevoNombre, String nuevoApellido, String nuevoCorreo,
                                        String nuevoNroCelular, OnCompleteListener<Void> listener) {

        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null) {
            listener.onComplete(Tasks.forException(new Exception("No hay usuario logueado")));
            return;
        }

        String userId = firebaseUser.getUid();
        Map<String, Object> campos = new HashMap<>();
        campos.put("nombre", nuevoNombre);
        campos.put("apellido", nuevoApellido);
        campos.put("nroCelular", nuevoNroCelular);

        // Actualizar correo en Auth si cambió
        Task<Void> authTask;
        if (!firebaseUser.getEmail().equals(nuevoCorreo)) {
            authTask = firebaseUser.updateEmail(nuevoCorreo);
        } else {
            authTask = Tasks.forResult(null);
        }

        authTask.addOnCompleteListener(authResult -> {
            if (authResult.isSuccessful()) {
                // Actualizar Firestore
                campos.put("correo", nuevoCorreo);
                db.collection("usuarios")
                        .document(userId)
                        .update(campos)
                        .addOnCompleteListener(listener);
            } else {
                // Error al actualizar Auth
                listener.onComplete(Tasks.forException(authResult.getException()));
            }
        });
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