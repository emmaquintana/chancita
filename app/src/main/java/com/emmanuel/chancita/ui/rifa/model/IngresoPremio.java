package com.emmanuel.chancita.ui.rifa.model;

public class IngresoPremio {
    private String head;
    private String titulo;
    private String descripcion;

    public IngresoPremio(String head, String titulo, String descripcion) {
        this.head = head;
        this.titulo = titulo;
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }
}
