package com.emmanuel.chancita.ui.notifications;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.data.model.Notificacion;
import com.emmanuel.chancita.ui.SharedViewModel;
import com.emmanuel.chancita.ui.notifications.adapters.NotificacionAdapter;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel mViewModel;
    private SharedViewModel sharedViewModel;

    public static NotificationsFragment newInstance() {
        return new NotificationsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtiene la instancia del ViewModel compartida con la MainActivity
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        inflarNotificaciones(view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        sharedViewModel.setToolbarTitle("Notificaciones");
    }

    private void inflarNotificaciones(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_notificaciones);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Simula notificaciones (debería recuperarlas desde la BD)
        List<Notificacion> notificaciones = new ArrayList<>();
        notificaciones.add(new Notificacion("15/07/2025 00:00", "¡Ganaste en la rifa “Salvemos a Pepito”!", "El número 7 fue el ganador. Dirígete a la rifa para contactar con el organizador"));
        notificaciones.add(new Notificacion("22/06/2025 23:59", "Mejor suerte para la próxima", "El número 15 fue el ganador en la rifa “Rifa para estudios”."));

        NotificacionAdapter adapter = new NotificacionAdapter(notificaciones);
        recyclerView.setAdapter(adapter);
    }
}