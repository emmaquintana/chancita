package com.emmanuel.chancita.ui.rifa.editar_rifa;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
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
import com.emmanuel.chancita.data.dto.RifaDTO;
import com.emmanuel.chancita.data.model.RifaPremio;
import com.emmanuel.chancita.ui.SharedViewModel;
import com.emmanuel.chancita.ui.rifa.RifaOrganizadorFragment;
import com.emmanuel.chancita.ui.rifa.RifaOrganizadorViewModel;
import com.emmanuel.chancita.ui.rifa.adapters.EditarPremioAdapter;
import com.emmanuel.chancita.ui.rifa.adapters.IngresoPremioAdapter;
import com.emmanuel.chancita.ui.rifa.model.IngresoPremio;
import com.emmanuel.chancita.utils.Utilidades;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditarRifaFragment extends Fragment implements EditarPremioAdapter.OnPremioChangedListener {

    private SharedViewModel sharedViewModel;
    private EditarRifaViewModel editarRifaViewModel;
    private String rifaId;
    private NavController navController;
    private RifaDTO rifaActual;
    private boolean botonGuardarClicked = false;

    // Inputs
    private TextInputEditText tietEditarRifaTitulo;
    private TextInputEditText tietEditarRifaDescripcion;
    private TextInputEditText tietEditarRifaPrecioNumero;
    private TextInputEditText tietEditarRifaFechaSorteo;
    private TextInputEditText tietEditarRifaHoraSorteo;
    private TextInputEditText tietEditarRifaCodigo;
    private MaterialButton btnGuardar;

    // RecyclerView para premios
    private RecyclerView rvPremios;
    private EditarPremioAdapter premiosAdapter;

    public static EditarRifaFragment newInstance() {
        return new EditarRifaFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_editar_rifa, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        editarRifaViewModel = new ViewModelProvider(this).get(EditarRifaViewModel.class);
        navController = NavHostFragment.findNavController(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rifaId = sharedViewModel.getRifaId();

        if (rifaId == null || rifaId.trim().isEmpty()) {
            Toast.makeText(getContext(), "Error: No se pudo cargar la rifa", Toast.LENGTH_SHORT).show();
            if (getActivity() != null) getActivity().finish();
            return;
        }

        // Referencias a los inputs
        tietEditarRifaTitulo = view.findViewById(R.id.editar_rifa_tiet_titulo);
        tietEditarRifaDescripcion = view.findViewById(R.id.editar_rifa_tiet_descripcion);
        tietEditarRifaPrecioNumero = view.findViewById(R.id.editar_rifa_tiet_precio);
        tietEditarRifaFechaSorteo = view.findViewById(R.id.editar_rifa_tiet_fecha_sorteo);
        tietEditarRifaHoraSorteo = view.findViewById(R.id.editar_rifa_tiet_hora_sorteo);
        tietEditarRifaCodigo = view.findViewById(R.id.editar_rifa_tiet_codigo);

        // RecyclerView para premios
        rvPremios = view.findViewById(R.id.editar_rifa_rv_ingreso_premios);
        setupRecyclerView();

        setearObservers();

        // Botón guardar
        btnGuardar = view.findViewById(R.id.editar_rifa_btn_guardar_cambios);
        btnGuardar.setOnClickListener(v -> guardarCambios());
    }

    private void setupRecyclerView() {
        rvPremios.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPremios.setHasFixedSize(true);
    }

    private void setearObservers() {
        editarRifaViewModel.obtenerRifa(rifaId).observe(getViewLifecycleOwner(), rifa -> {
            if (rifa == null) return;

            rifaActual = rifa;

            tietEditarRifaTitulo.setText(rifa.getTitulo());
            tietEditarRifaDescripcion.setText(rifa.getDescripcion());
            tietEditarRifaPrecioNumero.setText(String.valueOf(rifa.getPrecioNumero()));
            tietEditarRifaCodigo.setText(rifa.getCodigo());
            tietEditarRifaFechaSorteo.setText(Utilidades.formatearFechaHora(rifa.getFechaSorteo(), "dd-MM-yyyy"));
            tietEditarRifaHoraSorteo.setText(Utilidades.formatearFechaHora(rifa.getFechaSorteo(), "HH:mm"));

            // Configurar adapter de premios
            setupPremiosAdapter(rifa.getPremios());

            setearListenersFechaYHora();
        });

        editarRifaViewModel.resultadoEdicionRifa.observe(getViewLifecycleOwner(), resultado -> {
            if (resultado != null) {
                Toast.makeText(getContext(), resultado, Toast.LENGTH_SHORT).show();
                if (resultado.contains("éxito")) {
                    navController.popBackStack();
                }
            }
        });

        editarRifaViewModel.editandoRifa.observe(getViewLifecycleOwner(), editando -> {
            if (editando) {
                btnGuardar.setEnabled(false);
                btnGuardar.setText("Editando...");
            } else {
                btnGuardar.setEnabled(true);
                btnGuardar.setText("Guardar cambios");
            }
        });

        // Observer para errores de validación
        editarRifaViewModel.errorValidacion.observe(getViewLifecycleOwner(), error -> {
            if (!botonGuardarClicked) return;

            if (error != null) {
                // Mostrar error y resetear flag
                new AlertDialog.Builder(requireContext())
                        .setTitle("Error de validación")
                        .setMessage(error)
                        .setPositiveButton("Aceptar", null)
                        .show();
                botonGuardarClicked = false;
            }
        });

        // Nuevo observer para cuando la rifa está validada y lista para editar
        editarRifaViewModel.rifaValidadaParaEditar.observe(getViewLifecycleOwner(), rifaValidada -> {
            if (!botonGuardarClicked) return;

            if (rifaValidada != null) {
                // Todo OK, procedemos con la edición
                editarRifaViewModel.editarRifa(rifaValidada);
                botonGuardarClicked = false;
            }
        });
    }

    private void setupPremiosAdapter(List<RifaPremio> premios) {
        if (premios != null && !premios.isEmpty()) {
            // Crear una copia mutable de la lista
            List<RifaPremio> premiosMutable = new ArrayList<>(premios);
            premiosAdapter = new EditarPremioAdapter(premiosMutable, this);
            rvPremios.setAdapter(premiosAdapter);
        }
    }

    private void guardarCambios() {
        if (rifaActual == null) return;

        botonGuardarClicked = true;

        // Resetear estado previo
        editarRifaViewModel.resetearValidacion();

        // Actualizar datos de la rifa
        rifaActual.setTitulo(tietEditarRifaTitulo.getText().toString().trim());
        rifaActual.setDescripcion(tietEditarRifaDescripcion.getText().toString().trim());

        try {
            rifaActual.setPrecioNumero(Double.parseDouble(tietEditarRifaPrecioNumero.getText().toString().trim()));
        } catch (NumberFormatException e) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Error de validación")
                    .setMessage("El precio del número debe ser un valor numérico válido")
                    .setPositiveButton("Aceptar", null)
                    .show();
            botonGuardarClicked = false;
            return;
        }

        rifaActual.setCodigo(tietEditarRifaCodigo.getText().toString().trim());

        String fechaStr = tietEditarRifaFechaSorteo.getText().toString().trim();
        String horaStr = tietEditarRifaHoraSorteo.getText().toString().trim();

        try {
            LocalDateTime fechaSorteo = Utilidades.parsearFechaHora(fechaStr + " " + horaStr, "dd-MM-yyyy HH:mm");
            rifaActual.setFechaSorteo(fechaSorteo);
        } catch (Exception e) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Error de validación")
                    .setMessage("La fecha y hora ingresadas no son válidas")
                    .setPositiveButton("Aceptar", null)
                    .show();
            botonGuardarClicked = false;
            return;
        }

        // Actualizar los premios desde el adapter
        if (premiosAdapter != null) {
            rifaActual.setPremios(premiosAdapter.getPremios());
        }

        // Iniciar validación completa (incluye validación asíncrona del código)
        editarRifaViewModel.validarYProcesarRifa(rifaActual);
    }

    private void setearListenersFechaYHora() {
        LocalDateTime creadoEn = rifaActual.getCreadoEn();

        // Convierte a millis para usar en DatePicker
        ZoneId zoneId = ZoneId.systemDefault();
        long creadoEnMillis = creadoEn.atZone(zoneId).toInstant().toEpochMilli();

        Calendar calendar = Calendar.getInstance();

        // Selección de fecha
        tietEditarRifaFechaSorteo.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view1, year1, month1, dayOfMonth) -> {
                        calendar.set(Calendar.YEAR, year1);
                        calendar.set(Calendar.MONTH, month1);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // Formato dd-MM-yyyy
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        tietEditarRifaFechaSorteo.setText(sdf.format(calendar.getTime()));
                    },
                    year, month, day
            );

            // Restringir fecha mínima
            datePickerDialog.getDatePicker().setMinDate(creadoEnMillis);

            datePickerDialog.show();
        });

        // Selección de hora
        tietEditarRifaHoraSorteo.setOnClickListener(v -> {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    requireContext(),
                    (view12, hourOfDay, minute1) -> {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute1);

                        LocalDateTime seleccionada = LocalDateTime.ofInstant(
                                calendar.toInstant(),
                                zoneId
                        );

                        if (seleccionada.isBefore(creadoEn)) {
                            new AlertDialog.Builder(requireContext())
                                    .setTitle("Fecha/hora inválida")
                                    .setMessage("La fecha/hora de sorteo no puede ser menor a la fecha de creación de la rifa")
                                    .setPositiveButton("Aceptar", null)
                                    .show();
                        } else {
                            // Formato HH:mm
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                            tietEditarRifaHoraSorteo.setText(sdf.format(calendar.getTime()));
                        }
                    },
                    hour, minute, true // formato 24 horas
            );
            timePickerDialog.show();
        });
    }

    @Override
    public void onPremioChanged(int position, RifaPremio premio) {
        // Este método se llama automáticamente cuando el usuario cambia algún campo
        // Los cambios ya están reflejados en el objeto premio
        // No es necesario hacer nada adicional aquí a menos que quieras
        // guardar cambios automáticamente o realizar alguna validación en tiempo real
    }
}