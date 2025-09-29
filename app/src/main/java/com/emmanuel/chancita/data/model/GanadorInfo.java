package com.emmanuel.chancita.data.model;

public class GanadorInfo {
    private String nombre;
    private String celular;
    private String email;
    private int numeroGanador;
    private int puesto;

    public GanadorInfo(String nombre, String celular, String email, int numeroGanador, int puesto) {
        this.nombre = nombre;
        this.celular = celular;
        this.email = email;
        this.numeroGanador = numeroGanador;
        this.puesto = puesto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getNumeroGanador() {
        return numeroGanador;
    }

    public void setNumeroGanador(int numeroGanador) {
        this.numeroGanador = numeroGanador;
    }

    public int getPuesto() {
        return puesto;
    }

    public void setPuesto(int puesto) {
        this.puesto = puesto;
    }
}
