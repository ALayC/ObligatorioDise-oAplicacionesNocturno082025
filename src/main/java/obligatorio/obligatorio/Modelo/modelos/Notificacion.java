package obligatorio.obligatorio.Modelo.modelos;
import java.time.LocalDate;
import java.util.Objects;
public final class Notificacion {
    private final String mensaje;
    private final LocalDate fechaHora;
    private boolean leida;

    public Notificacion(String mensaje, LocalDate fechaHora) {
        this.mensaje = Objects.requireNonNull(mensaje);
        this.fechaHora = Objects.requireNonNull(fechaHora);
        this.leida = false;
    }

    public String getMensaje() { return mensaje; }
    public LocalDate getFechaHora() { return fechaHora; }
    public boolean isLeida() { return leida; }
    public void marcarLeida(){ this.leida = true; }
}
