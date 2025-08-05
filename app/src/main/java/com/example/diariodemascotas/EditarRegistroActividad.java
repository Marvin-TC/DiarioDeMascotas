package com.example.diariodemascotas;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.diariodemascotas.models.DiarioMascotaModel;

public class EditarRegistroActividad extends AppCompatActivity {

    DiarioMascotaModel diarioMascotaModel = new DiarioMascotaModel();
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

    }
}