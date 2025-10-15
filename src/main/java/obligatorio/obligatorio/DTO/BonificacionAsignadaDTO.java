package obligatorio.obligatorio.DTO;

import java.time.LocalDate;

public class BonificacionAsignadaDTO {
    public String bonificacion;
    public String puesto;
    public LocalDate fechaAsignada;

    public BonificacionAsignadaDTO(String bonificacion, String puesto, LocalDate fechaAsignada) {
        this.bonificacion = bonificacion;
        this.puesto = puesto;
        this.fechaAsignada = fechaAsignada;
    }
}
