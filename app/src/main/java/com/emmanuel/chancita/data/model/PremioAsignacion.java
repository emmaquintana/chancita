package com.emmanuel.chancita.data.model;

import java.util.List;

public class PremioAsignacion {
    private RifaPremio premio;
    private List<Integer> numerosDisponibles;
    private Integer numeroSeleccionado;

    public PremioAsignacion(RifaPremio premio, List<Integer> numerosDisponibles) {
        this.premio = premio;
        this.numerosDisponibles = numerosDisponibles;
    }

    public RifaPremio getPremio() { return premio; }
    public List<Integer> getNumerosDisponibles() { return numerosDisponibles; }
    public Integer getNumeroSeleccionado() { return numeroSeleccionado; }
    public void setNumeroSeleccionado(Integer numeroSeleccionado) { this.numeroSeleccionado = numeroSeleccionado; }
}
