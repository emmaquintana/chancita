package com.emmanuel.chancita.data.model;

public class PagoNumero {
    private String numeroId;
    private String pagoId;

    public PagoNumero(String numeroId, String pagoId) {
        this.numeroId = numeroId;
        this.pagoId = pagoId;
    }

    public String getPagoId() {
        return pagoId;
    }

    public void setPagoId(String pagoId) {
        this.pagoId = pagoId;
    }

    public String getNumeroId() {
        return numeroId;
    }

    public void setNumeroId(String numeroId) {
        this.numeroId = numeroId;
    }


}
