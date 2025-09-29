package com.emmanuel.chancita.ui.rifa;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.data.model.GanadorInfo;
import com.emmanuel.chancita.data.model.MetodoEleccionGanador;
import com.emmanuel.chancita.data.model.NumeroComprado;
import com.emmanuel.chancita.data.model.PremioAsignacion;
import com.emmanuel.chancita.data.model.Rifa;
import com.emmanuel.chancita.data.model.RifaEstado;
import com.emmanuel.chancita.data.model.RifaPremio;
import com.emmanuel.chancita.ui.SharedViewModel;
import com.emmanuel.chancita.ui.rifa.adapters.EleccionGanadoresAdapter;
import com.emmanuel.chancita.ui.rifa.adapters.GanadorInfoAdapter;
import com.emmanuel.chancita.ui.rifa.adapters.ParticipantesAdapter;
import com.emmanuel.chancita.ui.rifa.model.Participante;
import com.emmanuel.chancita.utils.Utilidades;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class RifaOrganizadorFragment extends Fragment {

    private RifaOrganizadorViewModel rifaOrganizadorViewModel;
    private SharedViewModel sharedViewModel;
    private String rifaId;
    private NavController navController;

    public static RifaOrganizadorFragment newInstance() {
        return new RifaOrganizadorFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rifa_organizador, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rifaOrganizadorViewModel = new ViewModelProvider(this).get(RifaOrganizadorViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        navController = NavHostFragment.findNavController(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        rifaId = sharedViewModel.getRifaId();
        Log.d("RifaParticipanteFragment", "rifaId obtenido de SharedViewModel en onViewCreated: " + rifaId);

        // Validar que rifaId no sea null antes de hacer llamadas
        if (rifaId == null || rifaId.trim().isEmpty()) {
            Log.e("RifaParticipanteFragment", "Error: rifaId es null o vacío en Fragment");
            Snackbar.make(getView(), "Error: No se pudo cargar la rifa", Snackbar.LENGTH_SHORT).show();
            if (getActivity() != null) {
                getActivity().finish();
            }
            return;
        }

        TextView txtRifaTitulo = view.findViewById(R.id.rifa_organizador_txt_titulo_rifa);
        TextView txtRifaRecaudado = view.findViewById(R.id.rifa_organizador_txt_monto_recaudado); // Debe iniciar con "Monto recaudado: $"
        TextView txtRifaEstado = view.findViewById(R.id.rifa_organizador_txt_info_estado);
        TextView txtRifaCodigo = view.findViewById(R.id.rifa_organizador_txt_info_codigo);
        TextView txtRifaFechaSorteo = view.findViewById(R.id.rifa_organizador_txt_info_fecha);
        TextView txtRifaMetodoEleccionGanador = view.findViewById(R.id.rifa_organizador_txt_info_metodo);
        TextView txtRifaPremios = view.findViewById(R.id.rifa_organizador_txt_premios);
        TextView txtRifaDescripcion = view.findViewById(R.id.rifa_organizador_txt_info_descripcion);
        TextView txtPrecioNumero = view.findViewById(R.id.rifa_organizador_txt_info_precio);
        TextView txtRifaCodigoInfo = view.findViewById(R.id.rifa_organizador_txt_codigo_info);
        MaterialButton btnCompartirRifa = view.findViewById(R.id.rifa_participante_btn_compartir_rifa);
        FloatingActionButton fab = view.findViewById(R.id.rifa_organizador_fab_editar);

        fab.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_rifaOrganizadorFragment_to_editarRifaFragment)
        );

        rifaOrganizadorViewModel.obtenerRifa(rifaId).observe(getViewLifecycleOwner(), rifa -> {

            btnCompartirRifa.setVisibility(View.VISIBLE);
            txtRifaTitulo.setText(rifa.getTitulo());
            txtRifaEstado.setText(Utilidades.capitalizar(rifa.getEstado().toString()));
            txtRifaCodigo.setText(rifa.getCodigo());
            txtRifaFechaSorteo.setText(Utilidades.formatearFechaHora(rifa.getFechaSorteo(), "dd-MM-yyyy HH:mm"));
            txtRifaMetodoEleccionGanador.setText(Utilidades.capitalizar(rifa.getMetodoEleccionGanador().toString()) + (rifa.getMetodoEleccionGanador() == MetodoEleccionGanador.DETERMINISTA ? " (" + rifa.getMotivoEleccionGanador() + ")" : ""));
            txtRifaPremios.setText(formatearPremios(rifa.getPremios()));
            if (rifa.getDescripcion() != null && !rifa.getDescripcion().equals("")) {
                txtRifaDescripcion.setText(rifa.getDescripcion());
            }
            else {
                txtRifaDescripcion.setVisibility(View.GONE);
                view.findViewById(R.id.rifa_organizador_ll_descripcion).setVisibility(View.GONE);
                view.findViewById(R.id.rifa_organizador_view_descripcion).setVisibility(View.GONE);
            }
            txtPrecioNumero.setText(String.valueOf(rifa.getPrecioNumero()));
            txtRifaRecaudado.setText("Monto recaudado: $" + calcularRecaudado(rifa));

            // Permite copiar el código de la rifa
            txtRifaCodigo.setOnClickListener(v -> {
                String codigo = txtRifaCodigo.getText().toString().replace("Código: ", "");
                ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Código Rifa", codigo);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(requireContext(), "Código copiado al portapapeles", Toast.LENGTH_SHORT).show();
            });

            // Permite compartir la rifa
            btnCompartirRifa.setOnClickListener(v -> {
                String codigoRifa = rifa.getCodigo();
                String rifaId = rifa.getId();
                String universalLink = "https://emmaquintana.github.io/appchancita/redireccion-rifa/redir.html?codigo=" + codigoRifa + "&rifa_id=" + rifaId;

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Únete a esta rifa: " + universalLink);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Compartir rifa"));
            });


            // Aclaración sobre el monto recaudado al usuario Organizador
            txtRifaRecaudado.setOnClickListener(v -> {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Monto recaudado")
                        .setMessage("El monto recaudado es tan solo un aproximado.\n" +
                                "Por cada compra de número, Mercado Pago se cobra una comisión (+ IVA sobre esa comisión) cuyo porcentaje depende del método de pago.\n" +
                                "Además, también puede descontarsele dependendiendo del plazo de liquidación que usted " +
                                "tenga configurado en su aplicación de Mercado Pago")
                        .setPositiveButton("Aceptar", null)
                        .show();
            });

            // Si el estado de la rifa NO es "Abierto", se imposibilita al usuario Organizador editar datos de la rifa
            if (rifa.getEstado() == RifaEstado.ABIERTO) {
                fab.setVisibility(View.VISIBLE);
            }

            if (!rifa.getNumerosComprados().isEmpty()) {
                // Si hay números comprados, NO se muestra el texto "No hay participantes"
                TextView noHayParticipantes = view.findViewById(R.id.rifa_organizador_txt_no_hay_participantes);
                noHayParticipantes.setVisibility(View.GONE);

                // Se muestra el nombre de cada participante y sus numeros comprados
                inflarParticipantes(view, rifa);

                // Permite al usuario Organizador escoger los ganadores de la rifa
                if (
                        rifa.getMetodoEleccionGanador().toString().equals(MetodoEleccionGanador.DETERMINISTA.toString()) // El organizador escoge los ganadores
                        && (LocalDateTime.now().isEqual(rifa.getFechaSorteo()) || LocalDateTime.now().isAfter(rifa.getFechaSorteo())) // Llegó/pasó la fecha de sorteo
                        && rifa.getEstado() != RifaEstado.SORTEADO
                ) {
                    inflarEleccionGanadores(view, rifa);
                }

                /*
                 * EL METODO PARA LA ASIGNACIÓN DE GANADORES CUANDO EL MÉTODO DE ELECCIÓN ES ALEATORIO
                 * ESTÁ DEFINIDO EN CLOUD FUNCTIONS
                 */

                // Muestra los ganadores
                if (rifa.getEstado() == RifaEstado.SORTEADO) {
                    inflarGanadores(view, rifa);
                }
            }
            else {
                // Oculta la cabecera "Nombre y apellido" - "Números comprados"
                view.findViewById(R.id.rifa_organizador_txt_nombre_participante).setVisibility(View.GONE);
                view.findViewById(R.id.rifa_organizador_txt_numeros_comprados_participante).setVisibility(View.GONE);
            }
        });
    }

    /**
     * Devuelve los premios de la rifa con el siguiente formato:<br/>
     *
     * "Puesto nro. 1: Titulo (descripcion)<br/>
     *  Puesto nro. 2: Titulo (descripcion)<br/>
     *  ...<br/>
     *  Puesto nro. N: TItulo (descripcion)"<br/>
     *
     * */
    private String formatearPremios(List<RifaPremio> rifaPremios) {
        StringBuilder stb = new StringBuilder();

        if (rifaPremios != null && !rifaPremios.isEmpty()) {
            for (RifaPremio premio : rifaPremios) {
                if (premio.getPremioDescripcion() != null && !premio.getPremioDescripcion().trim().isEmpty()) {
                    stb.append("Puesto nro." + premio.getPremioOrden() + ": " + premio.getPremioTitulo() + " (" + premio.getPremioDescripcion() + ")" + "\n\n");
                }
                else {
                    stb.append("Puesto nro." + premio.getPremioOrden() + ": " + premio.getPremioTitulo() + "\n\n");
                }

            }
        }

        return stb.toString().substring(0, stb.toString().length() - 2); // Quita el padding bottom que se forma por el último \n\n
    }

    private void inflarGanadores(View view, Rifa rifa) {
        RecyclerView rvGanadorInfo = view.findViewById(R.id.recycler_view_ganadores);
        rvGanadorInfo.setLayoutManager(new LinearLayoutManager(getContext()));

        rifaOrganizadorViewModel.obtenerNumerosGanadores(rifaId)
                .observe(getViewLifecycleOwner(), numerosGanadores -> {
                    if (numerosGanadores == null || numerosGanadores.isEmpty()) return;

                    List<GanadorInfo> ganadorInfoList = new ArrayList<>();
                    int total = numerosGanadores.size();
                    final int[] processed = {0};

                    for (int i = 0; i < numerosGanadores.size(); i++) {
                        int numeroGanador = numerosGanadores.get(i);
                        int puesto = i + 1;

                        boolean encontrado = false;

                        for (NumeroComprado nc : rifa.getNumerosComprados()) {
                            if (nc.getNumerosComprados().contains(numeroGanador)) {
                                encontrado = true;
                                final String usuarioId = nc.getUsuarioId();
                                final int finalPuesto = puesto;
                                final int finalNumeroGanador = numeroGanador;

                                rifaOrganizadorViewModel.obtenerUsuario(usuarioId)
                                        .observe(getViewLifecycleOwner(), usuario -> {
                                            if (usuario != null) {
                                                GanadorInfo dto = new GanadorInfo(
                                                        usuario.getNombre() + " " + usuario.getApellido(),
                                                        usuario.getNroCelular(),
                                                        usuario.getCorreo(),
                                                        finalNumeroGanador,
                                                        finalPuesto
                                                );
                                                ganadorInfoList.add(dto);
                                            }
                                            processed[0]++;
                                            if (processed[0] == total) {
                                                GanadorInfoAdapter ganadorInfoAdapter = new GanadorInfoAdapter(ganadorInfoList);
                                                rvGanadorInfo.setAdapter(ganadorInfoAdapter);
                                                rvGanadorInfo.setVisibility(View.VISIBLE);
                                                view.findViewById(R.id.rifa_organizador_mcv_ganadores).setVisibility(View.VISIBLE);
                                                view.findViewById(R.id.rifa_organizador_txt_seccion_ganadores)
                                                        .setVisibility(View.VISIBLE);
                                            }
                                        });
                                break;
                            }
                        }

                        // Si no encontramos usuario para ese número, igual incrementamos
                        if (!encontrado) {
                            processed[0]++;
                            if (processed[0] == total) {
                                GanadorInfoAdapter ganadorInfoParticipanteAdapter = new GanadorInfoAdapter(ganadorInfoList);
                                rvGanadorInfo.setAdapter(ganadorInfoParticipanteAdapter);
                                rvGanadorInfo.setVisibility(View.VISIBLE);
                                view.findViewById(R.id.rifa_organizador_mcv_ganadores).setVisibility(View.VISIBLE);
                                view.findViewById(R.id.rifa_organizador_txt_seccion_ganadores)
                                        .setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
    }


    private void inflarParticipantes(View view, Rifa rifa) {
        procesarParticipantes(rifa.getNumerosComprados(), participantes -> {
            RecyclerView rvParticipantes = view.findViewById(R.id.recycler_view_participantes);
            rvParticipantes.setLayoutManager(new LinearLayoutManager(getContext()));
            ParticipantesAdapter adapter = new ParticipantesAdapter(participantes);
            rvParticipantes.setAdapter(adapter);
        });
    }

    private void inflarEleccionGanadores(View view, Rifa rifa) {
        MaterialButton btnConfirmar = view.findViewById(R.id.rifa_organizador_btn_confirmar_ganadores);
        RecyclerView rvPremios = view.findViewById(R.id.recycler_view_eleccion_ganadores);
        view.findViewById(R.id.rifa_organizador_mcv_eleccion_ganadores).setVisibility(View.VISIBLE);
        view.findViewById(R.id.rifa_organizador_txt_seccion_eleccion_ganadores).setVisibility(View.VISIBLE);
        view.findViewById(R.id.rifa_organizador_txt_eleccion_ganadores_descripcion).setVisibility(View.VISIBLE);
        rvPremios.setVisibility(View.VISIBLE);
        btnConfirmar.setVisibility(View.VISIBLE);

        // Junta todos los números comprados
        List<Integer> todosNumeros = new ArrayList<>();
        for (int i = 1; i <= rifa.getCantNumeros(); i++) {
            todosNumeros.add(i);
        }

        // Mapea premios -> Asignación
        List<PremioAsignacion> asignaciones = new ArrayList<>();
        for (RifaPremio premio : rifa.getPremios()) {
            asignaciones.add(new PremioAsignacion(premio, todosNumeros));
        }

        // Setea adapter
        rvPremios.setLayoutManager(new LinearLayoutManager(getContext()));
        EleccionGanadoresAdapter adapter = new EleccionGanadoresAdapter(asignaciones);
        rvPremios.setAdapter(adapter);

        // Confirma selección
        btnConfirmar.setOnClickListener(v -> {
            List<PremioAsignacion> resultados = adapter.getResultados();
            List<Integer> numerosGanadores = new ArrayList<>();
            Set<Integer> usados = new HashSet<>();

            // Realiza validaciones
            for (PremioAsignacion res : resultados) {
                if (res.getNumeroSeleccionado() == null) {
                    Toast.makeText(requireContext(), "Todos los premios deben tener un número asignado", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!usados.add(res.getNumeroSeleccionado())) {
                    Toast.makeText(requireContext(), "No puedes asignar el mismo número a más de un premio", Toast.LENGTH_SHORT).show();
                    return;
                }
                numerosGanadores.add(res.getNumeroSeleccionado());
            }

            // Guarda numeros ganadores
            rifaOrganizadorViewModel.asignarNumerosGanadores(rifaId, numerosGanadores);

            // Muestra estado de carga
            rifaOrganizadorViewModel.asignandoNumerosGanadores.observe(getViewLifecycleOwner(), asignando -> {
                if (asignando) {
                    btnConfirmar.setEnabled(false);
                    btnConfirmar.setText("Cargando...");
                }
                else {
                    btnConfirmar.setEnabled(true);
                    btnConfirmar.setText("Confirmar ganadores");
                }
            });

            // Muestra el resultado de la asignación
            rifaOrganizadorViewModel.resultadoAsignacionNumerosGanadores.observe(getViewLifecycleOwner(), asignacionExitosa -> {
                if (asignacionExitosa) {
                    Snackbar.make(getView(), "¡Los números ganadores se han definido con éxito!", Snackbar.LENGTH_LONG).show();
                    // Se ocultan las vistas para la elección de los ganadores
                    view.findViewById(R.id.rifa_organizador_txt_seccion_eleccion_ganadores).setVisibility(View.GONE);
                    view.findViewById(R.id.rifa_organizador_txt_eleccion_ganadores_descripcion).setVisibility(View.GONE);
                    rvPremios.setVisibility(View.GONE);
                    btnConfirmar.setVisibility(View.GONE);

                    inflarGanadores(view, rifa);
                }
                else {
                    Toast.makeText(requireContext(), "Algo salió mal. Intente nuevamente", Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private double calcularRecaudado(Rifa rifa) {
        double acum = 0;

        for (NumeroComprado nc : rifa.getNumerosComprados()) {
            acum += nc.getNumerosComprados().size() * nc.getPrecioUnitario();
        }
        return acum;
    }

    private interface ParticipantesCallback {
        void onParticipantesListos(List<Participante> participantes);
    }

    private void procesarParticipantes(List<NumeroComprado> numerosComprados, ParticipantesCallback callback) {
        Map<String, Participante> mapParticipantes = new HashMap<>();
        AtomicInteger pendientes = new AtomicInteger(numerosComprados.size());

        if (numerosComprados.isEmpty()) {
            callback.onParticipantesListos(new ArrayList<>());
            return;
        }

        for (NumeroComprado nc : numerosComprados) {
            String usuarioId = nc.getUsuarioId();
            List<Integer> numeros = nc.getNumerosComprados();

            rifaOrganizadorViewModel.obtenerUsuario(usuarioId)
                    .observe(getViewLifecycleOwner(), usuario -> {
                        if (usuario != null) {
                            mapParticipantes.putIfAbsent(usuarioId,
                                    new Participante(usuario.getNombre() + " " + usuario.getApellido(), new ArrayList<>()));
                            mapParticipantes.get(usuarioId).getNumeros().addAll(numeros);
                        }

                        if (pendientes.decrementAndGet() == 0) {
                            callback.onParticipantesListos(new ArrayList<>(mapParticipantes.values()));
                        }
                    });
        }
    }
}