package com.example.diariodemascotas;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
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

public class EditarRegistroActividad extends AppCompatActivity {


    Toolbar toolbar;
    EditText fechaRegistro;
    EditText titulo;
    EditText editnota;
    Spinner spinnerActividades;
    Button btnAgregarImagen;
    ImageView imageView;
    CheckBox checkBoxFavoritoS;
    Button btnCancelar;
    Button btnGuardar;
    Button btnAgregarDireccion;

    String uriImage;
    Uri uri;
    ImageView btnDesplegarFecha;
    double lat=0.0;
    double lng=0.0;
    private static final int RC_PICK_LOCATION = 1234;
    private ActivityResultLauncher<Intent> mapaLauncher;
    DiarioMascotaModel diarioMascotaModel = new DiarioMascotaModel();
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
                    uriImage = uri.toString(); // Guardar en tu modelo
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.editar_regsitro_actividad);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Log.d("TAG", "onCreate: " + extras.getInt("id"));
            diarioMascotaModel=diarioMascotaModel.buscarPorId(extras.getInt("id"));
            Log.d("TAG", "onCreate: " + diarioMascotaModel.toString());
        }

        toolbar = findViewById(R.id.toolbar);
        fechaRegistro = findViewById(R.id.fecha_registro);
        titulo = findViewById(R.id.titulo);
        editnota = findViewById(R.id.nota);
        spinnerActividades = findViewById(R.id.spinner_actividades);
        btnAgregarImagen = findViewById(R.id.btn_agregar_imagen);
        imageView = findViewById(R.id.imageViewFoto);
        checkBoxFavoritoS = findViewById(R.id.checkBoxFavorito);
        btnCancelar = findViewById(R.id.btn_cancelar);
        btnGuardar = findViewById(R.id.btn_guardar);
        btnDesplegarFecha = findViewById(R.id.desplegarFecha);
        btnAgregarDireccion = findViewById(R.id.btn_agregar_direccion);


        // Seteo de valores generales
        toolbar.setTitle("Toto");
        seleccionarValorSpinner(diarioMascotaModel.getActividad());
        fechaRegistro.setText(diarioMascotaModel.getFecha());
        titulo.setText(diarioMascotaModel.getTitulo());
        editnota.setText(diarioMascotaModel.getNota());
        checkBoxFavoritoS.setChecked(diarioMascotaModel.isFavorito());
        String uriStr = diarioMascotaModel.getUriImage();
        if (uriStr != null && !uriStr.trim().isEmpty()) {
            uri = Uri.parse(uriStr);
            imageView.setImageURI(uri);
        } else {
            imageView.setImageResource(llenarImagen(diarioMascotaModel.getActividad()));
        }

        spinnerActividades.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (uri != null) {
                    return;
                }
                imageView.setImageResource(llenarImagen(spinnerActividades.getSelectedItem().toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        btnGuardar.setOnClickListener(View -> {
            ActualizarRegistro(diarioMascotaModel.getId());
            Toast.makeText(this, "“\"¡Entrada guardada! \uD83D\uDC36 Tu lomito está orgulloso", Toast.LENGTH_LONG).show();
            finish();
        });

        btnAgregarDireccion.setOnClickListener(view -> {
            Intent intent = new Intent(this, ActividadMaps.class);
            startActivityForResult(intent, RC_PICK_LOCATION);
        });


        btnCancelar.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();

        });
        toolbar.setNavigationOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        btnAgregarImagen.setOnClickListener(view -> {
            seleccionarImagenLauncher.launch(new String[] {"image/*"});
        });

        btnDesplegarFecha.setOnClickListener(view -> {
            Toast.makeText(this, "no puedes modifcar este valor", Toast.LENGTH_SHORT).show();
        });


    }

public int llenarImagen(String tipoActividad)
{
    switch (tipoActividad){
        case "Baño":
            return R.drawable.banio;
        case "Beber":
            return R.drawable.beber;
        case "Caminata":
            return R.drawable.caminata;
        case "Comer":
            return R.drawable.comer;
        case "Correr":
            return R.drawable.correr;
        case "Dormir":
            return R.drawable.dormir;
        case "Juego":
            return R.drawable.juego;
        case "Nececidades":
            return R.drawable.necesidades;
        case "Otros":
            return R.drawable.otros;
        case "Paseo":
            return R.drawable.paseo;
        case "Siesta":
            return R.drawable.siesta;
        case "Vacunación":
            return R.drawable.vacuna;
    };
    return 0;
}

    public void ActualizarRegistro(int idOriginal) {
        for (DiarioMascotaModel nota : DiarioMascotaModel.listaNotasDiario) {
            if (nota.getId() == idOriginal) {
                nota.setFecha(fechaRegistro.getText().toString());
                nota.setTitulo(titulo.getText().toString());
                nota.setNota(editnota.getText().toString());
                nota.setPathImagen(llenarImagen(spinnerActividades.getSelectedItem().toString()));
                nota.setFavorito(checkBoxFavoritoS.isChecked());
                nota.setActividad(spinnerActividades.getSelectedItem().toString());
                nota.setLatitud(lat);
                nota.setLongitud(lng);
                if (uriImage != null && !uriImage.equals(nota.getUriImage())) {
                    nota.setUriImage(uriImage);
                }

                break;
            }
        }
    }

    public void seleccionarValorSpinner(String valor) {
        SpinnerAdapter adapter = spinnerActividades.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equals(valor)) {
                spinnerActividades.setSelection(i);
                break;
            }
        }
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