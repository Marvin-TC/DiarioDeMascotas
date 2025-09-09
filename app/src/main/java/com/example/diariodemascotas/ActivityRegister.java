package com.example.diariodemascotas;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.diariodemascotas.database.DBHelper;
import com.example.diariodemascotas.utils.PasswordUtils;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class ActivityRegister extends AppCompatActivity {

    Button btnRegistrar;
    Button btnRegresar;
    EditText txtNombreCompleto;
    EditText txtApellidosCompleto;
    EditText txtUsuario;
    EditText txtContraseña;
    EditText txtConfirmarContraseña;

    private DBHelper db;
    private FirebaseAnalytics analytics;
    private boolean allowAnalytics = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtNombreCompleto = findViewById(R.id.txt_nombre_completo);
        txtApellidosCompleto = findViewById(R.id.txt_apellidos_completo);
        txtUsuario = findViewById(R.id.usuario);
        txtContraseña = findViewById(R.id.contraseña);
        txtConfirmarContraseña = findViewById(R.id.confirmar_contraseña);
        btnRegresar = findViewById(R.id.btn_regresar);
        btnRegistrar = findViewById(R.id.btn_registrar);
        db = new DBHelper(this);

        analytics = FirebaseAnalytics.getInstance(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        allowAnalytics = prefs.getBoolean("allow_analytics", true);

        // Evento de pantalla
        if (allowAnalytics) {
            Bundle screen = new Bundle();
            screen.putString(FirebaseAnalytics.Param.SCREEN_NAME, "registro");
            screen.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "ActivityRegister");
            analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, screen);
        }

        btnRegistrar.setOnClickListener(view -> {
            if (txtNombreCompleto.getText().toString().isEmpty()) {
                txtNombreCompleto.setError("Este campo es requerido");
                return;
            } else if (txtApellidosCompleto.getText().toString().isEmpty()) {
                txtApellidosCompleto.setError("Este campo es requerido");
                return;
            } else if (txtUsuario.getText().toString().isEmpty()) {
                txtUsuario.setError("Este campo es requerido");
                return;
            } else if (txtContraseña.getText().toString().isEmpty()) {
                txtContraseña.setError("Este campo es requerido");
                return;
            } else if (txtConfirmarContraseña.getText().toString().isEmpty()) {
                txtConfirmarContraseña.setError("Este campo es requerido");
                return;
            }

            // Validar que las contraseñas coincidan
            if (!txtContraseña.getText().toString().equals(txtConfirmarContraseña.getText().toString())) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                txtConfirmarContraseña.setError("Las contraseñas no coinciden");
                txtConfirmarContraseña.requestFocus();

                if (allowAnalytics) {
                    Bundle e = new Bundle();
                    e.putString("error_code", "PASSWORD_MISMATCH");
                    analytics.logEvent("registro_fail", e);
                }
                return;
            }

            registrarUsuario();
        });

        btnRegresar.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });
    }

    private void registrarUsuario() {
        String nombreCompleto = txtNombreCompleto.getText().toString().trim();
        String apellidosCompleto = txtApellidosCompleto.getText().toString().trim();
        String usuario = txtUsuario.getText().toString().trim();
        String contraseña = txtContraseña.getText().toString().trim();

        try {
            if (db.userExiste(usuario)) {
                Toast.makeText(this, "El usuario ya existe", Toast.LENGTH_SHORT).show();

                if (allowAnalytics) {
                    Bundle e = new Bundle();
                    e.putString("error_code", "USER_EXISTS");
                    e.putString("username", usuario);
                    analytics.logEvent("registro_fail", e);
                }

                return;
            }

            long id = db.insertUser(nombreCompleto, apellidosCompleto, usuario, contraseña.toCharArray());
            if (id > 0) {
                Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();

                if (allowAnalytics) {
                    Bundle e = new Bundle();
                    e.putString("user_id", usuario);
                    analytics.logEvent("registro_success", e);
                }

                finish();
            } else {
                Toast.makeText(this, "No se pudo crear el usuario", Toast.LENGTH_SHORT).show();

                if (allowAnalytics) {
                    Bundle e = new Bundle();
                    e.putString("error_code", "INSERT_FAILED");
                    analytics.logEvent("registro_fail", e);
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al crear el usuario", Toast.LENGTH_SHORT).show();

            FirebaseCrashlytics.getInstance().setCustomKey("flow", "registro");
            FirebaseCrashlytics.getInstance().recordException(e);

            if (allowAnalytics) {
                Bundle error = new Bundle();
                error.putString("error_code", e.getMessage());
                analytics.logEvent("registro_fail", error);
            }
        }
    }
}