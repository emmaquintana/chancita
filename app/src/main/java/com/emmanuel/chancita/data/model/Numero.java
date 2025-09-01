package com.emmanuel.chancita.data.model;

import java.time.LocalDateTime;

public class Numero {
    private String id;
    private int valor;
    private LocalDateTime creadoEn;
    private String usuarioId;
    private String rifaId;

    public Numero(String id, int valor, LocalDateTime creadoEn, String usuarioId, String rifaId) {
        this.id = id;
        this.valor = valor;
        this.creadoEn = creadoEn;
        this.usuarioId = usuarioId;
        this.rifaId = rifaId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getRifaId() {
        return rifaId;
    }

    public void setRifaId(String rifaId) {
        this.rifaId = rifaId;
    }
}
