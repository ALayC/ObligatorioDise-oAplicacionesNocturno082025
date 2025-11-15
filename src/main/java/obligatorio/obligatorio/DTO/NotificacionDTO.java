package obligatorio.obligatorio.DTO;

import java.time.LocalDate;
public class NotificacionDTO {
    public LocalDate fechaHora;
    public String mensaje;

    public NotificacionDTO(LocalDate fechaHora, String mensaje) {
        this.fechaHora = fechaHora;
        this.mensaje = mensaje;
    }
}
