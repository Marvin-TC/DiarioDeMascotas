package com.example.diariodemascotas;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.diariodemascotas.models.DiarioMascotaModel;

public class RegistroActividad extends AppCompatActivity {

    Toolbar toolbar;
    EditText fecha_registro;
    EditText titulo;
    EditText nota;
    Spinner spinner_actividades;
    Button btn_agregar_imagen;
    ImageView imageView;
    CheckBox checkBoxFavoritoS;
    Button btn_cancelar;
    Button btn_guardar;


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


        toolbar = findViewById(R.id.toolbar);
        fecha_registro = findViewById(R.id.fecha_registro);
        titulo = findViewById(R.id.titulo);
        nota = findViewById(R.id.nota);
        spinner_actividades = findViewById(R.id.spinner_actividades);
        btn_agregar_imagen = findViewById(R.id.btn_agregar_imagen);
        imageView = findViewById(R.id.imageViewFoto);
        checkBoxFavoritoS = findViewById(R.id.checkBoxFavorito);
        btn_cancelar = findViewById(R.id.btn_cancelar);
        btn_guardar = findViewById(R.id.btn_guardar);


        toolbar.setTitle("Toto");
        spinner_actividades.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                imageView.setImageResource(llenarImagen(spinner_actividades.getSelectedItem().toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Opcional
            }
        });


        btn_guardar.setOnClickListener(View -> {
            realizarRegistro();
            Toast.makeText(this, "“\"¡Entrada guardada! \uD83D\uDC36 Tu lomito está orgulloso", Toast.LENGTH_LONG).show();
            finish();
        });


        btn_cancelar.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();

        });
        toolbar.setNavigationOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

    }
    public void realizarRegistro(){
        DiarioMascotaModel diarioMascotaModel = new DiarioMascotaModel();
        diarioMascotaModel.setFecha(fecha_registro.getText().toString());
        diarioMascotaModel.setTitulo(titulo.getText().toString());
        diarioMascotaModel.setNota(nota.getText().toString());
        diarioMascotaModel.setPathImagen(llenarImagen(spinner_actividades.getSelectedItem().toString()));
        diarioMascotaModel.setFavorito(checkBoxFavoritoS.isChecked());
        diarioMascotaModel.setActividad(spinner_actividades.getSelectedItem().toString());
        DiarioMascotaModel.listaNotasDiario.add(diarioMascotaModel);
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


}