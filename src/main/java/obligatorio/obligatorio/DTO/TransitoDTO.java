package obligatorio.obligatorio.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class TransitoDTO {
    public String puesto;
    public String matricula;
    public String nombreTarifa;
    public BigDecimal montoTarifa;
    public String bonificacion;              
    public BigDecimal montoBonificacion;
    public BigDecimal montoPagado;
    public LocalDate fecha;
    public LocalTime hora;

    public TransitoDTO(String puesto, String matricula, String nombreTarifa,
                       BigDecimal montoTarifa, String bonificacion, BigDecimal montoBonificacion,
                       BigDecimal montoPagado, LocalDate fecha, LocalTime hora) {
        this.puesto = puesto;
        this.matricula = matricula;
        this.nombreTarifa = nombreTarifa;
        this.montoTarifa = montoTarifa;
        this.bonificacion = bonificacion;
        this.montoBonificacion = montoBonificacion;
        this.montoPagado = montoPagado;
        this.fecha = fecha;
        this.hora = hora;
    }
}
