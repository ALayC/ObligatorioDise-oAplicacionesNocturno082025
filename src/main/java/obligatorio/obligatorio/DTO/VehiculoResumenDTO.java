package obligatorio.obligatorio.DTO;

import java.math.BigDecimal;

public class VehiculoResumenDTO {
    public String matricula;
    public String modelo;
    public String color;
    public int cantidadTransitos;
    public BigDecimal montoTotalGastado;

    public VehiculoResumenDTO(String matricula, String modelo, String color, int cantidadTransitos, BigDecimal montoTotalGastado) {
        this.matricula = matricula;
        this.modelo = modelo;
        this.color = color;
        this.cantidadTransitos = cantidadTransitos;
        this.montoTotalGastado = montoTotalGastado;
    }
}
