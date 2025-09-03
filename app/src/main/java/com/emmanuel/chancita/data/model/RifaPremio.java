package com.emmanuel.chancita.data.model;

public class RifaPremio {
    private String premioId;
    private String premioTitulo;
    private String premioDescripcion;
    private int premioOrden; // Indica si el premio respecta al 1er/2do/3er/etc puesto

    public RifaPremio(String premioId, String premioTitulo, String premioDescripcion, int premioOrden) {
        setPremioId(premioId);
        setPremioTitulo(premioTitulo);
        setPremioDescripcion(premioDescripcion);
        setPremioOrden(premioOrden);
    }

    public String getPremioId() {
        return premioId;
    }

    public void setPremioId(String premioId) {
        this.premioId = premioId;
    }

    public String getPremioTitulo() {
        return premioTitulo;
    }

    public void setPremioTitulo(String premioTitulo) {
        this.premioTitulo = premioTitulo;
    }

    public String getPremioDescripcion() {
        return premioDescripcion;
    }

    public void setPremioDescripcion(String premioDescripcion) {
        this.premioDescripcion = premioDescripcion;
    }

    public int getPremioOrden() {
        return premioOrden;
    }

    public void setPremioOrden(int premioOrden) {
        this.premioOrden = premioOrden;
    }
}
