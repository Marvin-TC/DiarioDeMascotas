package com.example.diariodemascotas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.diariodemascotas.database.DBHelper;

public class MainActivity extends AppCompatActivity {

    Button btnIniciarSesion;
    EditText usuario;
    EditText contraseña;
    Button registrarNuevoUsuario;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnIniciarSesion = findViewById(R.id.button_iniciar_sesion);
        usuario = findViewById(R.id.usuario);
        contraseña = findViewById(R.id.contraseña);
        registrarNuevoUsuario = findViewById(R.id.button_registrar);
        db = new DBHelper(this);


        registrarNuevoUsuario.setOnClickListener(view -> {
            Intent intent = new Intent(this, ActivityRegister.class);
            startActivity(intent);
        });

        btnIniciarSesion.setOnClickListener(view -> {

            if (usuario.getText().toString().isEmpty() || contraseña.getText().toString().isEmpty())
            {
                Toast.makeText(this, "Ingrese usuario y contraseña", Toast.LENGTH_SHORT).show();
            }else{
                try {
                    boolean login = db.checkLogin(usuario.getText().toString().trim(), contraseña.getText().toString().trim().toCharArray());
                    if (login)
                    {
                        Toast.makeText(this,"Bienvenido", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, NotasDeDiario.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(this, "Error al iniciar sesion", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}