package obligatorio.obligatorio.DTO;

import java.time.LocalDateTime;

public class NotificacionDTO {
    public LocalDateTime fechaHora;
    public String mensaje;

    public NotificacionDTO(LocalDateTime fechaHora, String mensaje) {
        this.fechaHora = fechaHora;
        this.mensaje = mensaje;
    }
}
