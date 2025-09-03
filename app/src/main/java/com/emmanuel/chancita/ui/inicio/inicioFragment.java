package com.emmanuel.chancita.ui.inicio;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.data.dto.RifaDTO;
import com.emmanuel.chancita.data.model.MetodoEleccionGanador;
import com.emmanuel.chancita.data.model.Rifa;
import com.emmanuel.chancita.data.model.RifaEstado;
import com.emmanuel.chancita.ui.SharedViewModel;
import com.emmanuel.chancita.ui.rifa.crear_rifa.CrearRifaActivity;
import com.emmanuel.chancita.ui.inicio.adapters.RifaAdapter;
import com.emmanuel.chancita.ui.rifa.RifaOrganizadorActivity;
import com.emmanuel.chancita.ui.rifa.RifaParticipanteActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class inicioFragment extends Fragment {

    private InicioViewModel inicioViewModel;
    private SharedViewModel sharedViewModel;

    public static inicioFragment newInstance() {
        return new inicioFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtiene la instancia del ViewModel compartida con la MainActivity
        inicioViewModel = new ViewModelProvider(this).get(InicioViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inicio, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        View view = getView();
        if (view == null) return;

        inicioViewModel.obtenerRifasUnidasDeUsuarioActual().observe(getViewLifecycleOwner(), rifasUnidas -> {
            inflarRifasUnidas(view);
            inflarRifasCreadas(view);
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        sharedViewModel.setToolbarTitle("Inicio");

        TextView btnCrearRifa = view.findViewById(R.id.inicio_btn_crear_rifa);
        TextView btnUnirseRifa = view.findViewById(R.id.inicio_btn_unirse_a_rifa);

        btnCrearRifa.setOnClickListener(view1 -> {
            Intent intent = new Intent(requireActivity(), CrearRifaActivity.class);
            startActivity(intent);
        });

        btnUnirseRifa.setOnClickListener(view1 -> {
            mostrarDialogCodigo();
        });

        inflarRifasCreadas(view);
        inflarRifasUnidas(view);
    }

    private void inflarRifasCreadas(View view) {
        RecyclerView rvRifasCreadas = view.findViewById(R.id.recycler_view_rifas_creadas);

        inicioViewModel.obtenerRifasCreadasPorUsuarioActual().observe(getViewLifecycleOwner(), listaRifa -> {
            // Si hay rifas creadas, se las muestra
            if (listaRifa.size() != 0) {

                TextView msgNoRifasCreadas = view.findViewById(R.id.inicio_txt_no_rifas_creadas);
                msgNoRifasCreadas.setVisibility(View.GONE);

                /*
                // Si se "toca" una rifa, se redirige a la actividad correspondiente a esa rifa
                RifaAdapter.OnItemClickListener rifaCreadaListener = new RifaAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(RifaDTO rifa) {
                        Intent intent = new Intent(getContext(), RifaOrganizadorActivity.class);
                        intent.putExtra("rifa_id", rifa.getId());
                        startActivity(intent);
                    }
                };
*/

                // Genera el layout
                rvRifasCreadas.setLayoutManager(new LinearLayoutManager(getContext()));
                RifaAdapter rifasAdapter = new RifaAdapter(listaRifa, null);
                rvRifasCreadas.setAdapter(rifasAdapter);
            }
            else {
                // Si el usuario no posee rifas creadas, se le informa de ello al usuario
                TextView msgNoRifasCreadas = view.findViewById(R.id.inicio_txt_no_rifas_creadas);
                msgNoRifasCreadas.setVisibility(View.VISIBLE);
            }
        });
    }

    private void inflarRifasUnidas(View view) {
        RecyclerView rvRifasUnidas = view.findViewById(R.id.recycler_view_rifas_unidas);

        inicioViewModel.obtenerRifasUnidasDeUsuarioActual().observe(getViewLifecycleOwner(), listaRifas -> {
            if (listaRifas.size() != 0) {

                // Si el usuario no se unió a rifas, se le informa de ello al usuario
                TextView msgNoRifasUnidas = view.findViewById(R.id.inicio_txt_no_rifas_unidas);
                msgNoRifasUnidas.setVisibility(View.GONE);

                // Si se "toca" una rifa, se redirige a la actividad correspondiente
                RifaAdapter.OnItemClickListener rifaUnidaListener = new RifaAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(RifaDTO rifa) {
                        Intent intent = new Intent(getContext(), RifaParticipanteActivity.class);
                        intent.putExtra("rifa_id", rifa.getId());
                        startActivity(intent);
                    }
                };

                // Genera el layout
                rvRifasUnidas.setLayoutManager(new LinearLayoutManager(getContext()));
                RifaAdapter joinedAdapter = new RifaAdapter(listaRifas, rifaUnidaListener);
                rvRifasUnidas.setAdapter(joinedAdapter);
            }
            else {
                // Si el usuario no se unió a rifas, se le informa de ello al usuario
                TextView msgNoRifasUnidas = view.findViewById(R.id.inicio_txt_no_rifas_unidas);
                msgNoRifasUnidas.setVisibility(View.VISIBLE);
            }
        });
    }

    private void mostrarDialogCodigo() {
        View view = getLayoutInflater().inflate(R.layout.dialog_unirse_rifa, null);

        TextInputEditText input = view.findViewById(R.id.dialog_unirse_rifa_tiet_codigo);
        MaterialButton btnCancelar = view.findViewById(R.id.dialog_unirse_rifa_btn_cancelar);
        MaterialButton btnUnirse = view.findViewById(R.id.dialog_unirse_rifa_btn_unirse);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(view)
                .create();

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnUnirse.setOnClickListener(v -> {
            String codigo = input.getText().toString().trim();
            if (!codigo.isEmpty()) {
                inicioViewModel.unirseARifa(codigo);

                inicioViewModel.obtenerRifaPorCodigo(codigo).observe(getViewLifecycleOwner(), rifa -> {
                    if (rifa != null) {
                        Intent intent = new Intent(getContext(), RifaParticipanteActivity.class);
                        intent.putExtra("rifa_id", rifa.getId());
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(requireContext(), "No existe rifa con el código " + codigo, Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.dismiss();
            } else {
                Toast.makeText(requireContext(), "Ingrese un código válido", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }


}