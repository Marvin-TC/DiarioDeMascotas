package com.example.diariodemascotas.models;

import com.example.diariodemascotas.R;

import java.util.ArrayList;
import java.util.List;

public class DiarioMascotaModel {
    int id;
    int idMascota;
    String fecha;
    String titulo;
    String nota;
    int pathImagen;
    boolean favorito;
    String  actividad;
    public static List<DiarioMascotaModel> listaNotasDiario = new ArrayList<>();

    static {
        listaNotasDiario.add(new DiarioMascotaModel(1,1, "22/07/2025 20:57","se durmio todo el dia","despues de comer mucho se durmio como un bebe", R.drawable.dormir,true,"Dormir"));
        listaNotasDiario.add(new DiarioMascotaModel(2,2, "22/07/2025 20:57","lo bañe con nuevo shampoo","estoy probando un nuevo shampo antipulga, espero que funcione", R.drawable.banio,false,"Baño"));
    }


    public DiarioMascotaModel(int id, int idMascota, String fecha, String titulo, String nota, int pathImagen, boolean favorito, String actividad) {
        this.id = id;
        this.idMascota = idMascota;
        this.fecha = fecha;
        this.titulo = titulo;
        this.nota = nota;
        this.pathImagen = pathImagen;
        this.favorito = favorito;
        this.actividad = actividad;
    }

    public DiarioMascotaModel() {
    }

    //getters
    public int getId() {
        return id;
    }

    public int getIdMascota() {
        return idMascota;
    }

    public String getFecha() {
        return fecha;
    }

    public String getNota() {
        return nota;
    }

    public int getPathImagen() {
        return pathImagen;
    }

    public boolean isFavorito() {
        return favorito;
    }

    public String getActividad() {
        return actividad;
    }

    public String getTitulo() {
        return titulo;
    }
    //setters
    public void setId(int id) {
        this.id = id;
    }

    public void setIdMascota(int idMascota) {
        this.idMascota = idMascota;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public void setPathImagen(int pathImagen) {
        this.pathImagen = pathImagen;
    }

    public void setFavorito(boolean favorito) {
        this.favorito = favorito;
    }
    public void setActividad(String actividad) {
        this.actividad = actividad;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
}
