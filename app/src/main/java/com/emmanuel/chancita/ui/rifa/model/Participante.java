package com.emmanuel.chancita.ui.rifa.model;

import java.util.List;

public class Participante {
    private String nombre;
    private List<Integer> numeros;

    public Participante(String nombre, List<Integer> numeros) {
        this.nombre = nombre;
        this.numeros = numeros;
    }

    public List<Integer> getNumeros() {
        return numeros;
    }

    public void setNumeros(List<Integer> numeros) {
        this.numeros = numeros;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}