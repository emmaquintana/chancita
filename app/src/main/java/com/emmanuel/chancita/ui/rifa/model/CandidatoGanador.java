package com.emmanuel.chancita.ui.rifa.model;

import java.util.List;

public class CandidatoGanador {
    private String nombreApellido;
    private boolean esGanador;
    private List<Integer> numeros;

    public CandidatoGanador(String nombreApellido, boolean esGanador, List<Integer> numeros) {
        this.nombreApellido = nombreApellido;
        this.esGanador = esGanador;
        this.numeros = numeros;
    }

    public String getNombreApellido() {
        return nombreApellido;
    }

    public void setNombreApellido(String nombreApellido) {
        this.nombreApellido = nombreApellido;
    }

    public boolean esGanador() {
        return esGanador;
    }

    public void setEsGanador(boolean esGanador) {
        this.esGanador = esGanador;
    }

    public List<Integer> getNumeros() {
        return numeros;
    }

    public void setNumeros(List<Integer> numeros) {
        this.numeros = numeros;
    }
}
