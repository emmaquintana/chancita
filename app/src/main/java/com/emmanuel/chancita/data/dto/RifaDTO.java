package com.emmanuel.chancita.data.dto;

import com.emmanuel.chancita.data.model.MetodoEleccionGanador;
import com.emmanuel.chancita.data.model.RifaEstado;
import com.emmanuel.chancita.data.model.RifaPremio;

import java.time.LocalDateTime;
import java.util.List;

public class RifaDTO {
    private String id;
    private String titulo;
    private String descripcion;
    private int cantNumeros;
    private String creadoPor;
    private RifaEstado estado;
    private String codigo;
    private MetodoEleccionGanador metodoEleccionGanador;
    private String motivoEleccionGanador;
    private LocalDateTime fechaSorteo;
    private double precioNumero;
    private LocalDateTime creadoEn;
    private List<String> participantesIds;
    private List<RifaPremio> premios;

    // Constructor para crear nueva rifa
    public RifaDTO(String id, String titulo, String descripcion, int cantNumeros, String creadoPor,
                   String codigo, MetodoEleccionGanador metodoEleccionGanador,
                   String motivoEleccionGanador, LocalDateTime fechaSorteo,
                   double precioNumero, List<String> participantesIds, List<RifaPremio> premios) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.cantNumeros = cantNumeros;
        this.creadoPor = creadoPor;
        this.estado = RifaEstado.ABIERTO; // Estado inicial
        this.codigo = codigo;
        this.metodoEleccionGanador = metodoEleccionGanador;
        this.motivoEleccionGanador = motivoEleccionGanador;
        this.fechaSorteo = fechaSorteo;
        this.precioNumero = precioNumero;
        this.creadoEn = LocalDateTime.now();
        this.participantesIds = participantesIds;
        this.premios = premios;
    }

    // Constructor completo para mapear desde entidad
    public RifaDTO(String id, String titulo, String descripcion, int cantNumeros, String creadoPor,
                   RifaEstado estado, String codigo, MetodoEleccionGanador metodoEleccionGanador,
                   String motivoEleccionGanador, LocalDateTime fechaSorteo,
                   double precioNumero, LocalDateTime creadoEn, List<RifaPremio> premios) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.cantNumeros = cantNumeros;
        this.creadoPor = creadoPor;
        this.estado = estado;
        this.codigo = codigo;
        this.metodoEleccionGanador = metodoEleccionGanador;
        this.motivoEleccionGanador = motivoEleccionGanador;
        this.fechaSorteo = fechaSorteo;
        this.precioNumero = precioNumero;
        this.creadoEn = creadoEn;
        this.premios = premios;
    }

    public RifaDTO() {}

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCantNumeros() {
        return cantNumeros;
    }

    public void setCantNumeros(int cantNumeros) {
        this.cantNumeros = cantNumeros;
    }

    public List<String> getParticipantesIds() {
        return participantesIds;
    }

    public void setParticipantesIds(List<String> participantesIds) {
        this.participantesIds = participantesIds;
    }

    public List<RifaPremio> getPremios() {
        return premios;
    }

    public void setPremios(List<RifaPremio> premios) {
        this.premios = premios;
    }
}