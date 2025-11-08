package obligatorio.obligatorio.Modelo.modelos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public final class Transito {
    private final Vehiculo vehiculo;
    private final Puesto puesto;
    private final Tarifa tarifaAplicada;
    private final LocalDateTime fechaHora;
    private final BigDecimal montoBase;
    private final BigDecimal montoCobrado;
    private final Bonificacion bonificacionAplicada;
    private final boolean cobrada;

    public Transito(Vehiculo vehiculo, Puesto puesto, Tarifa tarifaAplicada, LocalDateTime fechaHora, BigDecimal montoBase, BigDecimal montoCobrado, Bonificacion bonificacionAplicada, boolean cobrada) {
        this.vehiculo = Objects.requireNonNull(vehiculo);
        this.puesto = Objects.requireNonNull(puesto);
        this.tarifaAplicada = Objects.requireNonNull(tarifaAplicada);
        this.fechaHora = Objects.requireNonNull(fechaHora);
        this.montoBase = Objects.requireNonNull(montoBase);
        this.montoCobrado = Objects.requireNonNull(montoCobrado);
        this.bonificacionAplicada = bonificacionAplicada;
        this.cobrada = cobrada;
    }

    public Vehiculo getVehiculo() { return vehiculo; }
    public Puesto getPuesto() { return puesto; }
    public Tarifa getTarifaAplicada() { return tarifaAplicada; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public BigDecimal getMontoBase() { return montoBase; }
    public BigDecimal getMontoCobrado() { return montoCobrado; }
    public Bonificacion getBonificacionAplicada() { return bonificacionAplicada; }
    public boolean isCobrada() { return cobrada; }
}
