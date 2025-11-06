package obligatorio.obligatorio.DTO;

import java.math.BigDecimal;

public class ResultadoEmulacionDTO {
    private String nombrePropietario;
    private String estado;
    private String categoria;
    private String bonificacion;
    private BigDecimal costoTransito;
    private BigDecimal saldoDespues;

    public ResultadoEmulacionDTO() {}

    public ResultadoEmulacionDTO(String nombrePropietario, String estado, String categoria, 
                                 String bonificacion, BigDecimal costoTransito, BigDecimal saldoDespues) {
        this.nombrePropietario = nombrePropietario;
        this.estado = estado;
        this.categoria = categoria;
        this.bonificacion = bonificacion;
        this.costoTransito = costoTransito;
        this.saldoDespues = saldoDespues;
    }

    public String getNombrePropietario() { return nombrePropietario; }
    public void setNombrePropietario(String nombrePropietario) { this.nombrePropietario = nombrePropietario; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getBonificacion() { return bonificacion; }
    public void setBonificacion(String bonificacion) { this.bonificacion = bonificacion; }

    public BigDecimal getCostoTransito() { return costoTransito; }
    public void setCostoTransito(BigDecimal costoTransito) { this.costoTransito = costoTransito; }

    public BigDecimal getSaldoDespues() { return saldoDespues; }
    public void setSaldoDespues(BigDecimal saldoDespues) { this.saldoDespues = saldoDespues; }
}
