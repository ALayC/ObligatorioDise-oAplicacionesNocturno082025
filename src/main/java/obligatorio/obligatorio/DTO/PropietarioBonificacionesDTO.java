package obligatorio.obligatorio.DTO;

import java.util.List;

public class PropietarioBonificacionesDTO {
    private String nombreCompleto;
    private String estado;
    private List<BonificacionAsignadaDTO> bonificacionesAsignadas;

    public PropietarioBonificacionesDTO(String nombreCompleto, String estado, List<BonificacionAsignadaDTO> bonificacionesAsignadas) {
        this.nombreCompleto = nombreCompleto;
        this.estado = estado;
        this.bonificacionesAsignadas = bonificacionesAsignadas;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getEstado() {
        return estado;
    }

    public List<BonificacionAsignadaDTO> getBonificacionesAsignadas() {
        return bonificacionesAsignadas;
    }
}
