package com.emmanuel.chancita.data.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Rifa {

    private String id;
    private String titulo;
    private String descripcion;
    private String creadoPor;
    private RifaEstado estado;
    private LocalDateTime creadoEn;
    private String codigo;
    private MetodoEleccionGanador metodoEleccionGanador;
    private String motivoEleccionGanador; // Este campo solo debe usarse si metodoEleccionGanador = DETERMINISTA
    private LocalDateTime fechaSorteo;
    private double precioNumero;

    public Rifa(String id, String titulo, String descripcion, String creadoPor, RifaEstado estado, LocalDateTime creadoEn, String codigo, MetodoEleccionGanador metodoEleccionGanador, String motivoEleccionGanador ,LocalDateTime fechaSorteo, double precioNumero) {
        setId(id);
        setTitulo(titulo);
        setDescripcion(descripcion);
        setCreadoPor(creadoPor);
        setEstado(estado);
        setCreadoEn(creadoEn);
        setCodigo(codigo);
        setMetodoEleccionGanador(metodoEleccionGanador);
        setMotivoEleccionGanador(motivoEleccionGanador);
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

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public LocalDateTime getFechaSorteo() {
        return fechaSorteo;
    }

    public void setFechaSorteo(LocalDateTime fechaSorteo) {
        this.fechaSorteo = fechaSorteo;
    }

    public double getPrecioNumero() {
        return precioNumero;
    }

    public void setPrecioNumero(double precioNumero) {
        this.precioNumero = precioNumero;
    }

    public MetodoEleccionGanador getMetodoEleccionGanador() {
        return metodoEleccionGanador;
    }

    public void setMetodoEleccionGanador(MetodoEleccionGanador metodoEleccionGanador) {
        this.metodoEleccionGanador = metodoEleccionGanador;
    }

    public String getMotivoEleccionGanador() {
        return motivoEleccionGanador;
    }

    public void setMotivoEleccionGanador(String motivoEleccionGanador) {
        this.motivoEleccionGanador = motivoEleccionGanador;
    }
}
