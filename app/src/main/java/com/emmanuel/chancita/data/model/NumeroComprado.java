package com.emmanuel.chancita.data.model;

import java.util.List;

public class NumeroComprado {
    private List<Integer> numerosComprados;
    private String usuarioId;
    private double precioUnitario;


    public NumeroComprado(List<Integer> numerosComprados, String usuarioId, double precioUnitario) {
        setNumerosComprados(numerosComprados);
        setUsuarioId(usuarioId);
        setPrecioUnitario(precioUnitario);
    }

    public List<Integer> getNumerosComprados() {
        return this.numerosComprados;
    }

    public void setNumerosComprados(List<Integer> numerosComprados) {
        this.numerosComprados = numerosComprados;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}
