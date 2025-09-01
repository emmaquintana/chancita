package com.emmanuel.chancita.data.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Usuario {
    private String id;
    private String correo;
    private String nombre;
    private String apellido;
    private String nroCelular;
    private String contraseña;
    private LocalDate fechaNacimiento;
    private LocalDateTime creadoEn;
    private LocalDateTime ultimoIngreso;

    public Usuario(String id, String correo, String nombre, String apellido, String nroCelular, String contraseña, LocalDate fechaNacimiento, LocalDateTime creadoEn, LocalDateTime ultimoIngreso) {
        setId(id);
        setCorreo(correo);
        setNombre(nombre);
        setApellido(apellido);
        setNroCelular(nroCelular);
        setContraseña(contraseña);
        setFechaNacimiento(fechaNacimiento);
        setCreadoEn(creadoEn);
        setUltimoIngreso(ultimoIngreso);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
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

    public String getNroCelular() {
        return nroCelular;
    }

    public void setNroCelular(String nroCelular) {
        this.nroCelular = nroCelular;
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

    public static String getFechaNacimientoFormateada(LocalDate fechaNacimiento) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        return fechaNacimiento.format(formatter);
    }
}