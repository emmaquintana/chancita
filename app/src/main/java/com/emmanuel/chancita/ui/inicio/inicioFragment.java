package com.emmanuel.chancita.ui.inicio;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.data.model.Rifa;
import com.emmanuel.chancita.data.model.RifaEstado;
import com.emmanuel.chancita.data.model.Usuario;
import com.emmanuel.chancita.ui.SharedViewModel;
import com.emmanuel.chancita.ui.rifa.crear_rifa.CrearRifaActivity;
import com.emmanuel.chancita.ui.inicio.adapters.RifaAdapter;
import com.emmanuel.chancita.ui.rifa.RifaOrganizadorActivity;
import com.emmanuel.chancita.ui.rifa.RifaParticipanteActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.Collections;
import java.util.List;

public class inicioFragment extends Fragment {

    private InicioViewModel inicioViewModel;
    private SharedViewModel sharedViewModel;
    private Usuario usuario;
    private Boolean poseeTokenMP;

    // Vistas
    private TextView btnCrearRifa;
    private TextView btnUnirseRifa;
    private RecyclerView rvRifasCreadas;
    private RecyclerView rvRifasUnidas;
    private TextView msgNoRifasCreadas;
    private TextView msgNoRifasUnidas;
    private ProgressBar progressBarCreadas;
    private ProgressBar progressBarUnidas;
    private RecyclerView rvRifasDisponibles;
    private TextView msgNoRifasDisponibles;
    private ProgressBar progressBarDisponibles;
    private ImageButton btnPrev;
    private ImageButton btnNext;
    private SnapHelper snapHelperDisponibles;




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

        progressBarCreadas.setVisibility(View.VISIBLE);
        progressBarUnidas.setVisibility(View.VISIBLE);
        progressBarDisponibles.setVisibility(View.VISIBLE);
        btnNext.setVisibility(View.GONE);
        btnPrev.setVisibility(View.GONE);

        View view = getView();
        if (view == null) return;

        // Actualiza datos
        inicioViewModel.obtenerRifasUnidasDeUsuarioActual().observe(getViewLifecycleOwner(), rifasUnidas -> {
            inflarRifasUnidas(rifasUnidas);
        });

        inicioViewModel.obtenerRifasCreadasPorUsuarioActual().observe(getViewLifecycleOwner(), rifasCreadas -> {
            inflarRifasCreadas(rifasCreadas);
        });

        inicioViewModel.obtenerRifasRecomendadas().observe(getViewLifecycleOwner(), rifasRecomendadas -> {
            inflarRifasDisponibles(rifasRecomendadas);
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        sharedViewModel.setToolbarTitle("Inicio");

        btnCrearRifa = view.findViewById(R.id.inicio_btn_crear_rifa);
        btnUnirseRifa = view.findViewById(R.id.inicio_btn_unirse_a_rifa);
        rvRifasCreadas = view.findViewById(R.id.recycler_view_rifas_creadas);
        rvRifasUnidas = view.findViewById(R.id.recycler_view_rifas_unidas);
        msgNoRifasCreadas = view.findViewById(R.id.inicio_txt_no_rifas_creadas);
        msgNoRifasUnidas = view.findViewById(R.id.inicio_txt_no_rifas_unidas);
        progressBarCreadas = view.findViewById(R.id.inicio_cargando_creadas);
        progressBarUnidas = view.findViewById(R.id.inicio_cargando_unidas);
        rvRifasDisponibles = view.findViewById(R.id.recycler_view_rifas_recomendadas);
        msgNoRifasDisponibles = view.findViewById(R.id.inicio_txt_no_rifas_recomendadas);
        progressBarDisponibles = view.findViewById(R.id.inicio_cargando_recomendadas);
        btnNext = view.findViewById(R.id.inicio_btn_siguiente);
        btnPrev = view.findViewById(R.id.inicio_btn_previo);
        snapHelperDisponibles = new LinearSnapHelper();
        snapHelperDisponibles.attachToRecyclerView(rvRifasDisponibles);



        setearObservers();

        setearListeners();
    }

    // Muestra ventana de dialogo que permite al usuario unirse a una rifa ingresando un código
    private void mostrarDialogCodigo() {
        View view = getLayoutInflater().inflate(R.layout.dialog_unirse_rifa, null);

        TextInputEditText input = view.findViewById(R.id.dialog_unirse_rifa_tiet_codigo);
        MaterialButton btnEscanear = view.findViewById(R.id.dialog_unirse_rifa_btn_escanear);
        MaterialButton btnCancelar = view.findViewById(R.id.dialog_unirse_rifa_btn_cancelar);
        MaterialButton btnUnirse = view.findViewById(R.id.dialog_unirse_rifa_btn_unirse);

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .create();

        btnEscanear.setOnClickListener(v -> {
            dialog.dismiss(); // cierro el diálogo para abrir el scanner
            new IntentIntegrator(requireActivity())
                    .setPrompt("Escanea el QR de la rifa")
                    .setOrientationLocked(false)
                    .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                    .initiateScan();
        });


        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnUnirse.setOnClickListener(v -> {
            String codigo = input.getText().toString().trim();
            if (!codigo.isEmpty()) {
                inicioViewModel.obtenerRifaPorCodigo(codigo).observe(getViewLifecycleOwner(), rifa -> {
                    if (rifa != null) {
                        if (rifa.getCreadoPor().equals(usuario.getId())) {
                            new MaterialAlertDialogBuilder(requireContext())
                                    .setTitle("Oops!")
                                    .setMessage("No te puedes unir a tu propia rifa.")
                                    .setPositiveButton("Aceptar", null)
                                    .show();

                            return;
                        }

                        if (rifa.getEstado() == RifaEstado.ABIERTO) {
                            inicioViewModel.unirseARifa(codigo);
                            Intent intent = new Intent(getContext(), RifaParticipanteActivity.class);
                            intent.putExtra("rifa_id", rifa.getId());
                            startActivity(intent);
                        }
                        else if (rifa.getEstado() == RifaEstado.CERRADO) {
                            new MaterialAlertDialogBuilder(requireContext())
                                    .setTitle("Oops!")
                                    .setMessage("La rifa con el código \"" + codigo + "\" ya no posee números disponibles")
                                    .setPositiveButton("Aceptar", null)
                                    .show();
                        }
                        else {
                            new MaterialAlertDialogBuilder(requireContext())
                                    .setTitle("Oops!")
                                    .setMessage("La rifa con el código \"" + codigo + "\" ya se sorteó")
                                    .setPositiveButton("Aceptar", null)
                                    .show();
                        }
                    }
                    else {
                        Snackbar.make(getView(), "No existe rifa con el código " + codigo, Snackbar.LENGTH_SHORT).show();
                    }
                });

                dialog.dismiss();
            } else {
                Snackbar.make(getView(), "Ingresa un código válido", Snackbar.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    public void setearObservers() {
        // Obtiene al usuario actual
        inicioViewModel.obtenerUsuarioActual().observe(getViewLifecycleOwner(), usuario -> {
            this.usuario = usuario;
        });

        // Obtiene rifas creadas por el usuario
        inicioViewModel.obtenerRifasCreadasPorUsuarioActual().observe(getViewLifecycleOwner(), listaRifa -> {
            if (listaRifa != null) {
                inflarRifasCreadas(listaRifa);
            }
        });

        inicioViewModel.obtenerRifasUnidasDeUsuarioActual().observe(getViewLifecycleOwner(), listaRifas -> {
            if (listaRifas != null) {
                inflarRifasUnidas(listaRifas);
            }
        });

        inicioViewModel.usuarioActualPoseeTokenMercadoPago().observe(getViewLifecycleOwner(), poseeTokenMP -> {
            this.poseeTokenMP = poseeTokenMP;
        });

        inicioViewModel.obtenerRifasRecomendadas().observe(getViewLifecycleOwner(), listaRifas -> {
            if (listaRifas != null) {
                inflarRifasDisponibles(listaRifas);
            }
        });

    }

    public void setearListeners() {
        btnCrearRifa.setOnClickListener(view1 -> {
            if (usuario != null && poseeTokenMP) {
                Intent intent = new Intent(requireActivity(), CrearRifaActivity.class);
                intent.putExtra("startStep2", true);
                startActivity(intent);
            }
        });

        btnUnirseRifa.setOnClickListener(view1 -> {
            mostrarDialogCodigo();
        });
    }

    private void inflarRifasUnidas(List<Rifa> listaRifas) {
        progressBarUnidas.setVisibility(View.GONE);


        if (listaRifas.size() != 0) {

            // Si el usuario no se unió a rifas, se le informa de ello al usuario
            msgNoRifasUnidas.setVisibility(View.GONE);

            // Si se "toca" una rifa, se redirige a la actividad correspondiente
            RifaAdapter.OnItemClickListener rifaUnidaListener = rifa -> {
                Intent intent = new Intent(getContext(), RifaParticipanteActivity.class);
                intent.putExtra("rifa_id", rifa.getId());
                startActivity(intent);
            };

            // Genera el layout
            rvRifasUnidas.setLayoutManager(new LinearLayoutManager(getContext()));
            rvRifasUnidas.setVisibility(View.VISIBLE);
            RifaAdapter joinedAdapter = new RifaAdapter(listaRifas, rifaUnidaListener);
            rvRifasUnidas.setAdapter(joinedAdapter);
        }
        else {
            // Si el usuario no se unió a rifas, se le informa de ello al usuario
            rvRifasUnidas.setAdapter(null);
            rvRifasUnidas.setVisibility(View.GONE);
            msgNoRifasUnidas.setVisibility(View.VISIBLE);
        }
    }

    private void inflarRifasCreadas(List<Rifa> listaRifas) {
        progressBarCreadas.setVisibility(View.GONE);

        // Si hay rifas creadas, se las muestra
        if (listaRifas.size() != 0) {

            msgNoRifasCreadas.setVisibility(View.GONE);

            // Si se "toca" una rifa, se redirige a la actividad correspondiente a esa rifa
            RifaAdapter.OnItemClickListener rifaCreadaListener = rifa -> {
                Intent intent = new Intent(getContext(), RifaOrganizadorActivity.class);
                intent.putExtra("rifa_id", rifa.getId());
                startActivity(intent);
            };

            // Genera el layout
            rvRifasCreadas.setLayoutManager(new LinearLayoutManager(getContext()));
            RifaAdapter rifasAdapter = new RifaAdapter(listaRifas, rifaCreadaListener);
            rvRifasCreadas.setAdapter(rifasAdapter);
        }
        else {
            msgNoRifasCreadas.setVisibility(View.VISIBLE);
        }
    }

    private void inflarRifasDisponibles(List<Rifa> listaRifas) {
        progressBarDisponibles.setVisibility(View.GONE);

        if (!listaRifas.isEmpty()) {
            msgNoRifasDisponibles.setVisibility(View.GONE);
            if (listaRifas.size() != 1) {
                btnNext.setVisibility(View.VISIBLE);
                btnPrev.setVisibility(View.VISIBLE);
            }
            rvRifasDisponibles.setVisibility(View.VISIBLE);

            RifaAdapter.OnItemClickListener listener = rifa -> {
                inicioViewModel.unirseARifa(rifa.getCodigo());
                Intent intent = new Intent(getContext(), RifaParticipanteActivity.class);
                intent.putExtra("rifa_id", rifa.getId());
                startActivity(intent);
            };

            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            rvRifasDisponibles.setLayoutManager(layoutManager);

            Collections.shuffle(listaRifas);
            List<Rifa> maxCinco = listaRifas.size() > 5 ? listaRifas.subList(0, 5) : listaRifas;
            RifaAdapter adapter = new RifaAdapter(maxCinco, listener);
            // --- Botón siguiente ---
            btnNext.setOnClickListener(v -> {
                int nextPos = layoutManager.findFirstVisibleItemPosition() + 1;
                if (nextPos < adapter.getItemCount()) {
                    rvRifasDisponibles.smoothScrollToPosition(nextPos);
                }
            });

            // --- Botón anterior ---
            btnPrev.setOnClickListener(v -> {
                int prevPos = layoutManager.findFirstVisibleItemPosition() - 1;
                if (prevPos >= 0) {
                    rvRifasDisponibles.smoothScrollToPosition(prevPos);
                }
            });
            rvRifasDisponibles.setAdapter(adapter);
        } else {
            msgNoRifasDisponibles.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.GONE);
            btnPrev.setVisibility(View.GONE);
            rvRifasDisponibles.setVisibility(View.GONE);
        }
    }

}