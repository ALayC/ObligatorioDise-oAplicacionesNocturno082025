package obligatorio.obligatorio.DTO;

import java.math.BigDecimal;
// imports innecesarios eliminados

public class TransitoDTO {
    public String puesto;
    public String matricula;
    public String nombreTarifa;
    public BigDecimal montoTarifa;
    public String bonificacion;              
    public BigDecimal montoBonificacion;
    public BigDecimal montoPagado;
    public java.time.LocalDateTime fechaHora;

    public TransitoDTO(String puesto, String matricula, String nombreTarifa,
                       BigDecimal montoTarifa, String bonificacion, BigDecimal montoBonificacion,
                       BigDecimal montoPagado, java.time.LocalDateTime fechaHora) {
        this.puesto = puesto;
        this.matricula = matricula;
        this.nombreTarifa = nombreTarifa;
        this.montoTarifa = montoTarifa;
        this.bonificacion = bonificacion;
        this.montoBonificacion = montoBonificacion;
        this.montoPagado = montoPagado;
        this.fechaHora = fechaHora;
    }
}
