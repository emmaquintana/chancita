package com.emmanuel.chancita.data.dto;

public class RifaPremioDTO {
    private String premioTitulo;
    private String premioDescripcion;
    private String rifaId;
    private int premioOrden;

    public RifaPremioDTO(String premioTitulo, String premioDescripcion,
                         String rifaId, int premioOrden) {
        this.premioTitulo = premioTitulo;
        this.premioDescripcion = premioDescripcion;
        this.rifaId = rifaId;
        this.premioOrden = premioOrden;
    }

    public String getPremioTitulo() { return premioTitulo; }
    public void setPremioTitulo(String premioTitulo) { this.premioTitulo = premioTitulo; }

    public String getPremioDescripcion() { return premioDescripcion; }
    public void setPremioDescripcion(String premioDescripcion) { this.premioDescripcion = premioDescripcion; }

    public String getRifaId() { return rifaId; }
    public void setRifaId(String rifaId) { this.rifaId = rifaId; }

    public int getPremioOrden() { return premioOrden; }
    public void setPremioOrden(int premioOrden) { this.premioOrden = premioOrden; }
}
