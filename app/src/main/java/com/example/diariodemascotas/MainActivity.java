package com.example.diariodemascotas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.diariodemascotas.database.DBHelper;
import com.google.firebase.BuildConfig;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;

public class MainActivity extends AppCompatActivity {

    Button btnIniciarSesion;
    EditText usuario;
    EditText contraseña;
    Button registrarNuevoUsuario;
    DBHelper db;
    private FirebaseAnalytics analytics;
    private SharedPreferences prefs;
    private boolean allowAnalytics = true;
    Trace trace = FirebasePerformance.getInstance().newTrace("trace_login_flow");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializaciones
        btnIniciarSesion = findViewById(R.id.button_iniciar_sesion);
        usuario = findViewById(R.id.usuario);
        contraseña = findViewById(R.id.contraseña);
        registrarNuevoUsuario = findViewById(R.id.button_registrar);
        db = new DBHelper(this);

        analytics = FirebaseAnalytics.getInstance(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        allowAnalytics = prefs.getBoolean("allow_analytics", true); // switch de privacidad

        // Evento de pantalla manual
        if (allowAnalytics) {
            Bundle screen = new Bundle();
            screen.putString(FirebaseAnalytics.Param.SCREEN_NAME, "login");
            screen.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity");
            analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, screen);
        }

        // Registrar nuevo usuario
        registrarNuevoUsuario.setOnClickListener(view -> {
            startActivity(new Intent(this, ActivityRegister.class));
        });

        // Iniciar sesión
        btnIniciarSesion.setOnClickListener(view -> {
            String user = usuario.getText().toString().trim();
            String pass = contraseña.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Ingrese usuario y contraseña", Toast.LENGTH_SHORT).show();

                if (allowAnalytics) {
                    Bundle e = new Bundle();
                    e.putString("error_code", "EMPTY_FIELDS");
                    analytics.logEvent("login_fail", e);
                }

                return;
            }

            trace.start();
            trace.putAttribute("pantalla", "login");

            try {
                boolean login = db.checkLogin(user, pass.toCharArray());
                if (login) {
                    analytics.setUserId(user);
                    analytics.setUserProperty("role", "ADMIN");

                    if (allowAnalytics) {
                        Bundle e = new Bundle();
                        e.putString("source", "email");
                        analytics.logEvent("login_success", e);
                    }

                    Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show();
                    trace.putAttribute("success", "true");
                    trace.stop();

                    startActivity(new Intent(this, NotasDeDiario.class));
                } else {
                    Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();

                    if (allowAnalytics) {
                        Bundle e = new Bundle();
                        e.putString("error_code", "INVALID_CREDENTIALS");
                        analytics.logEvent("login_fail", e);
                    }

                    trace.putAttribute("unsuccess", "false");
                    trace.stop();
                }
            } catch (Exception ex) {
                Toast.makeText(this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();

                FirebaseCrashlytics.getInstance().setCustomKey("flow", "login");
                FirebaseCrashlytics.getInstance().recordException(ex);

                if (allowAnalytics) {
                    Bundle e = new Bundle();
                    e.putString("error_code", ex.getMessage());
                    analytics.logEvent("login_fail", e);
                }

                trace.putAttribute("failure", ex.getMessage());
                trace.stop();
            }

            //Crash forzado para pruebas (solo debug)
            if (BuildConfig.DEBUG && user.equals("crash")) {
                throw new RuntimeException("Crash de prueba (debug)");
            }
        });
    }
}