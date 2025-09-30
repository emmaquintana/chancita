package com.emmanuel.chancita.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.emmanuel.chancita.data.AppDatabase;
import com.emmanuel.chancita.data.dao.RifaDaoRoom;
import com.emmanuel.chancita.data.model.MetodoEleccionGanador;
import com.emmanuel.chancita.data.model.NumeroComprado;
import com.emmanuel.chancita.data.model.Rifa;
import com.emmanuel.chancita.data.model.RifaEntityRoom;
import com.emmanuel.chancita.data.model.RifaEstado;
import com.emmanuel.chancita.data.model.RifaPremio;
import com.emmanuel.chancita.utils.Utilidades;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RifaRepositoryRoom {
    private final RifaDaoRoom rifaDao;
    public final FirebaseFirestore firestore;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public RifaRepositoryRoom(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        rifaDao = db.rifaDao();
        firestore = FirebaseFirestore.getInstance();
    }

    public LiveData<List<RifaEntityRoom>> obtenerTodasLocal() {
        return rifaDao.getAll();
    }

    public void sincronizarConFirestore() {
        firestore.collection("rifas").get()
                .addOnSuccessListener(querySnapshot -> {
                    List<RifaEntityRoom> entities = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot) {
                        RifaEntityRoom e = new RifaEntityRoom();
                        e.setId(doc.getId());
                        e.setTitulo(doc.getString("titulo"));
                        e.setEstado(doc.getString("estado"));
                        e.setCreadoEn(doc.getString("creadoEn"));
                        e.setCodigo(doc.getString("codigo"));
                        e.setFechaSorteo(doc.getString("fechaSorteo"));

                        entities.add(e);
                    }

                    Executors.newSingleThreadExecutor().execute(() -> {
                        rifaDao.insertAll(entities);
                    });
                });
    }

    /** Obtener rifas recomendadas: primero intenta Firestore, si falla usa Room */
    public LiveData<List<Rifa>> obtenerRifasRecomendadas(String usuarioId) {
        MutableLiveData<List<Rifa>> rifasLiveData = new MutableLiveData<>();

        firestore.collection("rifas")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Rifa> rifas = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        try {
                            Rifa rifa = mapearRifa(doc);
                            if (!rifa.getCreadoPor().equals(usuarioId)
                                    && !rifa.getParticipantesIds().contains(usuarioId)
                                    && rifa.getEstado() == RifaEstado.ABIERTO) {
                                rifas.add(rifa);
                            }
                        } catch (Exception e) {
                            Log.e("RifaRepoRoom", "Error mapeando rifa: " + e.getMessage());
                        }
                    }

                    rifasLiveData.setValue(rifas);

                    // Actualizar Room
                    executor.execute(() -> {
                        List<RifaEntityRoom> entities = Utilidades.Converters.toEntityList(rifas);
                        rifaDao.insertAll(entities);
                    });

                })
                .addOnFailureListener(e -> {
                    // Fallback a Room
                    rifaDao.getAll().observeForever(entities -> {
                        if (entities != null) {
                            List<Rifa> rifasCacheadas = Utilidades.Converters.toRifa(entities);
                            rifasLiveData.postValue(rifasCacheadas);
                        }
                    });
                });

        return rifasLiveData;
    }

    /** Convierte DocumentSnapshot a Rifa (igual que en tu ViewModel) */
    public Rifa mapearRifa(DocumentSnapshot doc) {
        return new Rifa(
                doc.getId(),
                doc.getString("titulo"),
                doc.getString("descripcion"),
                doc.getLong("cantNumeros") != null ? doc.getLong("cantNumeros").intValue() : 0,
                doc.getString("creadoPor"),
                RifaEstado.valueOf(doc.getString("estado")),
                doc.getString("creadoEn") != null ? LocalDateTime.parse(doc.getString("creadoEn"), Utilidades.Converters.formatter) : null,
                doc.getString("codigo"),
                MetodoEleccionGanador.valueOf(doc.getString("metodoEleccionGanador")),
                doc.getString("motivoEleccionGanador"),
                doc.getString("fechaSorteo") != null ? LocalDateTime.parse(doc.getString("fechaSorteo"), Utilidades.Converters.formatter) : null,
                doc.getDouble("precioNumero") != null ? doc.getDouble("precioNumero") : 0,
                doc.get("participantesIds") != null ? (List<String>) doc.get("participantesIds") : new ArrayList<>(),
                doc.get("premios") != null ? (List<RifaPremio>) doc.get("premios") : new ArrayList<>(),
                doc.get("numerosComprados") != null ? (List<NumeroComprado>) doc.get("numerosComprados") : new ArrayList<>()
        );
    }
}