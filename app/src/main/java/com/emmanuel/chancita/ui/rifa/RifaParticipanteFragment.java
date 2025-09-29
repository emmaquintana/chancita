package com.emmanuel.chancita.ui.rifa;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Activity;
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
import com.emmanuel.chancita.data.model.Rifa;
import com.emmanuel.chancita.data.model.RifaEstado;
import com.emmanuel.chancita.data.model.RifaPremio;
import com.emmanuel.chancita.ui.SharedViewModel;
import com.emmanuel.chancita.ui.rifa.adapters.NumerosAdapter;
import com.emmanuel.chancita.utils.Utilidades;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableOptions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
        MaterialButton btnSalir = view.findViewById(R.id.rifa_participante_btn_salir);

        // Permite copiar el código al portapapeles
        txtRifaCodigo.setOnClickListener(v -> {
            String codigo = txtRifaCodigo.getText().toString().replace("Código: ", "");
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Código Rifa", codigo);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(requireContext(), "Código copiado al portapapeles", Toast.LENGTH_SHORT).show();
        });

        // Muestra una ventana de dialogo que aclara al usuario el significado del método de elección de ganador
        view.findViewById(R.id.rifa_participante_txt_metodo_eleccion_head).setOnClickListener(v -> {
            String mensaje;

            if (rifa.getMetodoEleccionGanador() == MetodoEleccionGanador.ALEATORIO) {
                mensaje = "Llegada la fecha y hora del sorteo, el sistema escogerá automáticamente a los números ganadores.\n" +
                        "En caso de que resultes ganador, te notificaremos.";
            }
            else {
                mensaje = "Llegada la fecha y hora del sorteo, el creador de la rifa manualmente escogerá los números ganadores.\nTras ello," +
                        " el creador de la rifa se pondrá en contacto contigo.";
            }

            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Método de elección de ganador")
                    .setMessage(mensaje)
                    .setPositiveButton("Aceptar", null)
                    .show();
        });

        rifaParticipanteViewModel.obtenerRifa(rifaId).observe(getViewLifecycleOwner(), rifa -> {
            this.rifa = rifa;
            // Permite al usuario comprar números si la fecha de sorteo aún no llega
            if (LocalDateTime.now().isBefore(rifa.getFechaSorteo())) {
                inflarSeccionNumeros(view, rifa.getCantNumeros(), rifa.getNumerosComprados(), rifa.getPrecioNumero());
            }

            // Setea la información de la rifa
            txtRifaTitulo.setText(rifa.getTitulo());
            txtRifaEstado.setText(rifa.getEstado().toString());
            txtRifaCodigo.setText(rifa.getCodigo());
            txtPrecioNumero.setText("$" + rifa.getPrecioNumero());
            txtRifaFechaSorteo.setText(Utilidades.formatearFechaHora(rifa.getFechaSorteo(), "dd/MM/yyyy HH:mm"));
            txtRifaMetodoEleccionGanador.setText(Utilidades.capitalizar(rifa.getMetodoEleccionGanador().toString()) + (rifa.getMetodoEleccionGanador() == MetodoEleccionGanador.DETERMINISTA ? " (" + rifa.getMotivoEleccionGanador() + ")" : ""));
            txtRifaPremios.setText(formatearPremios(rifa.getPremios()));
            // Si no hay descripción, se oculta la sección "Descripción"
            if (rifa.getDescripcion() == null || rifa.getDescripcion().trim().isEmpty()) {
                view.findViewById(R.id.rifa_participante_mcv_descripcion).setVisibility(View.GONE);
                txtRifaDescripcion.setVisibility(View.GONE);
                txtRifaDescripcionHead.setVisibility(View.GONE);
            }
            else { // Se setea la descripción
                txtRifaDescripcion.setText(rifa.getDescripcion());
            }

            rifaParticipanteViewModel.obenerOrganizador(rifaId).observe(getViewLifecycleOwner(), organizador -> {
                TextView txtCorreoOrganizador = view.findViewById(R.id.rifa_participante_txt_correo_organizador);
                TextView txtCelularOrganizador = view.findViewById(R.id.rifa_participante_txt_nro_celular_organizador);

                txtCorreoOrganizador.setText(organizador.getCorreo());
                txtCelularOrganizador.setText(organizador.getNroCelular());


                Linkify.addLinks(
                        txtCelularOrganizador,
                        Linkify.PHONE_NUMBERS);

                Linkify.addLinks(
                        txtCorreoOrganizador,
                        Linkify.EMAIL_ADDRESSES
                );
            });

            // Condiciones para mostrar el botón "Salir"
            boolean usuarioComproNumeros = false;
            if (rifa.getNumerosComprados() != null) {
                for (NumeroComprado nc : rifa.getNumerosComprados()) {
                    if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(nc.getUsuarioId()) && nc.getNumerosComprados() != null && !nc.getNumerosComprados().isEmpty()) {
                        usuarioComproNumeros = true;
                        break;
                    }
                }
            }
            boolean yaSeSorteo = rifa.getEstado() == RifaEstado.SORTEADO;

            if (!usuarioComproNumeros || yaSeSorteo) {
                btnSalir.setVisibility(View.VISIBLE);
            } else {
                btnSalir.setVisibility(View.GONE);
            }

            // Acción del botón
            btnSalir.setOnClickListener(v -> {
                // Confirmación antes de salir
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle("Salir de la rifa")
                        .setMessage("¿Estás seguro de que quieres salir de la rifa?")
                        .setPositiveButton("Sí", (dialog, which) -> salirDeLaRifa(rifa))
                        .setNegativeButton("Cancelar", null)
                        .show();
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
                        view.findViewById(R.id.rifa_participante_mcv_comprar_numeros).setVisibility(View.GONE);

                        // Se oculta la sección de premios, pues después se muestra otra sección con premios pero que indica
                        // el número ganador correspondiente al premio
                        view.findViewById(R.id.rifa_participante_mcv_premios).setVisibility(View.GONE);


                        // Mostrar números ganadores
                        // TextView para los números ganadores
                        view.findViewById(R.id.rifa_participante_mcv_ganadores).setVisibility(View.VISIBLE);
                        TextView txtNumerosGanadores = view.findViewById(R.id.rifa_participante_txt_numeros_ganadores);
                        txtNumerosGanadores.setText("Números ganadores: " + Utilidades.listaAString(numerosGanadores));

                        // TextView para premios + número ganador
                        TextView txtPremiosGanadores = view.findViewById(R.id.rifa_participante_txt_numeros_ganadores_premios);
                        StringBuilder premiosText = new StringBuilder();


                        List<RifaPremio> premios = rifa.getPremios();
                        for (int i = 0; i < premios.size(); i++) {
                            String premioTitulo = premios.get(i).getPremioTitulo();
                            String premioDescripcion = premios.get(i).getPremioDescripcion();
                            String numeroGanador = (i < numerosGanadores.size()) ? String.valueOf(numerosGanadores.get(i)) : "-";

                            if (premioDescripcion != null && !premioDescripcion.trim().isEmpty()) {
                                premiosText.append("Premio ").append(i + 1).append(": ")
                                        .append(premioTitulo).append(" - ").append(premioDescripcion)
                                        .append("\nNúmero ganador: ").append(numeroGanador).append("\n\n");
                            }
                            else {
                                premiosText.append("Premio ").append(i + 1).append(": ")
                                        .append(premioTitulo)
                                        .append("\nNúmero ganador: ").append(numeroGanador).append("\n\n");
                            }

                        }
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
            btnComprarNumeros.setEnabled(false);
            if (numerosSeleccionadosPorUsuario.isEmpty()) {
                Toast.makeText(getContext(), "Selecciona al menos un número", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Toast.makeText(getContext(), "Error: no estás autenticado", Toast.LENGTH_SHORT).show();
                return;
            }

            // Obtiene el token ID actualizado
            user.getIdToken(true).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.e("MP_AUTH", "Error obteniendo token", task.getException());
                    Toast.makeText(getContext(), "Error de autenticación", Toast.LENGTH_LONG).show();
                    return;
                }

                String idToken = task.getResult().getToken();
                Log.d("MP_AUTH", "Token obtenido: " + idToken);

                Map<String, Object> data = new HashMap<>();
                data.put("tituloRifa", rifa.getTitulo() != null ? rifa.getTitulo() : "Rifa sin título");
                data.put("precio", precioNumero);
                data.put("cantidad", numerosSeleccionadosPorUsuario.size());
                data.put("numeros", numerosSeleccionadosPorUsuario);
                data.put("compradorId", user.getUid());
                data.put("compradorEmail", user.getEmail());
                data.put("organizerId", rifa.getCreadoPor());
                data.put("rifaId", rifaId);
                data.put("idToken", idToken);


                Log.d("MP_COMPRA", "Datos enviados a la función: " + data);

                // Llama a la función de Firebase
                FirebaseFunctions.getInstance("us-central1")
                        .getHttpsCallable("createPreference")
                        .call(data)
                        .addOnSuccessListener(result -> {
                            Map<String, Object> resultData = (Map<String, Object>) result.getData();
                            String initPoint = (String) resultData.get("init_point");
                            String preferenceId = (String) resultData.get("preference_id");
                            double precioTotal = ((Number) resultData.get("precio_total")).doubleValue();

                            // Guardar compra pendiente
                            FirebaseFirestore.getInstance()
                                    .collection("compras")
                                    .document(preferenceId)
                                    .set(new HashMap<String, Object>() {{
                                        put("rifaId", rifaId);
                                        put("compradorId", user.getUid());
                                        put("compradorEmail", user.getEmail());
                                        put("numeros", numerosSeleccionadosPorUsuario);
                                        put("cantidad", numerosSeleccionadosPorUsuario.size());
                                        put("precioTotal", precioTotal);
                                        put("estado", "pendiente");
                                        put("preferenceId", preferenceId);
                                        put("organizerId", rifa.getCreadoPor());
                                        put("createdAt", FieldValue.serverTimestamp());
                                    }})
                                    .addOnSuccessListener(aVoid -> Log.d("MP_COMPRA", "Compra guardada como pendiente"));

                            // Abrir checkout de Mercado Pago
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(initPoint));
                            startActivity(browserIntent);
                        })
                        .addOnFailureListener(e -> {
                            Log.e("MP_COMPRA", "Error en la función", e);
                            Toast.makeText(getContext(), "Error al procesar la compra: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            });
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

    // Método para verificar autenticación completa
    private void verificarAutenticacionCompleta(Runnable onSuccess) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(getContext(), "Error: No estás autenticado", Toast.LENGTH_LONG).show();
            return;
        }

        Log.d("AUTH_CHECK", "Usuario: " + user.getUid());
        Log.d("AUTH_CHECK", "Email: " + user.getEmail());
        Log.d("AUTH_CHECK", "Verificado: " + user.isEmailVerified());

        // Forzar renovación del token
        user.getIdToken(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String idToken = task.getResult().getToken();
                        Log.d("AUTH_CHECK", "Token renovado exitosamente");
                        Log.d("AUTH_CHECK", "Token length: " + (idToken != null ? idToken.length() : 0));

                        // Ahora sí ejecutar la función
                        onSuccess.run();

                    } else {
                        Log.e("AUTH_CHECK", "Error renovando token", task.getException());
                        Toast.makeText(getContext(), "Error de autenticación: " +
                                task.getException().getMessage(), Toast.LENGTH_LONG).show();
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
                btnComprarNumeros.setText("Comprar número ($" + precioNumero + ")");
            } else {
                btnComprarNumeros.setText("Comprar números ($" + numerosSeleccionados.size() * precioNumero + ")");
            }
        }
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

    private void debugFirebaseAuth() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d("AUTH_DEBUG", "Usuario autenticado: " + user.getUid());
            Log.d("AUTH_DEBUG", "Email: " + user.getEmail());

            // Obtener token manualmente para debug
            user.getIdToken(false)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            Log.d("AUTH_DEBUG", "Token obtenido: " + (idToken != null ? "SÍ" : "NO"));
                            Log.d("AUTH_DEBUG", "Token length: " + (idToken != null ? idToken.length() : 0));
                        } else {
                            Log.e("AUTH_DEBUG", "Error obteniendo token", task.getException());
                        }
                    });
        } else {
            Log.e("AUTH_DEBUG", "Usuario NO autenticado");
        }
    }

    private void salirDeLaRifa(RifaDTO rifa) {
        String usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Quitar al usuario de participantes
        List<String> participantes = new ArrayList<>(rifa.getParticipantesIds());
        participantes.remove(usuarioId);

        // Actualizar Firestore
        FirebaseFirestore.getInstance()
                .collection("rifas")
                .document(rifa.getId())
                .update("participantesIds", participantes)
                .addOnSuccessListener(aVoid -> {
                    requireActivity().finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al salir de la rifa", Toast.LENGTH_SHORT).show();
                });
    }


    // En tu Fragment donde está el botón de comprar
    @Override
    public void onResume() {
        super.onResume();

        // Verificar si regresamos de un pago
        Activity activity = getActivity();
        if (activity != null) {
            Intent intent = activity.getIntent();
            if (intent.getBooleanExtra("pagoExitoso", false)) {
                Toast.makeText  (getContext(), "¡Pago realizado con éxito!", Toast.LENGTH_LONG).show();
                intent.removeExtra("pagoExitoso");
            } else if (intent.getBooleanExtra("pagoFallido", false)) {
                Toast.makeText(getContext(), "El pago no se pudo completar", Toast.LENGTH_LONG).show();
                intent.removeExtra("pagoFallido");
            } else if (intent.getBooleanExtra("pagoPendiente", false)) {
                Toast.makeText(getContext(), "Pago pendiente de aprobación", Toast.LENGTH_LONG).show();
                intent.removeExtra("pagoPendiente");
            }
        }
    }
}