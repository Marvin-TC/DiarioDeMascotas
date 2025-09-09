package com.example.diariodemascotas;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.diariodemascotas.models.DiarioMascotaModel;
import com.example.diariodemascotas.utils.AppLogger;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

public class RegistrosDeUbicacion extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMaps;
    private FirebaseAnalytics firebaseAnalytics;
    private boolean analyticsEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registros_de_ubicacion);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        analyticsEnabled = prefs.getBoolean("analytics_enabled", false);


        if (analyticsEnabled) {
            Bundle bundle = new Bundle();
            bundle.putString("pantalla", "RegistrosDeUbicacion");
            firebaseAnalytics.logEvent("pantalla_abierta", bundle);
        }

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        AppLogger.info("RegistrosDeUbicacion", "Activity creada correctamente");
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMaps = googleMap;

        try {
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

            List<DiarioMascotaModel> registros = DiarioMascotaModel.listaNotasDiario;

            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            boolean hayMarcadores = false;

            for (DiarioMascotaModel registro : registros) {
                double lat = registro.getLatitud();
                double lng = registro.getLongitud();
                if (lat == 0.0 && lng == 0.0) continue;

                LatLng posicion = new LatLng(lat, lng);
                googleMap.addMarker(new MarkerOptions()
                        .position(posicion)
                        .title(registro.getTitulo()));

                boundsBuilder.include(posicion);
                hayMarcadores = true;
            }

            if (hayMarcadores) {
                googleMap.setOnMapLoadedCallback(() -> {
                    LatLngBounds bounds = boundsBuilder.build();
                    int padding = 100;
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
                });
            }

            googleMap.setOnMarkerClickListener(marker -> {
                marker.showInfoWindow();

                // Enviar evento si está habilitado
                if (analyticsEnabled) {
                    Bundle b = new Bundle();
                    b.putString("titulo_marcador", marker.getTitle());
                    firebaseAnalytics.logEvent("marcador_tocado", b);
                }
                AppLogger.info("RegistrosDeUbicacion", "Marcador tocado: " + marker.getTitle());
                return false;
            });

        } catch (Exception e) {
            AppLogger.error("RegistrosDeUbicacion", "Error al cargar el mapa", e);
        }
    }
}