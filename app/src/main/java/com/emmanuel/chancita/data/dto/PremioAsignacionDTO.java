package com.emmanuel.chancita.data.dto;

import com.emmanuel.chancita.data.model.RifaPremio;

import java.util.List;

public class PremioAsignacionDTO {
    private RifaPremio premio;
    private List<Integer> numerosDisponibles;
    private Integer numeroSeleccionado;

    public PremioAsignacionDTO(RifaPremio premio, List<Integer> numerosDisponibles) {
        this.premio = premio;
        this.numerosDisponibles = numerosDisponibles;
    }

    public RifaPremio getPremio() { return premio; }
    public List<Integer> getNumerosDisponibles() { return numerosDisponibles; }
    public Integer getNumeroSeleccionado() { return numeroSeleccionado; }
    public void setNumeroSeleccionado(Integer numeroSeleccionado) { this.numeroSeleccionado = numeroSeleccionado; }
}
