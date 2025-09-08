package com.emmanuel.chancita.data.model;

import java.util.Date;

public class Notificacion {
    private String id;
    private String titulo;
    private String cuerpo;
    private Date fecha;
    private String rifaId;
    private String usuarioId;
    private String tipo; // "ganador", "sorteo"
    private boolean leida;

    // Constructor vacío necesario para Firestore
    public Notificacion() {}

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getCuerpo() { return cuerpo; }
    public void setCuerpo(String cuerpo) { this.cuerpo = cuerpo; }
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    public String getRifaId() { return rifaId; }
    public void setRifaId(String rifaId) { this.rifaId = rifaId; }
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public boolean isLeida() { return leida; }
    public void setLeida(boolean leida) { this.leida = leida; }
}