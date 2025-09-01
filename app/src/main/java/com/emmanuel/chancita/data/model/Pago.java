package com.emmanuel.chancita.data.model;

import java.time.LocalDateTime;

public class Pago {
    private String id;
    private String moneda;
    private PagoEstado estado;
    private String idPreferencia;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    private String usuarioId;

    public Pago(String id, String moneda, PagoEstado estado, String idPreferencia, LocalDateTime creadoEn, LocalDateTime actualizadoEn, String usuarioId) {
        this.id = id;
        this.moneda = moneda;
        this.estado = estado;
        this.idPreferencia = idPreferencia;
        this.creadoEn = creadoEn;
        this.actualizadoEn = actualizadoEn;
        this.usuarioId = usuarioId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public PagoEstado getEstado() {
        return estado;
    }

    public void setEstado(PagoEstado estado) {
        this.estado = estado;
    }

    public String getIdPreferencia() {
        return idPreferencia;
    }

    public void setIdPreferencia(String idPreferencia) {
        this.idPreferencia = idPreferencia;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    public LocalDateTime getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(LocalDateTime actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }
}
