package obligatorio.obligatorio.Modelo.modelos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public final class BonificacionExonerados extends Bonificacion {

    public BonificacionExonerados() {
        super("Exonerados");
    }

    @Override
    public BigDecimal calcularMonto(BigDecimal montoBase,
                                    Propietario propietario,
                                    Vehiculo vehiculo,
                                    Puesto puesto,
                                    LocalDate fecha,
                                    List<Transito> transitosHistoricos) {
        return BigDecimal.ZERO;
    }
}
