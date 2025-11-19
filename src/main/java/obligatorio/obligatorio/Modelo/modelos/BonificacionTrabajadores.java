package obligatorio.obligatorio.Modelo.modelos;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public final class BonificacionTrabajadores extends Bonificacion {

    public BonificacionTrabajadores() {
        super("Trabajadores");
    }

    @Override
    public BigDecimal calcularMonto(BigDecimal montoBase,
                                    Propietario propietario,
                                    Vehiculo vehiculo,
                                    Puesto puesto,
                                    LocalDate fecha,
                                    List<Transito> transitosHistoricos) {
        //TODO: SEGUN EXPEROT RTANSITO ME TIENE QUE DECIR DE QUE DIAS , EJ: FUI REALIZADO UN LUNES                                
        DayOfWeek d = fecha.getDayOfWeek();
        boolean esDiaDeSemana = d != DayOfWeek.SATURDAY && d != DayOfWeek.SUNDAY;

        if (esDiaDeSemana) {
            return montoBase.multiply(new BigDecimal("0.2"));
        }

        return montoBase;
    }
}
