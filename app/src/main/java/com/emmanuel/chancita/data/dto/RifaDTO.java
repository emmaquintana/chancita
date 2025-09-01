package com.emmanuel.chancita.data.dto;

import com.emmanuel.chancita.data.model.MetodoEleccionGanador;
import com.emmanuel.chancita.data.model.RifaEstado;

import java.time.LocalDateTime;

public class RifaDTO {
    private String titulo;
    private String descripcion;
    private String creadoPor;
    private RifaEstado estado;
    private String codigo;
    private MetodoEleccionGanador metodoEleccionGanador;
    private String motivoEleccionGanador;
    private LocalDateTime fechaSorteo;
    private double precioNumero;
    private LocalDateTime creadoEn;

    // Constructor para crear nueva rifa
    public RifaDTO(String titulo, String descripcion, String creadoPor,
                   String codigo, MetodoEleccionGanador metodoEleccionGanador,
                   String motivoEleccionGanador, LocalDateTime fechaSorteo,
                   double precioNumero) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.creadoPor = creadoPor;
        this.estado = RifaEstado.ABIERTO; // Estado inicial
        this.codigo = codigo;
        this.metodoEleccionGanador = metodoEleccionGanador;
        this.motivoEleccionGanador = motivoEleccionGanador;
        this.fechaSorteo = fechaSorteo;
        this.precioNumero = precioNumero;
        this.creadoEn = LocalDateTime.now();
    }

    // Constructor completo para mapear desde entidad
    public RifaDTO(String titulo, String descripcion, String creadoPor,
                   RifaEstado estado, String codigo, MetodoEleccionGanador metodoEleccionGanador,
                   String motivoEleccionGanador, LocalDateTime fechaSorteo,
                   double precioNumero, LocalDateTime creadoEn) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.creadoPor = creadoPor;
        this.estado = estado;
        this.codigo = codigo;
        this.metodoEleccionGanador = metodoEleccionGanador;
        this.motivoEleccionGanador = motivoEleccionGanador;
        this.fechaSorteo = fechaSorteo;
        this.precioNumero = precioNumero;
        this.creadoEn = creadoEn;
    }

    // Getters y setters...
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCreadoPor() { return creadoPor; }
    public void setCreadoPor(String creadoPor) { this.creadoPor = creadoPor; }

    public RifaEstado getEstado() { return estado; }
    public void setEstado(RifaEstado estado) { this.estado = estado; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public MetodoEleccionGanador getMetodoEleccionGanador() { return metodoEleccionGanador; }
    public void setMetodoEleccionGanador(MetodoEleccionGanador metodoEleccionGanador) { this.metodoEleccionGanador = metodoEleccionGanador; }

    public String getMotivoEleccionGanador() { return motivoEleccionGanador; }
    public void setMotivoEleccionGanador(String motivoEleccionGanador) { this.motivoEleccionGanador = motivoEleccionGanador; }

    public LocalDateTime getFechaSorteo() { return fechaSorteo; }
    public void setFechaSorteo(LocalDateTime fechaSorteo) { this.fechaSorteo = fechaSorteo; }

    public double getPrecioNumero() { return precioNumero; }
    public void setPrecioNumero(double precioNumero) { this.precioNumero = precioNumero; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}