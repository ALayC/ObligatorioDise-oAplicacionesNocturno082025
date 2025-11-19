package obligatorio.obligatorio.Modelo.modelos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public final class BonificacionFrecuentes extends Bonificacion {

    public BonificacionFrecuentes() {
        super("Frecuentes");
    }

    @Override
    public BigDecimal calcularMonto(BigDecimal montoBase,
                                    Propietario propietario,
                                    Vehiculo vehiculo,
                                    Puesto puesto,
                                    LocalDate fecha,
                                    List<Transito> transitosHistoricos) {

        long transitosHoy = transitosHistoricos.stream()
                .filter(t -> t.getVehiculo().equals(vehiculo))
                .filter(t -> t.getPuesto().equals(puesto))
                .filter(t -> fecha.equals(t.getFecha()))
                .count();
        //TODO: SISTEMA TRANISSTO ME TIENE QUE DAR LOS TRANSITOS DE HOY, AC ALOS TENGO QUE RECIBIR . sINO ROMPEMOS EXPERTOS(LA CANTIDAD DE TRASNTISO)                                   
        if (transitosHoy == 0) {
            return montoBase;
        }

        return montoBase.multiply(new BigDecimal("0.5"));
    }
}
