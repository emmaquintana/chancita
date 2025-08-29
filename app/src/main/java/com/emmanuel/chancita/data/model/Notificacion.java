package com.emmanuel.chancita.data.model;

public class Notificacion {

    private String id;
    private String fecha;
    private String titulo;
    private String cuerpo;
    private String usuarioId;

    public Notificacion(String fecha, String titulo, String cuerpo) {
        setFecha(fecha);
        setTitulo(titulo);
        setCuerpo(cuerpo);
    }

    public Notificacion(String id, String fecha, String titulo, String cuerpo, String usuarioId) {
        setId(id);
        setFecha(fecha);
        setTitulo(titulo);
        setCuerpo(cuerpo);
        setUsuarioId(usuarioId);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setCuerpo(String cuerpo) {
        this.cuerpo = cuerpo;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getFecha() {
        return fecha;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getCuerpo() {
        return cuerpo;
    }
}