package com.emmanuel.chancita.data.model;

import java.time.LocalDateTime;
import java.util.List;

public class Rifa {

    private String id;
    private String titulo;
    private String descripcion;
    private int cantNumeros;
    private String creadoPor;
    private RifaEstado estado;
    private LocalDateTime creadoEn;
    private String codigo;
    private MetodoEleccionGanador metodoEleccionGanador;
    private String motivoEleccionGanador; // Este campo solo debe usarse si metodoEleccionGanador = DETERMINISTA
    private LocalDateTime fechaSorteo;
    private double precioNumero;
    private List<String> participantesIds;
    private List<RifaPremio> premios;
    private List<NumeroComprado> numerosComprados;

    public Rifa(String id, String titulo, String descripcion, int cantNumeros, String creadoPor, RifaEstado estado, LocalDateTime creadoEn, String codigo, MetodoEleccionGanador metodoEleccionGanador, String motivoEleccionGanador , LocalDateTime fechaSorteo, double precioNumero, List<String> participantesIds, List<RifaPremio> premios, List<NumeroComprado> numerosComprados) {
        setId(id);
        setTitulo(titulo);
        setDescripcion(descripcion);
        setCantNumeros(cantNumeros);
        setCreadoPor(creadoPor);
        setEstado(estado);
        setCreadoEn(creadoEn);
        setCodigo(codigo);
        setMetodoEleccionGanador(metodoEleccionGanador);
        setMotivoEleccionGanador(motivoEleccionGanador);
        setFechaSorteo(fechaSorteo);
        setPrecioNumero(precioNumero);
        setParticipantesIds(participantesIds);
        setPremios(premios);
        setNumerosComprados(numerosComprados);
    }

    public Rifa() {}

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

    public List<NumeroComprado> getNumerosComprados() {
        return numerosComprados;
    }

    public void setNumerosComprados(List<NumeroComprado> numerosComprados) {
        this.numerosComprados = numerosComprados;
    }
}
