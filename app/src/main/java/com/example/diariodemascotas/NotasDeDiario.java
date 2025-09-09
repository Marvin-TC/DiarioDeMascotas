package com.example.diariodemascotas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
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
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;

import java.util.List;

public class NotasDeDiario extends AppCompatActivity {

    List<MascotasModel> listaMascotasRegistradas;
    List<DiarioMascotaModel> listaNotasDiario;
    RecyclerView recyclerView_notas;
    NotasDiarioAdapter adapterMascotas;
    FloatingActionButton fab;
    Toolbar toolbar;
    ImageButton imageButon_sincronizar;
    ImageButton imageButon_mapa;

    private FirebaseAnalytics analytics;
    private boolean allowAnalytics = true;
    private Trace trace;

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

        analytics = FirebaseAnalytics.getInstance(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        allowAnalytics = prefs.getBoolean("allow_analytics", true);

        if (allowAnalytics) {
            Bundle screen = new Bundle();
            screen.putString(FirebaseAnalytics.Param.SCREEN_NAME, "notas_lista");
            screen.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "NotasDeDiario");
            analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, screen);
            // Propiedades de usuario
            analytics.setUserProperty("usa_mapa", "true");
            analytics.setUserProperty("usa_camara", "true");
        }

        // Trazar rendimiento del cargado de lista
        trace = FirebasePerformance.getInstance().newTrace("trace_list_load");
        trace.start();

        toolbar = findViewById(R.id.toolbar);
        fab = findViewById(R.id.fab);
        imageButon_sincronizar = findViewById(R.id.imageButon_sincronizar);
        imageButon_mapa = findViewById(R.id.imageButon_mapa);

        MascotasModel mascotasModel = new MascotasModel();
        DiarioMascotaModel diarioMascotaModel = new DiarioMascotaModel();
        listaMascotasRegistradas = mascotasModel.listaMascotasRegistradas;
        listaNotasDiario = diarioMascotaModel.listaNotasDiario;

        recyclerView_notas = findViewById(R.id.recycler_view);
        recyclerView_notas.setLayoutManager(new LinearLayoutManager(this));

        adapterMascotas = new NotasDiarioAdapter(listaNotasDiario, new NotasDiarioAdapter.OnClickListener() {
            @Override
            public void eliminarItem(int position) {
                listaNotasDiario.remove(position);
                adapterMascotas.notifyItemRemoved(position);
            }

            @Override
            public void editarItem(int id) {
                Intent intent = new Intent(NotasDeDiario.this, EditarRegistroActividad.class);
                intent.putExtra("id", id);
                startActivity(intent);

                if (allowAnalytics) {
                    DiarioMascotaModel nota = DiarioMascotaModel.buscarPorId(id);
                    if (nota != null) {
                        Bundle evento = new Bundle();
                        evento.putString("actividad", nota.getActividad());
                        evento.putBoolean("favorito", nota.isFavorito());
                        evento.putString("item_id", String.valueOf(id));
                        analytics.logEvent("diario_visto", evento);
                    }
                }
            }
        });

        recyclerView_notas.setAdapter(adapterMascotas);
        trace.stop(); //Finalizar medición de rendimiento de carga


        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, RegistroActividad.class);
            startActivity(intent);
        });

        imageButon_sincronizar.setOnClickListener(view -> {
            adapterMascotas.notifyDataSetChanged();
            Toast.makeText(this, "Sincronizando...", Toast.LENGTH_SHORT).show();
        });

        imageButon_mapa.setOnClickListener(view -> {
            Intent intent = new Intent(this, RegistrosDeUbicacion.class);
            startActivity(intent);
        });

        toolbar.setNavigationOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });
    }
}