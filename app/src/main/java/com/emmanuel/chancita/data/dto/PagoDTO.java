package com.emmanuel.chancita.data.dto;

import com.emmanuel.chancita.data.model.PagoEstado;

import java.time.LocalDateTime;

public class PagoDTO {
    private String moneda;
    private PagoEstado estado;
    private String idPreferencia;
    private String usuarioId;
    private LocalDateTime creadoEn;

    public PagoDTO(String moneda, String idPreferencia, String usuarioId) {
        this.moneda = moneda;
        this.estado = PagoEstado.Pendiente;
        this.idPreferencia = idPreferencia;
        this.usuarioId = usuarioId;
        this.creadoEn = LocalDateTime.now();
    }

    public PagoDTO(String moneda, PagoEstado estado, String idPreferencia,
                   String usuarioId, LocalDateTime creadoEn) {
        this.moneda = moneda;
        this.estado = estado;
        this.idPreferencia = idPreferencia;
        this.usuarioId = usuarioId;
        this.creadoEn = creadoEn;
    }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }

    public PagoEstado getEstado() { return estado; }
    public void setEstado(PagoEstado estado) { this.estado = estado; }

    public String getIdPreferencia() { return idPreferencia; }
    public void setIdPreferencia(String idPreferencia) { this.idPreferencia = idPreferencia; }

    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}
