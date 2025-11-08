package obligatorio.obligatorio.DTO;

import java.math.BigDecimal;

public class TarifaDTO {
    private String categoria;
    private BigDecimal monto;

    public TarifaDTO(String categoria, BigDecimal monto) {
        this.categoria = categoria;
        this.monto = monto;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
}
