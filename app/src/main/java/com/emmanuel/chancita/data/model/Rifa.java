package com.emmanuel.chancita.data.model;

import java.time.LocalDate;

public class Rifa {

    private String id;
    private String titulo;
    private String descripcion;
    private String creadoPor;
    private RifaEstado estado;
    private LocalDate creadoEn;
    private String codigo;
    private LocalDate fechaSorteo;
    private double precioNumero;

    public Rifa(String id, String titulo, String descripcion, String creadoPor, RifaEstado estado, LocalDate creadoEn, String codigo, LocalDate fechaSorteo, double precioNumero) {
        setId(id);
        setTitulo(titulo);
        setDescripcion(descripcion);
        setCreadoPor(creadoPor);
        setEstado(estado);
        setCreadoEn(creadoEn);
        setCodigo(codigo);
        setFechaSorteo(fechaSorteo);
        setPrecioNumero(precioNumero);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(String creadoPor) {
        this.creadoPor = creadoPor;
    }

    public RifaEstado getEstado() {
        return estado;
    }

    public void setEstado(RifaEstado estado) {
        this.estado = estado;
    }

    public LocalDate getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDate creadoEn) {
        this.creadoEn = creadoEn;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public LocalDate getFechaSorteo() {
        return fechaSorteo;
    }

    public void setFechaSorteo(LocalDate fechaSorteo) {
        this.fechaSorteo = fechaSorteo;
    }

    public double getPrecioNumero() {
        return precioNumero;
    }

    public void setPrecioNumero(double precioNumero) {
        this.precioNumero = precioNumero;
    }
}
