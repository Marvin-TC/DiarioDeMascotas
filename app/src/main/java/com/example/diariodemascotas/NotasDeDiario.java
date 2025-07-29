package com.example.diariodemascotas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diariodemascotas.adapters.NotasDiarioAdapter;
import com.example.diariodemascotas.models.DiarioMascotaModel;
import com.example.diariodemascotas.models.MascotasModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class NotasDeDiario extends AppCompatActivity {


    List<MascotasModel> listaMascotasRegistradas;
    List<DiarioMascotaModel> listaNotasDiario;
    RecyclerView recyclerView_notas;
    NotasDiarioAdapter adapterMascotas;
    FloatingActionButton fab;
    Toolbar toolbar;
    ImageButton imageButon_sincronizar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notas_de_diario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        toolbar = findViewById(R.id.toolbar);
        fab = findViewById(R.id.fab);
        imageButon_sincronizar = findViewById(R.id.imageButon_sincronizar);

        MascotasModel mascotasModel = new MascotasModel();
        DiarioMascotaModel diarioMascotaModel = new DiarioMascotaModel();

        listaMascotasRegistradas = mascotasModel.listaMascotasRegistradas;
        listaNotasDiario = diarioMascotaModel.listaNotasDiario;


        recyclerView_notas = findViewById(R.id.recycler_view);
        recyclerView_notas.setLayoutManager(new LinearLayoutManager(this));
        adapterMascotas = new NotasDiarioAdapter(listaNotasDiario);
        recyclerView_notas.setAdapter(adapterMascotas);

        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, RegistroActividad.class);
            startActivity(intent);
        });

        imageButon_sincronizar.setOnClickListener(view -> {
            adapterMascotas.notifyDataSetChanged();
            Toast.makeText(this, "Sincronizando...", Toast.LENGTH_SHORT).show();
        });

        toolbar.setNavigationOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

    }
}