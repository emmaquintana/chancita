package com.emmanuel.chancita.ui.notificaciones;

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
import android.widget.TextView;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.data.model.Notificacion;
import com.emmanuel.chancita.ui.SharedViewModel;
import com.emmanuel.chancita.ui.notificaciones.adapters.NotificacionAdapter;
import com.emmanuel.chancita.utils.Utilidades;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificacionesFragment extends Fragment {

    private NotificacionesViewModel mViewModel;
    private SharedViewModel sharedViewModel;

    public static NotificacionesFragment newInstance() {
        return new NotificacionesFragment();
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
        View view = inflater.inflate(R.layout.fragment_notificaciones, container, false);

        inflarNotificaciones(view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        sharedViewModel.setToolbarTitle("Notificaciones");
    }

    private void inflarNotificaciones(View view) {

        List<Notificacion> notificaciones = new ArrayList<>();

        if (notificaciones.isEmpty()) {
            TextView txtNoHayNotificaciones = view.findViewById(R.id.notificacion_txt_no_hay_notificaciones);
            txtNoHayNotificaciones.setVisibility(View.VISIBLE);
        }
        else {
            RecyclerView recyclerView = view.findViewById(R.id.recycler_view_notificaciones);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            NotificacionAdapter adapter = new NotificacionAdapter(notificaciones);
            recyclerView.setAdapter(adapter);
        }
    }
}