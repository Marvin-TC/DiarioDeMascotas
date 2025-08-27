package com.example.diariodemascotas;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper; // este sí es del framework
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
public class ActividadMaps extends AppCompatActivity implements OnMapReadyCallback {

    private static final int RC_LOCATION = 1001;

    private GoogleMap googleMaps;
    private FusedLocationProviderClient fusedLocationClient;
    public static final String EXTRA_LAT = "EXTRA_LAT";
    public static final String EXTRA_LNG = "EXTRA_LNG";

    private double selLat = Double.NaN;
    private double selLng = Double.NaN;

    Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_actividad_maps);
        View root = findViewById(R.id.main);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
                return insets;
            });
        }

        btnGuardar = findViewById(R.id.guardar_ubicacion);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        btnGuardar.setOnClickListener(v -> {
            if (!Double.isNaN(selLat) && !Double.isNaN(selLng)) {
                devolverResultadoYSalir();
            } else {
                Toast.makeText(ActividadMaps.this,
                        "Espere mientras se obtiene la ubicación…",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMaps = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        habilitarMiUbicacionYCentrar();

    }

    private boolean tienePermisoUbicacion() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void pedirPermisosUbicacion() {
        ActivityCompat.requestPermissions(
                this,
                new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                RC_LOCATION
        );
    }

    private void habilitarMiUbicacionYCentrar() {
        if (!tienePermisoUbicacion()) {
            pedirPermisosUbicacion();
            return;
        }
        try {
            googleMaps.setMyLocationEnabled(true);
        } catch (SecurityException ignored) {}

        centrarEnMiUbicacion();
    }

    private void centrarEnMiUbicacion() {
        if (!tienePermisoUbicacion()) {
            pedirPermisosUbicacion();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null && googleMaps != null) {
                            selLat = location.getLatitude();
                            selLng = location.getLongitude();
                        LatLng here = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(here, 17f));
                    } else {
                        solicitarUbicacionUnaVez();
                    }
                })
                .addOnFailureListener(e -> {
                    solicitarUbicacionUnaVez();
                });
    }

    private void solicitarUbicacionUnaVez() {
        if (!tienePermisoUbicacion()) {
            pedirPermisosUbicacion();
            return;
        }
        LocationRequest req = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                4000L)
                .setMaxUpdates(1)
                .setWaitForAccurateLocation(true)
                .build();

        LocationCallback callback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult result) {
                if (googleMaps != null && result.getLastLocation() != null) {
                    LatLng here = new LatLng(
                            result.getLastLocation().getLatitude(),
                            result.getLastLocation().getLongitude()
                    );

                    selLat = result.getLastLocation().getLatitude();
                    selLng = result.getLastLocation().getLongitude();

                    googleMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(here, 17f));
                }
                fusedLocationClient.removeLocationUpdates(this);
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            fusedLocationClient.requestLocationUpdates(req, getMainExecutor(), callback);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_LOCATION) {
            boolean concedido = false;
            for (int r : grantResults) {
                if (r == PackageManager.PERMISSION_GRANTED) { concedido = true; break; }
            }
            if (concedido) {
                habilitarMiUbicacionYCentrar();
            } else {
            }
        }
    }

    private void devolverResultadoYSalir() {
        Intent data = new Intent();
        data.putExtra(EXTRA_LAT, selLat);
        data.putExtra(EXTRA_LNG, selLng);
        setResult(RESULT_OK, data);
        finish();
    }
}