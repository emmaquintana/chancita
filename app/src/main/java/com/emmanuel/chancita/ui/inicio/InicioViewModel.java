package com.emmanuel.chancita.ui.inicio;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emmanuel.chancita.data.model.Rifa;
import com.emmanuel.chancita.data.model.RifaEstado;
import com.emmanuel.chancita.data.model.Usuario;
import com.emmanuel.chancita.data.repository.RifaRepository;
import com.emmanuel.chancita.data.repository.UsuarioRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class InicioViewModel extends ViewModel {

    private final RifaRepository rifaRepository;
    private final UsuarioRepository usuarioRepository;

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

    public InicioViewModel() {
        this.rifaRepository = new RifaRepository();
        this.usuarioRepository = new UsuarioRepository();
    }

    public LiveData<Rifa> obtenerRifa(String rifaId) {
        _obteniendoRifasCreadas.setValue(true);

        return rifaRepository.obtenerRifa(rifaId, task -> {
            _obteniendoRifasCreadas.setValue(false);

            if (!task.isSuccessful()) {
                _resultadoObtencionRifaCreadas.setValue("Algo salió mal");
            }
        });
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
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Rifa> rifas = new ArrayList<>();

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        try {
                            Rifa rifa = mapearRifa(doc);

                            // Filtrar para que no aparezcan rifas creadas ni unidas por el usuario y aparezcan las abiertas
                            if (
                                    !rifa.getCreadoPor().equals(usuarioId)
                                    && !rifa.getParticipantesIds().contains(usuarioId)
                                    && rifa.getEstado() == RifaEstado.ABIERTO
                            ) {
                                rifas.add(rifa);
                            }
                        } catch (Exception e) {
                            Log.e("MAPEO", "Error mapeando rifa: " + e.getMessage());
                        }
                    }

                    rifasLiveData.setValue(rifas);
                })
                .addOnFailureListener(e -> rifasLiveData.setValue(new ArrayList<>()));

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