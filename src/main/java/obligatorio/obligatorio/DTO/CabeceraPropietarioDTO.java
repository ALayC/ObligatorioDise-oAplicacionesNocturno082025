package obligatorio.obligatorio.DTO;

import java.math.BigDecimal;

public class CabeceraPropietarioDTO {
    public String propietarioNombreCompleto;
    public String estado;
    public BigDecimal saldoActual;

    public CabeceraPropietarioDTO(String propietarioNombreCompleto, String estado, BigDecimal saldoActual) {
        this.propietarioNombreCompleto = propietarioNombreCompleto;
        this.estado = estado;
        this.saldoActual = saldoActual;
    }
}
