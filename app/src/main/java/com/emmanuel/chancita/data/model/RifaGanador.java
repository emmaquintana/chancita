package com.emmanuel.chancita.data.model;

import java.time.LocalDateTime;

public class RifaGanador {
    private String id;
    private String rifaId;
    private String premioId;
    private String usuarioId;
    private String numeroId; // El número con el que ganó
    private LocalDateTime asignadoEn;

    public RifaGanador(String id, String rifaId, String premioId, String usuarioId, String numeroId, LocalDateTime asignadoEn) {
        this.id = id;
        this.rifaId = rifaId;
        this.premioId = premioId;
        this.usuarioId = usuarioId;
        this.numeroId = numeroId;
        this.asignadoEn = asignadoEn;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRifaId() {
        return rifaId;
    }

    public void setRifaId(String rifaId) {
        this.rifaId = rifaId;
    }

    public String getPremioId() {
        return premioId;
    }

    public void setPremioId(String premioId) {
        this.premioId = premioId;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNumeroId() {
        return numeroId;
    }

    public void setNumeroId(String numeroId) {
        this.numeroId = numeroId;
    }

    public LocalDateTime getAsignadoEn() {
        return asignadoEn;
    }

    public void setAsignadoEn(LocalDateTime asignadoEn) {
        this.asignadoEn = asignadoEn;
    }
}
