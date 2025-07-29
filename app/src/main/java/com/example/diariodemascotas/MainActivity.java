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

public class MainActivity extends AppCompatActivity {

    Button btnIniciarSesion;
    EditText usuario;
    EditText contraseña;

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

        btnIniciarSesion.setOnClickListener(view -> {

            if (usuario.getText().toString().isEmpty() || contraseña.getText().toString().isEmpty())
            {
                Toast.makeText(this, "Ingrese usuario y contraseña", Toast.LENGTH_SHORT).show();
            }else{
                Intent intent = new Intent(this, NotasDeDiario.class);

                if (usuario.getText().toString().equals("admin") && contraseña.getText().toString().equals("admin"))
                {

                    startActivity(intent);
                }
                else{
                    Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
            }

        });


    }
}