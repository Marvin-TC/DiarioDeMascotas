package com.example.diariodemascotas;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.diariodemascotas.models.DiarioMascotaModel;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;

import java.util.Calendar;

public class RegistroActividad extends AppCompatActivity {

    Toolbar toolbar;
    EditText fechaRegistro;
    EditText titulo;
    EditText nota;
    Spinner spinnerActividades;
    Button btnAgregarImagen;
    Button btnAgregarDireccion;
    ImageView imageView;
    CheckBox checkBoxFavoritoS;
    Button btnCancelar;
    Button btnGuardar;

    String uriImage;
    ImageView btnDesplegarFecha;
    double lat = 0.0;
    double lng = 0.0;
    private static final int RC_PICK_LOCATION = 1234;

    private FirebaseAnalytics analytics;
    private boolean allowAnalytics = true;
    private Trace trace;

    private final ActivityResultLauncher<String[]> seleccionarImagenLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri != null) {
                    imageView.setImageURI(uri);
                    final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
                    try {
                        getContentResolver().takePersistableUriPermission(uri, takeFlags);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                    uriImage = uri.toString();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro_actividad);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Firebase
        analytics = FirebaseAnalytics.getInstance(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        allowAnalytics = prefs.getBoolean("allow_analytics", true);
        trace = FirebasePerformance.getInstance().newTrace("trace_crear_registro");
        trace.start();

        if (allowAnalytics) {
            Bundle screen = new Bundle();
            screen.putString(FirebaseAnalytics.Param.SCREEN_NAME, "registro_actividad");
            screen.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "RegistroActividad");
            analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, screen);
        }

        // UI
        toolbar = findViewById(R.id.toolbar);
        fechaRegistro = findViewById(R.id.fecha_registro);
        titulo = findViewById(R.id.titulo);
        nota = findViewById(R.id.nota);
        spinnerActividades = findViewById(R.id.spinner_actividades);
        btnAgregarImagen = findViewById(R.id.btn_agregar_imagen);
        imageView = findViewById(R.id.imageViewFoto);
        checkBoxFavoritoS = findViewById(R.id.checkBoxFavorito);
        btnCancelar = findViewById(R.id.btn_cancelar);
        btnGuardar = findViewById(R.id.btn_guardar);
        btnDesplegarFecha = findViewById(R.id.desplegarFecha);
        btnAgregarDireccion = findViewById(R.id.btn_agregar_direccion);

        toolbar.setTitle("Toto");

        spinnerActividades.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                imageView.setImageResource(llenarImagen(spinnerActividades.getSelectedItem().toString()));
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnAgregarDireccion.setOnClickListener(view -> {
            Intent intent = new Intent(this, ActividadMaps.class);
            startActivityForResult(intent, RC_PICK_LOCATION);
        });

        btnGuardar.setOnClickListener(view -> {
            try {
                realizarRegistro();

                if (allowAnalytics) {
                    Bundle evento = new Bundle();
                    evento.putString("actividad", spinnerActividades.getSelectedItem().toString());
                    evento.putBoolean("favorito", checkBoxFavoritoS.isChecked());
                    evento.putBoolean("con_foto", uriImage != null);
                    evento.putBoolean("tiene_ubicacion", !(lat == 0.0 && lng == 0.0));
                    analytics.logEvent("diario_creado", evento);
                }

                trace.putAttribute("registro_creado", "true");
                trace.stop();

                Toast.makeText(this, "¡Entrada guardada! 🐶 Tu lomito está orgulloso", Toast.LENGTH_LONG).show();
                finish();

            } catch (Exception ex) {
                FirebaseCrashlytics.getInstance().setCustomKey("flow", "registro_actividad");
                FirebaseCrashlytics.getInstance().recordException(ex);

                if (allowAnalytics) {
                    Bundle e = new Bundle();
                    e.putString("error_code", ex.getMessage());
                    analytics.logEvent("registro_fail", e);
                }

                trace.putAttribute("error", ex.getMessage());
                trace.stop();

                Toast.makeText(this, "Error al guardar registro", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancelar.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        toolbar.setNavigationOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        btnAgregarImagen.setOnClickListener(view -> {
            seleccionarImagenLauncher.launch(new String[]{"image/*"});
        });

        btnDesplegarFecha.setOnClickListener(view -> mostrarDatePicker());
    }

    public void realizarRegistro() {
        DiarioMascotaModel diarioMascotaModel = new DiarioMascotaModel();
        diarioMascotaModel.setId(DiarioMascotaModel.generarNuevoId());
        diarioMascotaModel.setIdMascota(1);
        diarioMascotaModel.setFecha(fechaRegistro.getText().toString());
        diarioMascotaModel.setTitulo(titulo.getText().toString());
        diarioMascotaModel.setNota(nota.getText().toString());
        diarioMascotaModel.setPathImagen(llenarImagen(spinnerActividades.getSelectedItem().toString()));
        diarioMascotaModel.setUriImage(uriImage);
        diarioMascotaModel.setFavorito(checkBoxFavoritoS.isChecked());
        diarioMascotaModel.setActividad(spinnerActividades.getSelectedItem().toString());
        diarioMascotaModel.setLatitud(lat);
        diarioMascotaModel.setLongitud(lng);
        DiarioMascotaModel.listaNotasDiario.add(diarioMascotaModel);
    }

    public int llenarImagen(String tipoActividad) {
        switch (tipoActividad) {
            case "Baño": return R.drawable.banio;
            case "Beber": return R.drawable.beber;
            case "Caminata": return R.drawable.caminata;
            case "Comer": return R.drawable.comer;
            case "Correr": return R.drawable.correr;
            case "Dormir": return R.drawable.dormir;
            case "Juego": return R.drawable.juego;
            case "Nececidades": return R.drawable.necesidades;
            case "Otros": return R.drawable.otros;
            case "Paseo": return R.drawable.paseo;
            case "Siesta": return R.drawable.siesta;
            case "Vacunación": return R.drawable.vacuna;
        }
        return 0;
    }

    private void mostrarDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int anio = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String fechaSeleccionada = dayOfMonth + "/" + (month + 1) + "/" + year;
                    fechaRegistro.setText(fechaSeleccionada);
                },
                anio, mes, dia
        );
        datePickerDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_PICK_LOCATION && resultCode == RESULT_OK && data != null) {
            lat = data.getDoubleExtra(ActividadMaps.EXTRA_LAT, Double.NaN);
            lng = data.getDoubleExtra(ActividadMaps.EXTRA_LNG, Double.NaN);

            if (!Double.isNaN(lat) && !Double.isNaN(lng)) {
                Toast.makeText(this, "Lat: " + lat + "  Lng: " + lng, Toast.LENGTH_SHORT).show();
            }
        }
    }
}