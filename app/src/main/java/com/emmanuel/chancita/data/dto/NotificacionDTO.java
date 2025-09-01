package com.emmanuel.chancita.data.dto;

public class NotificacionDTO {
    private String fecha;
    private String titulo;
    private String cuerpo;
    private String usuarioId;

    public NotificacionDTO(String fecha, String titulo, String cuerpo, String usuarioId) {
        this.fecha = fecha;
        this.titulo = titulo;
        this.cuerpo = cuerpo;
        this.usuarioId = usuarioId;
    }

    // Constructor para crear notificación sin usuarioId (notificaciones generales)
    public NotificacionDTO(String fecha, String titulo, String cuerpo) {
        this.fecha = fecha;
        this.titulo = titulo;
        this.cuerpo = cuerpo;
    }

    // Getters y setters
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getCuerpo() { return cuerpo; }
    public void setCuerpo(String cuerpo) { this.cuerpo = cuerpo; }

    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
}
