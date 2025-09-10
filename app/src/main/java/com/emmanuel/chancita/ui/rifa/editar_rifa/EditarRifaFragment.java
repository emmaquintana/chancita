package com.emmanuel.chancita.ui.rifa.editar_rifa;

import androidx.appcompat.app.AlertDialog;;
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
import com.emmanuel.chancita.data.model.MetodoEleccionGanador;
import com.emmanuel.chancita.data.model.RifaPremio;
import com.emmanuel.chancita.ui.SharedViewModel;
import com.emmanuel.chancita.ui.rifa.RifaOrganizadorFragment;
import com.emmanuel.chancita.ui.rifa.RifaOrganizadorViewModel;
import com.emmanuel.chancita.ui.rifa.adapters.EditarPremioAdapter;
import com.emmanuel.chancita.ui.rifa.adapters.IngresoPremioAdapter;
import com.emmanuel.chancita.ui.rifa.model.IngresoPremio;
import com.emmanuel.chancita.utils.Utilidades;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class EditarRifaFragment extends Fragment implements EditarPremioAdapter.OnPremioChangedListener {

    private SharedViewModel sharedViewModel;
    private EditarRifaViewModel editarRifaViewModel;
    private String rifaId;
    private NavController navController;
    private RifaDTO rifaActual;
    private boolean botonGuardarClicked = false;
    private Calendar calendar = Calendar.getInstance();

    // Inputs
    private TextInputEditText tietEditarRifaTitulo;
    private TextInputEditText tietEditarRifaDescripcion;
    private TextInputEditText tietEditarRifaPrecioNumero;
    private TextInputEditText tietEditarRifaFechaSorteo;
    private TextInputEditText tietEditarRifaHoraSorteo;
    private TextInputEditText tietEditarRifaCodigo;
    private TextInputEditText tietEditarRifaMotivoEleccionGanador;
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
        tietEditarRifaMotivoEleccionGanador = view.findViewById(R.id.editar_rifa_tiet_motivo_eleccion_ganador);


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

            if (rifa.getMetodoEleccionGanador() == MetodoEleccionGanador.DETERMINISTA) {
                tietEditarRifaMotivoEleccionGanador.setText(rifa.getMotivoEleccionGanador());
            }
            else {
                tietEditarRifaMotivoEleccionGanador.setVisibility(View.GONE);
            }

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
                new MaterialAlertDialogBuilder(requireContext())
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
        if (tietEditarRifaDescripcion.getText() != null) {
            rifaActual.setDescripcion(tietEditarRifaDescripcion.getText().toString().trim());
        }
        if (tietEditarRifaMotivoEleccionGanador.getText() != null) {
            System.out.println("Entra a setear motivo");
            rifaActual.setMotivoEleccionGanador(tietEditarRifaMotivoEleccionGanador.getText().toString().trim());
        }

        try {
            rifaActual.setPrecioNumero(Double.parseDouble(tietEditarRifaPrecioNumero.getText().toString().trim()));
        } catch (NumberFormatException e) {
            new MaterialAlertDialogBuilder(requireContext())
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
            new MaterialAlertDialogBuilder(requireContext())
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
        // === DATE PICKER (MaterialDatePicker) ===
        tietEditarRifaFechaSorteo.setOnClickListener(v -> {
            // Fecha mínima: hoy (sin importar la hora)
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            long minDateMillis = today.getTimeInMillis();

            CalendarConstraints constraints = new CalendarConstraints.Builder()
                    .setStart(minDateMillis)
                    .setValidator(DateValidatorPointForward.now())
                    .build();

            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Seleccionar fecha")
                    .setCalendarConstraints(constraints)
                    .build();

            datePicker.addOnPositiveButtonClickListener(selection -> {
                // Solución: usar UTC para evitar problemas de zona horaria
                Calendar selected = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                selected.setTimeInMillis(selection);

                // Convertir a zona horaria local para mostrar
                Calendar localCalendar = Calendar.getInstance();
                localCalendar.set(Calendar.YEAR, selected.get(Calendar.YEAR));
                localCalendar.set(Calendar.MONTH, selected.get(Calendar.MONTH));
                localCalendar.set(Calendar.DAY_OF_MONTH, selected.get(Calendar.DAY_OF_MONTH));

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                tietEditarRifaFechaSorteo.setText(sdf.format(localCalendar.getTime()));

                // Actualizar el calendar principal
                calendar.set(Calendar.YEAR, localCalendar.get(Calendar.YEAR));
                calendar.set(Calendar.MONTH, localCalendar.get(Calendar.MONTH));
                calendar.set(Calendar.DAY_OF_MONTH, localCalendar.get(Calendar.DAY_OF_MONTH));
            });

            datePicker.show(getChildFragmentManager(), "MATERIAL_DATE_PICKER");
        });

        // === TIME PICKER (MaterialTimePicker) ===
        tietEditarRifaHoraSorteo.setOnClickListener(v -> {
            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            int currentMinute = calendar.get(Calendar.MINUTE);

            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(currentHour)
                    .setMinute(currentMinute)
                    .setTitleText("Seleccionar hora")
                    .build();

            timePicker.addOnPositiveButtonClickListener(dialog -> {
                int selectedHour = timePicker.getHour();
                int selectedMinute = timePicker.getMinute();

                calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                calendar.set(Calendar.MINUTE, selectedMinute);

                // Formato HH:mm
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                tietEditarRifaHoraSorteo.setText(sdf.format(calendar.getTime()));
            });

            timePicker.show(getChildFragmentManager(), "MATERIAL_TIME_PICKER");
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