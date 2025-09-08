package com.emmanuel.chancita.ui.notificaciones;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.data.model.Notificacion;
import com.emmanuel.chancita.ui.SharedViewModel;
import com.emmanuel.chancita.ui.inicio.InicioViewModel;
import com.emmanuel.chancita.ui.notificaciones.adapters.NotificacionAdapter;
import com.emmanuel.chancita.utils.Utilidades;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificacionesFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView txtNoHayNotificaciones;
    private NotificacionAdapter adapter;
    private List<Notificacion> notificaciones = new ArrayList<>();
    private SharedViewModel sharedViewModel; // Para toolbar

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notificaciones, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_notificaciones);
        txtNoHayNotificaciones = view.findViewById(R.id.notificacion_txt_no_hay_notificaciones);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotificacionAdapter(notificaciones);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedViewModel.setToolbarTitle("Notificaciones");
        cargarNotificacionesDesdeFirestore();
    }

    private void cargarNotificacionesDesdeFirestore() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.w("NotificacionesFragment", "Usuario no autenticado");
            mostrarEstadoSinNotificaciones();
            return;
        }

        Log.d("NotificacionesFragment", "Cargando notificaciones para usuario: " + user.getUid());

        FirebaseFirestore.getInstance().collection("notificaciones")
                .whereEqualTo("usuarioId", user.getUid())
                .orderBy("fecha", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Log.d("NotificacionesFragment", "Consulta exitosa. Documentos encontrados: " + task.getResult().size());

                        notificaciones.clear();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            try {
                                Notificacion notificacion = doc.toObject(Notificacion.class);
                                notificacion.setId(doc.getId());
                                notificaciones.add(notificacion);
                                Log.d("NotificacionesFragment", "Notificación cargada: " + notificacion.getTitulo());
                            } catch (Exception e) {
                                Log.e("NotificacionesFragment", "Error al convertir documento: " + doc.getId(), e);
                            }
                        }

                        // Actualizar UI en el hilo principal
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                actualizarUI();
                            });
                        }
                    } else {
                        Log.e("NotificacionesFragment", "Error en consulta", task.getException());
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                mostrarEstadoSinNotificaciones();
                            });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("NotificacionesFragment", "Error al cargar notificaciones", e);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            mostrarEstadoSinNotificaciones();
                        });
                    }
                });
    }

    private void actualizarUI() {
        if (notificaciones.isEmpty()) {
            mostrarEstadoSinNotificaciones();
        } else {
            Log.d("NotificacionesFragment", "Mostrando " + notificaciones.size() + " notificaciones");
            txtNoHayNotificaciones.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }

    private void mostrarEstadoSinNotificaciones() {
        txtNoHayNotificaciones.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Recargar notificaciones cuando el fragment vuelve a ser visible
        cargarNotificacionesDesdeFirestore();
    }
}