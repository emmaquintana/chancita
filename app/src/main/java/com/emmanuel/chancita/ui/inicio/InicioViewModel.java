package com.emmanuel.chancita.ui.inicio;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emmanuel.chancita.data.AppDatabase;
import com.emmanuel.chancita.data.dao.RifaDaoRoom;
import com.emmanuel.chancita.data.model.Rifa;
import com.emmanuel.chancita.data.model.RifaEntityRoom;
import com.emmanuel.chancita.data.model.RifaEstado;
import com.emmanuel.chancita.data.model.Usuario;
import com.emmanuel.chancita.data.repository.RifaRepository;
import com.emmanuel.chancita.data.repository.RifaRepositoryRoom;
import com.emmanuel.chancita.data.repository.UsuarioRepository;
import com.emmanuel.chancita.utils.Utilidades;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class InicioViewModel extends AndroidViewModel {

    private final RifaRepository rifaRepository;
    private final UsuarioRepository usuarioRepository;
    private final RifaDaoRoom rifaDaoRoom;

    // Obtener rifas creadas por el usuario actual
    private final MutableLiveData<Boolean> _obteniendoRifasCreadas = new MutableLiveData<>();
    public final LiveData<Boolean> obteniendoRifasCreadas = _obteniendoRifasCreadas;
    private final MutableLiveData<String> _resultadoObtencionRifaCreadas = new MutableLiveData<>();
    public final LiveData<String> resultadoObtencionRifaCreadas = _resultadoObtencionRifaCreadas;

    // Obtener rifas a las que el usuario actual se unió
    private final MutableLiveData<Boolean> _obteniendoRifasUnidas = new MutableLiveData<>();
    public final LiveData<Boolean> obteniendoRifasUnidas = _obteniendoRifasUnidas;
    private final MutableLiveData<String> _resultadoObtencionRifaUnidas = new MutableLiveData<>();
    public final LiveData<String> resultadoObtencionRifaUnidas = _resultadoObtencionRifaUnidas;

    public InicioViewModel(@NonNull Application application) {
        super(application);
        this.rifaRepository = new RifaRepository();
        this.usuarioRepository = new UsuarioRepository();
        this.rifaDaoRoom = AppDatabase.getInstance(application).rifaDao();
    }

    public void unirseARifa(String codigo) {
        rifaRepository.unirseARifa(codigo, null);
    }

    public LiveData<List<Rifa>> obtenerRifasCreadasPorUsuarioActual() {
        _obteniendoRifasCreadas.setValue(true);

        return rifaRepository.obtenerRifasCreadasPorUsuarioActual(task -> {
            _obteniendoRifasCreadas.setValue(false);

            if (!task.isSuccessful()) {
                _resultadoObtencionRifaCreadas.setValue("Algo salió mal al obtener las rifas que creaste");
            }
        });
    }

    public LiveData<List<Rifa>> obtenerRifasUnidasDeUsuarioActual() {
        _obteniendoRifasUnidas.setValue(true);

        return rifaRepository.obtenerRifasUnidasDeUsuarioActual(task -> {
            _obteniendoRifasUnidas.setValue(false);

            if (!task.isSuccessful()) {
                _resultadoObtencionRifaUnidas.setValue("Algo salió mal");
            }
        });
    }

    public LiveData<Rifa> obtenerRifaPorCodigo(String codigo) {
        return rifaRepository.obtenerRifaPorCodigo(codigo, null);
    }

    public LiveData<Usuario> obtenerUsuarioActual() {
        return usuarioRepository.obtenerUsuarioActual();
    }

    public LiveData<Boolean> usuarioActualPoseeTokenMercadoPago() {
        return rifaRepository.usuarioActualPoseeTokenMercadoPago();
    }

    public LiveData<List<Rifa>> obtenerRifasRecomendadas() {
        MutableLiveData<List<Rifa>> rifasLiveData = new MutableLiveData<>();
        String usuarioId = FirebaseAuth.getInstance().getUid();

        FirebaseFirestore.getInstance()
                .collection("rifas")
                .get(Source.SERVER)
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
                            Log.e("MAPEO", "Error mapeando rifa: " + e.getMessage());
                        }
                    }

                    rifasLiveData.setValue(rifas);

                    // Guardar en Room para fallback offline
                    Executors.newSingleThreadExecutor().execute(() -> {
                        List<RifaEntityRoom> entities = Utilidades.Converters.toEntityList(rifas);
                        rifaDaoRoom.insertAll(entities);
                    });
                })
                .addOnFailureListener(e -> {
                    // Si falla Firestore, leer desde Room
                    rifaDaoRoom.getAll().observeForever(rifaEntities -> {
                        if (rifaEntities != null) {
                            List<Rifa> rifasCacheadas = Utilidades.Converters.toRifa(rifaEntities);
                            rifasLiveData.postValue(rifasCacheadas);
                        }
                    });

                });

        return rifasLiveData;
    }


    private Rifa mapearRifa(DocumentSnapshot doc) {
        Rifa rifa = new Rifa();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        rifa.setId(doc.getId());
        rifa.setCreadoPor(doc.getString("creadoPor"));
        rifa.setEstado(RifaEstado.valueOf(doc.getString("estado")));

        // participantesIds
        List<String> participantes = (List<String>) doc.get("participantesIds");
        rifa.setParticipantesIds(participantes != null ? participantes : new ArrayList<>());

        // fechaSorteo -> parsear desde string
        String fechaSorteoStr = doc.getString("fechaSorteo");
        if (fechaSorteoStr != null) {
            rifa.setFechaSorteo(LocalDateTime.parse(fechaSorteoStr, formatter));
        }

        // creadoEn -> parsear desde string
        String creadoEnStr = doc.getString("creadoEn");
        LocalDateTime creadoEn = null;
        if (creadoEnStr != null) {
            creadoEn = LocalDateTime.parse(creadoEnStr, formatter);
            rifa.setCreadoEn(creadoEn);
        }


        rifa.setTitulo(doc.getString("titulo"));
        rifa.setDescripcion(doc.getString("descripcion"));
        rifa.setCodigo(doc.getString("codigo"));
        rifa.setId(doc.getId());

        return rifa;
    }

}