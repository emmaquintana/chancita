package com.emmanuel.chancita.data.dto;

import java.time.LocalDateTime;

public class OrganizadorDTO {
    private String usuarioId;
    private String mpUserid;
    private String mpTokenAcceso;
    private String tokenRefresh;
    private LocalDateTime expiraEn;
    private String mpScope;

    public OrganizadorDTO(String usuarioId, String mpUserid, String mpTokenAcceso,
                          String tokenRefresh, LocalDateTime expiraEn, String mpScope) {
        this.usuarioId = usuarioId;
        this.mpUserid = mpUserid;
        this.mpTokenAcceso = mpTokenAcceso;
        this.tokenRefresh = tokenRefresh;
        this.expiraEn = expiraEn;
        this.mpScope = mpScope;
    }

    // Getters y setters
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }

    public String getMpUserid() { return mpUserid; }
    public void setMpUserid(String mpUserid) { this.mpUserid = mpUserid; }

    public String getMpTokenAcceso() { return mpTokenAcceso; }
    public void setMpTokenAcceso(String mpTokenAcceso) { this.mpTokenAcceso = mpTokenAcceso; }

    public String getTokenRefresh() { return tokenRefresh; }
    public void setTokenRefresh(String tokenRefresh) { this.tokenRefresh = tokenRefresh; }

    public LocalDateTime getExpiraEn() { return expiraEn; }
    public void setExpiraEn(LocalDateTime expiraEn) { this.expiraEn = expiraEn; }

    public String getMpScope() { return mpScope; }
    public void setMpScope(String mpScope) { this.mpScope = mpScope; }
}
