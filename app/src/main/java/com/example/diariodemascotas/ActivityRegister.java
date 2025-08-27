package com.example.diariodemascotas;

import android.os.Build;
import android.os.Bundle;
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

public class ActivityRegister extends AppCompatActivity {

    Button btnRegistrar;
    Button btnRegresar;
    EditText txtNombreCompleto;
    EditText txtApellidosCompleto;
    EditText txtUsuario;
    EditText txtContraseña;
    EditText txtConfirmarContraseña;

    private DBHelper db;

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



        btnRegistrar.setOnClickListener(view -> {

            if (txtNombreCompleto.getText().toString().isEmpty()){ txtNombreCompleto.setError("Este campo es requerido");}
            else if (txtApellidosCompleto.getText().toString().isEmpty()){ txtApellidosCompleto.setError("Este campo es requerido");}
            else if(txtUsuario.getText().toString().isEmpty()){ txtUsuario.setError("Este campo es requerido");}
            else if (txtContraseña.getText().toString().isEmpty()){ txtContraseña.setError("Este campo es requerido");}
            else if (txtConfirmarContraseña.getText().toString().isEmpty()){ txtConfirmarContraseña.setError("Este campo es requerido");}
             else {
                    //Validar que las contraseñas sean iguales
                    if (txtContraseña.getText().toString().equals(txtConfirmarContraseña.getText().toString()))
                    {
                        registrarUsuario();
                    }else {
                        Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                        txtConfirmarContraseña.setError("Las contraseñas no coinciden");
                        txtConfirmarContraseña.requestFocus();
                    }
             }
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
                return;
            }
                long id = db.insertUser(nombreCompleto, apellidosCompleto, usuario, contraseña.toCharArray());
                if (id > 0) {
                    Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                }else {Toast.makeText(this, "no se pudo crear el usuario", Toast.LENGTH_SHORT).show();}
        }catch (Exception e)
        {
            Toast.makeText(this, "Error al crar el usuario", Toast.LENGTH_SHORT).show();
        }
    }

}