package obligatorio.obligatorio.DTO;

import java.time.LocalDateTime;

public class BonificacionAsignadaDTO {
    public String bonificacion;
    public String puesto;
    public LocalDateTime fechaAsignada;

    public BonificacionAsignadaDTO(String bonificacion, String puesto, LocalDateTime fechaAsignada) {
        this.bonificacion = bonificacion;
        this.puesto = puesto;
        this.fechaAsignada = fechaAsignada;
    }
}
