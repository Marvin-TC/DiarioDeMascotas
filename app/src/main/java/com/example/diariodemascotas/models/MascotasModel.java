package com.example.diariodemascotas.models;

import com.example.diariodemascotas.R;

import java.util.ArrayList;
import java.util.List;

public class MascotasModel {
    int id;
    String nombre;
    String raza;
    int edad;
    tipoMascota tipo;
    int imagen;

    public static List<MascotasModel> listaMascotasRegistradas = new ArrayList<>();

    static {
        listaMascotasRegistradas.add(new MascotasModel(1,"Toto","chiguawa",10, tipoMascota.perro, R.drawable.icono_perro));
        listaMascotasRegistradas.add(new MascotasModel(2,"Luna","no especifica",2, tipoMascota.gato,R.drawable.icono_gato));
    }

    public enum tipoMascota{
        perro, gato, hamster, Peces, Ave;
    }

    public MascotasModel(int id, String nombre, String raza, int edad, tipoMascota tipo, int imagen) {
        this.id = id;
        this.nombre = nombre;
        this.raza = raza;
        this.edad = edad;
        this.tipo = tipo;
        this.imagen = imagen;
    }

    public MascotasModel() {
    }
    //getters

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRaza() {
        return raza;
    }

    public int getEdad() {
        return edad;
    }

    public tipoMascota getTipo() {
        return tipo;
    }

    public int getImagen() {
        return imagen;
    }
    //setters

    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public void setTipo(tipoMascota tipo) {
        this.tipo = tipo;
    }

    public void setImagen(int imagen) {
        this.imagen = imagen;
    }
}
