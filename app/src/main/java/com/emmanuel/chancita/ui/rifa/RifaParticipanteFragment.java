package com.emmanuel.chancita.ui.rifa;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.data.dto.RifaDTO;
import com.emmanuel.chancita.data.model.MetodoEleccionGanador;
import com.emmanuel.chancita.data.model.NumeroComprado;
import com.emmanuel.chancita.data.model.RifaPremio;
import com.emmanuel.chancita.ui.SharedViewModel;
import com.emmanuel.chancita.ui.rifa.adapters.NumerosAdapter;
import com.emmanuel.chancita.utils.Utilidades;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RifaParticipanteFragment extends Fragment {

    private RifaParticipanteViewModel rifaParticipanteViewModel;
    private SharedViewModel sharedViewModel;
    private RifaDTO rifa;
    private String rifaId;

    public static RifaParticipanteFragment newInstance() {
        return new RifaParticipanteFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rifaParticipanteViewModel = new ViewModelProvider(this).get(RifaParticipanteViewModel.class);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rifa_participante, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Obtener el rifaId del SharedViewModel AQUÍ, después de que la Activity ya se haya configurado
        rifaId = sharedViewModel.getRifaId();
        Log.d("RifaParticipanteFragment", "rifaId obtenido de SharedViewModel en onViewCreated: " + rifaId);

        // Validar que rifaId no sea null antes de hacer llamadas
        if (rifaId == null || rifaId.trim().isEmpty()) {
            Log.e("RifaParticipanteFragment", "Error: rifaId es null o vacío en Fragment");
            Toast.makeText(getContext(), "Error: No se pudo cargar la rifa", Toast.LENGTH_SHORT).show();
            if (getActivity() != null) {
                getActivity().finish();
            }
            return;
        }

        TextView txtRifaTitulo = view.findViewById(R.id.rifa_participante_txt_titulo_rifa);
        TextView txtRifaEstado = view.findViewById(R.id.rifa_participante_txt_estado_rifa); // Debe iniciar con "Estado: "
        TextView txtRifaCodigo = view.findViewById(R.id.rifa_participante_txt_codigo_rifa); // Debe iniciar con "Código: "
        TextView txtRifaFechaSorteo = view.findViewById(R.id.rifa_participante_txt_fecha_sorteo); // Debe iniciar con "Fecha de sorteo: "
        TextView txtRifaMetodoEleccionGanador = view.findViewById(R.id.rifa_participante_txt_metodo_eleccion); // Debe iniciar con "Método de elección: "
        TextView txtRifaPremios = view.findViewById(R.id.rifa_participante_txt_premios); // Debe iniciar con "1er puesto: "
        TextView txtRifaDescripcion = view.findViewById(R.id.rifa_participante_txt_descripcion);
        TextView txtRifaDescripcionHead = view.findViewById(R.id.rifa_participante_txt_descripcion_titulo);
        TextView txtPrecioNumero = view.findViewById(R.id.rifa_participante_txt_precio_numero); // Debe iniciar con "Precio por número: $"

        // Permite copiar el código al portapapeles
        txtRifaCodigo.setOnClickListener(v -> {
            String codigo = txtRifaCodigo.getText().toString().replace("Código: ", "");
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Código Rifa", codigo);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(requireContext(), "Código copiado al portapapeles", Toast.LENGTH_SHORT).show();
        });

        rifaParticipanteViewModel.obtenerRifa(rifaId).observe(getViewLifecycleOwner(), rifa -> {
            this.rifa = rifa;
            // Permite al usuario comprar números si la fecha de sorteo aún no llega
            if (LocalDateTime.now().isBefore(rifa.getFechaSorteo())) {
                inflarSeccionNumeros(view, rifa.getCantNumeros(), rifa.getNumerosComprados(), rifa.getPrecioNumero());
            }

            // Setea la información de la rifa
            txtRifaTitulo.setText(rifa.getTitulo());
            txtRifaEstado.setText("Estado: " + rifa.getEstado());
            txtRifaCodigo.setText("Código: " + rifa.getCodigo());
            txtPrecioNumero.setText("Precio por número: $" + rifa.getPrecioNumero() + " + 2.2% comisión");
            txtRifaFechaSorteo.setText("Fecha de sorteo: " + Utilidades.formatearFechaHora(rifa.getFechaSorteo(), "dd/MM/yyyy hh:mm"));
            txtRifaMetodoEleccionGanador.setText("Método de elección: " + Utilidades.capitalizar(rifa.getMetodoEleccionGanador().toString()) + (rifa.getMetodoEleccionGanador() == MetodoEleccionGanador.DETERMINISTA ? " (" + rifa.getMotivoEleccionGanador() + ")" : ""));
            txtRifaPremios.setText(formatearPremios(rifa.getPremios()));
            // Si no hay descripción, se oculta la sección "Descripción"
            if (rifa.getDescripcion() == null || rifa.getDescripcion().trim().isEmpty()) {
                txtRifaDescripcion.setVisibility(View.GONE);
                txtRifaDescripcionHead.setVisibility(View.GONE);
            }
            else { // Se setea la descripción
                txtRifaDescripcion.setText(rifa.getDescripcion());
            }

            rifaParticipanteViewModel.obenerOrganizador(rifaId).observe(getViewLifecycleOwner(), organizador -> {
                TextView txtCorreoOrganizador = view.findViewById(R.id.rifa_participante_txt_correo_organizador);
                TextView txtCelularOrganizador = view.findViewById(R.id.rifa_participante_txt_nro_celular_organizador);

                txtCorreoOrganizador.setText("Correo del creador de la rifa: " + organizador.getCorreo());
                txtCelularOrganizador.setText("Nro. celular del creador de la rifa: " + organizador.getNroCelular());


                Linkify.addLinks(
                        txtCelularOrganizador,
                        Linkify.PHONE_NUMBERS);

                Linkify.addLinks(
                        txtCorreoOrganizador,
                        Linkify.EMAIL_ADDRESSES
                );
            });
        });

        rifaParticipanteViewModel.obtenerNumerosCompradosPorUsuarioActual(rifaId).observe(getViewLifecycleOwner(), numerosComprados -> {
            TextView txtNumerosCompradosPorUsuarioActual = view.findViewById(R.id.rifa_participante_txt_numeros_comprados_por_usuario_actual);

            if (numerosComprados.isEmpty()) {
                txtNumerosCompradosPorUsuarioActual.setText("Aún no compraste números");
            } else {
                txtNumerosCompradosPorUsuarioActual.setText("Números que compraste: " + Utilidades.listaAString(numerosComprados));
            }
        });

        rifaParticipanteViewModel.obtenerNumerosGanadores(rifaId)
                .observe(getViewLifecycleOwner(), numerosGanadores -> {
                    if (numerosGanadores != null && !numerosGanadores.isEmpty()) {

                        // Se oculta la sección de compra de números
                        view.findViewById(R.id.rifa_participante_btn_comprar_numeros).setVisibility(View.GONE);
                        view.findViewById(R.id.rifa_participante_rv_numeros).setVisibility(View.GONE);
                        view.findViewById(R.id.rifa_participante_txt_comprar_numeros_titulo).setVisibility(View.GONE);
                        view.findViewById(R.id.rifa_participante_txt_comprar_numeros_descripcion).setVisibility(View.GONE);
                        view.findViewById(R.id.rifa_participante_txt_aclaracion_comision).setVisibility(View.GONE);

                        // Se oculta la sección de premios, pues después se muestra otra sección con premios pero que indica
                        // el número ganador correspondiente al premio
                        view.findViewById(R.id.rifa_participante_txt_premios_titulo).setVisibility(View.GONE);
                        view.findViewById(R.id.rifa_participante_txt_premios).setVisibility(View.GONE);

                        // Se oculta la sección de compra de números

                        // TextView para los números ganadores
                        TextView txtNumerosGanadores = view.findViewById(R.id.rifa_participante_txt_numeros_ganadores);
                        txtNumerosGanadores.setVisibility(View.VISIBLE);
                        txtNumerosGanadores.setText("Números ganadores: " + Utilidades.listaAString(numerosGanadores));

                        // TextView para premios + número ganador
                        TextView txtPremiosGanadores = view.findViewById(R.id.rifa_participante_txt_numeros_ganadores_premios);
                        StringBuilder premiosText = new StringBuilder();


                        List<RifaPremio> premios = rifa.getPremios();
                        for (int i = 0; i < premios.size(); i++) {
                            String premioTitulo = premios.get(i).getPremioTitulo();
                            String premioDescripcion = premios.get(i).getPremioDescripcion();
                            String numeroGanador = (i < numerosGanadores.size()) ? String.valueOf(numerosGanadores.get(i)) : "-";

                            if (!premioDescripcion.trim().isEmpty()) {
                                premiosText.append("Premio ").append(i + 1).append(": ")
                                        .append(premioTitulo).append(" - ").append(premioDescripcion)
                                        .append("\nNúmero ganador: ").append(numeroGanador).append("\n\n");
                            }
                            else {
                                premiosText.append("Premio ").append(i + 1).append(": ")
                                        .append(premioTitulo).append(" - ")
                                        .append("\nNúmero ganador: ").append(numeroGanador).append("\n\n");
                            }

                        }

                        txtPremiosGanadores.setVisibility(View.VISIBLE);
                        txtPremiosGanadores.setText(premiosText.toString());

                        // Verifica si el usuario ganó
                        FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();
                        boolean gano = false;
                        for (int numero : numerosGanadores) {
                            for (NumeroComprado nc : rifa.getNumerosComprados()) {
                                if (nc.getUsuarioId().equals(usuarioActual.getUid()) && nc.getNumerosComprados().contains(numero)) {
                                    gano = true;
                                    break;
                                }
                            }
                            if (gano) break;
                        }

                        TextView txtMsjResultado = view.findViewById(R.id.rifa_participante_txt_msj_resultado);
                        txtMsjResultado.setVisibility(View.VISIBLE);
                        if (gano) {
                            txtMsjResultado.setText("¡Felicidades! El creador de la rifa se pondrá en contacto contigo.");
                        }
                        else {
                            txtMsjResultado.setText("¡Mejor suerte para la próxima!");
                        }
                    }
                });

    }

    /** Infla los numeros de los cuales el usuario participante podrá elegir cuál comprar */
    private void inflarSeccionNumeros(View view, int cantNumeros, List<NumeroComprado> numerosComprados, double precioNumero) {
        MaterialButton btnComprarNumeros = view.findViewById(R.id.rifa_participante_btn_comprar_numeros);
        RecyclerView rvNumeros = view.findViewById(R.id.rifa_participante_rv_numeros);
        view.findViewById(R.id.rifa_participante_txt_comprar_numeros_titulo).setVisibility(View.VISIBLE);
        view.findViewById(R.id.rifa_participante_txt_comprar_numeros_descripcion).setVisibility(View.VISIBLE);
        view.findViewById(R.id.rifa_participante_txt_aclaracion_comision).setVisibility(View.VISIBLE);
        rvNumeros.setVisibility(View.VISIBLE);
        btnComprarNumeros.setVisibility(View.VISIBLE);

        List<Integer> numeros = new ArrayList<>();
        List<Integer> numerosSeleccionadosPorUsuario = new ArrayList<>();

        for (int i = 1; i <= cantNumeros; i++) {
            numeros.add(i);
        }

        NumerosAdapter adapter = new NumerosAdapter(numeros, numerosComprados, numerosSeleccionados -> {
            numerosSeleccionadosPorUsuario.clear();
            numerosSeleccionadosPorUsuario.addAll(numerosSeleccionados);

            // Actualizar estado del botón basado en la selección
            actualizarEstadoBotonComprar(btnComprarNumeros, numerosSeleccionados, precioNumero);
        });

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 5);
        rvNumeros.setLayoutManager(layoutManager);
        rvNumeros.setAdapter(adapter);


            btnComprarNumeros.setOnClickListener(v -> {
                Map<String, Object> data = new HashMap<>();
                data.put("organizerId", rifa.getCreadoPor()); // el UID del dueño de la rifa
                data.put("tituloRifa", "Rifa " + rifaId);
                data.put("precio", precioNumero);
                data.put("cantidad", numerosSeleccionadosPorUsuario.size());

                FirebaseFunctions.getInstance()
                        .getHttpsCallable("createPreference")
                        .call(data)
                        .addOnSuccessListener(result -> {
                            String initPoint = (String) ((HashMap) result.getData()).get("init_point");
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(initPoint));
                            startActivity(browserIntent);
                        })
                        .addOnFailureListener(e ->
                                Log.e("MP", "Error creando preferencia", e)
                        );
            });

        // Observer para el estado de carga
        rifaParticipanteViewModel.comprandoNumeros.observe(getViewLifecycleOwner(), comprandoNumeros -> {
            if (comprandoNumeros) {
                btnComprarNumeros.setEnabled(false);
                btnComprarNumeros.setText("Comprando...");
            } else {
                // Solo habilitar si hay números seleccionados
                btnComprarNumeros.setEnabled(!numerosSeleccionadosPorUsuario.isEmpty());
                actualizarEstadoBotonComprar(btnComprarNumeros, numerosSeleccionadosPorUsuario, precioNumero);
            }
        });

        // Observer para el resultado de la compra
        rifaParticipanteViewModel.compraNumerosExitosa.observe(getViewLifecycleOwner(), compraExitosa -> {
            if (compraExitosa != null && compraExitosa) {
                Toast.makeText(getContext(), "¡Números comprados con éxito!", Toast.LENGTH_LONG).show();
                // Limpiar selección después de compra exitosa
                adapter.limpiarSeleccion();
                numerosSeleccionadosPorUsuario.clear();
                actualizarEstadoBotonComprar(btnComprarNumeros, numerosSeleccionadosPorUsuario, precioNumero);

                rifaParticipanteViewModel.obtenerRifa(rifaId).observe(getViewLifecycleOwner(), rifaActualizada -> {
                    if (rifaActualizada != null) {
                        adapter.actualizarNumerosComprados(rifaActualizada.getNumerosComprados());
                    }
                });

                rifaParticipanteViewModel
                        .obtenerNumerosCompradosPorUsuarioActual(rifaId)
                        .observe(getViewLifecycleOwner(), numerosCompradosPorUsuarioActual -> {
                            TextView txtNumerosCompradosPorUsuarioActual = getView().findViewById(R.id.rifa_participante_txt_numeros_comprados_por_usuario_actual);
                            if (numerosCompradosPorUsuarioActual.isEmpty()) {
                                txtNumerosCompradosPorUsuarioActual.setText("Aún no compraste números");
                            } else {
                                txtNumerosCompradosPorUsuarioActual.setText("Números que compraste: " + Utilidades.listaAString(numerosCompradosPorUsuarioActual));
                            }
                        });

                // Resetear el estado para evitar múltiples ejecuciones
                rifaParticipanteViewModel.resetCompraExitosa();
            }
        });
    }
    private void actualizarEstadoBotonComprar(MaterialButton btnComprarNumeros, List<Integer> numerosSeleccionados, double precioNumero) {
        if (numerosSeleccionados.isEmpty()) {
            btnComprarNumeros.setEnabled(false);
            btnComprarNumeros.setText("Comprar número/s");
        } else {
            btnComprarNumeros.setEnabled(true);
            if (numerosSeleccionados.size() == 1) {
                btnComprarNumeros.setText("Comprar número ($" + calcularPrecio(numerosSeleccionados.size(), precioNumero, 2.2) + ")");
            } else {
                btnComprarNumeros.setText("Comprar números ($" + calcularPrecio(numerosSeleccionados.size(), precioNumero, 2.2) + ")");
            }
        }
    }

    private double calcularPrecio(double cantNumerosSeleccionados, double precioNumero, double comision) {
        double precioBase = cantNumerosSeleccionados * precioNumero;
        return precioBase + (precioBase * comision / 100.0);
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
}