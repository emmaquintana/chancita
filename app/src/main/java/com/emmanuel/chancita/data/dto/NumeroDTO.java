package com.emmanuel.chancita.data.dto;

import java.time.LocalDateTime;

public class NumeroDTO {
    private int valor;
    private String usuarioId;
    private String rifaId;
    private LocalDateTime creadoEn;

    public NumeroDTO(int valor, String usuarioId, String rifaId) {
        this.valor = valor;
        this.usuarioId = usuarioId;
        this.rifaId = rifaId;
        this.creadoEn = LocalDateTime.now();
    }

    public NumeroDTO(int valor, String usuarioId, String rifaId, LocalDateTime creadoEn) {
        this.valor = valor;
        this.usuarioId = usuarioId;
        this.rifaId = rifaId;
        this.creadoEn = creadoEn;
    }

    public int getValor() { return valor; }
    public void setValor(int valor) { this.valor = valor; }

    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }

    public String getRifaId() { return rifaId; }
    public void setRifaId(String rifaId) { this.rifaId = rifaId; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}
