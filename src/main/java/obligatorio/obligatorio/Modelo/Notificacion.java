package obligatorio.obligatorio.Modelo;

import java.time.LocalDateTime;
import java.util.Objects;

public final class Notificacion {
    private final String mensaje;
    private final LocalDateTime fechaHora;
    private boolean leida;

    public Notificacion(String mensaje, LocalDateTime fechaHora) {
        this.mensaje = Objects.requireNonNull(mensaje);
        this.fechaHora = Objects.requireNonNull(fechaHora);
        this.leida = false;
    }

    public String getMensaje() { return mensaje; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public boolean isLeida() { return leida; }
    public void marcarLeida(){ this.leida = true; }
}
