package com.emmanuel.chancita.data.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UsuarioDTO {
    private String nombre;
    private String apellido;
    private String correo;
    private String nroCelular;
    private String contraseña; // Debe estar hasheada
    private LocalDate fechaNacimiento;
    private LocalDateTime creadoEn;
    private LocalDateTime ultimoIngreso;

    public UsuarioDTO(String nombre, String apellido, String correo, String nroCelular, String contraseña, LocalDate fechaNacimiento, LocalDateTime creadoEn, LocalDateTime ultimoIngreso) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.nroCelular = nroCelular;
        this.contraseña = contraseña;
        this.fechaNacimiento = fechaNacimiento;
        this.creadoEn = creadoEn;
        this.ultimoIngreso = ultimoIngreso;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNroCelular() {
        return nroCelular;
    }

    public void setNroCelular(String nroCelular) {
        this.nroCelular = nroCelular;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    public LocalDateTime getUltimoIngreso() {
        return ultimoIngreso;
    }

    public void setUltimoIngreso(LocalDateTime ultimoIngreso) {
        this.ultimoIngreso = ultimoIngreso;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
}
