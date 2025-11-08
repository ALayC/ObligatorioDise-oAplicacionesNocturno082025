package obligatorio.obligatorio.observador;

import java.time.LocalDateTime;

import obligatorio.obligatorio.Modelo.modelos.Puesto;
import obligatorio.obligatorio.Modelo.modelos.Vehiculo;

public class TransitoRealizadoEvento {
    private final Vehiculo vehiculo;
    private final Puesto puesto;
    private final LocalDateTime fechaHora;

    public TransitoRealizadoEvento(Vehiculo vehiculo, Puesto puesto, LocalDateTime fechaHora) {
        this.vehiculo = vehiculo;
        this.puesto = puesto;
        this.fechaHora = fechaHora;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public Puesto getPuesto() {
        return puesto;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
}
